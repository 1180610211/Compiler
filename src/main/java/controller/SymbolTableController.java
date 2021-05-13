package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import symbol.Symbol;
import symbol.SymbolTable;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class SymbolTableController implements Initializable {
    @FXML
    private TextArea symbolTable;

    public void setSymbolTable(SymbolTable s) {
        StringBuilder sb = new StringBuilder();
        Map<String, Symbol> m = s.getTable();
        for (String key : m.keySet()) {
            sb.append(m.get(key).print(0));
            sb.append("\n");
        }
        symbolTable.setText(sb.toString());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
