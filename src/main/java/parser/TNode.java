package parser;

import java.util.ArrayList;
import java.util.List;

// 分析树节点
public class TNode {
    private String symbol;
    private List<TNode> children = new ArrayList<>();
    private int lineNumber;
    private Object attribute;

    public TNode(String symbol, int lineNumber, Object attribute) {
        this.symbol = symbol;
        this.lineNumber = lineNumber;
        this.attribute = attribute;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setChildren(List<TNode> children) {
        this.children = children;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }

    public String getSymbol() {
        return symbol;
    }

    public List<TNode> getChildren() {
        return children;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Object getAttribute() {
        return attribute;
    }
}
