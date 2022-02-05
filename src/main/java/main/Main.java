package main;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import javax.lang.model.util.Elements;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
//        DBConnection.
        String url = "http://www.playback.ru ";
        Node root = new Node(url);
        ParseNode task = new ParseNode(root);
        task.invoke();

    }

}
