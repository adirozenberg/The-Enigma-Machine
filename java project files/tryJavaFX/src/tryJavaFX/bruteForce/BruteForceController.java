package tryJavaFX.bruteForce;

import bruteForce.CandidateToDecryptionDto;
import bruteForce.DM;
import bruteForce.Decipher;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tryJavaFX.bruteForce.dictionary.DictionaryController;
import tryJavaFX.properties.CurrentMachineConfigurationProperty;
import tryJavaFX.properties.DecryptMessageProperty;
import tryJavaFX.tabs.TabsController;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public class BruteForceController {

    @FXML
    private TextArea currentMachineConfigurationTextArea;
    @FXML
    private TextArea encryptTextArea;

    @FXML
    private TextArea decryptTextArea;
    @FXML
    private Button processButton;

    @FXML
    private Button dictionarySuggestionsButton;

    @FXML
    private Button resetButton;

    @FXML
    private Slider agentsSlider;

    @FXML
    private ComboBox<String> difficultyLevelComboBox;

    @FXML
    private TextField missionExtentTextField;

    @FXML
    private Button startButton;

    @FXML
    private Button pauseButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button resumeButton;
    @FXML
    private ProgressBar candidatesProgressBar;
    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView2;

    @FXML
    private ImageView imageView3;

    @FXML
    private TextArea finalTimeTextArea;

    private double startTimeOfBruteForce;
    private double durationTimeOfBruteForce;

    private TabsController mainController;
    final CurrentMachineConfigurationProperty currentConfigurationMachineProperty = new CurrentMachineConfigurationProperty();

    final private DecryptMessageProperty decryptMessageProperty = new DecryptMessageProperty();
    private DictionaryController dictionaryController;
    @FXML
    private ListView<String> agentsCandidateListView;

    private boolean firstTimeOfChosenWordFromDictionary;

    private DM DM =new DM();


    public TextArea getEncryptTextArea() {
        return encryptTextArea;
    }

    @FXML
    public void initialize() {
        difficultyLevelComboBox.getItems().add("Easy");
        difficultyLevelComboBox.getItems().add("Medium");
        difficultyLevelComboBox.getItems().add("Hard");
        difficultyLevelComboBox.getItems().add("Impossible");
        difficultyLevelComboBox.setValue("Easy");

        agentsSlider.setMajorTickUnit(1);
        agentsSlider.setMinorTickCount(0);
        agentsSlider.setShowTickLabels(true);

        firstTimeOfChosenWordFromDictionary = true;
        currentMachineConfigurationTextArea.textProperty().bind(new StringBinding() {
            {
                bind(currentConfigurationMachineProperty.valueProperty());
            }

            @Override
            protected String computeValue() {
                String value = currentConfigurationMachineProperty.getValue();
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

        missionExtentTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    missionExtentTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        agentsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                agentsSlider.setValue(newValue.intValue());
            }
        });
    }

    public DM getDM() {
        return mainController.getDM();
    }

    @FXML
    void clickDictionarySuggestionsAction(ActionEvent event) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getClassLoader().getResource("tryJavaFX/bruteForce/dictionary/dictionary.fxml");
        fxmlLoader.setLocation(url);
        AnchorPane dictionaryComponent = fxmlLoader.load(url.openStream());
        dictionaryController = fxmlLoader.getController();
        dictionaryController.setMainController(this);
        dictionaryController.setWordsToDictionary(getDecipher().getDictionary().getWords());
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Dictionary");
        primaryStage.setScene(new Scene(dictionaryComponent));
        primaryStage.show();
    }

    private Decipher getDecipher() {
        return mainController.getDecipher();
    }

    @FXML
    void clickPauseButtonAction(ActionEvent event) {
        if (DM != null)
            DM.pauseWork();
    }

    @FXML
    void clickProcessButtonAction(ActionEvent event) {
        String decrypt = mainController.encryptMessage(encryptTextArea.getText());

        if (mainController.getErrorMessages().size() > 0) {
            mainController.showMessage("Error!", Alert.AlertType.ERROR, mainController.getErrorMessages().toString());
            mainController.getErrorMessages().clear();
            return;
        }

        decryptMessageProperty.setValue(decrypt);
        mainController.setCurrentMachineConfigurationProperty();
        mainController.setMachineDetailsProperty();
    }

    public CurrentMachineConfigurationProperty getCurrentConfigurationMachineProperty() {
        return currentConfigurationMachineProperty;
    }

    @FXML
    void clickResetButtonAction(ActionEvent event) {
        mainController.resetToInitConfiguration();
        if (mainController.getErrorMessages().size() > 0) {
            mainController.showMessage("Error!", Alert.AlertType.ERROR, mainController.getErrorMessages().toString());
            mainController.getErrorMessages().clear();
            return;
        }
        mainController.setMachineDetailsProperty();
        mainController.setCurrentMachineConfigurationProperty();
        decryptMessageProperty.setValue("");
        encryptTextArea.clear();
    }

    @FXML
    void clickResumeButtonAction(ActionEvent event) {
        if(DM!=null){
            DM.resumeWork();
        }
    }

    @FXML
    void clickStartButtonAction(ActionEvent event) {
        candidatesProgressBar.setProgress(0.0);
        startTimeOfBruteForce = System.nanoTime();
        finalTimeTextArea.setText("");
        agentsCandidateListView.getItems().clear();
        try {
            if (checkValidOnFieldsStartBruteForce() == false) {
                return;
            }
            DM.setMachine(mainController.getMachineBytes());
            DM.setNumOfAgents(mainController.getDecipher().getAgents());
            DM.setEnigmaDictionary( mainController.getDecipher().getDictionary());
            DM.setNumOfAgentsInUse((int) agentsSlider.getValue());
            DM.setDifficultyLevelOfBruteForce(DM.convertDifficultyLevelToEnum(difficultyLevelComboBox.getValue()));
            DM.setMissionExtent(Integer.parseInt(missionExtentTextField.getText()));

            String output = mainController.encryptMessage(encryptTextArea.getText().toUpperCase());
            DM.setContentToDecrypt(output);
            DM.setFinish(false);
            mainController.setDM(DM);
            decryptMessageProperty.setValue(output);
            mainController.setCurrentMachineConfigurationProperty();
            new Thread(() -> {
                try {
                    DM.start();
                } catch (InterruptedException | IOException | ClassNotFoundException e) {
                    mainController.showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
                    return;
                } catch (Exception e) {
                    mainController.showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
                    return;
                }
            }).start();
            Thread listenerThread = null;
            listenerThread = new Thread(() -> {
                try {
                    while(true){
                        ////// sleep
                        Thread.sleep(700); // 2000
                        /*Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                candidatesProgressBar.setProgress(DM.getProgress());
                            }
                        });*/
                        if (!DM.isPause()) {
                            if (!DM.isFinish()) {
                                CandidateToDecryptionDto candidate = null;
                                if (DM != null) {
                                    candidate = DM.getAgentsCandidates();
                                    if (candidate != null) {
                                        updateCandidateProperty(candidate);
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                    double end = System.nanoTime();
                    durationTimeOfBruteForce = end - startTimeOfBruteForce;
                    finalTimeTextArea.setText(String.valueOf(durationTimeOfBruteForce));
                } catch (InterruptedException | ArrayIndexOutOfBoundsException e) {
                    mainController.showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
                    return;
                } catch (Exception e) {
                    mainController.showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
                    return;
                }
            });
            listenerThread.start();
            resumeButton.setDisable(false);
            pauseButton.setDisable(false);
            stopButton.setDisable(false);
        } catch (IOException | ClassNotFoundException e) {
            mainController.showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
            return;
        } catch (Exception e) {
            mainController.showMessage("Error", Alert.AlertType.ERROR, e.getMessage());
            return;
        }

    }

    private void updateCandidateProperty(CandidateToDecryptionDto candidate) {
        System.out.println(candidate.getCandidate());
        Platform.runLater(() -> agentsCandidateListView.getItems().add("Candidate: " + candidate.getCandidate() + "  Agent: " + candidate.getAgentId() + "  Configuration:" + candidate.getConfiguration()));
        candidatesProgressBar.setProgress(DM.getProgress());
    }

    private boolean checkValidOnFieldsStartBruteForce() {
        if (!isValidTextEncrypt(encryptTextArea.getText())) {
            mainController.showMessage("Error!", Alert.AlertType.ERROR, "You need to choose legal words from the dictionary!");
            return false;
        }
        if (missionExtentTextField.getText().length() == 0) {
            mainController.showMessage("Mission extent empty!", Alert.AlertType.ERROR, "You need to define mission extent before starting the brute force!");
            return false;
        }
        if (Integer.parseInt(missionExtentTextField.getText()) == 0) {
            mainController.showMessage("invalid mission extent!", Alert.AlertType.ERROR, "Mission extent need to be greater than 0!");
            return false;
        }
        if(!isValidWordInMachine(encryptTextArea.getText())){
            mainController.showMessage("Not in abc", Alert.AlertType.ERROR, "you need to choose words from the abc!");
            return false;
        }
        return true;
    }

    private boolean isValidWordInMachine(String text) {
        return mainController.isValidWordInMachine(text);
    }

    public boolean isValidTextEncrypt(String text) {
        Set<String> words=mainController.getDecipher().getDictionary().getWords();
        String excludeChars=mainController.getDecipher().getDictionary().getExcludeChars();
        text=text.replace(excludeChars,"");
        for (int i = 0; i < excludeChars.length(); i++) {
            text=text.replace(Character.toString(excludeChars.charAt(i)),"");
        }

        String[] textArr=text.split(" ");
        for (String tx:textArr) {
            if(!words.contains(tx))
                return false;
        }
        return true;
    }
    @FXML
    void clickStopButtonAction(ActionEvent event) {
        if(DM!=null){
            new Thread(() -> {
                try {
                    DM.stopDMWork();
                    double end = System.nanoTime();
                    durationTimeOfBruteForce = end - startTimeOfBruteForce;
                    finalTimeTextArea.setText(String.valueOf(durationTimeOfBruteForce));
                } catch (InterruptedException e) {
                    mainController.showMessage("Error!", Alert.AlertType.ERROR, e.getMessage());
                }
            }).start();
        }

    }
    public void setMainController(TabsController appController) {
        this.mainController = appController;
    }

    public void setAgentsSliderValue() {
        agentsSlider.setMin(2);
        agentsSlider.setMax(mainController.getDecipher().getAgents());
    }

    public void setEncryptText(MultipleSelectionModel<String> selectionModel) {
        if (firstTimeOfChosenWordFromDictionary) {
            encryptTextArea.setText(selectionModel.getSelectedItem());
            firstTimeOfChosenWordFromDictionary=false;
        }
        else {
            encryptTextArea.setText(encryptTextArea.getText() + " " + selectionModel.getSelectedItem());
        }
    }
}
