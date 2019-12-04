import java.util.ArrayList;
import java.util.Random;

public class Grid {
    int bombNum;
    int gridAxis;
    int gridSize;
    Square[][] grid;

    public Grid() {
        this(1);
    }// closes default constructor
    
    /*saturdayGrid.calculateConnections();
    public static void main(String[] args) {
        Grid saturdayGrid = new Grid(5);
        //System.out.println(saturdayGrid.toString());
        // System.out.println(this.grid.gridAxis);
        ArrayList<Square> visited = new ArrayList<Square>();
        for (int i=0; i<=saturdayGrid.gridAxis-1; i++){
            for (int j=0; j<=saturdayGrid.gridAxis-1; j++){
                visited.add(saturdayGrid.grid[i][j]);
                System.out.println("Before " + saturdayGrid.grid[i][j].nature);
                System.out.println("neighborbombs " + saturdayGrid.grid[i][j].neighborBombs);
                saturdayGrid.grid[i][j].changeAllBlankFriends(visited);
                System.out.println("After " + saturdayGrid.grid[i][j].nature);
            }

        }
        System.out.println("I'm done");
    }
    */

    public Grid(int num) {
        this.bombNum = num;
        this.gridAxis = 2 * this.bombNum;
        this.gridSize = gridAxis * gridAxis;
        this.grid = new Square[gridAxis][gridAxis];
        makeGrid();
    }// closes constructor

    public void makeGrid(){
                ArrayList<Integer> randomList = this.makeRandomList (); //makes the bomb objects and puts them in the grid
        //System.out.println("Making the grid, man");
                for (int i = 0; i<=this.gridAxis-1; i++){
                    for (int j = 0; j<=this.gridAxis-1; j++){ //goes through every item in the grid
                                if (this.grid[i][j] == null){
                                    this.grid[i][j] = new Square(' ');//makes a square object and puts it in the grid
                        }
                    }
                }
                this.calculateConnections();
            }// closes makeGrid

    public ArrayList<Integer> makeRandomList() { // because seriously, how hard is it to make a
                                                                          // truly random bomb placement????
        //System.out.println("Placing the bombs, man");
        Random rand = new Random();
        ArrayList<Integer> randNums = new ArrayList<>(); // the this.grid of all the places bombs will be; it is as long as
                                                         // the num of bombs we want
        boolean flag = (randNums.size() == this.bombNum);
        while (!flag) {// gonna signal when we've found 5 non-repeating randoms
            int aNum = rand.nextInt(this.gridSize);
            int aNumx = gridLocation(aNum)[0];
            int aNumy = gridLocation(aNum)[1];
            if (this.grid[aNumx][aNumy] == null) {
                this.grid[aNumx][aNumy] = new Square('*');
                //System.out.println("Hi, I'm a bomb");
                randNums.add(aNum);
                flag = randNums.size() == this.bombNum;
            } else {
                continue;
            }
        }
        return randNums;
    }// closes makeRandomList

    public void calculateConnections(){ //allows the squares to know each other if touching
        for (int i = 0; i<=this.gridAxis-1; i++){
            for (int j = 0; j<=this.gridAxis-1; j++){//for every real square
                for (int dy = -1; dy<=1; dy++){
                    //System.out.println("I'm finding friends!");
                    for (int dx = -1; dx<=1; dx++){//for every potential neighbor
                        if (dx==0 && dy==0){
                            continue;
                        }
                        int targetx = i + dx;
                        int targety = j + dy;
                        if (targetx>=0 && targetx<=gridAxis-1 && targety>=0 && targety<=gridAxis-1){
                            this.grid[i][j].connections.add(this.grid[targetx][targety]); //consider it a neighbor
                            //System.out.println("Neighbor");
                            if (this.grid[targetx][targety].nature == '*'){
                            this.grid[i][j].neighborBombs++;
                            //System.out.println("neighborBombs++" + this.grid[i][j].neighborBombs);
                            }
                        }
                    }
                }
            }
        }
    }

    

    public String toString(){ //the image of the board
       // System.out.println("Trying to toString");
        String rv = " ";
        for (int i = 0; i<=this.gridAxis-1; i++){
            if(i==0){
                for (int s = 0; s<=this.gridAxis; s++){
                    if(s==0){
                        rv+="   ";
                    } else rv+=" " + (s-1) + " ";
                }   
            }
            rv+="\n  " + " " + " ";
            for (int n = 0; n<=this.gridAxis-1; n++){
                rv+="---";
            } 
            rv+="--\n";
            rv+="| " + i + " |";
            for (int j = 0; j<=this.gridAxis-1; j++){
                //rv+= this.grid[i][j].nature + " |";
                rv = this.toStringWithMoves(rv, i, j);
            }
        }
        System.out.println(rv);
        return rv;
    }
    public String toStringWithMoves(String rv, int i, int j){ //the image of the board with bombs covered
                 if(this.grid[i][j].revealed==0){
                    rv+= " #|";
                 }else if (this.grid[i][j].revealed==1){
                    rv+= this.grid[i][j].nature + " |";
                    //System.out.println(this.grid[i][j].nature);
                 }else if (this.grid[i][j].revealed==2){
                    rv+= "!!|";
                 }else if (this.grid[i][j].revealed==3){
                    rv+= "??|";
         }
         return rv;
     }
    
     // takes 2, outputs 1
    public int listLocation(int x, int y) {
        return ((x * gridAxis) + y);
    }

    // takes 1, outputs 2
    public int[] gridLocation(int k) {
        int[] coord = new int[2];
        coord[0] = k / gridAxis; // x
        coord[1] = k % gridAxis; // y
        return coord;
    }


}// The Last Bracket. Hey, that should be a movie title.