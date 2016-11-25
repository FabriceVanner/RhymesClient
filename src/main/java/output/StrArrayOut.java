package output;

import client.ClientArgs;
import phonetic_entities.PhEntry;

import java.sql.SQLException;

/**
 * Created by Fabrice Vanner on 30.09.2016.
 */
public class StrArrayOut extends OutputBase {
    ClientArgs clientArgs;
    boolean twoDimensionalArray = true;

    String[][] outputArr;

    /**
     * @param sink where the formatted Output goes
     */
    public StrArrayOut(Sink sink) {
        super(sink);
    }


    @Override
    public void addToOutput(PhEntry entry, float similarity) {

    }

    @Override
    Object formatOutput(PhEntry entry, float similarity, boolean groupWithPrecedor) {
        return null;
    }

    @Override
    void sendRhymesToSink(Object out) {
        sink.sink((String [][])out);
    }


    @Override
    public void initSink() {
        try {
            sink.init(clientArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        outputArr= new String[2][];
    }



    @Override
    public void processOutput() {

    }

}
