import java.io.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.awt.Desktop;

public class Main {
    // Sorts HashMap in descending order and returns sortedMap (LinkedHashMap)
    private static LinkedHashMap<String, Integer> sortMap(HashMap<String, Integer> wordFreq) {
        LinkedHashMap<String, Integer> sortedMap = wordFreq.entrySet()
                // Converts Set to Stream
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                // Converts Stream to Map, puts elements into Map
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        // If the same key occurred more than once, it will keep the oldest one
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return sortedMap;
    }

    // Gets Phrase Matching per file
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

        // Sorts the temp_res in descending order and returns final_Res (LinkedHashMap)
        LinkedHashMap<String, Integer> final_res = sortMap(temp_res);
        return final_res;
    }

    // Creates HTML for Word Frequency for pairs of the files
    public static void HTMLForWordFrequency(LinkedHashMap<String, Integer> sortedWordFrequency1, LinkedHashMap<String, Integer> sortedWordFrequency2, String file1, String file2, LinkedHashMap<String, Integer> phraseMatchRes, Checker checker) {
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

            // Creates rows in the Word Frequency table for each word from the first map
            for (String word : sortedWordFrequency1.keySet()) {
                writer.println("<tr>");
                writer.println("<td>" + word + "</td>");
                // Frequency for the word from the first map
                writer.println("<td>" + sortedWordFrequency1.get(word) + "</td>");
                // Frequency for the word from the second map, 0 if this word is not present in the second map
                writer.println("<td>" + sortedWordFrequency2.getOrDefault(word, 0) + "</td>");
                writer.println("</tr>");
            }

            // Creates rows in the Word Frequency table for each word from the second map
            for (String word : sortedWordFrequency2.keySet()) {
                // If the word is not present in the first map
                if (!sortedWordFrequency1.containsKey(word)) {
                    writer.println("<tr>");
                    writer.println("<td>" + word + "</td>");
                    // Frequency for the word from the first map, 0 if this word is not present in the first map
                    writer.println("<td>" + sortedWordFrequency1.getOrDefault(word, 0) + "</td>");
                    // Frequency for the word from the second map
                    writer.println("<td>" + sortedWordFrequency2.get(word) + "</td>");
                    writer.println("</tr>");
                }
            }

            // Table for Phrase Matching pair of the files
            writer.println("</table>");

            writer.println("<h1>Phrase Matching: " + filename1 + " and " + filename2 + "</h1>");
            writer.println("<table border='1'>");
            writer.println("<tr><th>Pair</th><th>Phrase Match (%)</th></tr>");

            String matchPercentage = Integer.toString(phraseMatchRes.getOrDefault(file1 + " vs " + file2, 0));
            writer.println("<tr>");
            writer.println("<td>" + filename1 + " - " + filename2 + "</td>");
            writer.println("<td>" + matchPercentage + "</td>");
            writer.println("</tr>");

            writer.println("</table>");

            // For getHighLighting
            ArrayList<String> highlightedTexts = getHighLighting(file1, file2, checker);
            writer.println("<h1>Phrase Matches in " + filename1 + "</h1>");
            writer.println("<p>" + highlightedTexts.get(0) + "</p>");
            writer.println("<h1>Phrase Matches in " + filename2 + "</h1>");
            writer.println("<p>" + highlightedTexts.get(1) + "</p>");

            // Reference to the home page
            writer.println("<h3><a href='phraseMatching.html'>Home</a></h3>");

            writer.println("</body></html>");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Creates HTML for all Word Frequencies for all words in all the files
    public static void HTMLForAllWordFrequencies(ArrayList<String> files, Checker checker) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream("AllWordFrequencies.html"));
            writer.println("<html>");
            writer.println("<body>");
            writer.println("<h1>Word Frequencies for all of the files:</h1>");
            writer.println("<table border='1'>");

            writer.println("<tr>");
            writer.println("<th>Word</th>");
            for (String file : files) {
                String fileName = new File(file).getName();
                writer.println("<th>" + fileName + "</th>");
            }
            writer.println("</tr>");

            // HahSet to store all unique words from all the files
            HashSet<String> allWords = new HashSet<>();
            for (String file : files) {
                ArrayList<HashMap<String, Integer>> frequencies = checker.compareWordFrequency(file, file);
                HashMap<String, Integer> frequencyMap = frequencies.get(0);
                allWords.addAll(frequencyMap.keySet());
            }

            for (String word : allWords) {
                writer.println("<tr>");
                writer.println("<td>" + word + "</td>");

                for (String file : files) {
                    ArrayList<HashMap<String, Integer>> frequencies = checker.compareWordFrequency(file, file);
                    HashMap<String, Integer> frequencyMap = frequencies.get(0);
                    writer.println("<td>" + frequencyMap.getOrDefault(word, 0) + "</td>");
                }
                writer.println("</tr>");
            }

            writer.println("</table>");

            // Reference to the home page
            writer.println("<h3><a href='phraseMatching.html'>Home</a></h3>");

            writer.println("</body></html>");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Creates HTML for Phrase Matching tables
    public static void HTMLForPhraseMatching(ArrayList<String> files, Checker checker) {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream("phraseMatching.html"));

            writer.println("<html>");
            writer.println("<body>");

            for (String file : files) {
                String fileName = new File(file).getName();
                writer.println("<h1>Phrase Matching for file: " + fileName + "</h1>");
                // Gets results of Phrase Matching for the file, sorts them
                LinkedHashMap<String, Integer> res = sortMap(phraseMatchResultPerFile(checker, file, files));

                writer.println("<table border='1'>");
                writer.println("<tr><th>Pair</th><th>Phrase Match (%)</th></tr>");

                for (Map.Entry<String, Integer> entry : res.entrySet()) {
                    String otherFileName = new File(entry.getKey().split(" vs ")[1]).getName();
                    String href = fileName + "_" + otherFileName + "_wordFrequency.html";
                    writer.println("<tr>");
                    writer.println("<td><a href='" + href + "'>" + fileName + " - " + otherFileName + "</a></td>");
                    writer.println("<td>" + entry.getValue() + "</td>");
                    writer.println("</tr>");
                }
                writer.println("</table><br>");
            }

            // Table for Overall Phrase Matches
            writer.println("<h1>Overall Phrase Matches:</h1>");

            writer.println("<table border='1'>");
            writer.println("<tr><th>Pair</th><th>Phrase Match (%)</th></tr>");

            // HashMap stores overall results
            HashMap<String, Integer> overallRes = new HashMap<>();
            for (String file1 : files) {
                String fileName1 = new File(file1).getName();
                for (String file2 : files) {
                    String fileName2 = new File(file2).getName();
                    if (!file1.equals(file2)) {
                        LinkedHashMap<String, Integer> res = phraseMatchResultPerFile(checker, file1, files);
                        overallRes.put(fileName1 + " - " + fileName2, res.get(file1 + " vs " + file2));
                    }
                }
            }

            // Sorts the overall results
            LinkedHashMap<String, Integer> sortedOverallRes = sortMap(overallRes);
            for (Map.Entry<String, Integer> entry : sortedOverallRes.entrySet()) {
                String[] fileNames = entry.getKey().split(" - ");
                String href = fileNames[0] + "_" + fileNames[1] + "_wordFrequency.html";
                writer.println("<tr>");
                writer.println("<td><a href='" + href + "'>" + entry.getKey() + "</a></td>");
                writer.println("<td>" + entry.getValue() + "</td>");
                writer.println("</tr>");
            }

            writer.println("</table>");

            writer.println("<h1><a href='AllWordFrequencies.html'>Word Frequencies for all of the files</a></h1>");

            writer.println("</body></html>");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Highlights matched text for the pair of files
    public static ArrayList<String> getHighLighting(String file1Name, String file2Name, Checker checker) {
        HashMap<Pair, ArrayList<ArrayList<String>>> matches = checker.phraseMatching(file1Name, file2Name);
        // Store highlighted text for two files
        StringBuilder highlightedFile1 = new StringBuilder();
        StringBuilder highlightedFile2 = new StringBuilder();

        ArrayList<Integer> sentencesToMatch1 = new ArrayList<>();
        ArrayList<Integer> sentencesToMatch2 = new ArrayList<>();

        for (Pair pair : matches.keySet()) {
            if(!sentencesToMatch1.contains(pair.first)) {
                sentencesToMatch1.add(pair.first);
            }
            if(!sentencesToMatch2.contains(pair.second)){
                sentencesToMatch2.add(pair.second);
            }
        }

        for(Integer sentenceIdn: sentencesToMatch1){
            String sentence1 = checker.highlightMatch(file1Name, sentenceIdn, checker.collectMatches(matches,sentenceIdn,true), "<mark>", "</mark>");
            highlightedFile1.append(sentence1);
        }
        for(Integer sentenceIdn: sentencesToMatch2){
            String sentence2 = checker.highlightMatch(file2Name, sentenceIdn, checker.collectMatches(matches,sentenceIdn,false), "<mark>", "</mark>");
            highlightedFile2.append(sentence2);
        }

        ArrayList<String> highlightedTexts = new ArrayList<>();
        highlightedTexts.add(highlightedFile1.toString());
        highlightedTexts.add(highlightedFile2.toString());
        return highlightedTexts;
    }

    // Opens HTML file in the browser
    public static void openHTMLHomeFile(String filename) {
        try {
            File htmlFile = new File(filename);
            if(!Desktop.isDesktopSupported()){
                System.out.println("Desktop is not supported");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if(htmlFile.exists()) {
                desktop.browse(htmlFile.toURI());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Get the threshold from the user input
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the threshold value:");
        int threshold = scanner.nextInt();

        // Get the paths for the files from the user input
        ArrayList<String> files = new ArrayList<>();
        System.out.println("Please enter the number of files you want to check for plagiarism:");
        int numOfFiles = scanner.nextInt();
        scanner.nextLine();

        for(int i = 0; i < numOfFiles; i++){
            System.out.println("Please enter the path for file " + (i + 1) + ":");
            String filePath = scanner.nextLine();
            files.add(filePath);
        }

        Checker checker = new Checker(files, threshold);

        for (int i = 0; i < files.size(); i++) {
            for (int j = 0; j < files.size(); j++) {
                if (i != j) {
                    // Word Frequency for the pair of files
                    ArrayList<HashMap<String, Integer>> frequencies = checker.compareWordFrequency(files.get(i), files.get(j));
                    // Sorts the results
                    LinkedHashMap<String, Integer> sortedFreq1 = sortMap(frequencies.get(0));
                    LinkedHashMap<String, Integer> sortedFreq2 = sortMap(frequencies.get(1));

                    // Phrase Matching for the "first" file and all the pairs with it
                    LinkedHashMap<String, Integer> phraseMatchRes = phraseMatchResultPerFile(checker, files.get(i), files);

                    HTMLForWordFrequency(sortedFreq1, sortedFreq2, files.get(i), files.get(j), phraseMatchRes, checker);
                }
            }
        }
        HTMLForAllWordFrequencies(files, checker);
        HTMLForPhraseMatching(files, checker);
        // Opens Phrase Matching HTML
        openHTMLHomeFile("phraseMatching.html");
    }
}