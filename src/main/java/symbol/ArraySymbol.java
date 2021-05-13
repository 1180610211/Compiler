package symbol;

import lexer.Token;

import java.util.List;
import java.util.Map;

public class ArraySymbol extends Symbol {
    private List<Integer> dimensionList;
    private int baseTypeWidth;
    private String baseType;

    public ArraySymbol(Token token, String type, int offset, List<Integer> dimensionList, int baseTypeWidth, String baseType) {
        super(token, type, offset);
        this.dimensionList = dimensionList;
        this.baseTypeWidth = baseTypeWidth;
        this.baseType = baseType;
    }

    public List<Integer> getDimensionList() {
        return dimensionList;
    }

    public void setDimensionList(List<Integer> dimensionList) {
        this.dimensionList = dimensionList;
    }

    public int getWidth(int num) {
        int width = baseTypeWidth;
        for (int i = num; i < dimensionList.size(); i++) {
            width *= dimensionList.get(i);
        }
        return width;
    }

    public String getType(int num) {
        StringBuilder sb = new StringBuilder();
        if (num == dimensionList.size()) {
            return baseType;
        } else {
            sb.append(baseType);
            for (int i = num; i < dimensionList.size(); i++) {
                sb.append("[");
                sb.append(dimensionList.get(i));
                sb.append("]");
            }
            return sb.toString();
        }
    }

    @Override
    public String print(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("  ");
        }
        sb.append("Array Symbol: " + getToken().getAttribute() + " " + getType() + " " + getOffset());
        return sb.toString();
    }
}
