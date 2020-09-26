import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.LayerUI;

import java.util.HashMap;

/**
 * GUI for Othello game Takes player inputs and displays game state
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 */

public class GUI extends JFrame implements MouseListener, ItemListener {
    private String player = "Black";
    private Game game;
    private JPanel gamePanel;
    private JLabel playerLabel;
    private JMenuBar menuBar;
    private JCheckBoxMenuItem debugOnCB;
    private AI opponent;

    public GUI(String title, Game game, AI opp) throws InterruptedException {
        super(title);
        this.game = game;
        opponent = opp;
        gamePanel = new JPanel(new GridBagLayout());
        createMenus();
        addSquares(gamePanel);
        updateSquares(game.getBoard(), gamePanel);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        titlePanel.add(new JLabel("OTHELLO"));

        JPanel playPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        playerLabel = new JLabel(player + "'s turn");
        playPanel.add(playerLabel);

        getContentPane().add(gamePanel, BorderLayout.CENTER);
        getContentPane().add(titlePanel, BorderLayout.NORTH);
        getContentPane().add(playPanel, BorderLayout.SOUTH);
        setSize(new Dimension(500, 700));
        setResizable(false);

        setVisible(true);
        setLocation(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        game.start(this);
    }

    private class OverlayLayerUI extends LayerUI<JComponent> {
        private String overlayText = "";
        private boolean isVisible = false;

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);

            if (isVisible) {
                g.setColor(Color.RED);
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.drawString(overlayText, c.getWidth() / 4, c.getHeight() / 2 + 4);
            }
        }

        public void setText(String text) {
            overlayText = text;
        }

        public void setVisibility(boolean isVisible) {
            this.isVisible = isVisible;
        }

        public boolean isVisible() {
            return isVisible;
        }
    }

    /**
     *  Add Menus to JFrame
     */
    private void createMenus() {
        menuBar = new JMenuBar();
        JMenu debugMenu = new JMenu("Debug Mode");
        debugOnCB = new JCheckBoxMenuItem("Show Heuristic Values");
        debugOnCB.addItemListener(this);
        debugMenu.add(debugOnCB);
        menuBar.add(debugMenu);
        setJMenuBar(menuBar);
    }


    /**
     * Add Squares to panel
     *
     * @param gamePanel
     */
    private void addSquares(JPanel gamePanel) {

        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                c.gridx = j;
                c.gridy = i;
                Square sqComponent = new Square(i, j);
                OverlayLayerUI sqLayerUI = new OverlayLayerUI();
                JLayer sq = new JLayer(sqComponent, sqLayerUI);
                sq.addMouseListener(this);
                gamePanel.add(sq, c);
            }
        }
    }

    /**
     * Update board based on new board state
     *
     * @param board
     */
    private void updateSquares(char[][] board, JPanel gamePanel) {
        HashMap<Pair, Integer> heuristicMap = opponent.getHeuristicMap();
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                JLayer sqLayer = (JLayer) gamePanel.getComponent(8 * i + j);
                OverlayLayerUI sqOverlay = (OverlayLayerUI) sqLayer.getUI();
                Square sq = (Square)sqLayer.getView();
                if (board[i][j] == 'w')
                    sq.setWhite();
                else if (board[i][j] == 'b')
                    sq.setBlack();
                else
                    sq.resetColor();
                Pair tempPair = new Pair(i, j);
                if (heuristicMap.containsKey(tempPair)) {
                    sqOverlay.setText(heuristicMap.get(tempPair).toString()); }
                else
                    sqOverlay.setText("");
            }
        }
    }

    /**
     * Update state based on clicked square
     *
     * @param i
     * @param j
     * @throws InterruptedException
     */
    private void updateState(int i, int j) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                game.setChoice(i,j);
                try {
                    Thread.sleep(150);
                } catch(InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                player =game.getPlayer();
                playerLabel.setText(player +"'s Turn.");

                updateSquares(game.getBoard(),gamePanel);

                repaint();
                try
                {
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // should AI control be here rather than Game?
                if(player.equals(opponent.getPlayer())) {
                    Pair aiChoice = opponent.getMove();
                    updateState(aiChoice.getX(), aiChoice.getY());
                }
            }
        });
    }

    /**
     *  Toggles visibility of heuristic calculations on all squares
     */
    private void toggleHeuristicVisibility() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JLayer sqLayer =(JLayer) gamePanel.getComponent(8 * i + j);
                OverlayLayerUI sq = (OverlayLayerUI) sqLayer.getUI();
                sq.setVisibility(!sq.isVisible());
                sqLayer.repaint();
            }
        }
    }

    public void showEndDialog(String message) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    int choice = JOptionPane.showOptionDialog(getSelf(), message, "Game Over", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE, null, new String[]{"Play again", "Quit"}, null);
                    if (choice == 0) {
                        game.set();
                        try {
                            Thread.sleep(250); // make sure game has reset before update
                            updateSquares(game.getBoard(), gamePanel);
                        } catch (Exception e) { }
                    } else if (choice == 1)
                        System.exit(0);
                }
            });
        }catch(Exception e) {e.printStackTrace();}
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Square sq = (Square) ((JLayer)e.getSource()).getView();
        updateState(sq.getI(), sq.getJ());
        e.consume();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();
        if (source == debugOnCB) {
            toggleHeuristicVisibility();
        }
    }

    public GUI getSelf() {
        return this;
    }
}
