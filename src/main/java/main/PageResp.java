package main;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PageResp {
    String url;
    Map<Integer, Integer> mapLemm;
    double relR = 0.0;
    //    Double abs;
    double absRel = 0.0;

    public PageResp(String url, Map<Integer, Integer> mapLemm) {
        this.url = url;
        this.mapLemm = mapLemm;
    }
  /*  public static void main(String[] args) {
        Map<Integer, Integer> testMap = new LinkedHashMap<>();
        testMap.put(10, 2);
        testMap.put(11, 2);
        testMap.put(1, 2);
        testMap.put(3124, 3);
//        testMap.put(3, 7);
        testMap.put(16, 9);
        String url = "/course/career-path/";
        System.out.println(getAsbRel(testMap,url));

    }*/

    public Double getAsbRel() {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ROUND(SUM(indexes.rang),3) from indexes \n" +
                "JOIN page ON indexes.page_id = page.id\n" +
                "JOIN lemma ON lemma.id = indexes.lemma_id\n" +
                "WHERE Page.path ='").append(url).append("' AND lemma_id IN (");
        mapLemm.forEach((v, k) -> sb.append(v + ","));
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

    public void setRelR(double abs) {
        relR = absRel / abs;
    }

    public double getRelR() {
        return relR;
    }
}
