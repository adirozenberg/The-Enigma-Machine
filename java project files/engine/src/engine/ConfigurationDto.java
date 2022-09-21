package engine;

import javafx.util.Pair;

import java.util.LinkedList;

public class ConfigurationDto {
    private int selectedReflector;
    LinkedList<Pair<Integer,Integer>> selectedRotors;
    private String positions;
    private LinkedList<Pair<Character, Character>> selectedPlugs;

    public ConfigurationDto(int selectedReflector, LinkedList<Pair<Integer, Integer>> selectedRotors, String positions, LinkedList<Pair<Character, Character>> selectedPlugs) {
        this.selectedReflector = selectedReflector;
        this.selectedRotors = selectedRotors;
        this.positions = positions;
        this.selectedPlugs = selectedPlugs;
    }

    public int getSelectedReflector() {
        return selectedReflector;
    }

    public LinkedList<Pair<Integer,Integer>> getSelectedRotors() {
        return selectedRotors;
    }

    public String getPositions() {
        return positions;
    }

    public LinkedList<Pair<Character, Character>> getSelectedPlugs() {
        return selectedPlugs;
    }
}
