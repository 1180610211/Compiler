package symbol;

import lexer.Token;

import java.util.List;

public class ArraySymbol extends Symbol {
    private List<Integer> dimensionList;
    private int baseTypeWidth;

    public ArraySymbol(Token token, String type, int offset, List<Integer> dimensionList, int baseTypeWidth) {
        super(token, type, offset);
        this.dimensionList = dimensionList;
        this.baseTypeWidth = baseTypeWidth;
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
}
