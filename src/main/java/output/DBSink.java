package output;

import client.ClientOptions;
import client.PhEntriesStructure;
import client.RhymesClient;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */

//TODO:  DBExport Methoden in DBSink integrieren
public class DBSink extends SinkBase {
    Class outputType;
    //ClientOptions clientOptions;

    //TODO: dbIndex doesn't work if export won't start from beginning
    private int dbIndex=1;

    private Connection conn;
    public Statement statement;
    private String path; //= "/home/entwickler01/Downloads";
    private int primaryKeyForTableWords = 1;
    private PhEntriesStructure phEntriesStructure;
    private HashMap<String, Integer> wordIndexHashMap;


    @Override
    public void init(ClientOptions clientOptions)  {
        setDBFilePath(RhymesClient.clientsFolderPath);
        setPhEntriesStructure(phEntriesStructure);
        if (clientOptions.exportToSerHM) {
            wordIndexHashMap = new HashMap<String, Integer>();
        }
        super.init(clientOptions);
    }

    @Override
    public void openSink()throws SQLException {
        getConn();
        if (clientOptions.exportStartAtEntryIndex == 0) {
            createNewDatabase(null);
            createTable(clientOptions.fromTopTill);
        }
    }

    @Override
    public void closeSink() {
        closeConnection();
        serializeWordIndexHashMap(clientOptions);
        /** TODO: hier noch irgendwie aufräumen?*/
    }

    private String prepareStringForDB(String str){
        return str.replaceAll("'", "''");
    }


    @Override
    public void setQueryWord(String word) {
        this.word = prepareStringForDB(word);
    }

    public void setRhymes(String rhymes){


    }




    @Override
    public void sink(String str)throws Exception {
        this.dbIndex++;
        str = prepareStringForDB(str);;
        RhymesClient.prL2("DBSink.sink(): Sinking entry Nr "+ dbIndex+" to Db: <" + word + "> - <" + str.replaceAll("\n","\\\\n ") +"> \n");
            fillSimpleTableScheme(dbIndex,word, str);
        if (clientOptions.exportToSerHM) {
            wordIndexHashMap.put(word, dbIndex);
        }
    }

    @Override
    public void sink(String[][] str) {

    }

    @Override
    public void flush() {

    }


    public PhEntriesStructure getPhEntriesStructure() {
        return phEntriesStructure;
    }

    public void setPhEntriesStructure(PhEntriesStructure phEntriesStructure) {
        this.phEntriesStructure = phEntriesStructure;
    }

    /**
     * Creates a
     * @param clientArgs
     */
    public void serializeWordIndexHashMap(ClientOptions clientArgs){
        FileOutputStream fileOutputStream;
        File hmOut = new File(getDbFilePath() + "/" + clientArgs.exportToSerHM_Filename);
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




    public String getUrl() {
        return "jdbc:sqlite:" + getDbFilePath() + clientOptions.exportToDBFilename;
    }

    /**
     * Connect to a sample database
     * <p>
     * <p>
     * the database file name
     */
    public void createNewDatabase(Connection conn) throws SQLException {
        if (conn == null) conn = getConn();
        if (conn != null) {
            //if (statement==null) statement = conn.createStatement();
            //statement.execute("PRAGMA encoding = \"UTF-8\"; ");
            DatabaseMetaData meta = conn.getMetaData();
            RhymesClient.prL1("The driver name is " + meta.getDriverName() + " A new database has been created");
        }
    }

    private Connection createConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:sqlite:" + getDbFilePath() + clientOptions.exportToDBFilename;
        conn = DriverManager.getConnection(url);
        return conn;

    }

    public void setUpFillTableSimple(String tableName, Statement statement) {

    }

    /**
     * Creates a simple Table, COLUMNS: index(INT) / word(STRING) / rhymes (STRING)
     * it will eat up more space by redundantly storing all rhymes for each entry, but hopefully be faster
     * @param index
     * @param word
     * @param rhymes
     * @throws SQLException
     */
    public void fillSimpleTableScheme(int index, String word, String rhymes) throws SQLException {
        if (index!=-1){
            index = primaryKeyForTableWords;
        }
        statement.executeUpdate("insert into wordsArrLi values('" + index + "', '" + word + "', '" + rhymes + "')");
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
    public void fillComplexTableScheme(String word, String[] rhymes) throws SQLException {

        statement.executeUpdate("insert into person values(1, 'leo')");
        statement.executeUpdate("insert into person values(2, 'yui')");
    }

    public void checkResults() {
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

    public void closeConnection() {
        try {
            if (getConn() != null)
                getConn().close();
        } catch (SQLException e) {
            // connection close failed.
            System.err.println(e);
        }
    }

    public void createTableSimple(int nrOfRhymes) throws SQLException {
        //evtl. string größe angeben
        statement.executeUpdate("drop table if exists wordsArrLi");
        statement.executeUpdate("CREATE TABLE wordsArrLi (_id integer PRIMARY KEY, word string, rhymes string);");
    }


    public void createTableComplex(int nrOfRhymes) throws SQLException {
        statement.executeUpdate("CREATE TABLE wordsArrLi (_id integer PRIMARY KEY, word string);");
        String pt1 = "CREATE TABLE rhymes (_id integer PRIMARY KEY,";
        String pt2 = "";
        for (int i = 0; i < nrOfRhymes; i++) {
            pt2 += "r" + (i + 1) + " integer FOREIGN KEY";
            if (i < nrOfRhymes - 1) pt2 += ", ";
        }
        String pt3 = ");";
        statement.executeUpdate(pt1 + pt2 + pt3);

    }

    public void createTable(int nrOfRhymes) throws SQLException {
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


    public String getDbFilePath() {
        return path;
    }

    public void setDBFilePath(String path) {
        this.path = path;
    }

    public Connection getConn() throws SQLException {
        if (conn == null) createConnection();
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }


}
