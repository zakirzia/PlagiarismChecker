import java.util.*;

public class Checker {
    private ArrayList<File> files = new ArrayList<>();
    private Set<String> allWords = new HashSet<>();

    private int threshold = 3;

    public Checker(String[] fileNames, int threshold) {
        for (String f_name : fileNames) {
            File file = new File(f_name);
            this.files.add(file);
        }
        this.threshold = threshold;
        collectAllWords();
    }

    private File getFileByName(String fileName) {
        for (File file : this.files) {
            if (file.name == fileName) {
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

    public HashMap<String, Integer> wordFrequencyPerFile(String fileName) {
        File file = getFileByName(fileName);
        HashMap<String, Integer> wordFrequency = new HashMap<>();
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

    public ArrayList<HashMap<String, Integer>> compareWordFrequency(String fileName1, String fileName2) {
        File file1 = getFileByName(fileName1);
        File file2 = getFileByName(fileName2);
        HashSet<String> UniqueTokens = new HashSet<>();
        HashMap<String, Integer> wordFrequency1 = new HashMap<>();
        HashMap<String, Integer> wordFrequency2 = new HashMap<>();
        for (ArrayList<String> sentence : file1.getTokens()) {
            UniqueTokens.addAll(sentence);
        }
        for (ArrayList<String> sentence : file2.getTokens()) {
            UniqueTokens.addAll(sentence);
        }
        for (String token : UniqueTokens) {
            wordFrequency1.put(token, 0);
        }
        for (String token : UniqueTokens) {
            wordFrequency2.put(token, 0);
        }
        for (ArrayList<String> sentence : file1.getTokens()) {
            for (String token : sentence) {
                wordFrequency1.put(token, wordFrequency1.get(token) + 1);
            }
        }
        for (ArrayList<String> sentence : file2.getTokens()) {
            for (String token : sentence) {
                wordFrequency2.put(token, wordFrequency2.get(token) + 1);
            }
        }
        ArrayList<HashMap<String, Integer>> result = new ArrayList<>();
        result.add(wordFrequency1);
        result.add(wordFrequency2);
        return result;
    }

    public HashMap<Pair, ArrayList<ArrayList<String>>> phraseMatching(String fileName1, String fileName2) {
        HashMap<Pair, ArrayList<ArrayList<String>>> matchedPairs = new HashMap<>();
        File file1 = getFileByName(fileName1);
        File file2 = getFileByName(fileName2);
        for (int sent1Ind = 0; sent1Ind < file1.getTokens().size(); sent1Ind++) {
            for (int sent2Ind = 0; sent2Ind < file2.getTokens().size(); sent2Ind++) {
                ArrayList<String> sent1 = file1.getTokens().get(sent1Ind);
                ArrayList<String> sent2 = file2.getTokens().get(sent2Ind);
                for (int word1Ind = 0; word1Ind < sent1.size(); word1Ind++) {
                    for (int word2Ind = 0; word2Ind < sent2.size(); word2Ind++) {
                        String word1 = sent1.get(word1Ind);
                        String word2 = sent2.get(word2Ind);
                        if (word1.equals(word2)) {
                            int word1Ind2 = word1Ind;
                            int word2Ind2 = word2Ind;
                            int matchCount = 0;
                            ArrayList<String> match = new ArrayList<>();
                            while (word1Ind2 < sent1.size() && word2Ind2 < sent2.size()) {
                                if (sent1.get(word1Ind2).equals(sent2.get(word2Ind2))) {
                                    match.add(sent1.get(word1Ind2));
                                    matchCount += 1;
                                    word1Ind2 += 1;
                                    word2Ind2 += 1;
                                } else {
                                    break;
                                }
                            }
                            if (matchCount >= this.threshold) {
                                Pair pair = new Pair(sent1Ind, sent2Ind);
                                if (matchedPairs.containsKey(pair)) {
                                    if (!ifContains(matchedPairs.get(pair), match)) {
                                        matchedPairs.get(pair).add(match);
                                    }
                                } else {
                                    ArrayList<ArrayList<String>> matches = new ArrayList<>();
                                    matches.add(match);
                                    matchedPairs.put(pair, matches);
                                }
                            }
                        }
                    }
                }
            }
        }
        return matchedPairs;
    }

    private boolean ifContains(ArrayList<ArrayList<String>> main, ArrayList<String> sub) {
        for (ArrayList<String> list : main) {
            if (list.containsAll(sub)) {
                return true;
            }
        }
        return false;
    }

    public void printMatchingPhrases(HashMap<Pair, ArrayList<ArrayList<String>>> matchedPairs, String fileName1, String fileName2) {
        for (Pair pair : matchedPairs.keySet()) {
            System.out.println("Sentence " + pair.first + " in " + fileName1 + " matches with sentence " + pair.second + " in " + fileName2);
            for (ArrayList<String> match : matchedPairs.get(pair)) {
                System.out.println(match);
            }
        }
    }

    public int getMatchedPhrasesStatistics(HashMap<Pair, ArrayList<ArrayList<String>>> matchedPairs, String fileName) {
        File file = getFileByName(fileName);
        int totalWordsFile = 0;
        for (ArrayList<String> sentence : file.getTokens()) {
            totalWordsFile += sentence.size();
        }
        int matchingWords = 0;

        // make copy
        ArrayList<ArrayList<String>> copiedTokens = new ArrayList<>();
        for (ArrayList<String> sentence : file.getTokens()) {
            ArrayList<String> copySentence = new ArrayList<>(sentence);
            copiedTokens.add(copySentence);
        }

        for (Pair pair : matchedPairs.keySet()) {
            for (ArrayList<String> match : matchedPairs.get(pair)) {
                for (String token : match) {
                    if (copiedTokens.get(pair.second).contains(token)) {
                        copiedTokens.get(pair.second).remove(token);
                        matchingWords += 1;
                    }
                }
            }
        }
        return (int) (((double) matchingWords / (double) totalWordsFile) * 100);
    }

    public static void main(String[] args) {
        String[] files = { "/Users/peacemaker/project/test1.txt", "/Users/peacemaker/project/test2.txt"};
        Checker checker = new Checker(files, 3);
        HashMap<Pair, Integer> matchTable = new HashMap<>();
        for (int i = 0; i < files.length; i++) {
            for (int j = 0; j < files.length; j++) {
                if (i != j) {
                    HashMap<Pair, ArrayList<ArrayList<String>>> matchesResults = checker.phraseMatching(files[i], files[j]);
                    checker.printMatchingPhrases(matchesResults, files[i], files[j]);
                    System.out.println("Phrase matching for " + files[j] + " compared with " + files[i] + ": " + checker.getMatchedPhrasesStatistics(matchesResults, files[j]));
                }
            }
        }
    }
}