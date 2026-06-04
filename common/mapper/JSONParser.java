package mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class JSONParser {
    private final List<JSONTokenizer.Token> tokens;
    private int current;

    public JSONParser(List<JSONTokenizer.Token> tokens) {
        this.tokens = tokens;
        this.current = 0;
    }

    public Object parse() throws JSONException {
        JSONTokenizer.Token token = peek();
        switch (token.type) {
            case LBRACE: return parseObject();
            case LBRACKET: return parseArray();
            case STRING: consume(JSONTokenizer.TokenType.STRING); return token.value;
            case NUMBER: consume(JSONTokenizer.TokenType.NUMBER); return parseNumber(token.value);
            case BOOLEAN: consume(JSONTokenizer.TokenType.BOOLEAN); return Boolean.parseBoolean(token.value);
            case NULL: consume(JSONTokenizer.TokenType.NULL); return null;
            default: throw new JSONException("Unexpected token: " + token.type);
        }
    }

    private Map<String, Object> parseObject() throws JSONException {
        Map<String, Object> object = new LinkedHashMap<>();
        consume(JSONTokenizer.TokenType.LBRACE);
        if (!check(JSONTokenizer.TokenType.RBRACE)) {
            do {
                JSONTokenizer.Token keyToken = consume(JSONTokenizer.TokenType.STRING);
                String key = keyToken.value;
                consume(JSONTokenizer.TokenType.COLON);
                Object value = parse();
                object.put(key, value);
            } while (match());
        }
        consume(JSONTokenizer.TokenType.RBRACE);
        return object;
    }

    private List<Object> parseArray() throws JSONException {
        List<Object> array = new ArrayList<>();
        consume(JSONTokenizer.TokenType.LBRACKET);
        if (!check(JSONTokenizer.TokenType.RBRACKET)) {
            do {
                array.add(parse());
            } while (match());
        }
        consume(JSONTokenizer.TokenType.RBRACKET);
        return array;
    }

    private Object parseNumber(String value) {
        if (value.contains(".") || value.contains("e") || value.contains("E")) {
            return Double.parseDouble(value);
        } else {
            try { return Integer.parseInt(value); }
            catch (NumberFormatException e) { return Long.parseLong(value); }
        }
    }

    private JSONTokenizer.Token consume(JSONTokenizer.TokenType type) throws JSONException {
        if (check(type)) return tokens.get(current++);
        throw new JSONException("Expected " + type + " but found " + peek().type);
    }

    private boolean match() {
        if (check(JSONTokenizer.TokenType.COMMA)) { current++; return true; }
        return false;
    }

    private boolean check(JSONTokenizer.TokenType type) {
        return !isAtEnd() && peek().type == type;
    }

    private JSONTokenizer.Token peek() { return tokens.get(current); }
    private boolean isAtEnd() { return peek().type == JSONTokenizer.TokenType.EOF; }
}