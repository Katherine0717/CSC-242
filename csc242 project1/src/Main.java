import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Reversi by Xinyu Cai and Ningyuan Xiong");
        System.out.println("Choose your game:\n" +
                "1. Small 4x4 Reversi\n" +
                "2. Medium 6x6 Reversi\n" +
                "3. Standard 8x8 Reversi");
        System.out.println("Your choice?(1/2/3)");
        Scanner sc = new Scanner(System.in);
        int temp = sc.nextInt();
        int length;
        if(temp == 1){
            length = 4;
        }else if(temp == 2){
            length = 6;
        }else{
            length = 8;
        }

        System.out.println("Choose your opponent:\n" +
                "1. An agent that plays randomly\n" +
                "2. An agent that uses MINIMAX\n" +
                "3. An agent that uses MINIMAX with alpha-beta pruning\n" +
                "4. An agent that uses H-MINIMAX with a fixed depth cutoff and a-b pruning");
        System.out.println("Your choice?(1/2/3/4)");
        int option = sc.nextInt();

        System.out.println("Do you want to play DARK (x) or LIGHT (o)?");
        String turnstring = sc.next();
        int turn;
        if(turnstring.equals("x")) {
            turn =1;
        }
        else {
            turn = 2;
        }

        play(length, option, turn);
    }

    public static void play(int length, int option, int turn){
        Minimax minimax = new Minimax(length, option);
        Board xBoard = new Board(length, minimax);

        //Check whose turn and call the corresponding class
        if(turn == 1){
            xBoard.playBlack(turn, length, option);
        }else{
            xBoard.playWhite(turn, length, option);
        }
    }
}
