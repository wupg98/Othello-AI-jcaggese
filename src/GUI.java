import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * GUI for Othello game Takes player inputs and displays game state
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 */

public class GUI extends JFrame implements MouseListener {
    private String player = "Black";
    private Game game;
    private JPanel gamePanel;
    private JLabel playerLabel;
    private AI opponent;

    public GUI(String title, Game game, AI opp) throws InterruptedException {
        super(title);
        this.game = game;
        opponent = opp;
        gamePanel = new JPanel(new GridBagLayout());
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

    /**
     * Add Squares to panel
     *
     * @param gamePanel
     */
    private void addSquares(JPanel gamePanel) {
        Square sq;
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                c.gridx = j;
                c.gridy = i;
                sq = new Square(i, j);
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
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == 'w') {
                    ((Square) gamePanel.getComponent(8 * i + j)).setWhite();
                } else if (board[i][j] == 'b') {
                    ((Square) gamePanel.getComponent(8 * i + j)).setBlack();
                }
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
        game.setChoice(i, j);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        player = game.getPlayer();
        playerLabel.setText(player + "'s Turn.");
        updateSquares(game.getBoard(), gamePanel);
        repaint();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (player.equals(opponent.getPlayer())) {
            Pair aiChoice = opponent.getMove();
            updateState(aiChoice.getX(), aiChoice.getY());
        }

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
        Square sq = (Square) e.getSource();
        updateState(sq.getI(), sq.getJ());
        e.consume();
    }
}
