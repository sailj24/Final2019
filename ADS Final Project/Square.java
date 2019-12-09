import java.util.ArrayList;

public class Square {
    int ID;
    char nature;
    int revealed;
    ArrayList<Square> connections = new ArrayList<>();
    int neighborBombs;
    int chance;

    public Square(char nat, int place){
        ID = place;
        nature = nat; //either bomb '*' or space ' '
        revealed = 0; // 0 is hasn't yet been tampered w/, 
                        //1 is clicked
                        //2 is flagged
                        //3 is ?ed
        connections = new ArrayList<Square>();
        neighborBombs = 0;
        chance = 0;//only for the ai
    }

    public void changeAllBlankFriends(ArrayList<Square> visited){
        visited.add(this);
        for (int i=0; i<=this.connections.size()-1; i++){
            if(connections.get(i).nature==' '){
                if (visited.contains(connections.get(i))){
                    continue;
                } else{
                if (connections.get(i).neighborBombs==0){
                    connections.get(i).changeNature();
                    connections.get(i).changeAllBlankFriends(visited);
                } else connections.get(i).changeNature();
            }
        }
        }
    }
    public void changeNature(){
        if(this.nature!='*'){
            String changed = this.neighborBombs + " ";
            char change = changed.charAt(0);
            if (this.neighborBombs==0){
                this.revealed = 1;
            }else {
            this.nature = change;
            this.revealed = 1;
            }
        }
    }
    public boolean equals(Square s){
        return this.ID == s.ID;
    }
}//closes class