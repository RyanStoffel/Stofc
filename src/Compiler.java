import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Compiler {
    public static void runCommand(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }

        int code = p.waitFor();
        if (code != 0) {
            throw new IOException("Process returned non-zero exit code: " + code);
        }
    }

    public static void compileAndLinkAssembly() {
        try {
            runCommand("clang", "-c", "assembly/exit.s", "-o", "assembly/exit.o");
            runCommand("clang", "assembly/exit.o", "-o", "assembly/exit");
            Process process = new ProcessBuilder("./assembly/exit").start();
            int exitCode = process.waitFor();
            System.out.println("Assembly program exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.out.println("An error has occurred: " + e.getMessage());
        }
    }
}
