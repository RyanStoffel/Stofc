import java.util.Vector;

public class Tokenizer {
    ///////////////////////
    /// UTILITY CLASSES ///
    ///////////////////////
    public enum TokenType {
        // variable types
        STOF_INT,
        STOF_VAR,
        STOF_LET,
        STOF_BOOLEAN,

        // exit and return statements
        STOF_EXIT,
        STOF_RETURN,

        // operators
        STOF_PLUS,
        STOF_MINUS,
        STOF_MULTIPLY,
        STOF_DIVIDE,
        STOF_MODULO,
        STOF_EQUALS,
        STOF_GREATER_THAN,
        STOF_LESS_THAN,
        STOF_GREATER_THAN_OR_EQUAL,
        STOF_LESS_THAN_OR_EQUAL,

        // basic syntax
        STOF_OPEN_PARENTHESIS,
        STOF_CLOSE_PARENTHESIS,
        STOF_OPEN_CURLY_BRACKET,
        STOF_CLOSE_CURLY_BRACKET,
        STOF_OPEN_SQUARE_BRACKET,
        STOF_CLOSE_SQUARE_BRACKET,
        STOF_SEMICOLON,

        // class
        STOF_CLASS,

        // conditional statements
        STOF_IF,
        STOF_ELIF,
        STOF_ELSE,
        STOF_SWITCH,
        STOF_CASE,

        // loops
        STOF_FOR,
        STOF_WHILE,

        // numbers and variable names
        STOF_INT_LITERAL,
        STOF_IDENTIFIER,
    }

    public static class Token {
        TokenType type;
        String value;
        public Token(TokenType type) {
            this.type = type;
        }
        public Token(TokenType type, String[] split) {
            this.type = type;
            this.value = split[0];
        } // Constructor for TokenType that doesn't require a value.
        public Token(TokenType type,  String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return switch (type) {
                // variable types
                case STOF_INT -> "int";
                case STOF_VAR -> "var";
                case STOF_LET -> "let";
                case STOF_BOOLEAN -> "boolean";

                // exit and return statements
                case STOF_EXIT -> "exit";
                case STOF_RETURN -> "return";

                // operators
                case STOF_PLUS -> "+";
                case STOF_MINUS -> "-";
                case STOF_MULTIPLY -> "*";
                case STOF_DIVIDE -> "/";
                case STOF_MODULO -> "%";
                case STOF_EQUALS -> "=";
                case STOF_GREATER_THAN -> ">";
                case STOF_LESS_THAN -> "<";
                case STOF_GREATER_THAN_OR_EQUAL -> ">=";
                case STOF_LESS_THAN_OR_EQUAL -> "<=";

                // basic syntax
                case STOF_OPEN_PARENTHESIS -> "(";
                case STOF_CLOSE_PARENTHESIS -> ")";
                case STOF_OPEN_CURLY_BRACKET -> "{";
                case STOF_CLOSE_CURLY_BRACKET -> "}";
                case STOF_OPEN_SQUARE_BRACKET -> "[";
                case STOF_CLOSE_SQUARE_BRACKET -> "]";
                case STOF_SEMICOLON -> ";";

                // class
                case STOF_CLASS -> "class";

                // conditional statements
                case STOF_IF -> "if";
                case STOF_ELIF -> "elif";
                case STOF_ELSE -> "else";
                case STOF_SWITCH -> "switch";
                case STOF_CASE -> "case";

                // loops
                case STOF_FOR -> "for";
                case STOF_WHILE -> "while";

                // numbers and variable names
                case STOF_INT_LITERAL -> value;
                case STOF_IDENTIFIER -> "identifier";
            };
        }
    }

    /// /////////////////
    /// TOKENIZE FILE ///
    /////////////////////
    public static Vector<Token> tokenize(String file) {
        Vector<Token> tokens = new Vector<>();
        int i = 0;
        while (i < file.length()) {
            char c = file.charAt(i);

            // skip whitespace
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isAlphabetic(c)) { // check if the first character is a letter
                StringBuilder buffer = new StringBuilder();
                buffer.append(c);
                i++;
                while (i < file.length() && Character.isLetterOrDigit(file.charAt(i))) { //while the next character is a letter or a digit
                    buffer.append(file.charAt(i));
                    i++;
                }
                String word = buffer.toString();

                // identify keywords
                switch (buffer.toString()) {
                    // variable types
                    case "int" -> tokens.add(new Token(TokenType.STOF_INT));
                    case "let" -> tokens.add(new Token(TokenType.STOF_LET));
                    case "var" -> tokens.add(new Token(TokenType.STOF_VAR));
                    case "boolean" -> tokens.add(new Token(TokenType.STOF_BOOLEAN));

                    // return statements
                    case "exit" -> tokens.add(new Token(TokenType.STOF_EXIT));
                    case "return" -> tokens.add(new Token(TokenType.STOF_RETURN));

                    // conditional statements
                    case "if" -> tokens.add(new Token(TokenType.STOF_IF));
                    case "elif" -> tokens.add(new Token(TokenType.STOF_ELIF));
                    case "else" -> tokens.add(new Token(TokenType.STOF_ELSE));
                    case "switch" -> tokens.add(new Token(TokenType.STOF_SWITCH));
                    case "case" -> tokens.add(new Token(TokenType.STOF_CASE));

                    // loops
                    case "for" -> tokens.add(new Token(TokenType.STOF_FOR));
                    case "while" -> tokens.add(new Token(TokenType.STOF_WHILE));

                    // class
                    case "class" -> tokens.add(new Token(TokenType.STOF_CLASS));

                    // variable or class names
                    default -> tokens.add(new Token(TokenType.STOF_IDENTIFIER, word));
                }
                continue;
            }

            switch (c) {
                // basic syntax
                case '(' -> tokens.add(new Token(TokenType.STOF_OPEN_PARENTHESIS));
                case ')' -> tokens.add(new Token(TokenType.STOF_CLOSE_PARENTHESIS));
                case '{' -> tokens.add(new Token(TokenType.STOF_OPEN_CURLY_BRACKET));
                case '}' -> tokens.add(new Token(TokenType.STOF_CLOSE_CURLY_BRACKET));
                case '[' -> tokens.add(new Token(TokenType.STOF_OPEN_SQUARE_BRACKET));
                case ']' -> tokens.add(new Token(TokenType.STOF_CLOSE_SQUARE_BRACKET));
                case ';' -> tokens.add(new Token(TokenType.STOF_SEMICOLON));

                // operators
                case '=' -> tokens.add(new Token(TokenType.STOF_EQUALS));
                case '+' -> tokens.add(new Token(TokenType.STOF_PLUS));
                case '-' -> tokens.add(new Token(TokenType.STOF_MINUS));
                case '*' -> tokens.add(new Token(TokenType.STOF_MULTIPLY));
                case '/' -> tokens.add(new Token(TokenType.STOF_DIVIDE));
                case '%' -> tokens.add(new Token(TokenType.STOF_MODULO));
                case '<' -> tokens.add(new Token(TokenType.STOF_LESS_THAN));
                case '>' -> tokens.add(new Token(TokenType.STOF_GREATER_THAN));

                // default
                default -> { /* handle unexpected characters later if needed */ }
            }

            if (Character.isDigit(c)) { // executes if the first character is NOT a letter, checks if it is a digit
                StringBuilder number = new StringBuilder();
                while (i < file.length() && Character.isDigit(file.charAt(i))) { // while the next character is a digit
                    number.append(file.charAt(i)); // add to the buffer
                    i++; // increment i
                }
                tokens.add(new Token(TokenType.STOF_INT_LITERAL, number.toString()));
                continue;
            }

            i++;
        }
        return tokens; // return list of tokens
    }
}
