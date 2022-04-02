package main;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import java.io.IOException;
import java.util.*;

public class TestSearchClass {

    public static void main(String[] args) throws IOException {
        Lemmatizer lemmatizer = new Lemmatizer();
        Scanner scn = new Scanner(System.in);
        boolean loopRunning  = true;
        String[] arr;
        while (loopRunning) {
            System.out.println("Введи поисковый запрос");
            String searchString = scn.nextLine();
            if (searchString.equals("stop")) {
                loopRunning = false;
                break;
            }
            arr = lemmatizer.stringSplitter(searchString); //строка запроса в массив
            Set <String> SearchSet = new HashSet<>(LemmCounterMethod(arr).keySet());  //удаление повторов из запроса
            Map<Integer,Integer> mapTest = DBConnection.searchReq(SearchSet); // id лемм сорт-х по встреч-и
            mapTest.forEach((k,v)->System.out.println(k + " * " + v));  //провера выполнения запроса
        }

    }//main

    public static Map<String, Double> LemmCounterMethod(String[] arr) throws IOException {
        Map<String, Double> countMap = new HashMap<>();
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();
        List<String> wordBaseForms;
        for (String st : arr) {
            if (Lemmatizer.isCyrillic(st)) {
                wordBaseForms = russianLuceneMorphology.getNormalForms(st.toLowerCase(Locale.ROOT));
                for (String bf : wordBaseForms) {
                    if (isNotSpecial(bf)) {
                        Double c = countMap.get(bf);
                        if (c == null) {
                            countMap.put(bf, 1.0);
                        } else {
                            countMap.put(bf, ++c);
                        }
                    }
                }
            } else {
                wordBaseForms = englishLuceneMorphology.getNormalForms(st.toLowerCase(Locale.ROOT));
            }
            for (String bf : wordBaseForms) {
                Double c = countMap.get(bf);
                if (c == null) {
                    countMap.put(bf, 1.0);
                } else {
                    countMap.put(bf, ++c);
                }
            }
        }
        return countMap;
    }

    private static boolean isNotSpecial(String s) throws IOException {
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        List<String> wordBaseForms = luceneMorphology.getMorphInfo(s);
        for (String bf : wordBaseForms) {
            if (bf.contains("МЕЖД") || bf.contains("СОЮЗ") || bf.contains("ЧАСТ") || bf.contains("ПРЕДЛ")) {
                return false;
            }
        }
        return true;
    }
}
