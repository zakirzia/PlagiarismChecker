import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class File {
    public String name;
    private ArrayList<String> sentences = new ArrayList<>();
    private ArrayList<ArrayList<String>> tokens = new ArrayList<>();

    public File(String fileName) {
        this.name = fileName;
        readFile();
        tokenize();
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

    public ArrayList<String> getSentences() {
        return sentences;
    }

    public void printSentences() {
        for (int i = 0; i < this.sentences.size(); ++i) {
            System.out.println("Sentence " + (i+1) + ": " + this.sentences.get(i));
        }
    }

    public ArrayList<ArrayList<String>> getTokens() {
        return tokens;
    }

    public void printTokens() {
        for (int i = 0; i < this.tokens.size(); ++i) {
            System.out.println("Sentence " + (i+1) + ": " + this.tokens.get(i));
        }
    }

    public static void main(String[] args) {
        File new_file = new File("/Users/peacemaker/project/test1.txt");
        new_file.printSentences();
        System.out.println("\n");
        new_file.printTokens();
    }
}