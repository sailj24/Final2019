import java.util.ArrayList;
import java.util.Random;

public class Grid {
    int BOMB_NUM;
    int GRID_AXIS;
    int GRID_SIZE;
    Square[][] GRID;

    public Grid() {
        this(1);
    }// closes default constructor

    public Grid(int num) {
        this.BOMB_NUM = num;
        this.GRID_AXIS = this.BOMB_NUM + 2;
        this.GRID_SIZE = GRID_AXIS * GRID_AXIS;
        this.GRID = new Square[GRID_AXIS][GRID_AXIS];
        makeGrid();
    }// closes noisy constructor

    public void makeGrid() {
        this.makeRandomList(); // makes the bomb objects and puts them in the GRID
        for (int i = 0; i <= this.GRID_AXIS - 1; i++) {
            for (int j = 0; j <= this.GRID_AXIS - 1; j++) { // goes through every item in the GRID
                if (this.GRID[i][j] == null) {
                    int place = this.listLocation(i, j);
                    this.GRID[i][j] = new Square(' ', place);// makes a square object and puts it in the GRID
                }
            }
        }
        this.calculateConnections();
    }// closes makeGrid

    public void makeRandomList() { // because seriously, how hard is it to make a
                                   // truly random bomb placement????
        Random rand = new Random();
        ArrayList<Integer> randNums = new ArrayList<>(); // the this.GRID of all the places bombs will be; it is as long
                                                         // as
                                                         // the num of bombs we want
        boolean flag = (randNums.size() == this.BOMB_NUM);
        while (!flag) {// gonna signal when we've found 5 non-repeating randoms
            int aNum = rand.nextInt(this.GRID_SIZE);
            int aNumx = gridLocation(aNum)[0];
            int aNumy = gridLocation(aNum)[1];
            if (this.GRID[aNumx][aNumy] == null) {
                this.GRID[aNumx][aNumy] = new Square('*', aNum);
                randNums.add(aNum);
                flag = randNums.size() == this.BOMB_NUM;
            } else {
                continue;
            }
        }
    }// closes makeRandomList

    public void calculateConnections() { // allows the squares to know each other if touching
        for (int i = 0; i <= this.GRID_AXIS - 1; i++) {
            for (int j = 0; j <= this.GRID_AXIS - 1; j++) {// for every real square
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {// for every potential neighbor
                        if (dx == 0 && dy == 0) {
                            continue;
                        }
                        int targetx = i + dx;
                        int targety = j + dy;
                        if (targetx >= 0 && targetx <= GRID_AXIS - 1 && targety >= 0 && targety <= GRID_AXIS - 1) {
                            this.GRID[i][j].CONNECTIONS.add(this.GRID[targetx][targety]); // consider it a neighbor
                            if (this.GRID[targetx][targety].NATURE == '*') {
                                this.GRID[i][j].NEIGHBOR_BOMBS++;
                            }
                        }
                    }
                }
            }
        }
    }

    public String toString() { // the image of the board
        String rv = " ";
        for (int i = 0; i <= this.GRID_AXIS - 1; i++) {
            if (i == 0) {
                for (int s = 0; s <= this.GRID_AXIS; s++) {
                    if (s == 0) {
                        rv += "   ";
                    } else
                        rv += " " + (s - 1) + " ";
                }
            }
            rv += "\n  " + " " + " ";
            for (int n = 0; n <= this.GRID_AXIS - 1; n++) {
                rv += "---";
            }
            rv += "--\n";
            rv += "| " + i + " |";
            for (int j = 0; j <= this.GRID_AXIS - 1; j++) {
                rv = this.toStringWithMoves(rv, i, j);
            }
        }
        System.out.println(rv);
        return rv;
    }

    public String toStringWithMoves(String rv, int i, int j) { // the image of the board with bombs covered
        if (this.GRID[i][j].REVEALED == 0) {
            rv += " #|";
        } else if (this.GRID[i][j].REVEALED == 1) {
            rv += this.GRID[i][j].NATURE + " |";
        } else if (this.GRID[i][j].REVEALED == 2) {
            rv += "!!|";
        } else if (this.GRID[i][j].REVEALED == 3) {
            rv += "??|";
        }
        return rv;
    }

    // takes 2, outputs 1
    public int listLocation(int x, int y) {
        return ((x * GRID_AXIS) + y);
    }

    // takes 1, outputs 2
    public int[] gridLocation(int k) {
        int[] coord = new int[2];
        coord[0] = k / GRID_AXIS; // x
        coord[1] = k % GRID_AXIS; // y
        return coord;
    }

}// The Last Bracket. Hey, that should be a movie title.