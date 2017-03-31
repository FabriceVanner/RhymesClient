package wiktionary_parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;




/**
 ####################################################
 DESTINATION-DB-structue in SQL:
 ####################################################
 CREATE TABLE woerter
 (
 id serial NOT NULL,
 wort character varying(40),
 ipa character varying(40),
 reim character varying(20),
 worttrennung character varying(55),
 code0 bigint,
 code1 bigint,
 code2 bigint,
 CONSTRAINT woerter_pkey PRIMARY KEY (id),
 CONSTRAINT constraintname UNIQUE (wort)
 )
 WITH (
 OIDS=FALSE
 );
 ALTER TABLE woerter
 OWNER TO "MediaWikiPostgresUser";
 ####################################################

 */


/**
 * this class is only usefull for parsing wiktionary articles on a wikimedia server
 */
public class MediawikiParser {
    SqlAndRegExStrings sqlAndRegEx = new SqlAndRegExStrings();
    ArrayList<String> sqlExceptEntriesArr= new ArrayList<String>();
    ArrayList<String> failedPatternsArr = new ArrayList<String>();
    String rauten ="###################################################";
    public static void main(String[] args) {
//        ColognePhoneticTest  cpT = new ColognePhoneticTest();
        MediawikiParser mwP = new MediawikiParser();
        mwP.run();
    }
    private void run(){
        ;
        Connection mediaWikiConn = connectToPGDatabaseOrDie("jdbc:postgresql://localhost:5432/MediaWikiPostgres","MediaWikiPostgresUser","rQHEOdZgpIUYlD3Q1lxM"); //sql_MedWikiJoinTitleAndText
        Connection lautschriftConn = connectToPGDatabaseOrDie("jdbc:postgresql://localhost:5432/Lautschrift",  "MediaWikiPostgresUser", "rQHEOdZgpIUYlD3Q1lxM");
        transmitArticleContents(mediaWikiConn, lautschriftConn, sqlAndRegEx.sql_MedWikiJoinTitleAndText, 446844, 191001, "");
    }

    //191001-->255843

    /**
     * sends sqlQuery to src-db in batches of 500,  puts results in dst-db
     * outputs process to console, after finishing
     * @param mediaWikiConn  to source database ( mediawiki )
     * @param lautschriftConn to dest  database (see comment, at filebegining)
     * @param rowsToProcess nr of rows to process
     * @param sqlQuery query that returns rows with column-names: "page_title", "old_text"
     * @param startIndex sql starts with 1!
     */
   private void transmitArticleContents(Connection mediaWikiConn, Connection lautschriftConn, String sqlQuery, int rowsToProcess, int startIndex, String errorLogBaseName) {
        Map<String, String> titleStrContentMap;
        Map<String,WiktionArticle> titleWiktionContMap;
       int nrOfRowsPerBatch = 500;
       int nrOfSrcRowsToProcess = (rowsToProcess - (startIndex-1));
       int nrOfBatches=1;
       int i;
       if (nrOfSrcRowsToProcess >nrOfRowsPerBatch){ //calculate nr of nessecary batches
           nrOfBatches=nrOfSrcRowsToProcess / nrOfRowsPerBatch;
           if (rowsToProcess %nrOfRowsPerBatch!=0){nrOfBatches++;}
       }
       BufferedWriter bw = null;


       try {
           bw = new BufferedWriter(new FileWriter("test", true));
           Statement st = mediaWikiConn.createStatement();
           for(i=0;i<nrOfBatches;i++){
               try {

                   System.out.println(rauten+"\nBatch rhymesArrIndex = " + i + " of "+nrOfBatches+"\n"+rauten);
                   titleStrContentMap = new HashMap<>();
                   startIndex = startIndex + i * nrOfRowsPerBatch;
                   String queryWithOffsetAndLimit = sqlQuery + "OFFSET " + startIndex + "LIMIT " + nrOfRowsPerBatch;

                   System.out.println("st.ExecuteQuery: " + queryWithOffsetAndLimit.replace("\n", "").replace("\r", ""));

                   ResultSet rs = st.executeQuery(queryWithOffsetAndLimit);

                   System.out.println("writing titleStrContentMap... ");

                   while (rs.next()) {
                       titleStrContentMap.put(rs.getString("page_title"), rs.getString("old_text"));
                   }
                   rs.close();
                   System.out.println("translating Batch by getWiktionMap()...");
                   titleWiktionContMap = getWiktionMap(titleStrContentMap, bw);
                   System.out.println("titleStrContentMap.size() = " + titleStrContentMap.size() + "\titleWiktionContMap.size() = "+titleWiktionContMap.size());
                   System.out.println("sendToDestDB()...");
                   sendToDestDB(lautschriftConn, titleWiktionContMap,bw);
                   bw.flush();
               }catch (SQLException ex){
                   String str= "transmitArticleContents: Threw a SQLException. During iteration rhymesArrIndex = "+i+"\tstartIndex = "+startIndex;
                   bw.write(str);
                   bw.newLine();
                   bw.flush();
                   System.err.println(str);
                   System.err.println(ex.getMessage());
               }
           }
           st.close();
       } catch (SQLException se) {
           System.err.println(se.getMessage());
       } catch (IOException e) {
           e.printStackTrace();
       }finally {
           if (bw != null) try {
               bw.close();
           } catch (IOException e2) {
           }
       }
       System.out.println(rauten+"\nsqlExceptEntriesArr\n"+rauten);
       for (String s : sqlExceptEntriesArr) { System.out.println(s); }
       System.out.println(rauten+"\nfailedPatternsArr\n"+rauten);
       for (String s : failedPatternsArr) { System.out.println(s); }
   }


    /**
     * parses all fields in content strings into WiktionAtricle Object
     * @param titStrContMap Map containing article-titles as key and the article XML-contents as value
     * @return a map
     */
    private Map<String,WiktionArticle> getWiktionMap(Map<String, String> titStrContMap, BufferedWriter bw) throws IOException {
        Boolean print = false;
        int numberOfExc = 0;
        int numberOfFailedPattern=0;
        int numberOfPuttedWiktionEntries=0;
        String failedPatternEntries="";
        HashMap<String,WiktionArticle> titleWiktionContMap= new HashMap<String,WiktionArticle>();
        Iterator<Map.Entry<String, String>> iterator = titStrContMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (print){
                String ss = (entry.getValue().length()>20)? entry.getValue().substring(0, 20)+" ..." : entry.getValue();
                System.out.println("Key = "+entry.getKey() + " - "+"Val = "+ ss);
            }
            try {
                WiktionArticle wa = RegExParser.filterAndGetMatches(entry.getKey(),entry.getValue(), bw,false,false,false,"Deutsch");
                if ((wa ==null)){
                    failedPatternEntries +=  entry.getKey()+",  ";
                    numberOfFailedPattern++;
                    failedPatternsArr.add(entry.getKey());
                    continue;
                }
                titleWiktionContMap.put(entry.getKey(), wa);
                numberOfPuttedWiktionEntries++;
            }catch(IllegalStateException ex){
                numberOfExc++;
                String str = "getWiktionMap(): entry <" + entry.getKey() +">    threw IllegalStateException ";
                bw.write(str);
                bw.newLine();
                bw.flush();
                System.err.println(str);
            }
           // if (numberOfExc>50){break;}
        }
        System.out.println("numberOfFailedPattern:   "+numberOfFailedPattern+"  :  " + failedPatternEntries);
        System.out.println("numberOfPuttedWiktionEntries: " + numberOfPuttedWiktionEntries);
        return titleWiktionContMap;
    }




    // Posgres: C:/PostgreSQL/bin/pg_ctl.exe runservice -N "PostgreSQL" -D "C:/PostgreSQL/data" -w
    //C:/PostgreSQL/bin/pg_ctl.exe start -N "PostgreSQL" -D "C:/PostgreSQL/data" -w
    //netstat -ano | Select-String "8080"
    //-> procid 9508
    // Get-Process -Id 9508

    /**
     * transmitts the wiktion-articles of the map as rows to the dest db
     * @param conn the connection to the destination db (PG)
     * @param wiktionObjs a map containing article names as keys, article fields in one WikitionArticle-obj as val
     * @throws SQLException
     */
    private void sendToDestDB(Connection conn, Map<String, WiktionArticle> wiktionObjs, BufferedWriter bw) throws SQLException, IOException {
        Iterator<Map.Entry<String, WiktionArticle>> iterator = wiktionObjs.entrySet().iterator();
        PreparedStatement st1 = conn.prepareStatement(sqlAndRegEx.sqlPrepINSERTINTOLautschriftDb);
        int numberOfExc = 0;
        int j=0;
        while (iterator.hasNext()) {
            if(j%100==0) System.out.println("sendToDestDB(): j = " + j + "of " + wiktionObjs.size());
            Map.Entry<String, WiktionArticle> entry = iterator.next();
            st1.setString(1, entry.getKey());
            WiktionArticle wa = entry.getValue();
            if (wa.LautschriftSimple ==null) continue; // don't need those
            st1.setString(2, wa.LautschriftSimple);
            st1.setString(3, wa.ReimLS);
            st1.setString(4, wa.WorttrennungSimple);
            try {
                st1.executeUpdate();
            } catch (SQLException ex) {
                String str="\"sendToDestDB(): SQLException at entry: \" + entry.getKey() +\"  PrepStatement :\"+st1.toString().replace(\"\\n\", \"\").replace(\"\\r\", \"\")";
                System.err.println(str);
                bw.write(str);
                bw.newLine();
                sqlExceptEntriesArr.add(entry.getKey()); // collecting failed entries

                numberOfExc++;
                if (numberOfExc >50){
                    System.err.println("sendToDestDB(): numberOfExc >50 --> breaking");
                    System.err.println(ex.getMessage());break;}
            }
            j++;
        }
        st1.close();
    }


    public Connection connectToPGDatabaseOrDie(String databaseURL, String user, String pw) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(databaseURL, user, pw);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(2);
        }
        return conn;
    }



}

