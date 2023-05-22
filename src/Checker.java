import java.util.*;

public class Checker {
    private ArrayList<MyFile> myFiles = new ArrayList<>();
    private int threshold = 3;

    public Checker(ArrayList<String> fileNames, int threshold) {
        for (String f_name : fileNames) {
            MyFile myFile = new MyFile(f_name);
            this.myFiles.add(myFile);
        }
        this.threshold = threshold;
    }

    private MyFile getFileByName(String fileName) {
        for (MyFile myFile : this.myFiles) {
            if (myFile.name == fileName) {
                return myFile;
            }
        }
        return null;
    }

    public ArrayList<HashMap<String, Integer>> compareWordFrequency(String fileName1, String fileName2) {
        MyFile myFile1 = getFileByName(fileName1);
        MyFile myFile2 = getFileByName(fileName2);
        HashSet<String> UniqueTokens = new HashSet<>();
        HashMap<String, Integer> wordFrequency1 = new HashMap<>();
        HashMap<String, Integer> wordFrequency2 = new HashMap<>();
        for (ArrayList<String> sentence : myFile1.getTokens()) {
            UniqueTokens.addAll(sentence);
        }
        for (ArrayList<String> sentence : myFile2.getTokens()) {
            UniqueTokens.addAll(sentence);
        }
        for (String token : UniqueTokens) {
            wordFrequency1.put(token, 0);
        }
        for (String token : UniqueTokens) {
            wordFrequency2.put(token, 0);
        }
        for (ArrayList<String> sentence : myFile1.getTokens()) {
            for (String token : sentence) {
                wordFrequency1.put(token, wordFrequency1.get(token) + 1);
            }
        }
        for (ArrayList<String> sentence : myFile2.getTokens()) {
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
        MyFile myFile1 = getFileByName(fileName1);
        MyFile myFile2 = getFileByName(fileName2);
        for (int sent1Ind = 0; sent1Ind < myFile1.getTokens().size(); sent1Ind++) {
            for (int sent2Ind = 0; sent2Ind < myFile2.getTokens().size(); sent2Ind++) {
                ArrayList<String> sent1 = myFile1.getTokens().get(sent1Ind);
                ArrayList<String> sent2 = myFile2.getTokens().get(sent2Ind);
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

    public ArrayList<ArrayList<String>> collectMatches(HashMap<Pair, ArrayList<ArrayList<String>>> matchedPairs, int sentenceInd, boolean isFirstFile) {
        ArrayList<ArrayList<String>> matchesFromSameSentence = new ArrayList<>();
        for (Pair pair : matchedPairs.keySet()) {
            if (pair.first == sentenceInd && isFirstFile) {
                matchesFromSameSentence.addAll(matchedPairs.get(pair));
            }
            if(pair.second == sentenceInd && !isFirstFile) {
                matchesFromSameSentence.addAll(matchedPairs.get(pair));
            }
        }
        return matchesFromSameSentence;
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
        MyFile myFile = getFileByName(fileName);
        int totalWordsFile = 0;
        for (ArrayList<String> sentence : myFile.getTokens()) {
            totalWordsFile += sentence.size();
        }
        int matchingWords = 0;

        // make copy
        ArrayList<ArrayList<String>> copiedTokens = new ArrayList<>();
        for (ArrayList<String> sentence : myFile.getTokens()) {
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

    public Pair getMatchToHighlight(int sentenceInd, ArrayList<String> tokens, String fileName) {
        MyFile myFile = getFileByName(fileName);
        int start = -1;
        int end = -1;
        for (int i = 0; i < myFile.getParsed().get(sentenceInd).size(); i++) {
            String item = myFile.getParsed().get(sentenceInd).get(i);
            if (item.length() > 1 || (item.length() == 1 && Character.isLetter(item.charAt(0)))) {
                if (item.toLowerCase().equals(tokens.get(0).toLowerCase())) {
                    start = i;
                    end = i + 1;
                    int tokenInd = 0;
                    for (int j = i; j < myFile.getParsed().get(sentenceInd).size(); j++) {
                        String item2 = myFile.getParsed().get(sentenceInd).get(j).toLowerCase();
                        if (item2.length() > 1 || (item2.length() == 1 && Character.isLetter(item2.charAt(0)))) {
                            if (item2.equals(tokens.get(tokenInd).toLowerCase())) {
                                end = j + 1;
                                if (tokenInd < tokens.size() - 1) {
                                    tokenInd += 1;
                                } else {
                                    return new Pair(start, end);
                                }
                            } else {
                                break;
                            }

                        }
                    }
                }
            }
        }
        return new Pair(-1, -1);
    }

    public String highlightMatch(String fileName, int sentenceInd, ArrayList<ArrayList<String>> match, String OpenTag, String CloseTag) {
        ArrayList<Integer> openTagsPlaces = new ArrayList<>();
        ArrayList<Integer> closeTagsPlaces = new ArrayList<>();
        for (ArrayList<String> part : match) {
            Pair pair = getMatchToHighlight(sentenceInd, part, fileName);
            openTagsPlaces.add(pair.first);
            closeTagsPlaces.add(pair.second);
        }


        StringBuilder highlightedSentence = new StringBuilder();
        ArrayList<String> sentence = getFileByName(fileName).getParsed().get(sentenceInd);
        for(int i = 0; i< sentence.size();i++){
            if (openTagsPlaces.contains(i)) {
                for (int k = 0;k < Collections.frequency(openTagsPlaces, i);k++) {
                    highlightedSentence.append(OpenTag);
                }
            }
            if (closeTagsPlaces.contains(i)) {
                for (int k = 0;k < Collections.frequency(closeTagsPlaces, i);k++) {
                    highlightedSentence.append(CloseTag);
                }
            }
            highlightedSentence.append(sentence.get(i));
        }
        return highlightedSentence.toString();
    }

    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<>(Arrays.asList("/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test1.txt", "/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test2.txt", "/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test3.txt"));
        Checker checker = new Checker(files, 3);
        Pair pair = checker.getMatchToHighlight(1, new ArrayList<>(Arrays.asList("it", "Is", "AN")), "/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test1.txt");
        int start = pair.first;
        int end = pair.second;
        System.out.println(start + " " + end);
        for (int i = start; i < end; i++) {
            System.out.print(checker.getFileByName("/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test1.txt").getParsed().get(1).get(i));
        }
        System.out.println();
        ArrayList<ArrayList<String>> test = new ArrayList<>();
        test.add(new ArrayList<>(Arrays.asList("that", "things", "are", "not", "always", "what", "they", "seem")));
        test.add(new ArrayList<>(Arrays.asList("what", "they", "seem")));
        test.add(new ArrayList<>(Arrays.asList("important", "and", "popular", "fact")));
        System.out.println(checker.highlightMatch("/Users/peacemaker/IdeaProjects/PlagiarismChecker/src/test3.txt", 0, test, "<b>", "</b>"));
    }
}