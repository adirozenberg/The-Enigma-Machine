package tryJavaFX.header;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import tryJavaFX.app.AppController;

import java.io.File;

public class HeaderController {

    @FXML
    private TextField filePathTextField;

    @FXML
    private Button chooseFileButton;
    private AppController mainController;

    @FXML
    void clickChooseFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File f = fileChooser.showOpenDialog(null);
        if (f  != null) {
            filePathTextField.setText(f.getAbsolutePath());
            mainController.createMachineFromFile(f.getAbsolutePath());
        }
    }

    @FXML
    public void initialize() {
        filePathTextField.setDisable(true);
    }

    public void setMainController(AppController appController) {
        this.mainController = appController;
    }
}
