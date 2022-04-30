package main;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PageResp {
    String url;
    Map<Integer, Integer> mapLemm; //map лемм id и req
    double relR = 0.0; //относительная релевантность
    double absRel = 0.0; //абсолютная релевантность
    String title;
    String textCont;
    String[] SearchRequest;

    public PageResp(String url, Map<Integer, Integer> mapLemm, String[] SearchRequest) {
        this.url = url;
        this.mapLemm = mapLemm;
        this.SearchRequest = SearchRequest;
    }

    public Double getAsbRel() {
        StringBuilder sb = new StringBuilder();

        sb.append("""
                SELECT ROUND(SUM(indexes.rang),3) from indexes\s
                JOIN page ON indexes.page_id = page.id
                JOIN lemma ON lemma.id = indexes.lemma_id
                WHERE Page.path ='""").append(url).append("' AND lemma_id IN (");
        mapLemm.forEach((v, k) -> sb.append(v).append(","));
        int start = sb.length() - 1;
        int end = sb.length();
        sb.replace(start, end, ")");
        try {
            ResultSet resultSet = DBConnection.getConnection2().createStatement().executeQuery(sb.toString());
            while (resultSet.next()) {
                absRel = resultSet.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return absRel;
    }

    public void setRelR(double abs) throws SQLException {
        relR = absRel / abs;
        String content = DBConnection.getContent(url);
        Document doc = Jsoup.parse(content);
        title = doc.title();
        textCont = doc.text();
    } //формирование ответа
//

    public double getRelR() {
        String requestText = null;
        try{requestText = requestString(textCont,SearchRequest);
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("************");
        System.out.println("url - " + url);
        System.out.println("title - " + title);
        System.out.println(requestText);
        System.out.println("rel - " + relR);
        System.out.println("************");
        return relR;
    }

    public String getUrl() {
        return url;
    }

/*    public static String requestString(String textOfPage, String[] SearchRequest) {
        StringBuilder respons = new StringBuilder();
        List<Integer> indexes = new ArrayList<>(); //список индексов позиций слов
        String[] content = textOfPage.split(" ");
        for (String s : SearchRequest) {
            for (int j = 0; j < content.length; j++) {
                if (content[j].equals(s)) {
                    indexes.add(j);
                }
            }
        }
        int min = indexes.stream().min(Comparator.naturalOrder()).get();
        int max = indexes.stream().max(Comparator.naturalOrder()).get();
        if (indexes.size() == 1) {
            if (indexes.get(0) <= 3) {
                min = 0;
                if (indexes.get(0) + 3 <= content.length) {
                    max = indexes.get(0) + 3;
                }
            }
            if (indexes.get(0) >= content.length - 3) {
                max = content.length;
            }
        }
        for (int i = min; i <= max; i++) {
            if (indexes.contains(i)) {
                respons.append("<b>").append(content[i]).append("</b>").append(" ");
            } else {
                respons.append(content[i]).append(" ");
            }
        }
        return respons.toString();
    }*/
    public static String requestString(String list, String[] SearchRequest) throws IOException {
        LuceneMorphology luceneMorphE = new EnglishLuceneMorphology();
        LuceneMorphology luceneMorphR = new RussianLuceneMorphology();
        StringBuilder respons = new StringBuilder();
        List<Integer> indexes = new ArrayList<>(); //список индексов позиций слов
        String cleanSt = removePunct(list);
        String[] content = cleanSt.split(" ");
        for (String s : SearchRequest) {
            for (int j = 0; j < content.length; j++) {
                try {
                    Double.parseDouble(content[j]);
                } catch (NumberFormatException e) {
                    if (isCyrillic(content[j])) {
                        List<String> wordBaseForms;
                        try {
                            wordBaseForms = luceneMorphR.getNormalForms(content[j].toLowerCase(Locale.ROOT));
                            if (wordBaseForms.contains(s)) {
                                indexes.add(j);
                            }
                        }catch (WrongCharaterException we){
                            System.out.println("WrongCharaterR " + content[j] + " s " + s);
                            System.out.println("//////\n");
                        }
                    } else {
                        List<String> wordBaseForms;
                        try{
                        wordBaseForms = luceneMorphE.getNormalForms(content[j].toLowerCase(Locale.ROOT));
                        if(wordBaseForms.contains(s)){
                            indexes.add(j);
                        }}catch (WrongCharaterException we){
                            System.out.println("WrongCharaterE " + content[j] + " s " + s);
                            System.out.println("//////\n");
                        }
                        catch (IndexOutOfBoundsException ex){
                            System.out.println("Exception");
                            System.out.println("index "  + j);
                            System.out.println("s " + s);
//                            System.out.println(content.length);
//                            Arrays.stream(content).forEach(System.out::print);
                            System.out.println("Exception");
                        }
                    }
                }
            }
        }
        int min = indexes.stream().min(Comparator.naturalOrder()).get();
        int max = indexes.stream().max(Comparator.naturalOrder()).get();
        if (indexes.size() == 1) {
            if (indexes.get(0) <= 3) {
                min = 0;
                if (indexes.get(0) + 3 <= content.length) {
                    max = indexes.get(0) + 3;
                }
            }
            if (indexes.get(0) >= content.length - 3) {
                max = content.length;
            }
        }
        for (int i = min; i <= max; i++) {
            if (indexes.contains(i)) {
                respons.append("<b>").append(content[i]).append("</b>").append(" ");
            } else {
                respons.append(content[i]).append(" ");
            }
        }
        return respons.toString();
    }
    static boolean isCyrillic(String s) {
        boolean result = false;
        for (char a : s.toCharArray()) {
            if (Character.UnicodeBlock.of(a) == Character.UnicodeBlock.CYRILLIC) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }
    public static String removePunct(String str) {
        StringBuilder result = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isAlphabetic(c) || Character.isDigit(c) || Character.isSpaceChar(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
