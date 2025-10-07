import java.util.Vector;

public class Assembler {
    public static String tokensToAssembly(Vector<Tokenizer.Token> tokens) {
        StringBuilder output = new StringBuilder();
        output.append(".global _main\n.section __TEXT,__text\n_main:\n");
        for (int i = 0; i < tokens.size(); i++) {
            Tokenizer.Token token = tokens.get(i);
            if (token.type == Tokenizer.TokenType.STOF_RETURN) {
                if (i + 1 < tokens.size() && tokens.get(i + 1).type == Tokenizer.TokenType.STOF_INT_LITERAL) {
                    if (i + 2 < tokens.size() && tokens.get(i + 2).type == Tokenizer.TokenType.STOF_SEMICOLON) {
                        output.append("    mov x0, #").append(tokens.get(i + 1).value).append("\n");
                        output.append("    mov x16, #1\n");
                        output.append("    svc #0");
                    }
                }
            }
        }
        return output.toString();
    }
}
