package client;

import operational_entities.WordPair;
import phonetic_entities.PhEntry;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Fabrice Vanner on 20.12.2016.
 */
public class RhymesClientDirectAccess {

    private static RhymesClient rC;

    public static void main(String[] args) {
        ClientOptions cO = new ClientOptions();
        cO.setDebugOptions();
        System.err.println(" - DEBUG OPTIONS ACTIVATED - ");
        startEmptyClient(cO);

        System.out.println("\n\n\n");
        //rC.setSinkAndFormat(cO);


        //  simpleQueryWordPairWrapper("TESTWORD1","TESTWORD2");
        //  directIPACompare("a","ʊ");
        //directIPACompare("at","ʊt");
        directIPACompare("atʊ", "ata");
    }

    /**
     * Client without loaded and initialised DB
     */
    private static void startEmptyClient(ClientOptions cO) {
        rC = new RhymesClient();
        rC.setClientOptions(cO);
        //rC.setPhEntriesStructure(new PhEntriesStructure());
    }

    private static void startClient(ClientOptions cO) {
        try {
            rC = new RhymesClient(cO);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void directIPACompare(String IPA1, String IPA2) {
        PhEntry phEntry1 = null;
        try {
            phEntry1 = new PhEntry("TEST-IPA-Entry 1", IPA1);
            phEntry1.init();
            //System.out.println( phEntry1.toString(true));
            PhEntry phEntry2 = new PhEntry("TEST-IPA-Entry 2", IPA2);
            phEntry2.init();
            //System.out.println(phEntry2.toString(true));
            System.out.println(phEntry1.calcSimilarity(phEntry2, 0.3f));
        } catch (SignNotSuittedException e) {
            e.printStackTrace();
        }
    }


    private static void simpleQueryWordPairWrapper(String word1, String word2) {
        WordPair wP = new WordPair(word1, word2);
        System.out.println(rC.runOneOnOneQuery(wP, 0.4f) + " - " + wP.toString());
    }
}