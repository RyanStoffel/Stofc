import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
      System.out.println(System.getProperty("os.name"));
      File sourceCode = new File("stof/main.stof");
      StringBuilder fileString = new StringBuilder();
      try (Scanner reader = new Scanner(sourceCode)) {
        while (reader.hasNextLine()) {
            String data  = reader.nextLine();
            fileString.append(data);
        }
      } catch (FileNotFoundException e) {
        System.out.println("An error has occurred: " + e.getMessage());
      }

      Vector<Tokenizer.Token> tokens = Tokenizer.tokenize(fileString.toString()); // Tokenize the given stof sourceCode
      String assemblyFileString = Assembler.tokensToAssembly(tokens);
      System.out.println(tokens);
      System.out.println(assemblyFileString);

      try (FileOutputStream assemblyFile = new FileOutputStream("assembly/exit.s")) {
        assemblyFile.write(assemblyFileString.getBytes());
      } catch (IOException e) {
          System.out.println("An error has occurred: " + e.getMessage());
      }

      Compiler.compileAndLinkAssembly();
    }
}