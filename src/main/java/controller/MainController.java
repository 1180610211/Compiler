package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lexer.Lexer;
import lexer.Token;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import parser.Parser;
import parser.TNode;
import symbol.SymbolTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private AnchorPane root;

    private CodeArea codeArea = new CodeArea();
    private String path;
    private Lexer lexer;
    private Parser parser = new Parser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

//        codeArea.replaceText(0, 0, sampleCode);
        codeArea.setEditable(false);
        codeArea.setWrapText(true);

        StackPane stackPane = new StackPane(new VirtualizedScrollPane<>(codeArea));

        root.getChildren().add(stackPane);
//        codeArea.prefHeightProperty().bind(root.prefHeightProperty());
//        codeArea.prefWidthProperty().bind(root.prefWidthProperty());
        AnchorPane.setTopAnchor(stackPane, 25.0);
        AnchorPane.setRightAnchor(stackPane, 0.0);
        AnchorPane.setLeftAnchor(stackPane, 0.0);
        AnchorPane.setBottomAnchor(stackPane, 0.0);
    }

    @FXML
    private void fileLoad() throws IOException {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();
        fc.setTitle("选择源文件");
        fc.setInitialDirectory(new File("src/main/resources/test"));
        File file = fc.showOpenDialog(stage);

        if (file != null) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            sb.deleteCharAt(sb.length() - 1);
            codeArea.clear();
            codeArea.replaceText(0, 0, sb.toString());
            codeArea.setStyle("-fx-font-family: consolas; -fx-font-size: 12pt;");

            path = file.getAbsolutePath();
            System.out.println(file.getAbsolutePath());
        }
        System.out.println("file load");

        lexer = new Lexer(path);
        ArrayList<Token> tokenList = lexer.getTokenList();
        TNode root = parser.analysis(tokenList);
        parser.printParseTree(root, 0);
    }

    @FXML
    private void compile1() throws IOException {
        ArrayList<Token> tokenList = lexer.getTokenList();
        ArrayList<String> errorList = lexer.getErrorList();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lexical.fxml"));
        Parent root = loader.load();
        LexicalController controller = loader.getController();
        controller.setTokenList(tokenList);
        controller.setErrorInfo(errorList);

        stage.setTitle("Mini Compiler 词法分析 -- Designed By Gao Weize 1180610211");
        stage.getIcons().add(new Image("/img/logo.jpg"));

        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void compile2() throws IOException {
        String treeString = parser.getTree();
        List<String> errorList = parser.getErrorList();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/syntax.fxml"));
        Parent root = loader.load();
        SyntaxController controller = loader.getController();
        if (errorList.size() == 0) {
            controller.setTree(treeString);
        } else {
            controller.setErrorInfo(errorList);
        }

        stage.setTitle("Mini Compiler 语法分析 -- Designed By Gao Weize 1180610211");
        stage.getIcons().add(new Image("/img/logo.jpg"));

        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void compile3() throws IOException {
        List<String> instructionList = parser.getInstructionList();
        List<String> quadrupleList = parser.getQuadrupleList();
        List<String> errorList = parser.getErrorList();
        SymbolTable symbolTable = parser.getSymbolTable();

        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/semantic.fxml"));
        Parent root = loader.load();
        SemanticController controller = loader.getController();
        if (errorList.size() == 0) {
            controller.setInstructions(instructionList);
            controller.setQuadruples(quadrupleList);

            Stage stage2 = new Stage();
            FXMLLoader loader2 = new FXMLLoader(getClass().getResource("/fxml/symbolTable.fxml"));
            Parent root2 = loader2.load();
            SymbolTableController controller2 = loader2.getController();

            controller2.setSymbolTable(symbolTable);

            stage2.setTitle("Mini Compiler 语义分析 -- Designed By Gao Weize 1180610211");
            stage2.getIcons().add(new Image("/img/logo.jpg"));

            stage2.setResizable(false);
            stage2.setScene(new Scene(root2));
            stage2.show();
        } else {
            controller.setErrorInfo(errorList);
        }

        stage.setTitle("Mini Compiler 语义分析 -- Designed By Gao Weize 1180610211");
        stage.getIcons().add(new Image("/img/logo.jpg"));

        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void About() {

    }
}
