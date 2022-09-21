package enigmaMachine.components;

import java.io.Serializable;
import java.util.ArrayList;


public class Rotor implements Serializable {
    private int id;
    private int numOfSigns;
    private char startPosition;
    private int positionOfNotch;

    private ArrayList<Character> right = new ArrayList<>();

    private ArrayList<Character> left = new ArrayList<>();

    public Rotor() {
    }

    public Rotor(int id, int numOfSigns, int positionOfNotch, ArrayList<Character> right, ArrayList<Character> left) {
        this.id = id;
        this.numOfSigns = numOfSigns;
        this.positionOfNotch = positionOfNotch;
        this.right = right;
        this.left = left;
    }

    public void moveToInitState(){
        if(left==null || right==null) {
            return;
        }
       while(!right.get(0).equals(startPosition)){
             moveRotor();
        }
    }

    public int getPositionOfNotch() {
        return positionOfNotch;
    }

    public void moveRotor() {
        right.add(right.remove(0));
        left.add(left.remove(0));
        moveNotch();
    }

    private void moveNotch() {
        if(positionOfNotch==0){
            positionOfNotch=numOfSigns-1;
        }
        else{
            positionOfNotch--;
        }
    }
    public int mapRightToLeft(int rightIndex)
    {
        return left.indexOf(right.get(rightIndex));
    }
    public int mapLeftToRight(int leftIndex)
    {
        return right.indexOf(left.get(leftIndex));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumOfSigns() {
        return numOfSigns;
    }

    public void setNumOfSigns(int numOfSigns) {
        this.numOfSigns = numOfSigns;
    }

    public char getStartPosition() {
        return right.get(0);
    }

    public void setStartPosition(char startPosition) {
        this.startPosition = startPosition;
    }

    public ArrayList<Character> getRight() {
        return right;
    }

    public void setRight(ArrayList<Character> right) {
        this.right = right;
    }

    public ArrayList<Character> getLeft() {
        return left;
    }

    public void setLeft(ArrayList<Character> left) {
        this.left = left;
    }
}
