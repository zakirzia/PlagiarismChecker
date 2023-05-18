import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class File {
    public String name;
    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<ArrayList<String>> tokens = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> quotedText = new ArrayList<>();

    public File(String fileName) {
        this.name = fileName;
        readFile();
        tokenize();
        fillQuotedText();
    }

    private void readFile() {
        StringBuilder sentence = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(this.name))) {
            int c = 0;
            int dotCounter = 0;
            while ((c = br.read()) != -1) {
                char chr = (char) c;
                if (!isSentenceDelimiter(chr)) {
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
            if (sentence.length()>0){
                this.sentences.add(sentence.toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private boolean isSentenceDelimiter(char chr) {
        Set<Character> delimiters = new HashSet<Character>();
        //delimiters.add('.');
        delimiters.add('!');
        delimiters.add('?');
        delimiters.add(';');
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

    private void fillQuotedText() {
        for (int i = 0; i < this.sentences.size(); ++i) {
            ArrayList<Integer> emptyList = new ArrayList<>();
            this.quotedText.add(emptyList);
            boolean isQuote = false;
            int fromTokenIndex = 0;
            int toTokenIndex = 0;
            int tokenIndex = 0;
            String cleanedSentence = this.sentences.get(i).replaceAll("[;,]", " ");
            cleanedSentence = cleanedSentence.replaceAll("\\s+", " ");
            cleanedSentence = cleanedSentence.replaceAll("\\s+-\\s+", " ");
            cleanedSentence = cleanedSentence.replaceAll("\"\\s+", "\"");
            cleanedSentence = cleanedSentence.replaceAll("\\s+\"", "\"");
            for (int c = 0; c < cleanedSentence.length(); ++c) {
                char chr = cleanedSentence.charAt(c);
                if (chr == ' ') {
                    tokenIndex += 1;
                }
                if (chr == '\"' && !isQuote) {
                    fromTokenIndex = tokenIndex;
                    isQuote = true;
                } else if (chr == '\"') {
                    toTokenIndex = tokenIndex;
                    isQuote = false;
                    this.quotedText.get(i).add(fromTokenIndex);
                    this.quotedText.get(i).add(toTokenIndex);
                }
            }
        }
    }

    private void fillQuotedTextV2(){
        Pattern pattern = Pattern.compile("\"(.*?)\"");
        for (int i = 0; i < this.sentences.size(); ++i){
            ArrayList<Integer> emptyList = new ArrayList<>();
            this.quotedText.add(emptyList);
            String sentence = this.sentences.get(i);
            String quote = "";
            Matcher quotes = pattern.matcher(sentence);
            while (quotes.find()) {
                quote = quotes.group(1);
                quote = quote.replaceAll("[\\p{Punct}]", "").replaceAll("\\s+", " ");
                // Convert to lowercase
                quote = quote.toLowerCase();

                StringTokenizer string = new StringTokenizer(quote, " ");

                while (string.hasMoreTokens()) {
                    //tokenizedSentence.add(string.nextToken());
                }
            }
        }

    }

    public ArrayList<String> getSentences() {
        return sentences;
    }

    public void printSentences() {
        for (int i = 0; i < this.sentences.size(); ++i) {
            System.out.println("Sentence " + i + ": " + this.sentences.get(i));
        }
    }

    public ArrayList<ArrayList<String>> getTokens() {
        return tokens;
    }

    public void printTokens() {
        for (int i = 0; i < this.tokens.size(); ++i) {
            System.out.println("Sentence " + i + ": " + this.tokens.get(i));
        }
    }

    public ArrayList<ArrayList<Integer>> getQuotedText() {
        return quotedText;
    }

    public void printQuotedText() {
        System.out.println("Print tokens from quotes for each sentence, if there exist:");
        for (int i = 0; i < this.quotedText.size(); ++i) {
            if (!this.quotedText.get(i).isEmpty()) {
                System.out.println("For sentence " + i + ":");
                for (int j = 0; j < this.quotedText.get(i).size(); j += 2) {
                    int fromIndex = this.quotedText.get(i).get(j);
                    int toIndex = this.quotedText.get(i).get(j+1);
                    System.out.println(this.tokens.get(i).subList(fromIndex,toIndex+1));
                }
            }
        }
    }

    public static void main(String[] args) {
        File new_file = new File("/Users/peacemaker/project/test1.txt");
        new_file.printSentences();
        new_file.printTokens();
        new_file.printQuotedText();
    }
}