import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Game class handles everything game-related. Stores board state, generates
 * allowable moves, determines if end-game condition has been met, etc. Board
 * state is stored in an 8 x 8 char array where 'w' represents the white player
 * and 'b' represents the black player. 'e' represents an empty space.
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 *
 */
public class Game {
    private char[][] board = new char[8][8];
    private char player;
    private int x, y;
    private boolean waiting = true;
    private boolean canMove = false; // Can the player move? (End condition)
    private static Game game = null;

    /**
     * Only allows one instance of a game at a time.
     *
     * @return a new Game instance
     */
    static public Game newGame() {
        if (game == null) {game = new Game();}
        return game;
    }

    /**
     * Constructor should initialize game state.
     */
    private Game() {
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = 'e';
            }
        }
        board[3][3] = 'w';
        board[4][4] = 'w';
        board[3][4] = 'b';
        board[4][3] = 'b';
        player = 'b'; // By convention, black starts
    }

    /**
     * Method to start and control the game's flow
     *
     * @param frame
     * @throws InterruptedException
     */
    public void start(JFrame frame) throws InterruptedException {
        x = 0;
        y = 0;
        boolean turnFlag = false;
        boolean noMoveFlag = false;
        System.out.print(this);
        while (!noMoveFlag) {
            ArrayList<ArrayList<Pair>> moves = generateMoves(player, board);
            if (canMove) {
                noMoveFlag = false;
                while (!turnFlag) {
                    while (waiting) {
                        Thread.sleep(100);
                    } // waiting for input
                    turnFlag = true; // Player has taken their turn
                    if (!selectSpace(x, y, player, moves)) {
                        turnFlag = false;
                        waiting = true;
                        System.err.println("This is not a valid space");
                    }
                }
            } else { // if this player can't move
                if (noMoveFlag) { // and the other player cant move
                    endGame(frame); // end game
                    break;
                }
                else { // skip Player's turn
                    noMoveFlag = true;
                }
            }
            canMove = false;
            waiting = true;
            turnFlag = false;
            changePlayer();
            System.out.print(this);
        }
        endGame(frame);
    }

    private void changePlayer() {
        if (player == 'w')
            player = 'b';
        else
            player = 'w';
    }

    /**
     * Player Input
     *
     * @param x
     * @param y
     */
    public void setChoice(int x, int y) {
        this.x = x;
        this.y = y;
        waiting = false;
    }

    public String getPlayer() {
        if (player == 'w')
            return "White";
        else
            return "Black";
    }

    /**
     * User selects a space and this method determines if the move is valid using
     * info generated from move generation. If valid, the board state is changed
     * accordingly.
     *
     * @return True if the move succeeded
     */
    private boolean selectSpace(int i, int j, char player, ArrayList<ArrayList<Pair>> moves) {
        boolean flag = false;
        for (ArrayList<Pair> pairs : moves) {
            if (pairs != null) {
                if (new Pair(i, j).equals(pairs.get(0))) {
                    swap(player, pairs, board); // swap all between pieces
                    pairs.clear();
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * Swap all board pieces contained within pair info
     *
     * @param player
     * @param pairs
     *            Places on board to swap
     * @param board
     *            What board to alter (for minimax state generation)
     */
    public void swap(char player, ArrayList<Pair> pairs, char[][] board) {
        int size = pairs.size();
        Pair pair = null;
        for (int i = 0; i < size; i++) {
            pair = pairs.get(i);
            board[pair.getX()][pair.getY()] = player;
        }
    }

    /**
     * Generate allowable moves for player to make. This will be checked against
     * when the player chooses their move. Players may place their piece in any
     * position such that a tile ends up flipped.
     *
     * @param player
     * @param board
     *            For alternate board move generation (for minimax state generation)
     */
    public ArrayList<ArrayList<Pair>> generateMoves(char player, char[][] board) {
        ArrayList<ArrayList<Pair>> retList = new ArrayList<ArrayList<Pair>>();
        char opponent = 'o';
        if (player == 'w') {
            opponent = 'b';
        } else if (player == 'b') {
            opponent = 'w';
        } else
            System.err.println("Bad Player Char");
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == opponent) { // Possible moves occur around opponent
                    checkSpaceMoves(i, j, retList, player, opponent);
                }
            }
        }
        retList.removeAll(Collections.singleton(null)); // remove generated null items
        return retList;
    }

    /**
     * Check around specified space and add any allowable moves to retList This
     * method essentially just checks if the surround spaces are allowable and uses
     * helper methods to do the heavy lifting The "check" family of methods checks
     * if there is a corresponding player tile after a chain of opponent tiles.
     *
     * @param i
     *            i index
     * @param j
     *            j index
     * @param retList
     *            The return list will indicate all possible moves for a player,
     *            particularly, in one direction. So there will be many of the same
     *            space in the return list if the space affects multiple directions.
     */
    private void checkSpaceMoves(int i, int j, ArrayList<ArrayList<Pair>> retList, char player, char opponent) {
        if (i != 0) {
            retList.add(checkU(i, j, player, opponent));
            if (j != 0) {
                retList.add(checkUL(i, j, player, opponent));
                retList.add(checkL(i, j, player, opponent));
            }
            if (j != 7) {
                retList.add(checkUR(i, j, player, opponent));
                retList.add(checkR(i, j, player, opponent));
            }
        }
        if (i != 7) {
            retList.add(checkD(i, j, player, opponent));
            if (j != 0) {
                retList.add(checkDL(i, j, player, opponent));
            }
            if (j != 7) {
                retList.add(checkDR(i, j, player, opponent));
            }
        }
        // Next 2 are special cases not covered above
        if (i == 0 && j != 0)
            retList.add(checkL(i, j, player, opponent));
        if (i == 0 && j != 7)
            retList.add(checkR(i, j, player, opponent));
        for (ArrayList<Pair> move : retList) {
            if (move != null)
                canMove = true;
        }
    }

    /**
     * Check from top to bottom if a move can be made and what the resulting move
     * does
     *
     * @return an ArrayList where the first index is the placed move and the
     *         following indices are the tiles to be flipped Returns null if the
     *         move is invalid.
     */
    private ArrayList<Pair> checkU(int i, int j, char player, char opponent) {
        if (board[i - 1][j] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i - 1, j)); // move
            retList.add(new Pair(i, j));
            // begin below current space and go down
            for (int k = i + 1; k < 8; k++) {
                if (board[k][j] == opponent)
                    retList.add(new Pair(k, j));
                else if (board[k][j] == player)
                    return retList;
                else
                    return null;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkD(int i, int j, char player, char opponent) {
        if (board[i + 1][j] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i + 1, j)); // move
            retList.add(new Pair(i, j));
            // begin above current space and go up
            for (int k = i - 1; k >= 0; k--) {
                if (board[k][j] == opponent)
                    retList.add(new Pair(k, j));
                else if (board[k][j] == player)
                    return retList;
                else
                    return null;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkR(int i, int j, char player, char opponent) {
        if (board[i][j + 1] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i, j + 1)); // move
            retList.add(new Pair(i, j));
            // go left of current space
            for (int k = j - 1; k >= 0; k--) {
                if (board[i][k] == opponent)
                    retList.add(new Pair(i, k));
                else if (board[i][k] == player)
                    return retList;
                else
                    return null;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkL(int i, int j, char player, char opponent) {
        if (board[i][j - 1] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i, j - 1)); // move
            retList.add(new Pair(i, j));
            // go right of current space
            for (int k = j + 1; k < 8; k++) {
                if (board[i][k] == opponent)
                    retList.add(new Pair(i, k));
                else if (board[i][k] == player)
                    return retList;
                else
                    return null;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkUR(int i, int j, char player, char opponent) {
        if (board[i - 1][j + 1] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i - 1, j + 1)); // move
            retList.add(new Pair(i, j));
            // go down-left of current space
            int l = j - 1;
            for (int k = i + 1; k < 8 && l >= 0; k++) {
                if (board[k][l] == opponent)
                    retList.add(new Pair(k, l));
                else if (board[k][l] == player)
                    return retList;
                else
                    return null;
                l--;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkUL(int i, int j, char player, char opponent) {
        if (board[i - 1][j - 1] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i - 1, j - 1)); // move
            retList.add(new Pair(i, j));
            // go down-right of current space
            int l = j + 1;
            for (int k = i + 1; k < 8 && l < 8; k++) {
                if (board[k][l] == opponent)
                    retList.add(new Pair(k, l));
                else if (board[k][l] == player)
                    return retList;
                else
                    return null;
                l++;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkDL(int i, int j, char player, char opponent) {
        if (board[i + 1][j - 1] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i + 1, j - 1)); // move
            retList.add(new Pair(i, j));
            // go up-right of current space
            int l = j + 1;
            for (int k = i - 1; k >= 0 && l < 8; k--) {
                if (board[k][l] == opponent)
                    retList.add(new Pair(k, l));
                else if (board[k][l] == player)
                    return retList;
                else
                    return null;
                l++;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private ArrayList<Pair> checkDR(int i, int j, char player, char opponent) {
        if (board[i + 1][j + 1] == 'e') {
            ArrayList<Pair> retList = new ArrayList<Pair>();
            retList.add(new Pair(i + 1, j + 1)); // move
            retList.add(new Pair(i, j));
            // go up-left of current space
            int l = j - 1;
            for (int k = i - 1; k >= 0 && l >= 0; k--) {
                if (board[k][l] == opponent)
                    retList.add(new Pair(k, l));
                else if (board[k][l] == player)
                    return retList;
                else
                    return null;
                l--;
            }
        }
        return null; // null indicates this is not a viable move
    }

    private void endGame(JFrame frame) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int blackCount = 0, whiteCount = 0;
                String message = "";
                for (char[] row : board) {
                    for (char space : row) {
                        if (space == 'b')
                            blackCount++;
                        else if(space == 'w')
                            whiteCount++;
                    }
                }
                if (blackCount > whiteCount)
                    message += "Black Wins!\n";
                else if (whiteCount > blackCount)
                    message += "White Wins!\n";
                else
                    message += "Tie!\n";
                message += "Black: " + blackCount + "\n";
                message += "White: " + whiteCount + "\n";
                JOptionPane.showMessageDialog(frame, message, "Game Over", JOptionPane.PLAIN_MESSAGE);
            }
        });
    }

    public String toString() {
        String retStr = "\n";
        for (char[] row : board) {
            for (char space : row)
                retStr += space + " ";
            retStr += "\n";
        }
        return retStr;
    }

    public char[][] getBoard() {
        return board;
    }
}
