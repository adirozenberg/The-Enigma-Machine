package enigmaMachine;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;

public class Configuration implements Serializable {
        private int selectedReflector;
        private  LinkedList<Pair<Integer,Integer>> selectedRotors;
        private String positions;
        private LinkedList<Pair<Character, Character>> selectedPlugs;

    public Configuration(int selectedReflector, LinkedList<Pair<Integer, Integer>> selectedRotors, String positions, LinkedList<Pair<Character, Character>> selectedPlugs) {
        this.selectedReflector = selectedReflector;
        this.selectedRotors = selectedRotors;
        this.positions = positions;
        this.selectedPlugs = selectedPlugs;
    }
    public int getSelectedReflector() {
        return selectedReflector;
    }

    public String getPositions() {
        return positions;
    }

    public  LinkedList<Pair<Integer,Integer>> getSelectedRotors() {
        return selectedRotors;
    }

    public LinkedList<Pair<Character, Character>> getSelectedPlugs() {
        return selectedPlugs;
    }

    public void setPositions(String positions) {
        this.positions=positions;
    }
}
