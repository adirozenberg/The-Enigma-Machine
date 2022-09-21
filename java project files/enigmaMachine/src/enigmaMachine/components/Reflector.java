package enigmaMachine.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Reflector implements Serializable {
    private int id;
    private Map<Integer, Integer> content = new HashMap<>();

    public Reflector(int id, Map<Integer, Integer> content) {
        this.content = content;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public int mapChar(int key) {
        return this.content.get(key);
    }
}
