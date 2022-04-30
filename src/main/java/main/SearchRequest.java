package main;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class SearchRequest {

    public static void main(String[] args) throws IOException, SQLException {
        Lemmatizer lemmatizer = new Lemmatizer();
        Scanner scn = new Scanner(System.in);
        boolean loopRunning = true;
        String[] arr; //массив слов запроса
        while (loopRunning) {
            System.out.println("Введи поисковый запрос");
            String searchString = scn.nextLine();
            searchString = searchString.trim();
            if (searchString.equals("stop")) {
                loopRunning = false;
                break;
            }
            arr = lemmatizer.stringSplitter(searchString); //строка запроса в массив
            Set<String> SearchSet = getLemmSetMethod(arr);  //удаление повторов из запроса
            Map<Integer, Integer> mapLemm = DBConnection.searchReq(SearchSet); // id лемм сорт-х по встреч-и
            Set<String> page = new Indexer(mapLemm).getPages(); //список лемм запроса
            if(!page.isEmpty()) {
                String[] arr2 = arr;
                List<PageResp> resps = new ArrayList<>();
                page.forEach(p -> resps.add(new PageResp(p, mapLemm,arr2)));// список страниц по запросу(url)
                double maxAbs = resps.stream().map(PageResp::getAsbRel).max(Comparator.naturalOrder()).get(); //максимальное абсолютн-я релевантность
                for (PageResp p : resps) {
                    p.setRelR(maxAbs);
                }
                System.out.println("+*");
//              resps.stream().map(PageResp::getRelR).sorted(Comparator.reverseOrder()).forEach(System.out::println);
                resps.forEach(p-> System.out.println(p.getRelR()));
                System.out.println("*+");
            } else
            {
                System.out.println("no result");
            }
        }
    }//main

    public static Set<String> getLemmSetMethod(String[] arr) throws IOException {
        Set<String> lemmSet = new HashSet<>();
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();
        List<String> wordBaseForms;
        try {
            for (String st : arr) {
                if (Lemmatizer.isCyrillic(st)) {

                    wordBaseForms = russianLuceneMorphology.getNormalForms(st.toLowerCase(Locale.ROOT));

                    for (String bf : wordBaseForms) {
                        if (Lemmatizer.isNotSpecial(bf)) {
                            lemmSet.add(bf);
                        }
                    }
                } else {
                    wordBaseForms = englishLuceneMorphology.getNormalForms(st.toLowerCase(Locale.ROOT));
                }
                lemmSet.addAll(wordBaseForms);
            }
        }//try
        catch (Exception e){
            System.out.println("здесь");
            e.printStackTrace();
        }
        return lemmSet;
    }

}
