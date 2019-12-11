import java.util.Scanner;
//https://www.cs.rice.edu/~cork/book/node15.html Style Guide on capitalization I referenced
class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MineSweeper game = new MineSweeper();
        //game.computerPlay(scanner);//if you would like the computer to play it
        game.play(scanner); //If you would like to play it yourself
                                //AI only uses scanner if you would like to watch it work each step
    }

}