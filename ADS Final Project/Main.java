import java.util.Scanner;

class Main {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        MineSweeper game = new MineSweeper();
        //game.computerPlay(scanner);
        game.play(scanner);
    }
    
}