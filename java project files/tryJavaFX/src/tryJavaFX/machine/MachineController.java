package tryJavaFX.machine;

import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.util.Pair;
import tryJavaFX.properties.CurrentMachineConfigurationProperty;
import tryJavaFX.properties.MachineDetailsProperty;
import tryJavaFX.tabs.TabsController;

import java.util.LinkedList;

public class MachineController {

    @FXML
    private TextArea machineDetailsTextArea;

    @FXML
    private Button setCodeButton;

    @FXML
    private Button randomCodeButton;
    @FXML
    private TextArea currentMachineConfigurationTextArea;

    @FXML
    private TextField rotorsOrderTextField;

    @FXML
    private TextField rotorsStartPositionTextField;

    @FXML
    private TextField plugsTextField;

    @FXML
    private ImageView imageView;

    @FXML
    private ComboBox<String> reflectorComboBox;
    private TabsController mainController;
    final CurrentMachineConfigurationProperty currentConfigurationMachineProperty = new CurrentMachineConfigurationProperty();
    final MachineDetailsProperty machineDetailsProperty=new MachineDetailsProperty();
    @FXML
    public void initialize() {
        reflectorComboBox.getItems().add("I");
        reflectorComboBox.getItems().add("II");
        reflectorComboBox.getItems().add("III");
        reflectorComboBox.getItems().add("IV");
        reflectorComboBox.getItems().add("V");


        currentMachineConfigurationTextArea.textProperty().bind(new StringBinding() {
            {
                bind(currentConfigurationMachineProperty.valueProperty());
            }

            @Override
            protected String computeValue() {
                String value = currentConfigurationMachineProperty.getValue();
                return value;//mainController.getCurrentConfiguration();
            }
        });
        machineDetailsTextArea.textProperty().bind(new StringBinding() {
            {
                bind(machineDetailsProperty.valueProperty());
            }

            @Override
            protected String computeValue() {
                String value = machineDetailsProperty.getValue();
                return value;
            }
        });
    }


    @FXML
    void clickRandomCodeButtonAction(ActionEvent event) {
        mainController.getRandomConfiguration();
        if(mainController.getErrorMessages().size()>0){
          mainController.showMessage("Error!", Alert.AlertType.ERROR,mainController.getErrorMessages().toString());
          mainController.getErrorMessages().clear();
        }
        mainController.showMessage("Success!", Alert.AlertType.CONFIRMATION, "Your random configuration successfully loaded!");
        machineDetailsProperty.setValue(mainController.getMachineSpecification());
        mainController.setCurrentMachineConfigurationProperty();
        mainController.afterMachineComponentConfigured();
    }

    public MachineDetailsProperty getMachineDetailsProperty() {
        return machineDetailsProperty;
    }

    @FXML
    void clickSetCodeButtonAction(ActionEvent event) {

        LinkedList<Integer> rotorsOrder = mainController.getOrderOfRotors(rotorsOrderTextField.getText());
        String positions = null;

        if(rotorsOrder!=null) {
            positions = mainController.getPositionsFromUser(rotorsStartPositionTextField.getText(), rotorsOrder.size());
            rotorsOrder = mainController.reverseArr(rotorsOrder);
        }

        if(positions!=null) {
            positions = mainController.reverseStr(positions);
        }
        int selectedReflector=0;
        if(reflectorComboBox!=null &&reflectorComboBox.getValue()!=null &&!reflectorComboBox.getValue().isEmpty())
        {
            selectedReflector = mainController.convertRomanToIntAndValidate(reflectorComboBox.getValue());
        }else {
            mainController.getErrorMessages().add("You need to choose a reflector!");
        }

        String plugsStr= mainController.getPlugsFromUser(plugsTextField.getText());
        LinkedList<Pair<Character,Character>> plugs=null;

        if(plugsStr!=null) {
            if(plugsStr.isEmpty()) {
                plugs = null;
            }
            else {
                plugs = mainController.getPlugsFromStrPlugs(plugsStr);
            }
        }

        if (mainController.getErrorMessages().size() > 0) {
            mainController.showMessage("Invalid input", Alert.AlertType.ERROR, mainController.getErrorMessages().toString());
            mainController.getErrorMessages().clear();
            return;
        }

        mainController.initMachine(selectedReflector,rotorsOrder,positions,plugs,false);
        mainController.showMessage("Success!", Alert.AlertType.CONFIRMATION, "Your configuration successfully loaded!");
        mainController.setCurrentMachineConfigurationProperty();
        machineDetailsProperty.setValue(mainController.getMachineSpecification());
        mainController.afterMachineComponentConfigured();
    }


    public CurrentMachineConfigurationProperty getCurrentConfigurationMachineProperty() {
        return currentConfigurationMachineProperty;
    }

    public void setMainController(TabsController appController) {
        this.mainController = appController;
    }

}
