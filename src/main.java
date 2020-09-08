public class main {

    public static void main(String[] args) {
        try {
            Game game = Game.newGame();
            GUI gui = new GUI("Othello", game, new AI('w', game, 2));
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }

}
