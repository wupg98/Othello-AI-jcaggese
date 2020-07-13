public class main {

    public static void main(String[] args) throws InterruptedException {
        Game game = Game.newGame();
        GUI gui = new GUI("Othello", game, new AI('w', game, 2));
    }

}
