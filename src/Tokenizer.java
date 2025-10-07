import java.util.Vector;

public class Tokenizer {
    ///////////////////////
    /// UTILITY CLASSES ///
    ///////////////////////
    public enum TokenType {
        STOF_RETURN,
        STOF_INT_LITERAL,
        STOF_SEMICOLON
    }

    public static class Token {
        TokenType type;
        String value;
        public Token(TokenType type) {
            this.type = type;
        } // Constructor for TokenType that doesn't require a value.
        public Token(TokenType type,  String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            if (value != null) {
                return "Token Type: " + type + " , Token Value: " + value;
            } else {
                return "Token Type: " + type;
            }
        }
    }

    //////////////////////
    /// HELPER METHODS ///
    //////////////////////
    public static String vectorToString(Vector<Character> buffer) {
        StringBuilder sb = new StringBuilder();
        for (Character c : buffer) {
            sb.append(c);
        }
        return sb.toString();
    }
    /// /////////////////
    /// TOKENIZE FILE ///
    /////////////////////
    public static Vector<Token> tokenize(String file) {
        Vector<Character> buffer = new Vector<>();
        Vector<Token> tokens = new Vector<>();
        for (int i = 0; i < file.length(); i++) {
            char c = file.charAt(i);
            if (Character.isAlphabetic(c)) {
                buffer.add(c);
                i++;
                while (Character.isLetterOrDigit(file.charAt(i))) {
                    buffer.add(file.charAt(i));
                    i++;
                }
                i--;
                if (vectorToString(buffer).equals("return")) {
                    Token token = new Token(TokenType.STOF_RETURN);
                    tokens.add(token);
                    buffer.clear();
                } else {
                    System.out.println("An error has occurred.");
                }
            } else if (Character.isDigit(file.charAt(i))) {
                buffer.add(c);
                i++;
                while (Character.isDigit(file.charAt(i))) {
                    buffer.add(file.charAt(i));
                    i++;
                }
                i--;
                Token token = new Token(TokenType.STOF_INT_LITERAL, vectorToString(buffer));
                tokens.add(token);
                buffer.clear();
            } else if (c == ';') {
                buffer.add(c);
                Token token = new Token(TokenType.STOF_SEMICOLON);
                tokens.add(token);
                buffer.clear();
            }
            else if (Character.isWhitespace(file.charAt(i))) {
                continue;
            } else {
                System.out.println("An error has occurred.");
            }
        }
        return tokens;
    }
}
