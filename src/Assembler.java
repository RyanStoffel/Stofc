import java.util.Objects;
import java.util.Vector;

public class Assembler {
    public static String tokensToAssembly(Vector<Tokenizer.Token> tokens) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token t = tokens.get(i);
            if (t.type == Tokenizer.TokenType.STOF_EXIT) {
                if (i + 1 < tokens.size() && tokens.get(i + 1).type == Tokenizer.TokenType.STOF_INT_LITERAL) {
                    if (i + 2 < tokens.size() && tokens.get(i + 2).type == Tokenizer.TokenType.STOF_SEMICOLON) {
                        output.append("    mov x0, #").append(tokens.get(i + 1).value).append("\n");
                        output.append("    mov x16, #1\n");
                        output.append("    svc #0");
                    }
                }
            }
            if (t.type == Tokenizer.TokenType.STOF_IDENTIFIER && tokens.get(i + 1).type == Tokenizer.TokenType.STOF_OPEN_PARENTHESIS && t.value.equals("main")) {
                output.append(".global _main\n.section __TEXT,__text\n");
                output.append("_").append(t.value).append(":\n");
            }

            if (t.type == Tokenizer.TokenType.STOF_IDENTIFIER && tokens.get(i + 1).type == Tokenizer.TokenType.STOF_OPEN_PARENTHESIS && !Objects.equals(t.value, "main")) {
                output.append("\n").append(t.value).append(":\n");
            }
        }
        return output.toString();
    }
}
