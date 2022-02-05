package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static Connection connection;
    private static String url = "jdbc:mysql://localhost:3306/search_engine";
    private static String user = "root";
    private static String password = "cuafbu5k3a";

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Path TEXT NOT NULL, " +
                        "Code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    public static void fullTheDb (String path,int code,String content)throws SQLException{
        connection.createStatement().executeUpdate("INSERT INTO pages (Path, Code, content )" +
                "VALUES('" + path +"','" + code +"','"+content +"'" );

    }
}
