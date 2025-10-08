import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

void main(){
    System.out.println(System.getProperty("os.name"));

    File sourceCode = new File("stof/main.stof");
    StringBuilder fileString = new StringBuilder();

    try (Scanner reader = new Scanner(sourceCode)) {
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            fileString.append(data).append("\n"); // preserve newlines
        }
    } catch (FileNotFoundException e) {
        System.out.println("An error has occurred: " + e.getMessage());
        return;
    }

    new Tokenizer(fileString.toString());

    Tokenizer tokenizer = new Tokenizer(fileString.toString());
    Vector<Tokenizer.Token> tokens = tokenizer.tokenize();
    for (Tokenizer.Token token : tokens) {
        System.out.print(token + " ");
    }
    System.out.println();
}
