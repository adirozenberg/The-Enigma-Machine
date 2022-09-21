package bruteForce;

import java.util.Dictionary;

public class Decipher {

    private EnigmaDictionary dictionary;
    private int agents;

    public Decipher(EnigmaDictionary dictionary, int agents) {
        this.dictionary = dictionary;
        this.agents = agents;
    }

    public EnigmaDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(EnigmaDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public int getAgents() {
        return agents;
    }

    public void setAgents(int agents) {
        this.agents = agents;
    }
}
