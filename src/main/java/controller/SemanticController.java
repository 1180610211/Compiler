package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SemanticController implements Initializable {
    @FXML
    private TextArea instructions;

    @FXML
    private TextArea quadruples;

    @FXML
    private TextArea errorInfo;


    public void setErrorInfo(List<String> errorList) {
        StringBuilder sb = new StringBuilder();
        for (String e : errorList) {
            sb.append(e);
            sb.append('\n');
        }
        errorInfo.setText(sb.toString());
    }

    public void setInstructions(List<String> instructionList) {
        StringBuilder sb = new StringBuilder();
        for (String e : instructionList) {
            sb.append(e);
            sb.append('\n');
        }
        instructions.setText(sb.toString());
    }

    public void setQuadruples(List<String> quadrupleList) {
        StringBuilder sb = new StringBuilder();
        for (String e : quadrupleList) {
            sb.append(e);
            sb.append('\n');
        }
        quadruples.setText(sb.toString());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
