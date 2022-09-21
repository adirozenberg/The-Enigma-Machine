package playMachine;

import engine.ConfigurationDto;
import engine.Engine;
import exceptions.InvalidInputException;
import javafx.util.Pair;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class PlayMachine {

    private static Engine engine = new Engine();
    public static void play() {
        Scanner scanner = new Scanner(System.in);
        handleChoice(scanner);
    }

    private static void handleChoice(Scanner scanner) {

        int choiceFromUser;
        boolean run = true;
        while (run) {
            showMenu();
            choiceFromUser=getChoiceFromUser(scanner, 10);
            menu choice = menu.values()[choiceFromUser - 1];
            switch (choice) {
                case READ_FILE:
                    getFileFromUser(scanner);
                    break;
                case SHOW_DETAILS:
                    showDetails(scanner);
                    break;
                case CHOOSE_MANUAL_CONFIGURATION:
                    getConfigurationFromUser(scanner);
                    break;
                case CHOOSE_AUTO_CONFIGURATION:
                    getRandomConfiguration();
                    break;
                case INPUT_PROCESSING:
                     getInputFromUserAndShowResult(scanner);
                    break;
                case INIT_MACHINE:
                     configureOriginalInit();
                    break;
                case HISTORY_STATISTICS:
                    System.out.println(engine.getHistoryAndStatisticsSB());
                    break;
                case SAVE_MACHINE_TO_FILE:
                     saveMachineToFile(scanner);
                    break;
                case LOAD_MACHINE_FROM_FILE:
                    loadMachineFromFile(scanner);
                    break;
                case EXIT:
                    run = false;
                    break;
            }
        }
    }

    private static void loadMachineFromFile(Scanner scanner) {
        System.out.println("Please insert the path file");
        String path=scanner.nextLine();
        System.out.println("Please insert the name of the file");
        String name=scanner.nextLine();
        try {
            engine.loadMachineFromFile(path,name);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("The machine load successfully");
    }

    private static void saveMachineToFile(Scanner scanner) {
        System.out.println("Please insert the path you wish the machine file will be");
        String path=scanner.nextLine();
        System.out.println("Please insert the name of the file");
        String name=scanner.nextLine();
        try {
            engine.saveMachineToFile(path,name);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("The machine save successfully");

    }

    private static void configureOriginalInit() {
        StringBuilder sb = new StringBuilder();
        try {
            sb = engine.initToStartConfiguration();
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("The machine is now configured to it's init state ,which is:");
        System.out.println(sb);
    }

    private static void showDetails(Scanner scanner) {
        StringBuilder sb=new StringBuilder();
        if(!engine.isFileLoaded()){
            System.out.println("You need to load an xml file before showing machine details!");
            return;
        }
        sb.append("1. rotors:\n");
        sb.append("   you are use "+engine.getNumRotorsInUse()+" from "+ engine.getRotorsCount()+" number of rotors\n");
        sb.append("2. reflectors:\n");
        sb.append("   The reflectors count is: "+engine.getNumOfReflectors()+"\n");
        sb.append("3. There were "+engine.getCurrentMessagesCount()+ " messages!\n");
        if(engine.isInitDone()) {
            sb.append("4. The original code configuration is:\n");
            ConfigurationDto initCfi=engine.getInitConfigurationDto();
            engine.appendConfiguration(initCfi,sb);
            sb.append("\n5. The current code configuration is:\n");
            ConfigurationDto currentCfi=engine.getCurrentConfigurationDto();
            sb.append("\n");
            engine.appendConfiguration(currentCfi,sb);
        }
        System.out.println(sb);
    }

    private static void getRandomConfiguration() {
        String str = "";

        try {
            str = engine.getRandomConfiguration();
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println(str);
    }

    private static void getInputFromUserAndShowResult(Scanner scanner) {
        System.out.println("Please insert your input for the machine!");
        String input = scanner.nextLine();
        input=input.toUpperCase();
        boolean needBreak=true;

        while (needBreak) {
            needBreak=false;
            for (int i = 0; i < input.length()&&!needBreak; i++) {
                if (notContainInABC(input.charAt(i))) {
                    System.out.println("You must insert letters from your abc!\n try again or insert press enter to go back to menu");
                    input = scanner.nextLine();
                    input=input.toUpperCase();
                    if (input.isEmpty())
                        return;
                    needBreak=true;
                }
            }
        }

        try {
            System.out.println("The machine result is:\n"+engine.activateMachine(input));
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    private static void getConfigurationFromUser(Scanner scanner) {
        if(!engine.isFileLoaded()) {
            System.out.println("You need to load an xml file before configuring an manually init state!");
            return;
        }
        LinkedList<Integer> rotorsOrder = getOrderOfRotors(scanner);
        if(rotorsOrder==null)
            return;
        String positions = getPositionsFromUser(scanner, rotorsOrder.size());
        if(positions==null)
            return;
        positions=reverseStr(positions);
        rotorsOrder=reverseArr(rotorsOrder);
        showReflector();
        int selectedReflector = getReflectorFromUser(scanner,6);
        if(selectedReflector==6)
            return;

          String plugsStr=getPlugsFromUser(scanner);
         LinkedList<Pair<Character,Character>> plugs=null;
         if(plugsStr!=null) {
             if(plugsStr.isEmpty())
                 return;
            plugs=getPlugsFromStrPlugs(plugsStr);
          }
         engine.initMachine(selectedReflector,rotorsOrder,positions,plugs,false);
         System.out.println("Your configuration successfully loaded!");
        }

    private static int getReflectorFromUser(Scanner scanner, int range) {
        int selectedReflector = getChoiceFromUser(scanner, 6);
        if(selectedReflector==6)
            return selectedReflector;
        while(!engine.isValidReflector(selectedReflector)){
            System.out.println("Please try again! the reflector your machine can use is: "+engine.getReflectorSigns()+"\n");
            selectedReflector = getChoiceFromUser(scanner, 6);
            if(selectedReflector==6)
                return selectedReflector;
        }
        return selectedReflector;
    }

    private static LinkedList<Integer> reverseArr(LinkedList<Integer> rotorsOrder) {
        LinkedList<Integer> reverseArr=new LinkedList<>();
        int j=0;
        for (int i = rotorsOrder.size()-1; i >=0 ; i--,j++) {
            reverseArr.add(rotorsOrder.get(i));
        }
        return reverseArr;
    }

    private static String reverseStr(String positions) {
        String reverseStr="";
        for (int i = positions.length()-1; i >=0 ; i--) {
            reverseStr+=positions.charAt(i);
        }
        return reverseStr;
    }

    private static LinkedList<Pair<Integer, Character>> getPairsOfPositionsAndRotors(String positions, int[] rotorsOrder) {
     LinkedList<Pair<Integer,Character>> pairs=new LinkedList<>();
        for (int i = rotorsOrder.length-1; i >=0 ; i--) {
            pairs.add(new Pair<>(rotorsOrder[i],positions.charAt(i)));
        }
        return pairs;
    }

    private static LinkedList<Pair<Character, Character>> getPlugsFromStrPlugs(String plugsStr) {
        LinkedList<Pair<Character, Character>> plugs=new LinkedList<>();
        for (int i = 0; i < plugsStr.length()-1; i+=2) {
            plugs.add(new Pair<>(plugsStr.charAt(i),plugsStr.charAt(i+1)));
        }
        return plugs;
    }

    private static String getPlugsFromUser(Scanner scanner) {
        String plugs="";
        System.out.println("Would you like to choose plugs? (y/n)");
        String answer = scanner.nextLine();
        while(!(answer.equals("y")||answer.equals("n")))
        {
            System.out.println("Please insert y or n. or press enter to go ack to menu.");
            answer = scanner.nextLine();
            if(answer.isEmpty())
                return answer;
        }
        if(answer.equals("y")) {
            System.out.println("Please insert plugs or press enter to go back menu\n for example: dk49");
            boolean isValid = false;
            plugs = scanner.nextLine();
            plugs = plugs.toUpperCase();
            while (!isValid) {
                if (plugs.isEmpty()) {
                    return plugs;
                }
                if (plugs.length() % 2 != 0) {
                    System.out.println("The plugs need to be in pairs!\n please enter plugs again or press enter to go back menu.");
                    plugs = scanner.nextLine();
                    plugs = plugs.toUpperCase();
                    if (plugs.isEmpty()) {
                        return plugs;
                    }
                    continue;
                }
                for (int i = 0; i < plugs.length(); i++) {
                    if (notContainInABC(plugs.charAt(i))) {
                        System.out.println("You need to choose plug from your abc! \n please enter plugs again or press enter to go back menu.");
                        plugs = scanner.nextLine();
                        plugs = plugs.toUpperCase();
                        if (plugs.isEmpty()) {
                            return plugs;
                        }
                        continue;
                    }
                }
                for (int i = 0; i < plugs.length() - 1; i += 2) {
                    if (plugs.charAt(i) == plugs.charAt(i + 1)) {
                        System.out.println("you can't map one letter to itself! \n please enter plugs again or press enter to go back menu.");
                        plugs = scanner.nextLine();
                        plugs = plugs.toUpperCase();
                        if (plugs.isEmpty()) {
                            return plugs;
                        }
                        continue;
                    }
                }
                for (int i = 0; i < plugs.length(); i++) {
                    if (letterIsAlreadyUsed(plugs, plugs.charAt(i))) {
                        System.out.println("Each letter can be use for only one plug!\n Please enter plugs again or press enter to go back menu.");
                        plugs = scanner.nextLine();
                        plugs = plugs.toUpperCase();
                        if (plugs.isEmpty()) {
                            return plugs;
                        }
                        continue;
                    }
                }
                isValid = true;

            }
        }
        else if(answer.equals("n")) {
            return null;
        }
         return plugs;
    }
    private static String getPositionsFromUser(Scanner scanner, int numOfRotors) {
        System.out.println("Please enter the start position for each rotors.");
        System.out.println("For example for 5 rotors: XBKJN");
        String positions = scanner.nextLine();
        positions=positions.toUpperCase();
        String machineABC = engine.getABC();
        boolean needBreak=true;
        while(needBreak){
            needBreak=false;
          if (positions.length() != numOfRotors) {
            System.out.println("For each rotor you need to choose one letter for start position from the abc you chose:" + machineABC + "! Or press enter for go back to menu");
            positions = scanner.nextLine();
            positions=positions.toUpperCase();
            if (positions.isEmpty()) {
                return null;
            }
            needBreak=true;
        }
        for (int i = 0; i < positions.length()&&!needBreak; i++) {
            if(notContainInABC(positions.charAt(i))) {
                System.out.println("For each rotor you need to choose one letter for start position from the abc you chose:" + machineABC + "! Or press enter for go back to menu");
                positions = scanner.nextLine();
                positions=positions.toUpperCase();
                if (positions.isEmpty()) {
                    return null;
                }
                needBreak=true;
            }
         }
        }
        return positions;
    }

    private static LinkedList<Integer> getOrderOfRotors(Scanner scanner) {
        System.out.println("Please insert the rotors id in the order you want, you need to insert "+engine.getNumRotorsInUse()+" rotors.");
        System.out.println("For example: 2,3,1,4,5");
        String rotors = scanner.nextLine();
        String[] rotorsOrderStr;
        LinkedList<Integer> rotorsOrder=new LinkedList<>();
        boolean needBreak=true;
        while (needBreak) {
            needBreak=false;
            rotorsOrder.clear();
            rotorsOrderStr = rotors.split(",");
            while(rotorsOrderStr.length<engine.getNumRotorsInUse()){
                System.out.println("Use need to choose "+ engine.getNumRotorsInUse()+" rotors!");
                System.out.println("Please try again! Or press enter to go back to menu");
                rotors = scanner.nextLine();
                if (rotors.isEmpty()) {
                    return null;
                }
                rotorsOrderStr=rotors.split(",");
            }
            for (int i = 0; i < rotorsOrderStr.length && !needBreak; i++) {
                if (!isInteger(rotorsOrderStr[i])) {
                    System.out.println("Please enter the rotor number seperated by comma! Or press enter to go back to menu");
                    rotors = scanner.nextLine();
                    if (rotors.isEmpty()) {
                        return null;
                    }
                    needBreak=true;
                } else {
                    rotorsOrder.add(Integer.parseInt(rotorsOrderStr[i]));
                    if(rotorsOrder.get(i)> engine.getRotorsCount() || rotorsOrder.get(i) < 1 || rotorIsAlreadyUsed(rotorsOrder, rotorsOrder.get(i))) {
                        System.out.println("Please insert valid rotor id. Notice you can't choose the same rotor! Or press enter to go back to menu");
                        rotors = scanner.nextLine();
                        if (rotors.isEmpty()) {
                            return null;
                        }
                        needBreak=true;
                    }
                }

            }
        }
        return rotorsOrder;
    }

    private static boolean notContainInABC(char ch) {
        String abc= engine.getABC();
        boolean res=true;
        for (int i = 0; i < abc.length(); i++) {
            if(abc.charAt(i)==ch)
                res=false;
        }
        return res;
    }

    private static boolean rotorIsAlreadyUsed(LinkedList<Integer>rotorsOrder, int id) {
      int count=0;
        for (int i = 0; i < rotorsOrder.size(); i++) {
            if(rotorsOrder.get(i)==id)
                count++;
        }
        if(count>1)
            return true;
        return false;
    }
    private static boolean letterIsAlreadyUsed(String str, char ch) {
        int count=0;
        for (int i = 0; i < str.length(); i++) {
            if(str.charAt(i)==ch)
                count++;
        }
        if(count>1)
            return true;
        return false;
    }

    private static void getFileFromUser(Scanner scanner) {
        engine.setFileLoaded(false);
        while(!engine.isFileLoaded()) {
            System.out.println("Please enter xml file path: Or press enter to go back to menu");
            String filePath = scanner.nextLine();
            if(filePath.isEmpty())
                return;
            if (!filePath.endsWith(".xml")) {
                System.out.println("This is not a xml file.");
            }
            try {
                engine.createMachineFromFile(filePath);
            } catch (FileNotFoundException e) {
                System.out.println("Your file was not found!\n please make sure you enter an exist file!");
                continue;
            } catch (JAXBException e) {
                System.out.println(e.getMessage());
                continue;
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
                continue;
            }
            System.out.println("your file loaded successfully");
            engine.setFileLoaded(true);
        }
    }

    private static int getChoiceFromUser(Scanner scanner,int range) {
        String input=scanner.nextLine();
        while(!isValidInput(input,range))
        {
            input=scanner.nextLine();
        }
        return Integer.parseInt(input);
    }
    private static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
        }
        catch (Exception e)
        {
            System.out.println("Please enter a number.");
            return false;
        }
        return true;
    }
    private static boolean isValidInput(String input,int range) {
       if(!isInteger(input))
           return false;
        int choice = Integer.parseInt(input);

        if(range!=0 && !(choice >=1 && choice<=range)) {
            System.out.println("Please enter a number between 1-"+range);
            return false;
        }
        return true;
    }

    private static void showReflector(){
        System.out.printf("Please choose reflector (1-5)\n");
        StringBuilder sb=new StringBuilder();
        sb.append("1. I\n");
        sb.append("2. II\n");
        sb.append("3. III\n");
        sb.append("4. IV\n");
        sb.append("5. V\n");
        sb.append("6. Go back to main menu\n");
        System.out.println(sb);
    }
    private static void showMenu() {
        StringBuilder sb=new StringBuilder();
        sb.append("1. Read the machine information from file\n");
        sb.append("2. Display the machine specification\n");
        sb.append("3. Configure the init state of the machine manually\n");
        sb.append("4. Configure the init state of the machine automatically\n");
        sb.append("5. Insert input to the machine\n");
        sb.append("6. Init to start configuration\n");
        sb.append("7. Get history and statistics of the machine\n");
        sb.append("8. Save machine to file\n");
        sb.append("9. Load machine from file\n");
        sb.append("10. Exit\n");
        System.out.println(sb);
    }
}
