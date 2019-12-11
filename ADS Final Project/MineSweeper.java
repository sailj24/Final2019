import java.util.*;
import java.util.Scanner;

public class MineSweeper {

    int counter; // counts down
    int timer;
    Grid grid;
    boolean firstMove;

    public MineSweeper() { // default constructor calls parameterized constructor
        this(5);
    }

    public MineSweeper(int bombCount) { // parameterized constructor
        counter = bombCount - 1;
        timer = 0;
        grid = new Grid(bombCount);
        firstMove = true;
    }

    public void play(Scanner scanner) {//how a human plays
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
        while (this.checkGameOver().equals("still alive")) {
            this.timer++;
            //you can use the commented code below to watch the AI's move after each click
            /*this.grid.toString();
            System.out.println("Next Computer Move?");
            String hold = scanner.nextLine();
            if (hold.equals(" ")) {
                System.out.println("ok");
            */
            this.decision();

        }
        if (this.checkGameOver().equals("died")) {
            this.died();
        } else if (this.checkGameOver().equals("won")) {
            this.won();
        }
        System.out.println(this.timer + " moves");
    }

    public void decision() {
        if (this.firstMove) {
            int[] firstClick = { this.grid.gridAxis / 3, this.grid.gridAxis / 2 };
            this.adjustGrid(firstClick, "REVEAL");
            System.out.println("done");
            return;
        }
        ArrayList<Square> known = new ArrayList<>();
        ArrayList<Square> unknown = new ArrayList<>();
        this.sortKnowns(known, unknown);
        boolean flag = false;
        int iterator = 0;
        while (!flag && iterator<known.size()) {
            Square s = known.get(iterator);
            if (s.nature == '*') {
                iterator++;
                continue;
            }
            if (this.corner(s, unknown)) {//case of if a square is connected to as many unrevealed squares as it is certain there are bombs around it
                int[] place = this.findCorner(s);
                this.adjustGrid(place, "FLAG");
                flag = true;
                
            }else if (this.safeMove(s, unknown)) {
                int[] place = this.findCorner(s);
                this.adjustGrid(place, "REVEAL");
                flag = true;
            }
            if (iterator<known.size()){
                iterator++;
            } else {
                System.out.println("I'm at a loss here, boss"); //this should never show ut it's just in case
                System.exit(0);
            }
        
        }
    
        if (flag)
            System.out.println("I got through that click!");//the AI decided on a safe move
        else {
            System.out.println("I'm at a loss here, boss. For Real, this time");//The AI could not decide on a safe move
            System.exit(0);
        }

    }
    public boolean safeMove(Square s, ArrayList<Square> unknown) {
        if (s.neighborBombs == 0)
            return false;
        int neighborBs = s.neighborBombs;
        int stillCovered = 0;
        for (Square square : s.connections) {
            if (square.revealed == 2) {
                neighborBs--;
            }
            if (square.revealed == 0) {
                stillCovered++;
            }
        }
        return neighborBs==0 && stillCovered>0;
    }

    public boolean corner(Square s, ArrayList<Square> unknown) {
        if (s.neighborBombs == 0)
            return false;
        int neighborBs = s.neighborBombs;
        int stillCovered = 0;
        for (Square square : s.connections) {
            if (square.revealed == 2) {
                neighborBs-=2;
            }
           // System.out.println("Trying to decide if I'm a corner");
            if (square.revealed == 0) {
                stillCovered++;
            }
        }
        return neighborBs == stillCovered;
    }

    public int[] findCorner(Square s) {
        int[] rv = new int[2];
        int iterator = 0;
        while (iterator < s.connections.size()) {
            Square neighbor = s.connections.get(iterator);
            if (neighbor.revealed == 0) {
                rv = this.findInGrid(neighbor);
                return rv;
            }
            iterator++;
        }
        return rv;
    }

    public void sortKnowns(ArrayList<Square> k, ArrayList<Square> uk) {// sorts all squares into known and unknown
                                                                       // (unnrevealed)
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                Square s = this.grid.grid[i][j];
                if (s.revealed == 0) {
                    uk.add(s);
                } else
                    k.add(s);
            }
        }
    }

    public void adjustGrid(int[] squarePlace, String act) {
        Square mySquare = this.grid.grid[squarePlace[0]][squarePlace[1]];
        if (this.firstMove) {
            if (mySquare.nature == '*')
                this.changeFirstMove(mySquare);
            else
                this.firstMove = false;
        }
        if (act.equals("REVEAL")) {
            mySquare.revealed = 1;
            ArrayList<Square> visited = new ArrayList<Square>();
            visited.add(mySquare);
            mySquare.changeAllBlankFriends(visited);
        } else if (act.equals("FLAG")) {
            if (mySquare.revealed == 2) {
                mySquare.revealed = 0;
                this.counter++;
            } else {
                mySquare.revealed = 2;
                this.counter--;
            }
        } else if (act.equals("?")) {
            mySquare.revealed = 3;
        }
    }

    public boolean actIsLegal(String act) {
        if ((act.equals("FLAG")) || (act.equals("REVEAL")) || (act.equals("?"))) {
             System.out.println("legal act");
            return true;
        } else {
            System.out.println("illegal act");
            return false;
        }
    }

    public boolean squareIsLegal(int[] sq) {
        if ((sq[0] <= this.grid.gridAxis && sq[0] >= 0) && (sq[1] <= this.grid.gridAxis && sq[1] >= 0)) {
             System.out.println("legal place");
            return true;
        } else {
            System.out.println("illegal place");
            return false;
        }
    }

    public int[] findInGrid(Square s) {
        int[] rv = new int[2];
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                if (this.grid.grid[i][j].ID == s.ID) {
                    rv[0] = i;
                    rv[1] = j;
                }
            }
        }
        return rv;
    }

    public void changeFirstMove(Square s) {
        s.nature = (char)s.neighborBombs;
        for (Square square : s.connections) {
            square.neighborBombs--;
        }
        this.firstMove = false;
    }

    public String checkGameOver() {
        if (this.timer > 100) {
            return "died";
        }
        String rv = "!!";
        boolean allClicked = true;
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                Square thisSquare = this.grid.grid[i][j];
                if (thisSquare.revealed == 1 && thisSquare.nature == '*') {
                    rv = "died";
                    allClicked = false;
                    break;
                } else if (thisSquare.revealed != 1 && thisSquare.nature != '*') {
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

}//That's all, folks
