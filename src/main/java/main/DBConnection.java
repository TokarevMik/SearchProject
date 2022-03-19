package main;

import java.sql.*;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String password = "cuafbu5k3a";
                String user = "root";
                String url = "jdbc:mysql://localhost:3306/search_engine";
                connection = DriverManager.getConnection(url, user, password);
                connection.createStatement().execute("DROP TABLE IF EXISTS page");
                connection.createStatement().execute("DROP TABLE IF EXISTS field");
                connection.createStatement().execute("DROP TABLE IF EXISTS lemma");
                connection.createStatement().execute("DROP TABLE IF EXISTS indexes");
                connection.createStatement().execute("CREATE TABLE page(" +
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "Path TEXT NOT NULL, " +
                        "Code INT NOT NULL, " +
                        "content MEDIUMTEXT NOT NULL)");
                connection.createStatement().execute("CREATE TABLE field("+
                        "name TINYTEXT NOT NULL,"+
                        "selector TINYTEXT NOT NULL,"+
                        "weight FLOAT NOT NULL)");
                connection.createStatement().execute("INSERT INTO field(name, selector,weight)"+
                        "VALUES ('title','title','1.0')," +
                        "('body','body','0.8')");
                connection.createStatement().execute("CREATE TABLE lemma("+
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "+
                        "lemma VARCHAR(255) NOT NULL , "+
                        "frequency INT NOT NULL)");
                connection.createStatement().execute("CREATE TABLE indexes("+
                        "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "+
                        "page_id INT NOT NULL, "+
                        "lemma_id INT NOT NULL, "+
                        "rang FLOAT NOT NULL)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
    public static void fullTheDb (String path,int code,String content)throws SQLException{
        String sql = "INSERT INTO page (Path, Code, content) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,path);
        preparedStatement.setInt(2,code);
        preparedStatement.setString(3,content);
        preparedStatement.executeUpdate();
    }
    public static void executeMultiInsert(StringBuilder insertQuery) throws SQLException, SQLSyntaxErrorException {
        String sql = "INSERT INTO lemma(lemma, `frequency`) " +
                "VALUES" + insertQuery.toString();
        DBConnection.getConnection().createStatement().execute(sql);
    }
    public static void closeConnection() throws SQLException {
        connection.close();
    }

}
