import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Checker {
    private ArrayList<File> files = new ArrayList<>();
    private Set<String> allWords = new HashSet<>();

    public Checker(String[] fileNames) {
        for (String f_name : fileNames) {
            File file = new File(f_name);
            this.files.add(file);
        }

        collectAllWords();
    }

    private File getFileByName(String fileName){
        for(File file:this.files){
            if(file.name == fileName){
                return file;
            }
        }
        return null;
    }

    private void collectAllWords() {
        for (File file : this.files) {
            for (ArrayList<String> sentence : file.getTokens()) {
                this.allWords.addAll(sentence);
            }
        }
    }

    public Set<String> getAllWords() {
        return allWords;
    }

    public Map<String, Integer> wordFrequencyPerFile(String fileName) {
        File file = getFileByName(fileName);
        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String uniqueToken : this.allWords) {
            wordFrequency.put(uniqueToken, 0);
        }

        for (ArrayList<String> sentence : file.getTokens()) {
            for (String token : sentence) {
                wordFrequency.put(token, wordFrequency.get(token) + 1);
            }
        }
        return wordFrequency;
    }

    public static void main(String[] args) {
        String[] files = { "/Users/peacemaker/project/test1.txt", "/Users/peacemaker/project/test2.txt"};
        Checker checker = new Checker(files);
        System.out.println(checker.getAllWords());
        System.out.println("\n");
        System.out.println(checker.wordFrequencyPerFile( "/Users/peacemaker/project/test1.txt"));
    }
}