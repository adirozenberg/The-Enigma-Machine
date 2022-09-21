package enigmaMachine;

import enigmaMachine.components.Plug;
import enigmaMachine.components.Reflector;
import enigmaMachine.components.Rotor;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;


public class EnigmaMachine implements coding , Serializable {
    private int numOfRotorsInUse;
    private int numOfReflectors;
    private ArrayList<Rotor> rotors=new ArrayList<>();
    private ArrayList<Rotor> rotorsInUse=new ArrayList<>();
    private ArrayList<Reflector> reflectors=new ArrayList<>();
    private Reflector reflectorInUse;
    private ArrayList<Plug> plugs =new ArrayList<>();
    private String ioWheel;
    private Configuration initConfiguration;
    private StringBuilder historyAndStatisticsSB = new StringBuilder("Machine history and statistics:\n");

    public EnigmaMachine(int numOfRotors, int numOfReflectors, ArrayList<Rotor> rotors, ArrayList<Reflector> reflectors, String ioWheel) {
        this.numOfRotorsInUse = numOfRotors;
        this.numOfReflectors = numOfReflectors;
        this.rotors = rotors;
        this.reflectors = reflectors;
        this.ioWheel = ioWheel;
    }

    public ArrayList<Reflector> getReflectors() {
        return reflectors;
    }

    public StringBuilder getHistoryAndStatisticsSB() {
        return historyAndStatisticsSB;
    }

    public EnigmaMachine() {
    }
    @Override
    public void init(int selectedReflector,  LinkedList<Integer> selectedRotors,String positions,LinkedList<Pair<Character, Character>> selectedPlugs) {
        ArrayList<Plug> plugsInit=new ArrayList<>();
        if(selectedPlugs!=null) {
            for (Pair<Character, Character> plugPair : selectedPlugs) {
                plugsInit.add(new Plug(plugPair.getKey(), plugPair.getValue()));
            }
        }
        plugs=plugsInit;
        Rotor rotor;
        this.reflectorInUse=getReflectorById(selectedReflector);
        ArrayList<Rotor> rotorsInUseInit=new ArrayList<>();
        for (int i = 0; i <selectedRotors.size(); i++) {
            rotor=getRotorById(selectedRotors.get(i));
            rotor.setStartPosition(positions.charAt(i));
            rotor.moveToInitState();
            rotorsInUseInit.add(rotor);
        }
        rotorsInUse=rotorsInUseInit;
        LinkedList<Pair<Integer,Integer>>selectedRotorsWithPositions=getRotorsWithPositions();
        initConfiguration=new Configuration(selectedReflector,selectedRotorsWithPositions,positions,selectedPlugs);
    }

    private LinkedList<Pair<Integer, Integer>> getRotorsWithPositions() {
        LinkedList<Pair<Integer,Integer>>selectedRotorsWithPositions=new LinkedList<>();
        for (Rotor rotor:rotorsInUse) {
            selectedRotorsWithPositions.add(new Pair<>(rotor.getId(),rotor.getPositionOfNotch()+1));
        }
        return selectedRotorsWithPositions;
    }

    @Override
    public char activate(char input) {
        input = changeAccordingToPlugs(input);
        changeRotorsPositionsAccordingToNotch();
        int index = ioWheel.indexOf(input);
        for (Rotor rotor : rotorsInUse) {
            index = rotor.mapRightToLeft(index);
        }
        index = reflectorInUse.mapChar(index);
        for (int i = rotorsInUse.size()-1; i >= 0; i--) {
            index = rotorsInUse.get(i).mapLeftToRight(index);
        }
        return changeAccordingToPlugs(ioWheel.charAt(index));
    }
    private void changeRotorsPositionsAccordingToNotch() {
        for (Rotor rotor:rotorsInUse) {
            rotor.moveRotor();
            if(rotor.getPositionOfNotch()!=0)
            {
                break;
            }
        }
    }
    public int getNumOfRotorsInUse() {
        return numOfRotorsInUse;
    }
    public int getRotorsCount() {
        return rotors.size();
    }
    public char changeAccordingToPlugs(char input) {
        for (Plug p:plugs) {
            if(p.getSign1()==input)
                return p.getSign2();
            if(p.getSign2()==input)
                return p.getSign1();
        }
        return input;
    }

    public int getNumOfReflectors() {
        return numOfReflectors;
    }
    public String getIoWheel() {
        return ioWheel;
    }

    public Rotor getRotorById(int id)
    {
        for (Rotor rotor:rotors) {
            if(rotor.getId()==id)
                return rotor;
        }
        return null;
    }
    public Reflector getReflectorById(int id)
    {
        for (Reflector reflector:reflectors) {
            if(reflector.getId()==id)
                return reflector;
        }
        return null;
    }

    public Configuration getInitConfiguration() {
        return initConfiguration;
    }
    public boolean isValidReflector(int selectedReflector){
        for (Reflector reflector:reflectors) {
            if(reflector.getId()==selectedReflector)
                return true;
        }
        return false;
    }
    public LinkedList<Integer> getReflectorsId(){
        LinkedList<Integer>res=new LinkedList<>();
        for (Reflector reflector:reflectors) {
            res.add(reflector.getId());
        }
        return res;
    }
    public Configuration getCurrentConfiguration() {
        LinkedList<Pair<Character,Character>> plugConf=new LinkedList<>();
        if(plugs!=null) {
            for (Plug plug:plugs) {
                plugConf.add(new Pair<>(plug.getSign1(),plug.getSign2()));
            }
        }
        return new Configuration(reflectorInUse.getId(),getRotorsWithPositions(),getCurrentPosition(),plugConf);
    }

    public String getCurrentPosition() {
        String position="";
        for (Rotor rotor:rotorsInUse) {
             position+=rotor.getStartPosition();
        }
        return position;
    }

    public static byte[] getMachineByte(EnigmaMachine enigmaMachine) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        byte[] yourBytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(enigmaMachine);
            out.flush();
            yourBytes = bos.toByteArray();
        } finally {
            bos.close();
        }

        return yourBytes;
    }

    public static EnigmaMachine getMachineFromByte(byte[] bytesArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream dis = new ByteArrayInputStream(bytesArray);
        ObjectInputStream in = new ObjectInputStream(dis);
        return (EnigmaMachine) in.readObject();
    }

}
