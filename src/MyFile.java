import java.io.*;
import java.util.*;

public class MyFile {
    public String name;
    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<ArrayList<String>> tokens = new ArrayList<>();
    private ArrayList<ArrayList<String>> parsed = new ArrayList<>();

    // Constructor for the MyFile Class
    public MyFile(String fileName) {
        this.name = fileName;
        readFile();
        tokenize();
        fullParser();
    }

    // Reads file character by character
    private void readFile() {
        StringBuilder sentence = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(this.name))) {
            int c = 0;
            int dotCounter = 0;
            boolean isQuote = false;
            while ((c = br.read()) != -1) {
                char chr = (char) c;
                // Checks that chr is not a sentence delimiter
                if (!isSentenceDelimiter(chr)) {
                    if (chr == '"') {
                        isQuote = !isQuote;
                    }
                    if (isQuote) {
                        continue;
                    }
                    if (chr != '\n' && chr != '.') {
                        // Checks for ellipsis, appends them to sentence straightly
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

    // Checks if character is a sentence delimiter
    private boolean isSentenceDelimiter(char chr) {
        Set<Character> delimiters = new HashSet<Character>();
        delimiters.add('!');
        delimiters.add('?');
        return delimiters.contains(chr);
    }

    // Tokenizes sentences in the file
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

    // Parses a full sentence
    private void fullParser() {
        StringBuilder token = new StringBuilder();
        ArrayList<String> sentence = new ArrayList<>();
        int dotCounter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(this.name))) {
            int c = 0;
            while ((c = br.read()) != -1) {
                char chr = (char) c;
                // Checks if character is a part of token for parsing
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

    // Checks if character is a part of token for parsing, returns true if so
    private boolean checkCharForParsing(char chr) {
        if (Character.isLetter(chr) || chr == '-') {
            return true;
        } else {
            return false;
        }
    }

//    public void printSentences() {
//        for (int i = 0; i < this.sentences.size(); ++i) {
//            System.out.println("Sentence " + (i + 1) + ": " + this.sentences.get(i));
//        }
//    }
//
//    public void printTokens() {
//        for (int i = 0; i < this.tokens.size(); ++i) {
//            System.out.println("Sentence " + (i + 1) + ": " + this.tokens.get(i));
//        }
//    }

    // Getter for tokens
    public ArrayList<ArrayList<String>> getTokens() {
        return tokens;
    }

    // Getter for parsed
    public ArrayList<ArrayList<String>> getParsed() {
        return parsed;
    }
}