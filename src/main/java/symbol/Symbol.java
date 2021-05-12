package symbol;

import lexer.Token;

public class Symbol {
    private Token token;
    private String type;
    private int offset;

    public Symbol(Token token, String type, int offset) {
        this.token = token;
        this.type = type;
        this.offset = offset;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
