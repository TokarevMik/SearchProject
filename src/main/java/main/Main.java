package main;
import java.io.IOException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        DBConnection.getConnection();
        String url = "https://skillbox.ru";
        Node root = new Node(url);
        ParseNode task = new ParseNode(root);
        task.invoke();
        DBConnection.closeConnection();
    }

}
