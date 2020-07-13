import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;

/**
 * The AI is created using a minimax algorithm w/ a-B pruning Multiple
 * heuristics are implemented which are expected to act at unique difficulty
 * levels.
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 *
 */
public class AI {
    private char player;
    private char opponent;
    private Game game;
    private int depth = 5; // depth of minimax search
    private int select; // heuristic choice

    /**
     * Construct AI
     */
    public AI(char player, Game game, int select) {
        this.player = player;
        if (player == 'w')
            opponent = 'b';
        else
            opponent = 'w';
        this.game = game;
        this.select = select;
    }

    /**
     * First layer minimax The real work occurs in minimaxR (Recursive minimax)
     *
     * @return The pair corresponding to the best move
     */
    public Pair getMove() {
        Pair choice = null;
        int max;
        int check;
        char[][] checkBoard;
        // Generate pairs that correspond to first layer board states
        ArrayList<ArrayList<Pair>> plays = game.generateMoves(player, game.getBoard());
        if (plays.isEmpty()) // hack to fix a bug where the stack is empty at no plays
            return new Pair(0, 0);
        Stack<char[][]> boards = generateBoards(player, game.getBoard());
        if (!boards.empty()) {
            checkBoard = boards.pop();
            max = minimaxR(true, null, null, 1, generateBoards(player, checkBoard), checkBoard);
            // this max can be used to prune the tree at the highest level, used as
            // rootValMAX in next call
            choice = plays.get(0).get(0);
            for (int i = 1; i < plays.size(); i++) {
                checkBoard = boards.pop();
                check = minimaxR(true, max, null, 1, generateBoards(player, checkBoard), checkBoard);
                if (check > max) {
                    max = check;
                    choice = plays.get(i).get(0);
                }
            }
        }
        return choice;
    }

    /**
     * Recursive minimax First checks that there are child nodes to explore. If not,
     * return this nodes heuristic Then checks depth. If we've reached maximum
     * depth, return heuristic (leaf). Then checks if max node or min. Recursively
     * determine the max and min from nodes below. Deep A-B Pruning implemented
     * (Fairly sure this is deep)
     *
     * @param max
     * @param rootValMAX
     *            Current pruning value at max nodes
     * @param rootValMIN
     *            Current pruning value at min nodes
     * @param boards
     *            layer of child boards
     * @param thisBoard
     *            parent node
     * @return nodeValue
     */
    private int minimaxR(boolean max, Integer rootValMAX, Integer rootValMIN, int nodeDepth, Stack<char[][]> boards,
                         char[][] thisBoard) {
        int nodeVal;
        int tempVal;
        if (boards.empty()) {
            return heuristic(thisBoard);
        }
        thisBoard = boards.pop();
        nodeVal = heuristic(thisBoard);
        if (nodeDepth >= depth) { // return heuristic
            if (max) {
                while (!boards.empty()) { // while there are still children, compare children
                    thisBoard = boards.pop();
                    tempVal = heuristic(thisBoard);
                    if (tempVal > nodeVal)
                        nodeVal = tempVal;
                }
            } else {
                while (!boards.empty()) { // while there are still children, compare children
                    thisBoard = boards.pop();
                    tempVal = heuristic(thisBoard);
                    if (tempVal < nodeVal)
                        nodeVal = tempVal;
                }
            }
            return nodeVal;
        }
        if (max) {
            while (!boards.empty()) { // while there are still children, compare children
                thisBoard = boards.pop();
                tempVal = minimaxR(!max, rootValMAX, rootValMIN, nodeDepth + 1, generateBoards(opponent, thisBoard),
                        thisBoard);
                if (rootValMAX == null) // pruning segment, if rootVal is null, set it
                    rootValMAX = tempVal;
                else if (tempVal < rootValMAX) // if the min node can not be higher than this max node
                    return rootValMAX; // prune
                if (tempVal > nodeVal)
                    nodeVal = tempVal;
            }
        } else { // min
            while (!boards.empty()) { // while there are still children, compare children
                thisBoard = boards.pop();
                tempVal = minimaxR(!max, rootValMAX, rootValMIN, nodeDepth + 1, generateBoards(player, thisBoard),
                        thisBoard);
                if (tempVal < nodeVal)
                    nodeVal = tempVal;
                if (rootValMIN == null) // pruning segment, if rootVal is null, set it
                    rootValMIN = tempVal;
                else if (tempVal > rootValMIN) // if the max node can not be lower than this min node
                    return rootValMIN; // prune
                if (tempVal > nodeVal)
                    nodeVal = tempVal;
            }
        }
        return nodeVal;
    }

    /**
     * Generate the possible boards from the set of possible moves
     *
     * @param player
     * @param moves
     *            The possible moves
     * @param board
     *            The current board state
     * @param boards
     *            A stack of possible board states (The minimax "tree")
     * @return
     */
    private Stack<char[][]> generateBoards(char player, char[][] board) {
        Stack<char[][]> boards = new Stack<char[][]>();
        ArrayList<ArrayList<Pair>> moves = game.generateMoves(player, board);
        HashSet<Pair> usedPairs = new HashSet<Pair>(); // to prevent non-unique boards
        for (ArrayList<Pair> move : moves) {
            Pair moveSelect = move.get(0); // Determines the new tile placement
            if (!usedPairs.contains(moveSelect)) {
                usedPairs.add(moveSelect);
                boards.push(selectSpace(moveSelect, player, moves, board));
            }
        }
        return boards;
    }

    /**
     * Heuristic selector
     * Names not necessarily indicative of true difficulty
     *
     * @param board
     * @return
     */
    private int heuristic(char[][] board) {
        switch (select) {
            case 1:
                return simpleHeuristic(board);
            case 2:
                return mediumHeuristic(board);
            case 3:
                return hardHeuristic(board);
            default:
                return simpleHeuristic(board);
        }
    }

    /**
     * Generate heuristic value based on board configuration This heuristic simply
     * counts the number of player tiles on the board It is expected to be a simple,
     * easy heuristic to beat.
     *
     * @param board
     */
    private int simpleHeuristic(char[][] board) {
        int count = 0;
        for (char[] row : board) {
            for (char space : row) {
                if (space == player)
                    count++;
            }
        }
        return count;
    }

    /**
     * Generate heuristic value based on board configuration This heuristic counts
     * positions of board pieces relative to taking corners which is, admittedly,
     * arbitrary. The weights are scaled from -1000 - 1000, where corner pieces are
     * 100 and the spaces just before corner pieces are -100. The pieces around
     * those are weighted at 75, with the pieces surrounding those significantly
     * more arbitrary. A significant flaw in this heuristic is that it will not know
     * how to play after acquiring a corner. It is expected to be a medium
     * difficulty heuristic
     *
     * @param board
     */
    private int mediumHeuristic(char[][] board) {
        int count = 0;
        int[][] boardHeuristic = { { 100000, -10000, 75, 25, 25, 75, -10000, 100000 },
                { -10000, -10000, 75, 50, 50, 75, -10000, -10000 }, { 75, 75, 75, 65, 65, 75, 75, 75 },
                { 25, 50, 65, 25, 25, 65, 50, 25 }, { 25, 50, 65, 25, 25, 65, 50, 25 },
                { 75, 75, 75, 65, 65, 75, 75, 75 }, { -10000, -10000, 75, 50, 50, 75, -10000, -10000 },
                { 100000, -1000, 75, 25, 25, 75, -10000, 100000 } };
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == player)
                    count += boardHeuristic[i][j];
            }
        }
        return count;
    }

    /**
     * Generate heuristic value based on board configuration This heuristic counts
     * possible positions from which the player's pieces may be flipped. It thereby
     * seeks to reduce the effectiveness of the other player's best turn. To do
     * this, it will use the generate moves method from the game class and count all
     * affected pairs. (Everything but first item in the generated ArrayLists) It is
     * hoped to be a difficult heuristic
     *
     * @param board
     */
    private int hardHeuristic(char[][] board) {
        int count = 0;
        ArrayList<ArrayList<Pair>> moves = game.generateMoves(opponent, board);
        for (ArrayList<Pair> move : moves) {
            for (int i = 1; i < move.size(); i++)
                count++;
        }
        // Return -count so that the maximum value is preferable.
        return -count;
    }

    /**
     * Modified version of the selectSpace method in Game class Generates new board
     * state
     *
     * @param pair
     *            The new tile to place
     * @param player
     *            The color of tile to place
     * @param moves
     *            The previously generated moves
     * @return The new board state
     */
    private char[][] selectSpace(Pair pair, char player, ArrayList<ArrayList<Pair>> moves, char[][] board) {
        char[][] retBoard = deepCopy(board);
        for (ArrayList<Pair> pairs : moves) {
            if (pair.equals(pairs.get(0))) {
                game.swap(player, pairs, retBoard); // swap all between pieces
            }
        }
        retBoard[pair.getX()][pair.getY()] = player; // add new piece to board
        // printBoard(board);
        // printBoard(retBoard);
        return retBoard;
    }

    private void printBoard(char[][] board) {
        String retStr = "";
        for (char[] row : board) {
            for (char space : row)
                retStr += space + " ";
            retStr += "\n";
        }
        System.out.println(retStr);
    }

    private char[][] deepCopy(char[][] board) {
        char[][] newBoard = new char[8][8];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = Arrays.copyOf(board[i], 8);
        }
        return newBoard;
    }

    public String getPlayer() {
        if (player == 'w')
            return "White";
        else
            return "Black";
    }
}
