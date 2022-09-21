package engine;


import bruteForce.DM;
import bruteForce.Decipher;
import enigmaMachine.EnigmaMachine;
import exceptions.InvalidInputException;
import factory.Factory;
import generated.CTEEnigma;
import generated.SchemaBasedJAXB;
import javafx.util.Pair;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

public class Engine implements engineering {

    private DM DM;
    private EnigmaMachine enigmaMachine=new EnigmaMachine();
    private ConfigurationDto initConfigurationDto;
    private Decipher decipher;
    private boolean isInitDone=false;
    private boolean isFileLoaded=false;
    private int currentMessagesCount=0;
    @Override
    public StringBuilder getHistoryAndStatisticsSB() {
        return enigmaMachine.getHistoryAndStatisticsSB();
    }

    public Engine() {
    }

    public bruteForce.DM getDM() {
        return DM;
    }

    public Decipher getDecipher() {
        return decipher;
    }

    public void setCurrentMessagesCount(int currentMessagesCount) {
        this.currentMessagesCount = currentMessagesCount;
    }

    @Override
    public void createMachineFromFile(String filePath) throws JAXBException, FileNotFoundException, InvalidInputException {
        CTEEnigma cteEnigma = SchemaBasedJAXB.deserialize(filePath);
        enigmaMachine= Factory.createEnigmaFromGeneratedEnigma(cteEnigma);
        decipher=Factory.createDecipherFromGeneratedDecipher(cteEnigma.getCTEDecipher());
        isInitDone=false;
        isFileLoaded=false;
        currentMessagesCount=0;
    }
    public int getNumOfReflectors(){return enigmaMachine.getNumOfReflectors();}
    @Override
    public String getMachineSpecification() {
        StringBuilder sb=new StringBuilder();
        sb.append("1. rotors:");
        sb.append(" you are use "+getNumRotorsInUse()+" from "+ getRotorsCount()+" number of rotors\n");
        sb.append("2. reflectors:");
        sb.append(" The reflectors count is: "+getNumOfReflectors()+"\n");
        sb.append("3. There were "+getCurrentMessagesCount()+ " messages!\n");
        if(isInitDone()) {
            sb.append("4. The original code configuration is:\n");
            ConfigurationDto initCfi=getInitConfigurationDto();
            appendConfiguration(initCfi,sb);
            sb.append("\n5. The current code configuration is:\n");
            ConfigurationDto currentCfi=getCurrentConfigurationDto();
            appendConfiguration(currentCfi,sb);
        }
        return sb.toString();
    }
    @Override
    public void initMachine(int selectedReflector, LinkedList<Integer> selectedRotors,String positions, LinkedList<Pair<Character, Character>> selectedPlugs,boolean initToStart) {
        enigmaMachine.init(selectedReflector,selectedRotors,positions,selectedPlugs);
        if(!initToStart){
            initConfigurationDto=Factory.createDtoClassFromConfiguration(enigmaMachine.getInitConfiguration());
            enigmaMachine.getHistoryAndStatisticsSB().append("\n");
            appendConfiguration(initConfigurationDto,  enigmaMachine.getHistoryAndStatisticsSB());
            enigmaMachine.getHistoryAndStatisticsSB().append("\n");
            isInitDone=true;
            currentMessagesCount=0;
        }
    }

    public boolean isInitDone() {
        return isInitDone;
    }

    @Override
    public String activateMachine(String input) throws InvalidInputException {
        String output="";
        long start = System.nanoTime();
        if(!isFileLoaded) {
            throw new InvalidInputException("You need to load an xml file first!");
        }
        if(!isInitDone) {
            throw new InvalidInputException("You need to configured the init state first!");
        }

        for (int i = 0; i < input.length(); i++) {
            output+=enigmaMachine.activate(input.charAt(i));
        }
        currentMessagesCount++;
        long end = System.nanoTime();
        long duration = end - start;
        enigmaMachine.getHistoryAndStatisticsSB().append("      " + currentMessagesCount + ". "+ input + " -> " + output + " : " + duration + "\n");
        return output;
    }

    public void updateHistoryAndStatisticsForManualState(String inputStringForManualState, String outputStringForManualState, long duration) {
        enigmaMachine.getHistoryAndStatisticsSB().append("      " + currentMessagesCount + ". "+ inputStringForManualState + " -> " + outputStringForManualState + " : " + duration + "\n");
    }

    public String activateMachineForManualState(String input) throws InvalidInputException {

        String output="";
        char decryptOutput = ' ';
        if(!isFileLoaded) {
            throw new InvalidInputException("You need to load an xml file first!");
        }
        if(!isInitDone) {
            throw new InvalidInputException("You need to configured the init state first!");
        }

        decryptOutput=enigmaMachine.activate(input.charAt(0));
        output += decryptOutput;
        return output;
    }

    public int getCurrentMessagesCount() {
        return currentMessagesCount;
    }
    public void setFileLoaded(boolean fileLoaded) {
        isFileLoaded = fileLoaded;
    }

    public boolean isFileLoaded() {
        return isFileLoaded;
    }

    @Override
    public StringBuilder initToStartConfiguration() throws InvalidInputException {
        if(!isFileLoaded) {
            throw new InvalidInputException("You need to load an xml file first!");
        }
        if(!isInitDone) {
            throw new InvalidInputException("You need to configured the init state first!");
        }
        LinkedList<Integer>selectedRotors=getArrRotorFromCfi(initConfigurationDto);
        initMachine(initConfigurationDto.getSelectedReflector(),selectedRotors,initConfigurationDto.getPositions(),initConfigurationDto.getSelectedPlugs(),true);
        StringBuilder sb=new StringBuilder();
        appendConfiguration(getInitConfigurationDto(),sb);
        return sb;
    }

    private LinkedList<Integer> getArrRotorFromCfi(ConfigurationDto cfi) {
        LinkedList<Integer> rotors=new LinkedList<>();
        for (Pair<Integer,Integer>pair:cfi.getSelectedRotors()) {
            rotors.add(pair.getKey());
        }
        return rotors;
    }
    public int getRotorsCount(){
        return enigmaMachine.getRotorsCount();
    }
    public String getABC(){
        return enigmaMachine.getIoWheel();
    }
    public int getNumRotorsInUse(){return enigmaMachine.getNumOfRotorsInUse();}

    public ConfigurationDto getInitConfigurationDto() {
        return initConfigurationDto;
    }

    public ConfigurationDto getCurrentConfigurationDto() {
        return Factory.createDtoClassFromConfiguration(enigmaMachine.getCurrentConfiguration());
    }
    public int getReflectorIdByIndex(int index)
    {
        return enigmaMachine.getReflectors().get(index).getId();
    }

    public String getIoWheel() {
        return enigmaMachine.getIoWheel();
    }
    public String reverseStr(String positions) {
        String reverseStr="";
        for (int i = positions.length()-1; i >=0 ; i--) {
            reverseStr+=positions.charAt(i);
        }
        return reverseStr;
    }

    public LinkedList<Integer> reverseArr(LinkedList<Integer> rotorsOrder) {
        LinkedList<Integer> reverseArr=new LinkedList<>();
        int j=0;
        for (int i = rotorsOrder.size()-1; i >=0 ; i--,j++) {
            reverseArr.add(rotorsOrder.get(i));
        }
        return reverseArr;
    }

    private  String getRoman(int selectedReflector) {
        switch (selectedReflector)
        {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
        }
        return null;
    }

    public int convertRomanToInt(String selectedReflector) {

        switch (selectedReflector)
        {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "VI":
                return 5;
            default:
                return 0;
        }
    }

    public boolean notContainInABC(char ch) {
        String abc= getABC();
        boolean res=true;
        for (int i = 0; i < abc.length(); i++) {
            if(abc.charAt(i)==ch)
                res=false;
        }
        return res;
    }
    public boolean letterIsAlreadyUsed(String str, char ch) {
        int count=0;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)==ch)
                count++;
        }
        if(count>1)
            return true;
        return false;
    }

    public LinkedList<Pair<Character, Character>> getPlugsFromStrPlugs(String plugsStr) {
        LinkedList<Pair<Character, Character>> plugs=new LinkedList<>();
        for (int i = 0; i < plugsStr.length()-1; i+=2) {
            plugs.add(new Pair<>(plugsStr.charAt(i),plugsStr.charAt(i+1)));
        }
        return plugs;
    }

    public void appendConfiguration(ConfigurationDto cfi, StringBuilder sb) {
        LinkedList<Pair<Integer,Integer>> initRotors=cfi.getSelectedRotors();
        sb.append("   <");
        for (int i = initRotors.size()-1; i >=1 ; i--) {
            sb.append(initRotors.get(i).getKey()+",");
        }
        sb.append(initRotors.get(0).getKey()+">");
        sb.append("<");
        String positions = enigmaMachine.getCurrentPosition();
        for (int i = initRotors.size()-1; i >=1; i--) {
            sb.append(positions.charAt(i)+"("+initRotors.get(i).getValue()+"),");
        }
        sb.append(positions.charAt(0)+"("+initRotors.get(0).getValue()+")>");
        LinkedList<Pair<Character,Character>>plugs=cfi.getSelectedPlugs();
        String reflector=getRoman(cfi.getSelectedReflector());
        sb.append("<"+reflector+">");
        if(plugs!=null&&plugs.size()!=0) {
            sb.append("<");
            for (int i = 0; i < plugs.size() - 1; i++) {
                sb.append(plugs.get(i).getValue() + "|" + plugs.get(i).getKey() + ",");
            }
            sb.append(plugs.get(plugs.size() - 1).getValue() + "|" + plugs.get(plugs.size() - 1).getKey() + ">");
        }
    }

    public boolean isValidReflector(int selectedReflector) {
        return enigmaMachine.isValidReflector(selectedReflector);
    }

    public String getReflectorSigns() {
        StringBuilder sb=new StringBuilder();
        LinkedList<Integer> reflectorsId=enigmaMachine.getReflectorsId();
        sb.append(getRoman(reflectorsId.get(0)));
        for (int i = 1; i <reflectorsId.size(); i++) {
            sb.append(","+getRoman(reflectorsId.get(i)));
        }

        return sb.toString();
    }
    public void saveMachineToFile(String path,String name) throws IOException {
        FileOutputStream fos = new FileOutputStream(path+"\\"+name);
        String pathToSaveMachine=path+name;
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        // write object to file
        oos.writeObject(enigmaMachine);
        // closing resources
        oos.close();
        fos.close();
    }
    public void loadMachineFromFile(String path,String name) throws IOException, ClassNotFoundException {
        FileInputStream is = new FileInputStream(path+"\\"+name);
        ObjectInputStream ois = new ObjectInputStream(is);
        enigmaMachine= null;
        enigmaMachine = (EnigmaMachine) ois.readObject();
        if(enigmaMachine.getInitConfiguration()!=null) {
            initConfigurationDto = Factory.createDtoClassFromConfiguration(enigmaMachine.getInitConfiguration());
            isInitDone=true;
        }else{
            isInitDone=false;
        }
        ois.close();
        is.close();
        isFileLoaded=true;
    }

    public String getRandomConfiguration() throws InvalidInputException {
        if(!isFileLoaded) {
            throw new InvalidInputException("You need to load an xml file first!");
        }
        int rotorsInUseCount = getNumRotorsInUse();
        int rotorsCount = getRotorsCount();
        String ioWheel =getIoWheel();

        LinkedList<Integer> selectedRotors = getRandomRotors(rotorsCount, rotorsInUseCount);
        String startPositions = getRandomPositions(rotorsInUseCount,ioWheel);
        int selectedReflectorById = getRandomReflector();
        LinkedList<Pair<Character,Character>> selectedPlugs = getRandomPlugs(ioWheel);

        initMachine(selectedReflectorById,selectedRotors,startPositions,selectedPlugs,false);
        return showInitStateOfMachineChosenAutomatically(selectedRotors,startPositions, selectedReflectorById, selectedPlugs);
    }

    private String showInitStateOfMachineChosenAutomatically(LinkedList<Integer> selectedRotors,String positions, int selectedReflectorById, LinkedList<Pair<Character, Character>> selectedPlugs) {
        StringBuilder sb = new StringBuilder();
        sb.append("This is the machines' init state that the system chose for you automatically: \n");
        appendConfiguration(getInitConfigurationDto(),sb);
        return sb.toString();
    }

    private LinkedList<Pair<Character, Character>> getRandomPlugs(String ioWheel) {
        List<Integer> randomIndicesOfPlugs = new ArrayList<>();
        LinkedList<Pair<Character,Character>> selectedPlugs = new LinkedList<>();
        Random rand = new Random();

        int randomNumOfPlugs = rand.nextInt((ioWheel.length() / 2) +1);
        if (randomNumOfPlugs == 0) {
            selectedPlugs = null;
        }
        else {

            for (int i = 0; i < randomNumOfPlugs; i++) {
                int firstIndex = rand.nextInt(ioWheel.length());
                while (randomIndicesOfPlugs.contains(firstIndex)) {
                    firstIndex = rand.nextInt(ioWheel.length());
                }
                randomIndicesOfPlugs.add(firstIndex);

                int secondIndex = rand.nextInt(ioWheel.length());
                while (randomIndicesOfPlugs.contains(secondIndex)) {
                    secondIndex = rand.nextInt(ioWheel.length());
                }
                randomIndicesOfPlugs.add(secondIndex);

                selectedPlugs.add(new Pair<>(ioWheel.charAt(firstIndex), ioWheel.charAt(secondIndex)));
            }
        }

        return selectedPlugs;
    }

    private int getRandomReflector() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(getNumOfReflectors());
        return getReflectorIdByIndex(randomIndex);
    }

    private LinkedList<Integer> getRandomRotors(int rotorsCount, int rotorsInUseCount) {

        Random rand = new Random();
        int randomId;
        LinkedList<Integer> chosenIdOfRotors = new LinkedList<>();

        for(int i=0; i<rotorsInUseCount; i++) {
            randomId = rand.nextInt(rotorsCount) + 1;
            while (chosenIdOfRotors.contains(randomId)) {
                randomId = rand.nextInt(rotorsCount) + 1;
            }
            chosenIdOfRotors.add(randomId);
        }


        return chosenIdOfRotors;
    }
    private String getRandomPositions(int rotorsInUseCount,String ioWheel)
    {
        Random rand = new Random();
        String res="";
        for(int i=0; i<rotorsInUseCount; i++) {
            int indexOfPosition = rand.nextInt(ioWheel.length());
            res+=ioWheel.charAt(indexOfPosition);
        }
        return res;
    }

    public boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public boolean rotorIsAlreadyUsed(LinkedList<Integer>rotorsOrder, int id) {
        int count=0;
        for (int i = 0; i < rotorsOrder.size(); i++) {
            if(rotorsOrder.get(i)==id)
                count++;
        }
        if(count>1)
            return true;
        return false;
    }

    public void setDM(bruteForce.DM dm) {
        DM = dm;
    }

    public EnigmaMachine getEnigmaMachine() {
        return enigmaMachine;
    }

    public char activateMachineOneChar(char text) throws InvalidInputException {
        if (!isFileLoaded) {
            throw new InvalidInputException("You need to load an xml file first!");
        }
        if (!isInitDone) {
            throw new InvalidInputException("You need to configured the init state first!");
        }
        return enigmaMachine.activate(text);
    }
}
