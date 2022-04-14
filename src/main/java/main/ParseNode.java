package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;


public class ParseNode extends RecursiveAction {
    private Node node;
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getRootLogger();
    private static final Marker EXCEPTION_MARKER = MarkerManager.getMarker("EXCEPTION_ST");

    public Node getNode() {
        return node;
    }

    public ParseNode(Node node) {
        this.node = node;
    }

    public static Set<String> isAlreadyAdded = new CopyOnWriteArraySet<>();  // url already in DB

    @Override
    protected void compute() {
        String url = node.getUrl(); //текущий адрес
        node.getParseNode();
        try {
            DBConnection.fullTheDb(node.getPath(), node.getStatusCode(), node.getContentOfPage());
            Lemmatizer lemmatizer = new Lemmatizer(node.getTitle(), node.getBodyText());
            StringBuffer builder = lemmatizer.getInsertQuery();
            DBConnection.executeMultiInsert(builder);
            int pageId = DBConnection.getPageId(node.getPath());
            lemmatizer.queryForIndex(pageId);

        } catch (SQLSyntaxErrorException e) {
            e.printStackTrace();
            LOGGER.error(EXCEPTION_MARKER, (Object) e + " node.getUrl()");
        } catch (IOException e) {
            LOGGER.error(EXCEPTION_MARKER, (Object) e);
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException ex){
            System.out.println("SQL **" + " Path" + node.getPath());
            System.out.println("URL " + node.getUrl());
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        Set<ParseNode> taskList = new CopyOnWriteArraySet<>();

        for (Node child : node.getChildren()) {
            if (!isAlreadyAdded.contains(child.getUrl())) {
                isAlreadyAdded.add(child.getUrl());
                ParseNode parseNodeTask = new ParseNode(child);
                parseNodeTask.fork();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                taskList.add(parseNodeTask);
            }
        }
        for (ParseNode task : taskList) {
            try{task.join();}
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
