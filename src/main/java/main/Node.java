
package main;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node {
    private String url;
    private String path; // адрес для бд
    private String domain = "https://skillbox.ru";
    private Connection.Response response;
    private Integer statusCode;
    private String contentOfPage = "";

    public String getPath() {
        return path;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getContentOfPage() {
        return contentOfPage;
    }

    public Node(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    private Collection<Node> nodes = new HashSet<>();

    public Collection<Node> getChildren() {
        return nodes;
    }

    public void getParseNode() {
        try {
            Thread.sleep(200);
            response = Jsoup.connect(url).timeout(0).userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com").maxBodySize(0).execute();
            statusCode = response.statusCode(); //статус ответа
            Document doc = response.parse();
            contentOfPage = doc.html();   //содержимое страницы
            Element content = doc.body();
            Elements links = content.getElementsByTag("a");
            if (url.equals(domain)) {
                path = domain;
            } else {
                path = url.replace(domain, "");
            }

            for (Element link : links) {
                String linkHref = link.attr("href");
                Pattern pattern = Pattern.compile("https://skillbox.ru/[\\w,\\D]+/$");
                Matcher matcher = pattern.matcher(linkHref);

                Pattern pattern2 = Pattern.compile("^/[\\w,-,_]+/$");
                Matcher matcher2 = pattern2.matcher(linkHref);
                if (matcher.matches()) {
                    nodes.add(new Node(linkHref));   // добавление дочерней ссылки в список , но уровнем не ниже 1 от родительской
                }
                if (matcher2.matches()) {
                    path = linkHref;
                    linkHref = domain.concat(linkHref);
                    nodes.add(new Node(linkHref)); // ссылка типа "/****/"
                }
            }
        } catch (HttpStatusException se){
            path = url.replace(domain, "");
            contentOfPage = "";
            statusCode = se.getStatusCode();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
