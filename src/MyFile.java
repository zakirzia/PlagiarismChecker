import java.io.*;
import java.util.*;

public class MyFile {
    public String name;
    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<ArrayList<String>> tokens = new ArrayList<>();
    private ArrayList<ArrayList<String>> parsed = new ArrayList<>();

    public MyFile(String fileName) {
        this.name = fileName;
        readFile();
        tokenize();
        fullParser();
    }

    private void readFile() {
        StringBuilder sentence = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(this.name))) {
            int c = 0;
            int dotCounter = 0;
            boolean isQuote = false;
            while ((c = br.read()) != -1) {
                char chr = (char) c;
                if (!isSentenceDelimiter(chr)) {
                    if (chr == '"') {
                        isQuote = !isQuote;
                        //continue;
                    }
                    if (isQuote) {
                        continue;
                    }
                    if (chr != '\n' && chr != '.') {
                        if (dotCounter == 3) {
                            sentence.append("...");
                            dotCounter = 0;
                        } else if (dotCounter > 0) {
                            this.sentences.add(sentence.toString());
                            sentence.setLength(0);
                            dotCounter = 0;
                        }
                        sentence.append(chr);
                    }
                    if (chr == '.') {
                        dotCounter += 1;
                    }
                } else {
                    this.sentences.add(sentence.toString());
                    sentence.setLength(0);
                }

            }
            if (sentence.length() > 0) {
                this.sentences.add(sentence.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean isSentenceDelimiter(char chr) {
        Set<Character> delimiters = new HashSet<Character>();
        delimiters.add('!');
        delimiters.add('?');
        return delimiters.contains(chr);
    }

    private void tokenize() {
        for (String sentence : this.sentences) {
            ArrayList<String> tokenizedSentence = new ArrayList<>();
            // Remove punctuation and whitespaces
            sentence = sentence.replaceAll("[\\p{Punct}]", "").replaceAll("\\s+", " ");
            // Convert to lowercase
            sentence = sentence.toLowerCase();

            StringTokenizer string = new StringTokenizer(sentence, " ");

            while (string.hasMoreTokens()) {
                tokenizedSentence.add(string.nextToken());
            }

            this.tokens.add(tokenizedSentence);
        }
    }

    private void fullParser() {
        StringBuilder token = new StringBuilder();
        ArrayList<String> sentence = new ArrayList<>();
        int dotCounter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(this.name))) {
            int c = 0;
            while ((c = br.read()) != -1) {
                char chr = (char) c;
                if (checkCharForParsing(chr)) {
                    token.append(chr);
                    if (dotCounter == 1) {
                        this.parsed.add(sentence);
                        sentence = new ArrayList<>();
                    }
                    dotCounter = 0;
                } else {
                    sentence.add(token.toString());
                    token.setLength(0);
                    sentence.add(String.valueOf(chr));
                    if (chr == '!' || chr == '?') {
                        this.parsed.add(sentence);
                        sentence = new ArrayList<>();
                        dotCounter = 0;
                    }
                    if (chr == '.') {
                        dotCounter += 1;
                    }
                }
            }
            if (sentence.size() > 0) {
                this.parsed.add(sentence);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean checkCharForParsing(char chr) {
        if (Character.isLetter(chr) || chr == '-') {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getSentences() {
        return sentences;
    }

    public void printSentences() {
        for (int i = 0; i < this.sentences.size(); ++i) {
            System.out.println("Sentence " + (i + 1) + ": " + this.sentences.get(i));
        }
    }

    public ArrayList<ArrayList<String>> getTokens() {
        return tokens;
    }

    public void printTokens() {
        for (int i = 0; i < this.tokens.size(); ++i) {
            System.out.println("Sentence " + (i + 1) + ": " + this.tokens.get(i));
        }
    }

    public ArrayList<ArrayList<String>> getParsed() {
        return parsed;
    }

    public static void main(String[] args) {
        MyFile new_My_My_file = new MyFile("/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test1.txt");
        new_My_My_file.printSentences();
        new_My_My_file.printTokens();
        System.out.println(new_My_My_file.parsed);
        String filename = "output.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (ArrayList<String> sentence : new_My_My_file.parsed) {
                for (String item : sentence) {
                    writer.write(item);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to file: " + e.getMessage());
        }
    }
}