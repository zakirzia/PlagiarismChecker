import java.io.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static LinkedHashMap<String, Integer> sortMap(HashMap<String, Integer> wordFreq) {
        LinkedHashMap<String, Integer> sortedMap = wordFreq.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return sortedMap;
    }

    private static LinkedHashMap<String, Integer> phraseMatchResultPerFile(Checker checker, String fileName, ArrayList<String> files) {
        HashMap<String, Integer> temp_res = new HashMap<>();

        for (String file : files) {
            if (file != fileName) {
                HashMap<Pair, ArrayList<ArrayList<String>>> res = checker.phraseMatching(fileName, file);
                int matchPercentage = checker.getMatchedPhrasesStatistics(res, file);
                String key = fileName + " vs " + file;
                temp_res.put(key, matchPercentage);
            }
        }

        LinkedHashMap<String, Integer> final_res = sortMap(temp_res);
        return final_res;
    }

    public static void HTMLPageForWordFrequency(LinkedHashMap<String, Integer> sortedWordFrequency1, LinkedHashMap<String, Integer> sortedWordFrequency2, String file1, String file2) {
        try {
            String filename1 = new File(file1).getName();
            String filename2 = new File(file2).getName();

            String outputFileName = filename1 + "_" + filename2 + "_wordFrequency.html";

            PrintWriter writer = new PrintWriter(new FileOutputStream(outputFileName));

            writer.println("<html>");
            writer.println("<head><title>Word Frequency</title></head>");
            writer.println("<body>");
            writer.println("<h1>Word Frequency: " + filename1 + " and " + filename2 + "</h1>");
            writer.println("<table border='1'>");
            writer.println("<tr><th>Word</th><th>Frequency in " + filename1 + "</th><th>Frequency in " + filename2 + "</th></tr>");

            for (String word : sortedWordFrequency1.keySet()) {
                writer.println("<tr>");
                writer.println("<td>" + word + "</td>");
                writer.println("<td>" + sortedWordFrequency1.get(word) + "</td>");
                writer.println("<td>" + sortedWordFrequency2.getOrDefault(word, 0) + "</td>");
                writer.println("</tr>");
            }

            for (String word : sortedWordFrequency2.keySet()) {
                if (!sortedWordFrequency1.containsKey(word)) {
                    writer.println("<tr>");
                    writer.println("<td>" + word + "</td>");
                    writer.println("<td>" + sortedWordFrequency1.getOrDefault(word, 0) + "</td>");
                    writer.println("<td>" + sortedWordFrequency2.get(word) + "</td>");
                    writer.println("</tr>");
                }
            }

            writer.println("</table>");
            writer.println("</body></html>");

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<>(Arrays.asList("/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test1.txt", "/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test2.txt", "/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test3.txt"));
        Checker checker = new Checker(files, 3);

        for (String file : files) {
            System.out.println("For file " + file);
            LinkedHashMap<String, Integer> res = phraseMatchResultPerFile(checker, file, files);
            System.out.println(res);
            System.out.println("\n");
        }

        for (int i = 0; i < files.size(); i++) {
            for (int j = i + 1; j < files.size(); j++) {
                ArrayList<HashMap<String, Integer>> frequencies = checker.compareWordFrequency(files.get(i), files.get(j));
                LinkedHashMap<String, Integer> sortedFreq1 = sortMap(frequencies.get(0));
                LinkedHashMap<String, Integer> sortedFreq2 = sortMap(frequencies.get(1));

                HTMLPageForWordFrequency(sortedFreq1, sortedFreq2, files.get(i), files.get(j));
            }
        }
    }
}