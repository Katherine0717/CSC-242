import java.util.ArrayList;
import java.util.Scanner;

public class Board implements BoardInterface {
    public int boardlength;
    public int[][] board; //0 is null;1 is black ;2 is white
    public int turn;//This is whose turn; 1 is black; 2 is white; 0 is null
    public final int[] dr={0, 0, 1, 1, 1, -1, -1, -1};
    public final int[] dc={1, -1, 0, 1, -1, 0, 1, -1};
    public Minimax minimax;

    public int getBoardLength()
    {
        return boardlength;
    }

    public int[][] getBoard(){
        return board;
    }

    public void initialState(int length) { //Give board the initial setting
        if(length==8) {
            board = new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 2, 1, 0, 0, 0},
                    {0, 0, 0, 1, 2, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0}
            };
        } else if(length == 6){
            board=new int[][]{
                    {0,0,0,0,0,0},
                    {0,0,0,0,0,0},
                    {0,0,2,1,0,0},
                    {0,0,1,2,0,0},
                    {0,0,0,0,0,0},
                    {0,0,0,0,0,0},
            };
        } else {
            board=new int[][]{
                    {0,0,0,0},
                    {0,2,1,0},
                    {0,1,2,0},
                    {0,0,0,0},
            };
        }
    }

    public Board(int length){ // constructor
        boardlength=length;
    }

    public Board(int length, Minimax minimax){ //This constructor makes the default starting board
        boardlength=length;
        this.minimax = minimax;
        turn = 0; //0 for black, 1 for white
        board = new int[boardlength][boardlength];
        initialState(boardlength);
        printBoard(length, 1, board);
    }

    public int[][] copyBoard(int[][] board){ //This one clones a given Board template
        int [][] newBoard=new int[board.length][board[0].length];
        for(int i=0;i<board.length;i++)
            for (int j=0;j<board.length;j++)
                newBoard[i][j]= board[i][j];
        return newBoard;
    }

    public void printBoard(int length, int turn, int[][]board) {
        System.out.println();
        System.out.print(" ");
        for(int i=0;i<length;i++) {
            char c =(char)(i+97); //a b c d...
            System.out.print(" "+c);
        }
        System.out.println();
        for (int i = 0; i < length; i++) {
            System.out.printf(i+1 + " ");
            for (int j = 0; j < length; j++) {
                if (board[i][j] == 2) {//White turn, print o
                    System.out.printf("o ");
                } else if (board[i][j] == 1) {//Black turn, print x
                    System.out.printf("x ");
                } else if (Valid(i, j, turn,length,board)) {//remind player which block they can move
                    System.out.printf("* "); //Show the available move to next player
                } else {
                    System.out.printf("  ");
                }
                if(j==length-1) { // print 1 2 3 4 ...
                    System.out.print(i+1);
                    System.out.println();
                }
            }
        }
        System.out.print(" ");
        for(int i=0;i<length;i++) {
            char c =(char)(i+97);
            System.out.print(" "+c);
        }
        System.out.println();
    }

    public boolean checkValid(int row, int column, int dr, int dc, int turn, int length,int[][]board) {
        //The block is already occupied by the piece
        if(board[row][column]!=0) {
            return false;
        }
        int startRow=row;
        int startColumn=column;
        //Check whether it reaches over the board and whether the block next to it is another player's piece
        while(startRow+dr>=0&&startRow+dr<length&&startColumn+dc>=0&&startColumn+dc<length&&board[startRow+dr][startColumn+dc]==3-turn) {
            startRow+=dr;
            startColumn+=dc;
        }
        // For the block that exits the above loop, check whether it is my piece
        if(startRow==row&&startColumn==column) {
            return false;
        }
        if(startRow+dr>=0&&startRow+dr<length&&startColumn+dc>=0&&startColumn+dc<length&&board[startRow+dr][startColumn+dc]==turn) {
            return true;
        }
        return false;
    }

    public boolean Valid(int a,int b, int turn,int length,int[][] board) {
        for(int i=0;i<8;i++) {
            //Check 8 blocks that near [a,b]
            if(checkValid(a, b, dr[i], dc[i], turn, length, board)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Point> getMoveList(int turn,int length,int[][]board){//Get all valid move blocks
        ArrayList<Point> movelist=new ArrayList<Point>();
        for(int i=0;i<length;i++) {
            for(int j=0;j<length;j++) {
                if(Valid(i, j, turn, length, board)) {
                    movelist.add(new Point(i, j));
                }
            }
        }
        return movelist;
    }

    public void Flip(int row, int column, int turn, int length, int[][]board) {
        for(int i=0;i<8;i++) {
            //Check all eight directions and flip all
            ReadyFlip(row, column, dr[i],dc[i],turn,length,board);
        }
        board[row][column]=turn;
    }

    public void ReadyFlip(int row, int column, int dr, int dc, int turn, int length,int[][] board) {
        if(!checkValid(row, column, dr, dc, turn, length, board)) {
            return;
        }
        //Flip all pieces that are trapped by my piece
        while(row+dr>=0&&row+dr<length&&column+dc>=0&&column+dc<length&&board[row+dr][column+dc]==3-turn) {
            board[row+dr][column+dc]=turn;
            row+=dr;
            column+=dc;
        }
    }

    public boolean end(int turn, int length,int[][]board) {//Check whether there is other available moves
        for(int i=0;i<length;i++) {
            for(int j=0;j<length;j++) {
                if(Valid(i, j, turn, length,board)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int num(int turn, int length, int[][]board) {//Calculate the number of pieces on the board
        int s=0;
        for(int i=0;i<length;i++) {
            for(int j=0;j<length;j++) {
                if(board[i][j]==turn) {
                    s++;
                }
            }
        }
        return s;
    }

    public void result(int length,int[][] board) { //Check who wins
        if(num(1,length, board)<num(2, length,board)) {
            System.out.println("White wins!");
        }else if(num(1,length,board)>num(2, length,board)) {
            System.out.println("Black wins!");
        }
        else {
            System.out.println("Draw");
        }
    }

    public void playBlack(int turn, int length, int option){ //Human Black turn
        Scanner sc = new Scanner(System.in);
        while(true){
            if(turn==1) { //Human is Dark piece
                System.out.println("Next to play: DARK");
                System.out.println("Your move: ");
                long startMillis = System.currentTimeMillis();
                String moveb=sc.next();
                long endMillis = System.currentTimeMillis();
                System.out.println("Elapsed time: " + (endMillis-startMillis)/1000 + " seconds.");
                System.out.println("x: "+moveb);
                char[] moveb1 = moveb.toCharArray();
                int column =(int)(moveb1[0])-97;
                int row =(int)(moveb1[1])-49;
                //Check whether the move is valid
                if(Valid(row, column, turn, length, board)) {
                    Flip(row, column, turn, length, board);
                    //Check whether the end of the game
                    if(end(turn,length,board) && end(3-turn, length, board)) {
                        printBoard(length, turn, board);
                        result(length,board);
                        break;
                    }else {
                        turn=2;
                        printBoard(length, turn, board);
                    }
                }
                else { //Not valid move
                    System.out.println("It's not a valid move.");
                    System.out.println("Please choose again!");
                    continue;
                }
                if(end(2, length, board)) {
                    System.out.println("No avaliable moves! Another player's turn. ");
                    turn=1;
                    printBoard(length, turn,board);
                }

            }
            if(turn == 2){//Computer is Light piece
                System.out.println("Next to play: LIGHT");
                long startMillis = System.currentTimeMillis();
                turn=2;
                int x=1;
                int y=1;
                ArrayList<Integer> arrayList=new ArrayList<Integer>();
                arrayList = minimax.minimaxDecision(board, turn, x, y, Integer.MIN_VALUE, Integer.MAX_VALUE);
                long endMillis = System.currentTimeMillis();
                System.out.println("Elapsed time: " + (endMillis-startMillis)/1000 + " seconds.");
                char alphabet=(char)(arrayList.get(1)+97);
                System.out.println("o: "+alphabet+(arrayList.get(0)+1));
                Flip(arrayList.get(0), arrayList.get(1), turn, length, board);

                if(end(turn,length,board)&&end(3-turn, length,board)) {
                    printBoard(length, turn,board);
                    result(length,board);
                    break;
                }else {
                    turn=1;
                    printBoard(length, turn,board);
                }
                if(end(1, length,board)) {
                    System.out.println("No avaliable moves! Another player's turn. ");
                    turn=2;
                    printBoard(length, turn,board);
                }
            }
        }
    }
    public void playWhite(int turn1, int length, int option){ //Human is Light piece
        int turn = 3-turn1;
        Scanner sc = new Scanner(System.in);
        while(true){
            if(turn==2) {
                System.out.println("Next to play: White");
                System.out.println("Your move: ");
                long startMillis = System.currentTimeMillis();
                String moveb=sc.next();
                long endMillis = System.currentTimeMillis();
                System.out.println("Elapsed time: " + (endMillis-startMillis)/1000 + " seconds.");
                System.out.println("x: "+moveb);
                char[] moveb1 = moveb.toCharArray();
                int column =(int)(moveb1[0])-97;
                int row =(int)(moveb1[1])-49;
                //Check whether the move is valid
                if(Valid(row, column, turn, length, board)) {
                    Flip(row, column, turn, length, board);
                    if(end(turn,length,board) && end(3-turn, length, board)) {
                        printBoard(length, turn, board);
                        result(length,board);
                        break;
                    }else {
                        turn=1;
                        printBoard(length, turn, board);
                    }
                }
                else {
                    System.out.println("It's not a valid move.");
                    System.out.println("Please choose again!");
                    continue;
                }
                if(end(1, length, board)) {
                    System.out.println("No avaliable moves! Another player's turn. ");
                    turn=2;
                    printBoard(length, turn, board);
                }
            }
            if(turn == 1){
                System.out.println("Next to play: Black");
                long startMillis = System.currentTimeMillis();
                turn=1;
                int x=1;
                int y=1;
                ArrayList<Integer> arrayList=new ArrayList<Integer>();
                arrayList = minimax.minimaxDecision(board, turn, x, y, Integer.MIN_VALUE, Integer.MAX_VALUE);
                long endMillis = System.currentTimeMillis();
                System.out.println("Elapsed time: " + (endMillis-startMillis)/1000 + " seconds.");
                char alphabet=(char)(arrayList.get(1)+97);
                System.out.println("o: "+alphabet+(arrayList.get(0)+1));
                Flip(arrayList.get(0), arrayList.get(1), turn, length, board);

                if(end(turn,length,board)&&end(3-turn, length, board)) {
                    printBoard(length, turn, board);
                    result(length, board);
                    break;
                }else {
                    turn=2;
                    printBoard(length, turn, board);
                }
                if(end(2, length, board)) {
                    System.out.println("No avaliable moves! Another player's turn. ");
                    turn=1;
                    printBoard(length, turn, board);
                }
            }
        }
    }

}
