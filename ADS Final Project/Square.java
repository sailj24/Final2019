import java.util.ArrayList;

public class Square {
    int ID;
    char NATURE;
    int REVEALED;
    ArrayList<Square> CONNECTIONS = new ArrayList<>();
    int NEIGHBOR_BOMBS;

    public Square(char nat, int place) {
        ID = place;
        NATURE = nat; // either bomb '*' or space ' '
        REVEALED = 0; // 0 is hasn't yet been tampered w/,
                      // 1 is clicked
                      // 2 is flagged
                      // 3 is ?ed
        CONNECTIONS = new ArrayList<Square>();
        NEIGHBOR_BOMBS = 0;
    }

    public void changeAllBlankFriends(ArrayList<Square> visited) { // reveals blank spaces when you click
        visited.add(this);
        for (int i = 0; i <= this.CONNECTIONS.size() - 1; i++) {//depth-first search
            if (CONNECTIONS.get(i).NATURE == ' ') {
                if (visited.contains(CONNECTIONS.get(i))) {
                    continue;
                } else {
                    if (CONNECTIONS.get(i).NEIGHBOR_BOMBS == 0) {
                        CONNECTIONS.get(i).changeNature();
                        CONNECTIONS.get(i).changeAllBlankFriends(visited);//recursive call
                    } else
                        CONNECTIONS.get(i).changeNature();
                }
            }
        }
    }

    public void changeNature() {
        if (this.NATURE != '*') {
            String changed = this.NEIGHBOR_BOMBS + " ";
            char change = changed.charAt(0);
            if (this.NEIGHBOR_BOMBS == 0) {
                this.REVEALED = 1;
            } else {
                this.NATURE = change;
                this.REVEALED = 1;
            }
        }
    }

    public boolean equals(Square s) {
        return this.ID == s.ID;
    }
}// closes class