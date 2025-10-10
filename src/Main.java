import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

void main() {
    System.out.println("OS: " + System.getProperty("os.name"));
    System.out.println();

    File sourceCode = new File("stof/main.stof");
    StringBuilder fileString = new StringBuilder();

    try (Scanner reader = new Scanner(sourceCode)) {
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            fileString.append(data).append("\n");
        }
    } catch (FileNotFoundException e) {
        System.out.println("An error has occurred: " + e.getMessage());
        return;
    }

    // Step 1: Tokenize
    System.out.println("========== TOKENIZATION ==========");
    Tokenizer tokenizer = new Tokenizer(fileString.toString());
    Vector<Tokenizer.Token> tokens = tokenizer.tokenize();

    System.out.println("Tokens:");
    for (Tokenizer.Token token : tokens) {
        System.out.print(token + " ");
    }
    System.out.println();

    // Step 2: Parse
    System.out.println("========== PARSING ==========");
    Parser parser = new Parser(tokens);

    try {
        ParseTree.ProgramNode program = parser.parse();

        // Step 3: Write parse tree to file
        String sourceFileName = sourceCode.getName().replace(".stof", "");
        String outputPath = "stof/" + sourceFileName + "_parse_tree.txt";
        parser.writeParseTreeToFile(program, outputPath);
        System.out.println("Parse tree written to: " + outputPath);

    } catch (RuntimeException e) {
        System.out.println("Parse error: " + e.getMessage());
        e.printStackTrace();
    }
}