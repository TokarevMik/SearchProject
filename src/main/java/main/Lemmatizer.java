package main;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

public class Lemmatizer {

    //        lemCounter(sd);
    private String titleS;
    private String bodyS;

    public Lemmatizer(String titleS, String bodyS) throws IOException {
        this.titleS = titleS;
        this.bodyS = bodyS;
    }

/*    bodyS = "В повторное появление леопарда обитающего в " +
            "Осетии позволяет предположить, что леопард постоянно " +
            "обитает в некоторых районах Северного Кавказа.";*/

    private Map<String, Double> titleMap = lemCounter(titleS);

    private Map<String, Double> bodyMap = lemCounter(titleS);

    public Map<String, Double> lemRank(Map<String, Double> titleMap, Map<String, Double> bodyMap) {
        Map<String, Double> rankMap = new HashMap<String, Double>();
        bodyMap.forEach((k,v)->rankMap.put(k, (0.8*v)));
        for (Map.Entry<String, Double> entryT : titleMap.entrySet()) {
            String keyT = entryT.getKey();
            Double valueT = entryT.getValue();
            if (rankMap.containsKey(keyT)) {                     //search for words matches
                Double valueNew = rankMap.get(keyT) + valueT;
                rankMap.put(keyT, valueNew);
            } else {
                rankMap.put(keyT, valueT);
            }
        }
        return rankMap;
    }

    private String[] stringSplitter(String s) {
        s = s.replaceAll("[^А-Яа-яA-Za-z\\s]", ""); //удаление лишних символов
        s = s.replaceAll("[\\s]{2,}", ""); // удаление лишних пробелов
        System.out.println(s);
        return s.split(" ");
    }

    public Map<String, Double> lemCounter(String s) throws IOException {
        Map<String, Double> countMap = new HashMap<>();
        LuceneMorphology luceneMorphology = new RussianLuceneMorphology();
        String[] arr = stringSplitter(s);
        for (String st : arr) {
            List<String> wordBaseForms = luceneMorphology.getNormalForms(st.toLowerCase(Locale.ROOT));
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
        }
        for (Map.Entry<String, Double> entry : countMap.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        return countMap;
    }

    private boolean isNotSpecial(String s) throws IOException {
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
