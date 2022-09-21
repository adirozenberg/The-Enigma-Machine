package tryJavaFX.app;

import bruteForce.DM;
import bruteForce.Decipher;
import engine.ConfigurationDto;
import engine.Engine;
import enigmaMachine.EnigmaMachine;
import exceptions.InvalidInputException;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import tryJavaFX.header.HeaderController;
import tryJavaFX.tabs.TabsController;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class AppController {

    private static Engine engine = new Engine();
    private LinkedList<String> errorMessages = new LinkedList<>();
    @FXML private BorderPane appBorderPane;

    @FXML private ScrollPane headerComponent;
    @FXML private HeaderController headerComponentController;

    @FXML private AnchorPane tabsComponent;
    @FXML private TabsController tabsComponentController;

    public LinkedList<String> getErrorMessages() {
        return errorMessages;
    }

    public Engine getEngine() {
        return engine;
    }

    @FXML
    public void initialize() {
        if (headerComponentController != null && tabsComponentController != null) {
            headerComponentController.setMainController(this);
            tabsComponentController.setMainController(this);
            errorMessages.clear();
            //bindComponentWidthToScene(appBorderPane.widthProperty(),appBorderPane.heightProperty());
        }
    }
    public void bindComponentWidthToScene(ReadOnlyDoubleProperty widthProperty,ReadOnlyDoubleProperty heightProperty){
        headerComponent.prefWidthProperty().bind(widthProperty);
        headerComponent.prefHeightProperty().bind(heightProperty);

        tabsComponent.prefWidthProperty().bind(widthProperty);
        tabsComponent.prefHeightProperty().bind(heightProperty);


    }
    public DM getDM() {
        return engine.getDM();
    }

    public LinkedList<Integer> getOrderOfRotors(String rotors) {
        String[] rotorsOrderStr;
        LinkedList<Integer> rotorsOrder=new LinkedList<>();

        rotorsOrder.clear();
        rotorsOrderStr = rotors.split(",");

        if (rotorsOrderStr.length < engine.getNumRotorsInUse()) {
            errorMessages.add("need to choose"+ engine.getNumRotorsInUse()+" rotors!");
            return rotorsOrder;
        }

        for (int i = 0; i < rotorsOrderStr.length; i++) {
            if (!engine.isInteger(rotorsOrderStr[i])) {
                errorMessages.add("Please enter the rotors number seperated by comma! ");
                return rotorsOrder;
            } else {
                rotorsOrder.add(Integer.parseInt(rotorsOrderStr[i]));
                if(rotorsOrder.get(i)> engine.getRotorsCount() || rotorsOrder.get(i) < 1 || engine.rotorIsAlreadyUsed(rotorsOrder, rotorsOrder.get(i))) {
                    errorMessages.add("Please insert valid rotors id. Notice you can't choose the same rotor!");
                    return rotorsOrder;
                }
            }
        }

        return rotorsOrder;
    }

    public String getPositionsFromUser(String positions, int numOfRotors) {
        positions=positions.toUpperCase();
        String machineABC = engine.getABC();

        if (positions.length() != numOfRotors) {
            errorMessages.add("For each rotor you need to choose one letter for start position from the abc you chose:" + machineABC);
            return null;
        }

        for (int i = 0; i < positions.length(); i++) {
            if(engine.notContainInABC(positions.charAt(i))) {
                errorMessages.add("For each rotor you need to choose one letter for start position from the abc you chose:" + machineABC);
                return null;
            }
        }

        return positions;
    }

    public String getPlugsFromUser(String plugs) {

        plugs = plugs.toUpperCase();

        if (plugs.isEmpty()) {
            return plugs;
        }

        if (plugs.length() % 2 != 0) {
            errorMessages.add("The plugs need to be in pairs!\n please enter plugs again");
            return null;
        }

        for (int i = 0; i < plugs.length(); i++) {
            if (engine.notContainInABC(plugs.charAt(i))) {
                errorMessages.add("You need to choose plug from your abc! \n please enter plugs again");
                return null;
            }
        }

        for (int i = 0; i < plugs.length() - 1; i += 2) {
            if (plugs.charAt(i) == plugs.charAt(i + 1)) {
                errorMessages.add("you can't map one letter to itself! \n please enter plugs again");
                return null;
            }
        }

        for (int i = 0; i < plugs.length(); i++) {
            if (engine.letterIsAlreadyUsed(plugs, plugs.charAt(i))) {
                errorMessages.add("Each letter can be use for only one plug!\n Please enter plugs again");
                return null;
            }
        }

        return plugs;
    }

    public void showMessage(String title, Alert.AlertType alertType, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setAlertType(alertType);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public int convertRomanToIntAndValidate(String value) {
        int res= engine.convertRomanToInt(value);
        if(!engine.isValidReflector(res) ){
            errorMessages.add("the reflector your machine can use is: "+engine.getReflectorSigns()+"\n");
        }
        return res;
    }

    public String reverseStr(String str) {
        return engine.reverseStr(str);
    }

    public LinkedList<Integer> reverseArr(LinkedList<Integer> arr) {
        return engine.reverseArr(arr);
    }

    public void initMachine(int selectedReflector, LinkedList<Integer> rotorsOrder, String positions, LinkedList<Pair<Character, Character>> plugs, boolean b) {
        engine.initMachine(selectedReflector, rotorsOrder, positions, plugs, b);
    }

    public LinkedList<Pair<Character, Character>> getPlugsFromStrPlugs(String plugsStr) {
        return engine.getPlugsFromStrPlugs(plugsStr);
    }

    public void createMachineFromFile(String absolutePath) {
        try {
            engine.createMachineFromFile(absolutePath);
            engine.setFileLoaded(true);
            tabsComponentController.setItemsAfterLoadFile();
        } catch (JAXBException e) {
            showMessage("XML file error", Alert.AlertType.ERROR, e.getMessage());
        } catch (FileNotFoundException e) {
            showMessage("XML file error", Alert.AlertType.ERROR, e.getMessage());
        } catch (InvalidInputException e) {
            showMessage("XML file error", Alert.AlertType.ERROR, e.getMessage());
        } catch (Exception e) {
            showMessage("XML file error", Alert.AlertType.ERROR, e.getMessage());
        }
    }

    public void getRandomConfiguration() {
        try {
            engine.getRandomConfiguration();
        } catch (InvalidInputException e) {
            errorMessages.add(e.getMessage());
        }
    }

    public String getCurrentConfiguration() {
        StringBuilder sb =new StringBuilder();
        engine.appendConfiguration(engine.getCurrentConfigurationDto(),sb);
        return sb.toString();
    }

    public String getMachineSpecification() {
        return engine.getMachineSpecification();
    }

    public String encryptMessage(String message) {
        message = message.toUpperCase();
        for (int i = 0; i < message.length(); i++) {
            if (engine.notContainInABC(message.charAt(i))) {
                errorMessages.add("You must insert letters from your abc: " + engine.getABC() + "\n");
                return null;
            }
        }
        String decrypt = null;
        try {
            decrypt = engine.activateMachine(message);
        } catch (InvalidInputException e) {
            errorMessages.add(e.getMessage());
        }
        return decrypt;
    }

    public String getHistoryAndStatistics() {
        return engine.getHistoryAndStatisticsSB().toString();
    }

    public void resetToInitConfiguration() {
        try {
            engine.initToStartConfiguration();
        } catch (InvalidInputException e) {
            errorMessages.add(e.getMessage());
        }
    }

    public Decipher getDecipher() {
        return engine.getDecipher();
    }

    public void setDM(DM dm) {
        engine.setDM(dm);
    }

    public byte[] getMachineBytes() {
        byte[] results = null;
        try {
            results = EnigmaMachine.getMachineByte(engine.getEnigmaMachine());
        } catch (IOException e) {
            showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
        }

        return results;
    }

    public String getConfigurationString(ConfigurationDto configuration) {
        StringBuilder sb = new StringBuilder();
        engine.appendConfiguration(configuration, sb);
        return sb.toString();
    }

    public boolean isValidWordInMachine(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (engine.notContainInABC(text.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public String encryptMessageForManualState(String message) {
        message = message.toUpperCase();
        for (int i = 0; i < message.length(); i++) {
            if (engine.notContainInABC(message.charAt(i))) {
                errorMessages.add("You must insert letters from your abc: " + engine.getABC() + "\n");
                return null;
            }
        }

        String decrypt = null;
        try {
            decrypt = engine.activateMachineForManualState(message);
        } catch (InvalidInputException e) {
            errorMessages.add(e.getMessage());
        }
        return decrypt;
    }
}
