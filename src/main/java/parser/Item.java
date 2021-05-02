package parser;

import lexer.Token;

import java.util.ArrayList;
import java.util.Objects;

public class Item {
    private int index;
    private int dot = 0;
    private int left;
    private ArrayList<Integer> right;
    private int lookAhead;

    public Item(int index, int dot, int left, ArrayList<Integer> right, int lookAhead) {
        this.index = index;
        this.dot = dot;
        this.left = left;
        this.right = right;
        this.lookAhead = lookAhead;
    }

    // 获得点后的文法符号
    public int getDotSymbol() {
        if (dot >= right.size()) return LR1Generator.DOLLAR;
        else return right.get(dot);
    }

    // 获得beta和a
    public ArrayList<Integer> getBetaAndA() {
        ArrayList list = new ArrayList();
        for (int i = dot + 1; i < right.size(); i++) {
            list.add(right.get(i));
        }
        list.add(lookAhead);
        return list;
    }

    // 点向后移动一位
    public Item dotAdvance() {
        return new Item(index, (dot + 1) < right.size() ? dot + 1 : right.size(), left, right, lookAhead);
    }

    // 是否为归约项目
    public boolean isReducible() {
        return dot == right.size();
    }

    public int getIndex() {
        return index;
    }

    public int getDot() {
        return dot;
    }

    public int getLeft() {
        return left;
    }

    public ArrayList<Integer> getRight() {
        return right;
    }

    public int getLookAhead() {
        return lookAhead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return index == item.index && dot == item.dot && left == item.left && lookAhead == item.lookAhead && Objects.equals(right, item.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, dot, left, right, lookAhead);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(index);
        sb.append("{");
        sb.append(LR1Generator.NON_TERMINAL_NUMBER_2_STRING.get(left));
        sb.append("->");
        for (int i = 0; i < right.size(); i++) {
            if (i == dot) {
                sb.append(".");
            }
            int x = right.get(i);
            if (x == LR1Generator.DOLLAR) sb.append("$");
            else if (x < 60) sb.append(Token.TOKEN_NUMBER_2_STRING.get(x));
            else sb.append(LR1Generator.NON_TERMINAL_NUMBER_2_STRING.get(x));
        }
        if (dot == right.size()) {
            sb.append(".");
        }
        sb.append(",");
        if (lookAhead == LR1Generator.DOLLAR) sb.append("$");
        else sb.append(Token.TOKEN_NUMBER_2_STRING.get(lookAhead));
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) {

    }
}
