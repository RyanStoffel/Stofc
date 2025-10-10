import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

// main entry point for the Stof Compiler
void main() {
    File sourceCode = new File("stof/main.stof"); // grab the source code
    StringBuilder fileString = new StringBuilder(); // initialize a string builder to store the .stof file as a string

    // go through each line of the .stof file and append it to the string
    try (Scanner reader = new Scanner(sourceCode)) {
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            fileString.append(data).append("\n");
        }
    } catch (FileNotFoundException e) {
        System.out.println("An error has occurred: " + e.getMessage());
        return;
    }

    // tokenize the source code. this assigns a keyword for every single word within the original source code
    Tokenizer tokenizer = new Tokenizer(fileString.toString());
    Vector<Tokenizer.Token> tokens = tokenizer.tokenize();

    // parse the tokens returned from the Tokenizer class
    Parser parser = new Parser(tokens);
    try {
        ParseTree.ProgramNode program = parser.parse(); // contains the entire Stof program

        // TODO: delete this, this is for testing purposes only
        // write parseTree to a txt file for debugging purposes
        String sourceFileName = sourceCode.getName().replace(".stof", "");
        String outputPath = "stof/" + sourceFileName + "_parse_tree.txt";
        parser.writeParseTreeToFile(program, outputPath);
    } catch (RuntimeException e) {
        System.out.println("Parse error: " + e.getMessage());
        e.printStackTrace();
    }
}