import java.util.ArrayList;
import java.lang.*;

public class Minimax {
    private int length;
    private int option;
    private int maxSearch = 10;
    private Board boardTemp;

    private int[][] BOARD_VALUE = {
            {20, -3, 11, 8, 8, 11, -3, 20},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {8, 1, 2, -3, -3, 2, 1, 8},
            {11, -4, 2, 2, 2, 2, -4, 11},
            {-3, -7, -4, 1, 1, -4, -7, -3},
            {20, -3, 11, 8, 8, 11, -3, 20}
    };

    public Minimax(int length, int option) {
        this.length = length;
        this.option = option;
        boardTemp = new Board(length);
    }

    public int heuristic(int[][] board, int turn, int boardVal) {  //Heuristic function
        int opponent=1;
        if(turn==1) {
            opponent=2;
        }
        int myscore = boardTemp.num(turn, length, board);
        int opposcore = boardTemp.num(opponent, length, board);
        int returnVal = myscore - opposcore + boardVal;
        return returnVal;
    }

    public int utility(int[][] board, int turn){ // Utility function for minimax
        int opponent=1;
        if(turn==1) {
            opponent = 2;
        }
        int myscore = boardTemp.num(turn, length, board);
        int opposcore = boardTemp.num(opponent, length, board);
        int result = 0;
        if(myscore - opposcore < 0){
            result = -1;
        }else if(myscore - opposcore > 0){
            result = 1;
        }
        return result;
    }

    public int minimaxValue(int[][] board, int origiTurn, int currTurn, int search, int alpha, int beta, int boardVal) {
        //  Option 1. An agent that plays randomly
        //  Option 2. An agent that uses MINIMAX
        //  Option 3. An agent that uses MINIMAX with alpha-beta pruning
        //  Option 4. An agent that uses H-MINIMAX with a fixed depth cutoff and a-b pruning
        if(option==4) {
            if(boardTemp.end(origiTurn,length,board) || search == maxSearch){ //Option 4: H-Minimax with a fixed depth cutoff at depth = 10
                return heuristic(board, origiTurn, boardVal);
            }

        }else {
            if(boardTemp.end(origiTurn,length,board)) { // Option 2 and 3: Minimax: comparing utility value
                //System.out.println("...................");
                return utility(board, origiTurn);
            }
        }

        int opponent=1;
        if(currTurn==1) {
            opponent=2;
        }

        ArrayList<Point> AvailableMove = boardTemp.getMoveList(currTurn, length, board);
        if(AvailableMove.size()==0) {
            return minimaxValue(board, origiTurn, opponent, search+1, alpha, beta, boardVal);
        }
        else {
            int utility = -2; //give a initial value
            int BestValue=-99999;
            if(origiTurn!=currTurn) {//Min level
                BestValue=99999;
            }

            for(int i=0;i<AvailableMove.size();i++) {//Traverse all possible moves
                int[][] newBoard = boardTemp.copyBoard(board);
                boardTemp.Flip(AvailableMove.get(i).x,AvailableMove.get(i).y, currTurn, length, newBoard); // Flip the move on the clone board
                int boardVal2 = BOARD_VALUE[AvailableMove.get(i).x][AvailableMove.get(i).y]; // Get the value for heuristic value

                int value = minimaxValue(newBoard, origiTurn, opponent, search+1, alpha, beta, boardVal2); // recursive call

                if(option == 2){ //  Option 2. An agent that uses MINIMAX with no alpha beta pruning
                    if(utility == -2){
                        utility = value;
                    }else {
                        if (origiTurn == currTurn) {
                            if (value > utility) {
                                utility = value;
                            }
                        } else {
                            if (value < utility) {
                                utility = value;
                            }
                        }
                    }
                }else{ // Option 3 and 4: minimax and h-minimax with a-b prunning
                    if(origiTurn==currTurn) {
                        if(value>BestValue) {
                            BestValue=value;
                        }
                        alpha=Math.max(alpha, value); //update alpha value
                        if(beta<=alpha) { //pruning
                            break;
                        }
                    }else {
                        if(value<BestValue) {
                            BestValue=value;
                        }
                        beta=Math.min(value, beta);//update beta value
                        if(beta<=alpha) { //pruning
                            break;
                        }
                    }
                }
            }
            if(option == 2){
                return utility;
            }else{
                return BestValue;
            }
        }
    }

    public ArrayList<Integer> minimaxDecision(int[][]board, int turn, int x, int y, int alpha, int beta) {
        ArrayList <Integer> array=new ArrayList<Integer>();
        boardTemp = new Board(length);

        int opponent=1;
        if(turn==1) {
            opponent=2;
        }

        ArrayList<Point> AvailableMove = boardTemp.getMoveList(turn, length, board); //Get all possible moves
        int BestX=AvailableMove.get(0).x;
        int BestY=AvailableMove.get(0).y;

        if(option == 1){ //  Option 1. An agent that plays randomly
            array = random(AvailableMove);
        }else {
            int BestValue = -99999;
            for (int i = 0; i < AvailableMove.size(); i++) { //Traverse all possible moves
                int[][] newBoard = boardTemp.copyBoard(board);
                boardTemp.Flip(AvailableMove.get(i).x, AvailableMove.get(i).y, turn, length, newBoard); //Flip the move on clone board
                int boardVal = BOARD_VALUE[AvailableMove.get(i).x][AvailableMove.get(i).y]; // Get the value for heuristic array
                int value = minimaxValue(newBoard, turn, opponent, 1, alpha, beta, boardVal); // call minimaxValue = Recursion start
                alpha = Math.max(value, alpha);
                if (value > BestValue) {//always the max level
                    BestValue = value;//Find the max value
                    BestX = AvailableMove.get(i).x;
                    BestY = AvailableMove.get(i).y;
                }
            }
            array.add(BestX);
            array.add(BestY);
        }
        return array;
    }

    public ArrayList<Integer> random(ArrayList<Point> AvailableMove){ // Option 1 Random move
        ArrayList <Integer> array=new ArrayList<Integer>();
        int max = AvailableMove.size();
        int val = (int) (Math.random() * max);
        array.add(AvailableMove.get(val).x);
        array.add(AvailableMove.get(val).y);
        return array;
    }
}


