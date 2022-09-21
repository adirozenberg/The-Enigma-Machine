package tryJavaFX.encryptDecrypt;

import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import tryJavaFX.properties.CurrentMachineConfigurationProperty;
import tryJavaFX.properties.DecryptMessageProperty;
import tryJavaFX.properties.HistoryAndStatisticsProperty;
import tryJavaFX.tabs.TabsController;

public class EncryptDecryptController {

    @FXML
    private TextArea currentMachineConfigurationTextArea;

    @FXML
    private TextArea encryptTextArea;

    @FXML
    private TextArea decryptTextArea;

    @FXML
    private Button processButton;

    @FXML
    private Button resetButton;

    @FXML
    private TextArea statisticsTextArea;
    @FXML
    private Button clearButton;

    private TabsController mainController;
    @FXML
    private ComboBox<String> stateComoBox;
    @FXML
    private Button doneButton;
    @FXML
    private ImageView imageView;
    private boolean manualState=false;

    private String outputStringForManualState = "";

    private String inputStringForManualState = "";

    private long start;
    private long duration;
    private boolean firstManualCharacter = true;

    private int countOfInputString = 0;

    final private CurrentMachineConfigurationProperty currentMachineConfigurationProperty=new CurrentMachineConfigurationProperty();
    final private DecryptMessageProperty decryptMessageProperty=new DecryptMessageProperty();
    final private HistoryAndStatisticsProperty historyAndStatisticsProperty=new HistoryAndStatisticsProperty();
    public CurrentMachineConfigurationProperty getCurrentMachineConfigurationProperty() {
        return currentMachineConfigurationProperty;
    }

    @FXML
    public void initialize() {
        stateComoBox.getItems().add("Manual");
        stateComoBox.getItems().add("Un Manual");
        stateComoBox.setValue("Un Manual");
        doneButton.setDisable(true);
        currentMachineConfigurationTextArea.textProperty().bind(new StringBinding() {
            {
                bind(currentMachineConfigurationProperty.valueProperty());
            }

            @Override
            protected String computeValue() {
                String value = currentMachineConfigurationProperty.getValue();
                return value;
            }
        });
        decryptTextArea.textProperty().bind(new StringBinding() {
            {
                bind(decryptMessageProperty.valueProperty());
            }

            @Override
            protected String computeValue() {
                String value = decryptMessageProperty.getValue();
                return value;
            }
        });
        statisticsTextArea.textProperty().bind(new StringBinding() {
            {
                bind(historyAndStatisticsProperty.valueProperty());
            }

            @Override
            protected String computeValue() {
                String value = historyAndStatisticsProperty.getValue();
                return value;
            }
        });

    }
    @FXML
    void clickProcessButtonAction(ActionEvent event) {
        String decrypt=mainController.encryptMessage(encryptTextArea.getText());
        if(mainController.getErrorMessages().size()>0){
            mainController.showMessage("Error!", Alert.AlertType.ERROR, mainController.getErrorMessages().toString());
            mainController.getErrorMessages().clear();
            return;
        }
        decryptMessageProperty.setValue(decrypt);
        mainController.setCurrentMachineConfigurationProperty();
        mainController.setMachineDetailsProperty();
        historyAndStatisticsProperty.setValue(mainController.getHistoryAndStatistics());
    }

    @FXML
    void clickResetButtonAction(ActionEvent event) {
        mainController.resetToInitConfiguration();
        if(mainController.getErrorMessages().size()>0) {
            mainController.showMessage("Error!", Alert.AlertType.ERROR, mainController.getErrorMessages().toString());
            mainController.getErrorMessages().clear();
            return;
        }
        mainController.setMachineDetailsProperty();
        mainController.setCurrentMachineConfigurationProperty();
        decryptMessageProperty.setValue("");
        encryptTextArea.clear();
        countOfInputString = 0;
        firstManualCharacter = true;
        outputStringForManualState = "";
        inputStringForManualState = "";
    }

    @FXML
    void clickClearButtonAction(ActionEvent event) {
        encryptTextArea.clear();
        decryptMessageProperty.setValue("");
        outputStringForManualState = "";
        inputStringForManualState = "";
        firstManualCharacter = true;
        countOfInputString = 0;
    }
    public void setMainController(TabsController appController) {
        this.mainController = appController;
    }
    @FXML
    void onStateComboBoxAction(ActionEvent event) {
        switch (stateComoBox.getSelectionModel().getSelectedItem()){
            case "Manual":
                processButton.setDisable(true);
                doneButton.setDisable(false);
                manualState=true;
                encryptTextArea.clear();
                decryptMessageProperty.setValue("");
                break;
            case "Un Manual":
                doneButton.setDisable(true);
                processButton.setDisable(false);
                manualState=false;
                encryptTextArea.clear();
                decryptMessageProperty.setValue("");
                break;
        }


    }

    @FXML
    void OnTextChangeAction(KeyEvent event) {
        String inputString = "";
        if (manualState) {
            if (firstManualCharacter) {
                firstManualCharacter = false;
                start = System.nanoTime();
            }

            inputStringForManualState = encryptTextArea.getText().toUpperCase();
            if (inputStringForManualState.length() == 0) {
                mainController.showMessage("Error!", Alert.AlertType.ERROR, "you need to enter a character one by one manually!");
                mainController.getErrorMessages().clear();
                encryptTextArea.clear();
                decryptMessageProperty.setValue("");
                firstManualCharacter = true;
                countOfInputString = 0;
                outputStringForManualState = "";
                inputStringForManualState = "";
                return;
            }

            inputString += inputStringForManualState.charAt(countOfInputString);
            outputStringForManualState += mainController.encryptMessageForManualState(inputString);
            countOfInputString++;

            if (mainController.getErrorMessages().size() > 0) {
                mainController.showMessage("Error!", Alert.AlertType.ERROR, mainController.getErrorMessages().toString());
                mainController.getErrorMessages().clear();
                encryptTextArea.clear();
                return;
            }

            decryptMessageProperty.setValue(outputStringForManualState);
            mainController.setCurrentMachineConfigurationProperty();
        }
    }

    @FXML
    void clickDoneButtonAction(ActionEvent event) {
        mainController.updateCurrentMessagesCount();
        long end = System.nanoTime();
        duration = end - start;
        firstManualCharacter = true;
        countOfInputString = 0;
        mainController.updateHistoryAndStatisticsForManualState(inputStringForManualState, outputStringForManualState, duration);
        historyAndStatisticsProperty.setValue(mainController.getHistoryAndStatistics());
        outputStringForManualState = "";
        inputStringForManualState = "";
        encryptTextArea.clear();
        decryptMessageProperty.setValue("");
    }
}
