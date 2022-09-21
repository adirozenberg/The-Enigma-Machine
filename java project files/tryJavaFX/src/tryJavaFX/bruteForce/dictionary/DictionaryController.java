package tryJavaFX.bruteForce.dictionary;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import tryJavaFX.bruteForce.BruteForceController;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class DictionaryController implements Initializable {

    ArrayList<String> words = new ArrayList<>();
    private BruteForceController mainController;

    @FXML
    private TextField searchBar;

    @FXML
    private ListView<String> listView;
    @FXML
    private Button closeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}
    public void setWordsToDictionary(Set<String> wordsToAdd) {
        words.addAll(wordsToAdd);
        listView.getItems().addAll(words);
    }
    public void setMainController(BruteForceController mainController) {
        this.mainController = mainController;
    }

    private List<String> searchList(String searchWords, List<String> listOfStrings) {

        List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));

        return listOfStrings.stream().filter(input -> {
            return searchWordsArray.stream().allMatch(word ->
                    input.toLowerCase().contains(word.toLowerCase()));
        }).collect(Collectors.toList());
    }


    @FXML
    void onTextChangedAction(KeyEvent event) {
        listView.getItems().clear();
        listView.getItems().addAll(searchList(searchBar.getText(),words));
    }


    @FXML
    void OnCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    void onClickListViewAction(MouseEvent event){
        mainController.setEncryptText(listView.getSelectionModel());
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

}