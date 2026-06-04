package mapper;

import java.util.ArrayList;
import java.util.List;

class JSONTokenizer {
    public enum TokenType { LBRACE, RBRACE, LBRACKET, RBRACKET, COLON, COMMA, STRING, NUMBER, BOOLEAN, NULL, EOF }

    public static class Token {
        public final TokenType type;
        public final String value;
        public Token(TokenType type, String value) { this.type = type; this.value = value; }
    }

    private final String json;
    private int pos;
    private final List<Token> tokens;

    public JSONTokenizer(String json) {
        this.json = json;
        this.pos = 0;
        this.tokens = new ArrayList<>();
    }

    public List<Token> tokenize() throws JSONException {
        while (pos < json.length()) {
            char current = json.charAt(pos);
            switch (current) {
                case '{': tokens.add(new Token(TokenType.LBRACE, "{")); pos++; break;
                case '}': tokens.add(new Token(TokenType.RBRACE, "}")); pos++; break;
                case '[': tokens.add(new Token(TokenType.LBRACKET, "[")); pos++; break;
                case ']': tokens.add(new Token(TokenType.RBRACKET, "]")); pos++; break;
                case ':': tokens.add(new Token(TokenType.COLON, ":")); pos++; break;
                case ',': tokens.add(new Token(TokenType.COMMA, ",")); pos++; break;
                case '"': tokens.add(new Token(TokenType.STRING, parseString())); break;
                case ' ': case '\t': case '\n': case '\r': pos++; break;
                default:
                    if (Character.isDigit(current) || current == '-') {
                        tokens.add(new Token(TokenType.NUMBER, parseNumber()));
                    } else if (Character.isLetter(current)) {
                        String word = parseWord();
                        if (word.equals("true") || word.equals("false")) {
                            tokens.add(new Token(TokenType.BOOLEAN, word));
                        } else if (word.equals("null")) {
                            tokens.add(new Token(TokenType.NULL, word));
                        } else {
                            throw new JSONException("Unexpected word: " + word);
                        }
                    } else {
                        throw new JSONException("Unexpected character: " + current);
                    }
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));

        return tokens;
    }

    private String parseString() throws JSONException {
        StringBuilder sb = new StringBuilder();
        pos++;
        while (pos < json.length()) {
            char current = json.charAt(pos);
            if (current == '"') { pos++; return sb.toString(); }
            else if (current == '\\') {
                pos++;
                if (pos >= json.length()) throw new JSONException("Unterminated string");
                char escaped = json.charAt(pos);
                switch (escaped) {
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    case '/': sb.append('/'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default: throw new JSONException("Invalid escape: \\" + escaped);
                }
                pos++;
            } else { sb.append(current); pos++; }
        }
        throw new JSONException("Unterminated string");
    }

    private String parseNumber() {
        int start = pos;
        if (json.charAt(pos) == '-') pos++;
        while (pos < json.length() && Character.isDigit(json.charAt(pos))) pos++;
        if (pos < json.length() && json.charAt(pos) == '.') {
            do pos++;
            while (pos < json.length() && Character.isDigit(json.charAt(pos)));
        }
        if (pos < json.length() && (json.charAt(pos) == 'e' || json.charAt(pos) == 'E')) {
            pos++;
            if (pos < json.length() && (json.charAt(pos) == '+' || json.charAt(pos) == '-')) pos++;
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) pos++;
        }
        return json.substring(start, pos);
    }

    private String parseWord() {
        int start = pos;
        while (pos < json.length() && Character.isLetter(json.charAt(pos))) pos++;
        return json.substring(start, pos);
    }
}