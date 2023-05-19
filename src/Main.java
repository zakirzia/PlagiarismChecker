import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static LinkedHashMap<String, Integer> sortMap(HashMap<String, Integer> wordFreq){
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

    private static LinkedHashMap<String, Integer> phraseMatchResultPerFile(Checker checker, String fileName, ArrayList<String> files){
        HashMap<String,Integer> temp_res = new HashMap<>();

        for (String file : files){
            if (file != fileName){
                HashMap<Pair, ArrayList<ArrayList<String>>> res =  checker.phraseMatching(fileName, file);
                int matchPercentage = checker.getMatchedPhrasesStatistics(res,file);
                String key = fileName + " vs " + file;
                temp_res.put(key, matchPercentage);
            }
        }

        LinkedHashMap<String, Integer> final_res = sortMap(temp_res);
        return final_res;
    }

    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<>(Arrays.asList("/Users/peacemaker/project/test1.txt", "/Users/peacemaker/project/test2.txt"));
        Checker checker = new Checker(files, 3);
        ArrayList<HashMap<String, Integer>> frequencies = checker.compareWordFrequency(files.get(0), files.get(1));
        System.out.println(sortMap(frequencies.get(0)));
        System.out.println(sortMap(frequencies.get(1)));
        for (String file : files){
            System.out.println("For file " + file);
            LinkedHashMap<String, Integer> res = phraseMatchResultPerFile(checker, file,files);
            System.out.println(res);
        }
    }
}