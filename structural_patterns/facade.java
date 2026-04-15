package structural_patterns;

public class facade {
    
    enum GameState{
        IN_PROGRESS,
        WIN,
        DRAW
    }

    static class Board{

        public boolean placeMark(int row, int col, String mark){
            // Make a move logic
            return true;
        }

        public boolean checkWin(int row, int col){
            //check win logic
            return true;
        }

        public boolean isFull(){
            //check full board logic
            return true;
        }

    }

    static class Player{
        private String mark;
        public Player(String mark){
            this.mark = mark;
        }
        public String getMark(){
            return this.mark;
        }
    }

    static class Game{
        private Board board;
        private Player playerX;
        private Player playerO;
        private GameState state;
        private Player currPlayer;

        public Game(){
            this.board = new Board();
            this.playerX = new Player("X");
            this.playerO = new Player("O");
            this.state = GameState.IN_PROGRESS;
            this.currPlayer = playerX;
        }

        public boolean makeMake(int row, int col){
            if (state!=GameState.IN_PROGRESS) {
                return false;
            }
            if (!board.placeMark(row, col, currPlayer.getMark())) {
                return false;
            }
            if (board.checkWin(row, col)) {
                state = GameState.WIN;
            }else if (board.isFull()) {
                state = GameState.DRAW;
            }else{
                currPlayer = (currPlayer==playerX) ? playerO : playerX;
            }
            return true;
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.makeMake(0, 0);
        game.makeMake(1, 3);
    }

}
