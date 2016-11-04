package client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Fab on 31.05.2015.
 * Database Connection to postgres for loading words and rhymes from a DB
 *
 */
public class DBConnection {
    public static Connection connectToDatabaseLautschriftOrDie() throws ClassNotFoundException,SQLException{
        Connection conn = null;
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/Lautschrift";
            conn = DriverManager.getConnection(url, "MediaWikiPostgresUser", "rQHEOdZgpIUYlD3Q1lxM");

        return conn;
    }
}
