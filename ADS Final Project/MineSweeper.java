import java.util.*;
import java.util.Scanner;

public class MineSweeper {

    int COUNTER; // counts down
    int TIMER;
    Grid BOARD;
    boolean FIRST_MOVE;

    public MineSweeper() { // default constructor calls parameterized constructor
        this(7);//the one magic number--change this and the AI will likely have some problems. It's best at 7
    }

    public MineSweeper(int bombCount) { // parameterized constructor
        COUNTER = bombCount - 1;
        TIMER = 0;
        BOARD = new Grid(bombCount);
        FIRST_MOVE = true;
    }

    public void play(Scanner scanner) {// how a human plays
        while (this.checkGameOver().equals("still alive")) {
            this.BOARD.toString();
            System.out.println("Bombs hidden: " + this.COUNTER);
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
                this.adjustGrid(actionPlace, action); // adjustGrid int[], string -> void, adjusts BOARD
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
            this.TIMER++;
            // you can use the commented code below to watch the AI's move after each click
            /*
             * this.BOARD.toString(); System.out.println("Next Computer Move?"); String hold
             * = scanner.nextLine(); if (hold.equals(" ")) { System.out.println("ok");
             */
            this.decision();

        }
        if (this.checkGameOver().equals("died")) {
            this.died();
        } else if (this.checkGameOver().equals("won")) {
            this.won();
        }
        System.out.println(this.TIMER + " moves");
    }

    public void decision() {
        if (this.FIRST_MOVE) {
            int[] firstClick = { this.BOARD.GRID_AXIS / 3, this.BOARD.GRID_AXIS / 2 };
            this.adjustGrid(firstClick, "REVEAL");
            System.out.println("done");
            return;
        }
        ArrayList<Square> known = new ArrayList<>();
        ArrayList<Square> unknown = new ArrayList<>();
        this.sortKnowns(known, unknown);
        boolean flag = false;
        int iterator = 0;
        while (!flag && iterator < known.size()) {//keeps looping until arrives at a good move
            Square s = known.get(iterator);//loops through all known squares(the blanks and numbers on the board)
            if (s.NATURE == '*') {//this is flagged(known and '*')already
                iterator++;
                continue;
            }
            if (this.corner(s, unknown)) {// if a square is connected to as many unrevealed squares as it is
                                          // certain there are bombs around it
                int[] place = this.findCorner(s);
                this.adjustGrid(place, "FLAG");
                flag = true;//yes, I know where to move
            } else if (this.safeMove(s, unknown)) {// if a square is connected to enough revealed bombs
                                                        //that the rest must not be bombs
                int[] place = this.findCorner(s);
                this.adjustGrid(place, "REVEAL");
                flag = true;//yes, I know where to move
            }
            if (iterator < known.size()) {
                iterator++;
            } else {
                System.out.println("I'm at a loss here, boss"); // this should never show but it's just in case
                System.exit(0);
            }
        }
        if (flag)
            System.out.println("I got through that click!");// the AI decided on a safe move
        else {
            System.out.println("I'm at a loss here, boss. For Real, this time");// The AI could not decide on a safe
                                                                                // move
            System.exit(0);
        }

    }

    public boolean safeMove(Square s, ArrayList<Square> unknown) {
        if (s.NEIGHBOR_BOMBS == 0)
            return false;
        int neighborBs = s.NEIGHBOR_BOMBS;
        int stillCovered = 0;
        for (Square square : s.CONNECTIONS) {
            if (square.REVEALED == 2) {
                neighborBs--;
            }
            if (square.REVEALED == 0) {
                stillCovered++;
            }
        }
        return neighborBs == 0 && stillCovered > 0;
    }

    public boolean corner(Square s, ArrayList<Square> unknown) {
        if (s.NEIGHBOR_BOMBS == 0)
            return false;
        int neighborBs = s.NEIGHBOR_BOMBS;
        int stillCovered = 0;
        for (Square square : s.CONNECTIONS) {
            if (square.REVEALED == 2) {
                neighborBs -= 2;
            }
            if (square.REVEALED == 0) {
                stillCovered++;
            }
        }
        return neighborBs == stillCovered;
    }

    public int[] findCorner(Square s) {
        int[] rv = new int[2];
        int iterator = 0;
        while (iterator < s.CONNECTIONS.size()) {
            Square neighbor = s.CONNECTIONS.get(iterator);
            if (neighbor.REVEALED == 0) {
                rv = this.findInGrid(neighbor);
                return rv;
            }
            iterator++;
        }
        return rv;
    }

    public void sortKnowns(ArrayList<Square> k, ArrayList<Square> uk) {// sorts all squares into 2 lists
        for (int i = 0; i <= this.BOARD.GRID_AXIS - 1; i++) {
            for (int j = 0; j <= this.BOARD.GRID_AXIS - 1; j++) {
                Square s = this.BOARD.GRID[i][j];
                if (s.REVEALED == 0) {
                    uk.add(s);//add unrevealed to unknown list
                } else
                    k.add(s);//add revealed to known list
            }
        }
    }

    public void adjustGrid(int[] squarePlace, String act) {//makes sure the game changes with the move decision
        Square mySquare = this.BOARD.GRID[squarePlace[0]][squarePlace[1]];
        if (this.FIRST_MOVE) {//renders the first move innocuous (Part II)
            if (mySquare.NATURE == '*')
                this.changeFirstMove(mySquare);
            else
                this.FIRST_MOVE = false;
        }
        if (act.equals("REVEAL")) {
            mySquare.REVEALED = 1;
            ArrayList<Square> visited = new ArrayList<Square>();
            visited.add(mySquare);
            mySquare.changeAllBlankFriends(visited);
        } else if (act.equals("FLAG")) {
            if (mySquare.REVEALED == 2) {//allows you to unflag things if you need to
                mySquare.REVEALED = 0;
                this.COUNTER++;
            } else {
                mySquare.REVEALED = 2;
                this.COUNTER--;
            }
        } else if (act.equals("?")) {
            mySquare.REVEALED = 3;
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
        if ((sq[0] <= this.BOARD.GRID_AXIS && sq[0] >= 0) && (sq[1] <= this.BOARD.GRID_AXIS && sq[1] >= 0)) {
            System.out.println("legal place");
            return true;
        } else {
            System.out.println("illegal place");
            return false;
        }
    }

    public int[] findInGrid(Square s) {//given a square ID, finds the x and y of it
        int[] rv = new int[2];
        for (int i = 0; i <= this.BOARD.GRID_AXIS - 1; i++) {
            for (int j = 0; j <= this.BOARD.GRID_AXIS - 1; j++) {
                if (this.BOARD.GRID[i][j].ID == s.ID) {
                    rv[0] = i;
                    rv[1] = j;
                }
            }
        }
        return rv;
    }

    public void changeFirstMove(Square s) {//if click on bomb your first move, it's rendered innocuous
        s.NATURE = (char) s.NEIGHBOR_BOMBS;
        for (Square square : s.CONNECTIONS) {
            square.NEIGHBOR_BOMBS--;
        }
        this.FIRST_MOVE = false;
    }

    public String checkGameOver() {//win if all non-bombs are clicked, lose if a bomb is clicked. Else stay alive forever...
        if (this.TIMER > 100) {//in case the AI is stubborn and struggling(unlikely)
            return "died";
        }
        String rv = "!!";
        boolean allClicked = true;
        for (int i = 0; i <= this.BOARD.GRID_AXIS - 1; i++) {
            for (int j = 0; j <= this.BOARD.GRID_AXIS - 1; j++) {
                Square thisSquare = this.BOARD.GRID[i][j];
                if (thisSquare.REVEALED == 1 && thisSquare.NATURE == '*') {
                    rv = "died";
                    allClicked = false;
                    break;
                } else if (thisSquare.REVEALED != 1 && thisSquare.NATURE != '*') {
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
        this.BOARD.toString();
        System.out.println("U DYED. SORRY NOT SORRY");
    }

    public void won() {
        this.BOARD.toString();
        System.out.println("U WON. BIEN OUEJ");
        System.out.println("THAT MEANS WELL PLAYED");
    }

}// That's all, folks
