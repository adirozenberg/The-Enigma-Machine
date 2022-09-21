package enigmaMachine;

import javafx.util.Pair;

import java.util.LinkedList;

public interface coding {
    char activate(char input);
    void init(int selectedReflector,  LinkedList<Integer>selectedRotors,String position,LinkedList<Pair<Character, Character>> selectedPlugs);
}
