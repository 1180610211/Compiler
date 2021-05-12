package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import lexer.Token;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SyntaxController implements Initializable {

    @FXML
    private TextArea tree;

    @FXML
    private TextArea errorInfo;

    public void setTree(String treeString) {
        tree.setText(treeString);
    }

    public void setErrorInfo(List<String> errorList) {
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
