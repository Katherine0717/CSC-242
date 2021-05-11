import java.util.ArrayList;

interface BoardInterface{
    int getBoardLength();

    int[][] getBoard();

    void initialState(int length); //initial state

    int[][] copyBoard(int[][] board);

    void printBoard(int length, int turn, int[][]board);

    boolean checkValid(int row, int column, int dr, int dc, int turn, int length,int[][]board);

    boolean Valid(int a,int b, int turn,int length,int[][] board);

    ArrayList<Point> getMoveList(int turn, int length, int[][]board);

    void Flip(int row, int column, int turn, int length, int[][]board); // actions

    void ReadyFlip(int row, int column, int dr, int dc, int turn, int length,int[][] board);

    boolean end(int turn, int length,int[][]board); // goal state

    int num(int turn, int length, int[][]board); // heuristic value

    void result(int length,int[][] board);

    void playBlack(int turn, int length, int option); // if the user wants to play black

    void playWhite(int turn, int length, int option); // if the user wants to play white

}

