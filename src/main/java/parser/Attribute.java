package parser;

import lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class Attribute {
    private Token token;
    private Map<String, Object> attributes = new HashMap<>();

    public Attribute() {}
    public Attribute(Token token) {
        this.token = token;
    }

    public void putAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

}
