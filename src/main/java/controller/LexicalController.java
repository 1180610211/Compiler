package controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import lexer.Token;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class LexicalController implements Initializable {

    private ArrayList<Token> tokenList;

    @FXML
    private TableView tableview;

    @FXML
    private TableColumn lineNum;

    @FXML
    private TableColumn tokenClass;

    @FXML
    private TableColumn attributeValue;

    @FXML
    private TextArea errorInfo;

    public void setTokenList(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        ObservableList<Token> observableList = FXCollections.observableList(tokenList);

        lineNum.setCellValueFactory(new PropertyValueFactory<>("lineNumber"));
        tokenClass.setCellValueFactory(new PropertyValueFactory<>("tokenClass"));
        attributeValue.setCellValueFactory(new PropertyValueFactory<>("attribute"));

        tableview.setItems(observableList);
    }

    public void setErrorInfo(ArrayList<String> errorList) {
        StringBuilder sb = new StringBuilder();
        for (String e : errorList) {
            sb.append(e);
            sb.append('\n');
        }
        errorInfo.setText(sb.toString());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
