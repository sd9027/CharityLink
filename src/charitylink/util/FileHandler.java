package charitylink.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static void writeLines(String filePath, List<String> lines, boolean append) {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, append))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[FileHandler] Write error: " + e.getMessage());
        }
    }

    public static void appendLine(String filePath, String line) {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("[FileHandler] Append error: " + e.getMessage());
        }
    }

    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("[FileHandler] Read error: " + e.getMessage());
        }
        return lines;
    }

    public static void overwrite(String filePath, List<String> lines) {
        writeLines(filePath, lines, false);
    }
}
