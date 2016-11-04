package output;

import client.ClientArgs;
import phonetic_entities.PhEntry;

/**
 * Created by Fabrice Vanner on 30.09.2016.
 */
public class OutputToStringArray extends OutputBase {
    ClientArgs clientArgs;
    boolean twoDimensionalArray = true;

    String[][] outputArr;



    @Override
    public void addToOutput(PhEntry entry, float similarity) {

    }

    @Override
    void sendToOutputSink(PhEntry entry, float similarity, boolean groupWithPrecedor) {

    }

    @Override
    void initOutput() {
        outputArr= new String[2][];
    }

    @Override
    public void processOutput() {

    }

}
