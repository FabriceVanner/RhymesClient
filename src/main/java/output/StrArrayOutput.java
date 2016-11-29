package output;

import phonetic_entities.PhEntry;

/**
 * Created by Fabrice Vanner on 30.09.2016.
 */
public class StrArrayOutput extends OutputBase {
    boolean twoDimensionalArray = true;

    String[][] outputArr;

    /**
     * @param sink where the formatted Output goes
     */
    public StrArrayOutput(Sink sink) {
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
    void sendRhymeToSink(Object out) {
        sink.sink((String [][])out);
    }


    @Override
    public void initSink() {
            sink.init(clientOptions);
        outputArr= new String[2][];
    }



    @Override
    public void processOutput() {

    }

}
