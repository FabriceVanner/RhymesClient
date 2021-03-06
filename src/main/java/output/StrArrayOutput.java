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
    public boolean addToOutput(PhEntry entry, float similarity) {

        return true;
    }

    @Override
    Object formatOutput(PhEntry entry, float similarity, boolean groupWithPrecedor) {
        return null;
    }

    @Override
    public boolean appendRhymeToSink(Object out) {
        sink.sink((String [][])out);
        return true;
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
