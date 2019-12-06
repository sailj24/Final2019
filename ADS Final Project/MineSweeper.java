import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class MineSweeper {

    int counter; // counts down
    int timer; // counts up
    Grid grid;
    boolean firstMove;

    public MineSweeper() { // default constructor calls parameterized constructor
        this(5);
    }

    public MineSweeper(int bombCount) { // parameterized constructor
        counter = bombCount-1;
        timer = 0;
        grid = new Grid(bombCount);
        firstMove=true;
    }

    public void play(Scanner scanner) {
        while (this.checkGameOver().equals("still alive")) {
            this.grid.toString();
            System.out.println("Bombs hidden: " + this.counter);
            int[] actionPlace = new int[2];
            String action = "";
            System.out.println("Input where you want to click(x coordinate): ");
            actionPlace[1] = scanner.nextInt();
            System.out.println("Input where you want to click(y coordinate): ");
            actionPlace[0] = scanner.nextInt();
            System.out.println("Input what to do: ");
            action = scanner.nextLine();
            action = scanner.nextLine().toUpperCase();

            if (this.actIsLegal(action) && this.squareIsLegal(actionPlace)) {
               // System.out.println("Legal move");
                this.adjustGrid(actionPlace, action); // adjustGrid int[], string -> void, adjusts grid
            } else
                continue;
        }
        if (this.checkGameOver().equals("died")) {
            this.died();
        } else if (this.checkGameOver().equals("won")) {
            this.won();
        }
    }
    
    public void computerPlay(Scanner scanner) {
        ArrayList<Square> flags = new ArrayList<Square>();
        while (this.checkGameOver().equals("still alive")) {
            this.grid.toString();
            System.out.println("Next Computer Move?");
            String hold = scanner.nextLine();
            if (hold.equals(" ")){
                System.out.println("ok");
                this.decision(flags);
            }
        }
        if (this.checkGameOver().equals("died")) {
            this.died();
        } else if (this.checkGameOver().equals("won")) {
            this.won();
        }
    }
    public void decision(ArrayList<Square> flagList){
        RetVal move = getMove(flagList);
        System.out.println(move.a + move.x + move.y);
        int[] actionPlace = {move.x,move.y};
        String action = move.a.toUpperCase();
        this.adjustGrid(actionPlace, action); // adjustGrid int[], string -> void, adjusts grid
        System.out.println("done");
    }
    public RetVal getMove(ArrayList<Square> fList){
        //...decision-making...
        if (this.firstMove){
            return new RetVal((this.grid.gridAxis/2),(this.grid.gridAxis/2+1), "reveal");
        }
        for (int i = 0; i<=this.grid.gridAxis-1; i++){
            for (int j = 0; j<=this.grid.gridAxis-1; j++){//for every real square
                Square s = this.grid.grid[i][j];
                if (s.revealed==1){
                    this.highChance(s);
            }}}
            int highest = 0;
            int[] place = new int[2];
            for (int i = 0; i<=this.grid.gridAxis-1; i++){
                for (int j = 0; j<=this.grid.gridAxis-1; j++){
                    if (this.grid.grid[i][j].chance>highest)
                    highest = this.grid.grid[i][j].chance;
                    place[0] = i;
                    place[1] = j;
                
                }}    
            if (highest>0){
                System.out.println(highest);
            }
        /*Random random = new Random();
        int x= random.nextInt(this.grid.gridAxis);
        int y= random.nextInt(this.grid.gridAxis);
        int b= random.nextInt(3);
        String a="REVEAL";
        */
        return new RetVal(place[0], place[1], "flag");
    }
    class RetVal{
        int x;
        int y;
        String a;

        public RetVal(int i, int j, String act){
            x=i;
            y=j;
            a=act;
        }
    }

    
    public void highChance(Square center){
        for(Square next: center.connections){
            next.chance += Character.getNumericValue(center.nature);
        }
    }

    public void adjustGrid(int[] squarePlace, String act) {
        Square mySquare = this.grid.grid[squarePlace[0]][squarePlace[1]];
        if (this.firstMove){
            mySquare.nature = ' ';
            this.firstMove = !this.firstMove;
        }
        if (act.equals("REVEAL")) {
            mySquare.revealed = 1;
            ArrayList<Square> visited = new ArrayList<Square>();
            visited.add(mySquare);
            mySquare.changeAllBlankFriends(visited);
            System.out.println("changed all blanks");
        } else if (act.equals("FLAG")) {
            mySquare.revealed = 2;
            this.counter--;
        } else if (act.equals("?")) {
            mySquare.revealed = 3;
        }
    }

    public boolean actIsLegal(String act) {
        if ((act.equals("FLAG")) || (act.equals("REVEAL")) || (act.equals("?"))) {
           // System.out.println("legal act");
            return true;
        } else {
            System.out.println("illegal act");
            return false;
        }
    }

    public boolean squareIsLegal(int[] sq) {
        if ((sq[0] <= this.grid.gridAxis && sq[0] >= 0) && (sq[1] <= this.grid.gridAxis && sq[1] >= 0)) {
            //System.out.println("legal place");
            return true;
        } else {
            System.out.println("illegal place");
            return false;
        }
    }

    public String checkGameOver() {
        /*
         * // 0 is hasn't yet been tampered w/, //1 is clicked //2 is flagged //3 is ?ed
         */
        String rv = "!!";
        boolean allClicked = true;
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                Square thisSquare = this.grid.grid[i][j];
                // System.out.printf("nature="+thisSquare.nature+" revealed = %d",
                // thisSquare.revealed);
                if (thisSquare.revealed == 1 && thisSquare.nature == '*') {
                    rv = "died";
                    allClicked = false;
                    break;
                } else if (thisSquare.revealed == 0 || thisSquare.revealed == 3) {
                    allClicked = false;
                    rv = "still alive";
                }
            }
            if (rv.equals("died")) {
                break;
            }
        }
        if (allClicked) {
            rv = "won";
        }
        // System.out.println(rv + " --I think I just looped through the while");
        return rv;
    }

    public void died() {
        this.grid.toString();
        System.out.println("U DYED. SORRY NOT SORRY");
    }

    public void won() {
        this.grid.toString();
        System.out.println("U WON. BIEN OUEJ");
        System.out.println("THAT MEANS WELL PLAYED");
    }

}
