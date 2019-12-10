import java.util.*;
import java.util.Scanner;
import java.util.Random;

public class MineSweeper {

    int counter; // counts down
    int timer;
    Grid grid;
    boolean firstMove;

    public MineSweeper() { // default constructor calls parameterized constructor
        this(13);
    }

    public MineSweeper(int bombCount) { // parameterized constructor
        counter = bombCount - 1;
        timer = 0;
        grid = new Grid(bombCount);
        firstMove = true;
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
        while (this.checkGameOver().equals("still alive")) {
            this.timer++;
           /* this.grid.toString();
            System.out.println("Next Computer Move?");
            String hold = scanner.nextLine();
            if (hold.equals(" ")) {
                System.out.println("ok");
              */  this.decision();
            
        }
        if (this.checkGameOver().equals("died")) {
            this.died();
        } else if (this.checkGameOver().equals("won")) {
            this.won();
        }
        System.out.println(this.timer + " moves");
    }

    public void decision() {
        RetVal move;
        if (this.firstMove) {
            move = new RetVal((this.grid.gridAxis / 2), (this.grid.gridAxis / 2 + 1), "reveal");
            System.out.println("FirstMove " + this.firstMove);
        } else move = getMove();
        System.out.println(move.a + move.x + move.y);
        int[] actionPlace = { move.x, move.y };
        String action = move.a.toUpperCase();
        this.adjustGrid(actionPlace, action); // adjustGrid int[], string -> void, adjusts grid

        System.out.println("done");
    }

    public RetVal getMove() { //////////////////////////////////////////////////////////////////////////////////////
        // ...decision-making...
        ArrayList<Square> known = new ArrayList<Square>();
        ArrayList<Square> unknown = new ArrayList<Square>();
        this.sortKnowns(known, unknown);
        //System.out.println("sorted");
        if (unknown.size()==0){
            this.died();
            System.exit(0);
        }
        for (Square s : known) {
            if (s.revealed == 2) {// if flagged
                this.foundOne(s);
            }
            if (s.neighborBombs > 0) {
                if (s.neighborBombs == this.bombsFound(s)) { // if all its neighborbombs are counted
                    this.powerOfBomb(s);
                }
                this.highChance(s);
            }
            
        }
      /*  System.out.println("These are the known:");
        for (Square s: known){
            System.out.println(s.nature + " " + s.chance);
        }
        System.out.println("These are the unknown:");
        for (Square s: unknown){
            System.out.println(s.nature + " " + s.chance);
        }*/
        // for (Square s: unknown){
        // System.out.println(s.nature);
        // }
        boolean readyToClick = false;
        boolean readyToFlag = false;
        int id = 0;
        int figuredOut = 0;
        for (Square s : unknown) {
           // System.out.println(s.chance);
            if (s.chance >= s.connections.size()) {
                if ((this.CoveredCount(s)<=s.neighborBombs) && (this.bombsFound(s)<=s.neighborBombs)){
                readyToFlag = true;
                id = s.ID;
                break;
                }
               // System.out.println("Got a bomb to flag");
            } else if (s.chance == 0) {
                readyToClick = true;
                id = s.ID;
               // System.out.println("Got a space to click");
                break;
            } else if (s.chance > 8) {
                figuredOut++; // there are a enough big numbers in the list of unknown squares to imagine
                              // you've figured out where all the bombs are
            }
        }
        // figured out by now if we should be looking for something to click or flag
        this.resetAll();
        if (readyToClick) {
            int[] coord = findInGrid(id);
            RetVal rv = new RetVal(coord[0], coord[1], "reveal");
            return rv;
        } else if (readyToFlag) {
            int[] coord = findInGrid(id);
            RetVal rv = new RetVal(coord[0], coord[1], "flag");
            return rv;
        } else if (figuredOut == this.counter) {
            int ident = this.clickTheRest(unknown);
            int[] coord = findInGrid(ident);
            RetVal rv = new RetVal(coord[0], coord[1], "reveal");
            return rv;
        } else {
            int randid = this.randomlyChoose(unknown);
            if (this.tooCloseToCivilization(new RetVal(this.findInGrid(randid)[0],this.findInGrid(randid)[1], "reveal"))){
                randid = this.randomlyChoose(unknown);
            }
            int[] coord = findInGrid(randid);
            RetVal rv = new RetVal(coord[0], coord[1], "reveal");
            System.out.println("At a loss here, boss");
            return rv;
        }

    } ////// t/////////////////////////////////////////////////////////////////////////////////////////////////

    public int bombsFound(Square s) {
        int count = 0;
        for (Square neighbor : s.connections) {
            if (neighbor.revealed == 2) count++;
        }
        return count;
    }
    public int CoveredCount (Square s) {
        int count = 0;
        for (Square next: s.connections){
            if (next.revealed==0) count++;
        }
        return count;
    }

    public void powerOfBomb(Square center) {
        for (Square next : center.connections) {
            if (next.revealed == 2) {
                next.chance = next.chance -center.neighborBombs;
                //System.out.println("I'm a badass");
            }
        }
    }

    public int clickTheRest(ArrayList<Square> list) {
        int rv = 0;
        for (Square s : list) {
            if (s.chance < 8) {
                rv = s.ID;
            }
        }
        return rv;
    }

    public void foundOne(Square center) {
        for (Square next : center.connections) {
            next.chance--;
        }
    }

    public void highChance(Square center) {
        for (Square next : center.connections) {
            next.chance += center.neighborBombs;
        }
    }

    public int[] findInGrid(int s) {
        int[] rv = new int[2];
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                if (this.grid.grid[i][j].ID == s) {
                    rv[0] = i;
                    rv[1] = j;
                }
            }
        }
        return rv;
    }

    public int randomlyChoose(ArrayList<Square> unknown) {
            int rv = unknown.get(this.timer % unknown.size()).ID;
            return rv;
       
    }

    public void resetAll() {
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                this.grid.grid[i][j].chance = 0;
            }
        }
    }

    public void sortKnowns(ArrayList<Square> known, ArrayList<Square> unknown) {
        for (int i = 0; i <= this.grid.gridAxis - 1; i++) {
            for (int j = 0; j <= this.grid.gridAxis - 1; j++) {
                if (this.grid.grid[i][j].revealed == 0) {
                    unknown.add(this.grid.grid[i][j]);
                } else
                    known.add(this.grid.grid[i][j]);
            }
        }
    }

    public boolean tooCloseToCivilization(RetVal choice) {
        boolean rv = false;
        Square s = this.grid.grid[choice.x][choice.y];
        for (int i = 0; i < s.connections.size(); i++) {
            if (s.connections.get(i).revealed == 0)
                continue;
            else
                rv = true;
        }
        return rv;
    }

    class RetVal {
        int x;
        int y;
        String a;

        public RetVal(int i, int j, String act) {
            x = i;
            y = j;
            a = act;
        }
    }

    public void adjustGrid(int[] squarePlace, String act) {
        Square mySquare = this.grid.grid[squarePlace[0]][squarePlace[1]];
        if (this.firstMove) {
            mySquare.nature = (char) mySquare.neighborBombs;
            this.firstMove = !this.firstMove;
        }
        if (act.equals("REVEAL")) {
            mySquare.revealed = 1;
            ArrayList<Square> visited = new ArrayList<Square>();
            visited.add(mySquare);
            mySquare.changeAllBlankFriends(visited);
            System.out.println("changed all blanks");
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
            // System.out.println("legal act");
            return true;
        } else {
            System.out.println("illegal act");
            return false;
        }
    }

    public boolean squareIsLegal(int[] sq) {
        if ((sq[0] <= this.grid.gridAxis && sq[0] >= 0) && (sq[1] <= this.grid.gridAxis && sq[1] >= 0)) {
            // System.out.println("legal place");
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
                } else if (thisSquare.revealed == 2 && thisSquare.nature == ' ') {
                    allClicked = false;
                    rv = "still alive";
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
