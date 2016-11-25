package output;

/**
 * Created by Fabrice Vanner on 31.08.2016.
 */

import client.ClientArgs;
import client.PhEntriesStructure;
import phonetic_entities.PhEntry;
import client.Utils;
import com.google.common.collect.Multimap;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

/**
 * @author
 * Exports Data to SQLite Database optimized for Android-App-Use
 *
 */
public class DBExport {
    private static Connection conn;
    public static Statement statement;
    private static String path; //= "/home/entwickler01/Downloads";
    private static String fileName = "rhymes.db";
    private static int primaryKeyForTableWords = 1;
    private static PhEntriesStructure phEntriesStructure;
    private static HashMap<String, Integer> wordIndexHashMap;

    public static PhEntriesStructure getPhEntriesStructure() {
        return phEntriesStructure;
    }

    public static void setPhEntriesStructure(PhEntriesStructure phEntriesStructure) {
        DBExport.phEntriesStructure = phEntriesStructure;
    }

    /**
     * Creates a
     * @param clientArgs
     */
    public static void serializeWordIndexHashMap(ClientArgs clientArgs){
        FileOutputStream fileOutputStream;
        File hmOut = new File(getPath() + "/" + clientArgs.exportToSerHM_Filename);
        try {
            fileOutputStream = new FileOutputStream(hmOut);
            ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
            oos.writeObject(wordIndexHashMap);
            oos.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {

        }
    }

    /**
     * Exports all iniialised PhEntries to an SQLite Database
     * for every Entry all rhymes get calculated (see EXPORT options in ClientArgs)
     * If set, will also create a serialized HashMap for faster row QUERY in Android App
     * (the row indice of a given word will be found by the hashmap, then the rhymes string will be taken from the database)
     *
     * RELEVANT CLIENTARGS options: exportToDB; FILTER_EQU_ENDS; serializeWordIndexHashMap; exportStopAtEntryIndex
     *
     * @param clientArgs
     * @throws SQLException
     */
    //TODO umbauen, exportToDBAlt funktioniert noch
    public static void exportToDB(ClientArgs clientArgs) throws SQLException {
        if (clientArgs.exportToSerHM) {
            wordIndexHashMap = new HashMap<String, Integer>();
        }
        Multimap<Float, PhEntry> mp;
        List<PhEntry> entries = Utils.getSubList(getPhEntriesStructure().getEntries(), clientArgs.fromIndex, clientArgs.tillIndex, clientArgs.eachEntry);
        int size = entries.size();
        int dbIndex=1;
        for (int i = clientArgs.exportStartAtEntryIndex; i < size; i++) {
            PhEntry entry = entries.get(i);
            String word = entry.getWord();

            if (clientArgs.exportToDB) {
                mp = getPhEntriesStructure().calcSimilaritiesTo(entry.getWord(), 100000, entries, clientArgs.lowThreshold);


                getPhEntriesStructure().outputResult(mp,entry,  true, clientArgs);
                /**TODO:d OutputTODB muss fillSimpleTableScheme ersetzten/ integrieren...*/
                String rhymes = getPhEntriesStructure().similaritiesToString(mp, false, true, clientArgs);

                word = word.replaceAll("'", "''");
                System.out.println("runExportToDBTask(): Exporting entry to Database: <" + word + "> - <" + rhymes + "> - " + dbIndex + " of " + size+ "\n");
                DBExport.fillSimpleTableScheme(dbIndex,word, rhymes);

            }

            System.out.println("i = " + i + "   db-index = "+dbIndex);

            if (clientArgs.exportToSerHM) {
                wordIndexHashMap.put(word, dbIndex);
            }

            if(clientArgs.exportStopAtEntryIndex!=-1){
                if (i>=clientArgs.exportStopAtEntryIndex)break;
            }
            dbIndex++;
        }
        if (clientArgs.exportToSerHM) {
            serializeWordIndexHashMap(clientArgs);
        }
    }


    /**
     * Exports all iniialised PhEntries to an SQLite Database
     * for every Entry all rhymes get calculated (see EXPORT options in ClientArgs)
     * If set, will also create a serialized HashMap for faster row QUERY in Android App
     * (the row indice of a given word will be found by the hashmap, then the rhymes string will be taken from the database)
     *
     * RELEVANT CLIENTARGS options: exportToDB; FILTER_EQU_ENDS; serializeWordIndexHashMap; exportStopAtEntryIndex
     *
     * @param clientArgs
     * @throws SQLException
     */
    public static void exportToDBALT(ClientArgs clientArgs) throws SQLException {
        if (clientArgs.exportToSerHM) {
            wordIndexHashMap = new HashMap<String, Integer>();
        }
        Multimap<Float, PhEntry> mp;
        List<PhEntry> entries = Utils.getSubList(getPhEntriesStructure().getEntries(), clientArgs.fromIndex, clientArgs.tillIndex, clientArgs.eachEntry);
        int size = entries.size();
        int dbIndex=1;
        for (int i = clientArgs.exportStartAtEntryIndex; i < size; i++) {
            PhEntry entry = entries.get(i);
            String word = entry.getWord();

            if (clientArgs.exportToDB) {
                mp = getPhEntriesStructure().calcSimilaritiesTo(entry.getWord(), 100000, entries, clientArgs.lowThreshold);
                if (clientArgs.filterEquEnds) {
                    PhEntry phE = phEntriesStructure.getEntry(entry.getWord(), true);
                    mp = phEntriesStructure.filterEqualEndingWordsOut(mp, phE, clientArgs.filterEquEnds, clientArgs.fromTopTill);
                }
                /**TODO: hier muss die Methode outputResult() statt similaritiesToString() benutzt werden, und OutputTODB muss fillSimpleTableScheme ersetzten...*/
                String rhymes = getPhEntriesStructure().similaritiesToString(mp, false, true, clientArgs);
                rhymes = rhymes.replaceAll("'", "''");
                word = word.replaceAll("'", "''");
                System.out.println("runExportToDBTask(): Exporting entry to Database: <" + word + "> - <" + rhymes + "> - " + dbIndex + " of " + size+ "\n");
                DBExport.fillSimpleTableScheme(dbIndex,word, rhymes);

            }

            System.out.println("i = " + i + "   db-index = "+dbIndex);

            if (clientArgs.exportToSerHM) {
                wordIndexHashMap.put(word, dbIndex);
            }

            if(clientArgs.exportStopAtEntryIndex!=-1){
                if (i>=clientArgs.exportStopAtEntryIndex)break;
            }
            dbIndex++;
        }
        if (clientArgs.exportToSerHM) {
            serializeWordIndexHashMap(clientArgs);
        }
    }


    public static String getUrl() {
        return "jdbc:sqlite:" + getPath() + getFileName();
    }

    /**
     * Connect to a sample database
     * <p>
     * <p>
     * the database file name
     */
    public static void createNewDatabase(Connection conn) throws SQLException {
        if (conn == null) conn = getConn();
        if (conn != null) {
            //if (statement==null) statement = conn.createStatement();
            //statement.execute("PRAGMA encoding = \"UTF-8\"; ");
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("The driver name is " + meta.getDriverName());
            System.out.println("A new database has been created.");
        }
    }

    private static Connection createConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:sqlite:" + getPath() + getFileName();
        conn = DriverManager.getConnection(url);
        return conn;

    }

    public static void setUpFillTableSimple(String tableName, Statement statement) {

    }

    /**
     * Creates a simple Table, COLUMNS: index(INT) / word(STRING) / rhymes (STRING)
     * it will eat up more space by redundantly storing all rhymes for each entry, but hopefully be faster
     * @param index
     * @param word
     * @param rhymes
     * @throws SQLException
     */
    public static void fillSimpleTableScheme(int index, String word, String rhymes) throws SQLException {
        if (index!=-1){
            index = primaryKeyForTableWords;
        }
        statement.executeUpdate("insert into words values('" + index + "', '" + word + "', '" + rhymes + "')");
        primaryKeyForTableWords++;
    }

    /**
     * TODO
     *
     * Creates a more complex table(s):
     * 1. Option
     * COLUMNS:  index / word(STRING) / rhyme1(INDEX) / rhyme2(INDEX) / rhyme3(INDEX)
     * 2. Option
     * COLUMNS: index / word(STRING) /
     *
     *
     * @param word
     * @param rhymes
     * @throws SQLException
     */
    public static void fillComplexTableScheme(String word, String[] rhymes) throws SQLException {

        statement.executeUpdate("insert into person values(1, 'leo')");
        statement.executeUpdate("insert into person values(2, 'yui')");
    }

    public static void checkResults() {
        ResultSet rs;
        try {
            rs = statement.executeQuery("select * fromIndex person");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (getConn() != null)
                getConn().close();
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e);
        }
    }

    public static void createTableSimple(int nrOfRhymes) throws SQLException {
        //evtl. string größe angeben
        statement.executeUpdate("drop table if exists words");
        statement.executeUpdate("CREATE TABLE words (_id integer PRIMARY KEY, word string, rhymes string);");
    }


    public static void createTableComplex(int nrOfRhymes) throws SQLException {
        statement.executeUpdate("CREATE TABLE words (_id integer PRIMARY KEY, word string);");
        String pt1 = "CREATE TABLE rhymes (_id integer PRIMARY KEY,";
        String pt2 = "";
        for (int i = 0; i < nrOfRhymes; i++) {
            pt2 += "r" + (i + 1) + " integer FOREIGN KEY";
            if (i < nrOfRhymes - 1) pt2 += ", ";
        }
        String pt3 = ");";
        statement.executeUpdate(pt1 + pt2 + pt3);

    }

    public static void createTable(int nrOfRhymes) throws SQLException {
        if (statement == null) statement = conn.createStatement();
        statement.executeUpdate("drop table if exists android_metadata");
        statement.executeUpdate("CREATE TABLE \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US')");
        // Then, it is necessary to rename the primary id field of your tables to "_id" so Android will know where to bind the id field of your tables.
        //statement.executeUpdate()

        statement.executeUpdate("INSERT INTO \"android_metadata\" VALUES ('en_US');");

        // entweder ein alphab. table mit einem wort und einer liste von wörter (könnte sogar ein einzelner comma string sein...) --> schneller
        // oder zwei Table einer mit einer liste von wörtern und ids und ein zweiter mit den relationen --> kleinere Db

        createTableSimple(nrOfRhymes);

        //https://blog.xojo.com/2013/12/04/renaming-columns-in-sqlite-tables/
        //    statement.executeUpdate("ALTER TABLE \"android_metadata\" RENAME TO \"android_metadata_orig\";");
        //    statement.executeUpdate("CREATE TABLE \"android_metadata\"(Name TEXT, Coach TEXT, Location TEXT);");
        //    statement.executeUpdate("INSERT INTO \"android_metadata\"(Name, Coach, Location) SELECT Name, Coach, City FROM \"android_metadata_orig\";");
        //    statement.executeUpdate
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //createNewDatabase(null);
        // conn.createStatement();
        // statement.setQueryTimeout(30); // set timeout to 30 sec.
    }

    public static String getPath() {
        return path;
    }

    public static void setDBFilePath(String path) {
        DBExport.path = path;
    }

    public static Connection getConn() throws SQLException {
        if (conn == null) createConnection();
        return conn;
    }

    public static void setConn(Connection conn) {
        DBExport.conn = conn;
    }

    public static String getFileName() {
        return fileName;
    }

    public static void setDBFileName(String fileName) {
        DBExport.fileName = fileName;
    }
}
