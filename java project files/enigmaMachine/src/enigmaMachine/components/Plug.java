package enigmaMachine.components;

import java.io.Serializable;

public class Plug implements Serializable {
    private char sign1;
    private char sign2;

    public Plug(char sign1, char sign2) {
        this.sign1 = sign1;
        this.sign2 = sign2;
    }

    public char getSign1() {
        return sign1;
    }
    public char getSign2() {
        return sign2;
    }
}

