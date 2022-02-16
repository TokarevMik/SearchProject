package main;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;

public class ParseNode extends RecursiveAction {
    private Node node;

    public Node getNode() {
        return node;
    }
    public ParseNode(Node node) {
        this.node = node;
    }
    public static Set<String> isAlreadyAdded = new CopyOnWriteArraySet<>();  // url already in DB (????)
    @Override
    protected void compute() {
        String url = node.getUrl(); //текущий адрес
        node.getParseNode();
        try {
            DBConnection.fullTheDb(node.getPath(), node.getStatusCode(), node.getContentOfPage()); //заполнение таблицы текущей нодой
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        Set<ParseNode> taskList = new CopyOnWriteArraySet<>();
//******************************
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
                task.join();
        }
    }
}
