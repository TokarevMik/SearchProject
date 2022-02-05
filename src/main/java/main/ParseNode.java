package main;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;

public class ParseNode extends RecursiveAction {
    private Node node;

    public ParseNode(Node node) {
        this.node = node;
    }

    public static Set<String> isAlreadyAdded = new CopyOnWriteArraySet<>();  // url already in DB (????)

    @Override
    protected void compute() {
        String url = node.getUrl(); //текущий адрес
        if (isAlreadyAdded.contains(url)) {   //внесен ли текущий адрес
            try {
                Thread.sleep(300);
                node.getParseNode();



/*
                if (url.equals("http://www.playback.ru ")){
                    urlAddress = url;
                } else {
                    urlAddress = url.replace("https://skillbox.ru","");
                }
*/


                Set<ParseNode> taskList = new CopyOnWriteArraySet<>();
//                for (Node child : node.getChildren()) {
//
//                }
                isAlreadyAdded.add(url);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
