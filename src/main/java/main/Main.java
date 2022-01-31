import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import javax.lang.model.util.Elements;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://skillbox.ru";
        String path = "src/main/resources/file.html";
//        Document doc = Jsoup.connect(url).get();
        Document doc = Jsoup.parse(htmlFile(path));
//        processDoc(doc);

        Elements links = doc.select("a[href]");
//        Elements links = doc.getElementsByAttribute("a[href]");
        for (Element link : links) {
            String absLink = link.attr("abs:href");
            if (!absLink.isEmpty()){
            System.out.println(absLink);}
//            if (absLink.contains("site.com"))
//                addToQueue(absLink);
        }

    }
    private static String htmlFile (String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        List<String> lines = Files.readAllLines(Paths.get(path));
        lines.forEach(line-> builder.append(line).append("\n"));
        return builder.toString();
    }
}
