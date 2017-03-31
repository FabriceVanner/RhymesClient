package client;
import operational_entities.WordPair;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Fabrice Vanner on 20.12.2016.
 */
public class RhymesClientDirectAccess {

    public static void main(String [] args){
        RhymesClient rC = null;
        ClientOptions cO = new ClientOptions();
        cO.setDebugOptions();


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

        //rC.setSinkAndFormat(cO);
        WordPair wP = new WordPair("Ä","Auroras");
        System.out.println(rC.runOneOnOneQuery(wP)+" - "+wP.toString());

    }
}
