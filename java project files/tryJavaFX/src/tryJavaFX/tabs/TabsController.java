package tryJavaFX.tabs;

import bruteForce.DM;
import bruteForce.Decipher;
import engine.ConfigurationDto;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.util.Pair;
import tryJavaFX.app.AppController;
import tryJavaFX.bruteForce.BruteForceController;
import tryJavaFX.encryptDecrypt.EncryptDecryptController;
import tryJavaFX.machine.MachineController;

import java.util.LinkedList;

public class TabsController {

    @FXML private ScrollPane machineComponent;
    @FXML private MachineController machineComponentController;

    @FXML private ScrollPane encryptDecryptComponent;
    @FXML private EncryptDecryptController encryptDecryptComponentController;

    @FXML private ScrollPane bruteForceComponent;
    @FXML private BruteForceController bruteForceComponentController;

    public void setCurrentMachineConfigurationProperty() {
        String value= mainController.getCurrentConfiguration();
        machineComponentController.getCurrentConfigurationMachineProperty().setValue(value);
        encryptDecryptComponentController.getCurrentMachineConfigurationProperty().setValue(value);
        bruteForceComponentController.getCurrentConfigurationMachineProperty().setValue(value);
    }
    public void setMachineDetailsProperty(){
        String value= mainController.getMachineSpecification();
        machineComponentController.getMachineDetailsProperty().setValue(value);
    }
    public MachineController getMachineComponentController() {
        return machineComponentController;
    }

    @FXML private Tab machineTab;

    @FXML private Tab encryptDecryptTab;

    @FXML private Tab bruteForceTab;
    private AppController mainController;



    @FXML
    public void initialize() {
        if (machineComponentController != null && encryptDecryptComponentController != null && bruteForceComponentController != null) {
            machineComponentController.setMainController(this);
            encryptDecryptComponentController.setMainController(this);
            bruteForceComponentController.setMainController(this);
        }
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }


    public void initMachine(int selectedReflector, LinkedList<Integer> rotorsOrder, String positions, LinkedList<Pair<Character, Character>> plugs, boolean b) {
        mainController.initMachine(selectedReflector, rotorsOrder, positions, plugs, b);
    }

    public void showMessage(String title, Alert.AlertType alertType, String content) {
        mainController.showMessage(title, alertType, content);
    }

    public LinkedList<String> getErrorMessages() {
        return mainController.getErrorMessages();
    }

    public LinkedList<Pair<Character, Character>> getPlugsFromStrPlugs(String plugsStr) {
        return mainController.getPlugsFromStrPlugs(plugsStr);
    }

    public String getPlugsFromUser(String text) {
        return mainController.getPlugsFromUser(text);
    }

    public int convertRomanToIntAndValidate(String value) {
        return mainController.convertRomanToIntAndValidate(value);
    }

    public String reverseStr(String positions) {
        return mainController.reverseStr(positions);
    }

    public LinkedList<Integer> reverseArr(LinkedList<Integer> rotorsOrder) {
        return mainController.reverseArr(rotorsOrder);
    }

    public String getPositionsFromUser(String text, int size) {
        return mainController.getPositionsFromUser(text, size);
    }

    public LinkedList<Integer> getOrderOfRotors(String text) {
        return mainController.getOrderOfRotors(text);
    }

    public void getRandomConfiguration() {
        mainController.getRandomConfiguration();
    }

    public String getCurrentConfiguration() {return mainController.getCurrentConfiguration();}

    public String getMachineSpecification() {
        return mainController.getMachineSpecification();
    }

    public String encryptMessage(String message) {
        return mainController.encryptMessage(message);
    }

    public String encryptMessageForManualState(String message) {
        return mainController.encryptMessageForManualState(message);
    }

    public String getHistoryAndStatistics() {
        return mainController.getHistoryAndStatistics();
    }

    public void resetToInitConfiguration() {
        mainController.resetToInitConfiguration();
    }

    public DM getDM() {
        return mainController.getDM();
    }
    public void setItemsAfterLoadFile() {
        machineComponent.setDisable(false);
        machineTab.setDisable(false);
        setMachineDetailsProperty();
        bruteForceComponentController.setAgentsSliderValue();
    }

    public void afterMachineComponentConfigured() {
        encryptDecryptComponent.setDisable(false);
        encryptDecryptTab.setDisable(false);
        bruteForceComponent.setDisable(false);
        bruteForceTab.setDisable(false);
    }

    public Decipher getDecipher() {
        return mainController.getDecipher();
    }

    public void setDM(DM dm) {
        mainController.setDM(dm);
    }

    public byte[] getMachineBytes() {
        return mainController.getMachineBytes();
    }

    public String getConfigurationString(ConfigurationDto configuration) {
        return mainController.getConfigurationString(configuration);
    }


    public boolean isValidWordInMachine(String text) {
        return mainController.isValidWordInMachine(text);
    }

    public void updateCurrentMessagesCount() {
        mainController.getEngine().setCurrentMessagesCount(mainController.getEngine().getCurrentMessagesCount()+1);
    }

    public void updateHistoryAndStatisticsForManualState(String inputStringForManualState, String outputStringForManualState, long duration) {
        mainController.getEngine().updateHistoryAndStatisticsForManualState(inputStringForManualState, outputStringForManualState, duration);
    }
}
