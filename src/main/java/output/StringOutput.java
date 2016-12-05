package output;

import client.RhymesClient;
import phonetic_entities.PhEntry;

/**
 * Created by Fabrice Vanner on 09.09.2016.
 */
public class StringOutput extends OutputBase {
    private int nrOfCharsInLine = 0;

    /**
     * @param sink where the formatted Output goes
     */
    public StringOutput(Sink sink) {
        super(sink);
    }

    @Override
    Object formatOutput(PhEntry entry, float similarity, boolean groupWithPrecedor) {
        StringBuilder outStrBuild = new StringBuilder();
        //  delimiter in die Konsole schreiben
        switch (clientOptions.outDelimiting) {
            case FACTOR_LINE:
                outStrBuild.append(String.format("# %5f%s\n", similarity, entry.toString(clientOptions.printDetail)));
                return outStrBuild.toString();
            case DELIM:
                if (nrOfOutputtedEntries > 0) {
                    // line breaks einfügen, wenn zu lang...
                    if (clientOptions.consoleWidth != -1) {
                        if (nrOfCharsInLine + entry.getWord().length() > clientOptions.consoleWidth) {
                            outStrBuild.append("\n");
                            nrOfCharsInLine = 0;
                        }
                        nrOfCharsInLine += entry.getWord().length() + clientOptions.outputDelimiter.length();
                    }
                    outStrBuild.append(clientOptions.outputDelimiter);
                }
                break;
            case GROUP:
                if (nrOfOutputtedEntries > 0) {
                    if (groupWithPrecedor) {
                        outStrBuild.append(clientOptions.outputDelimiterForGrouped);
                    } else {
                        outStrBuild.append(clientOptions.outputDelimiter);
                    }
                }
                break;
        }
        outStrBuild.append(entry.getWord());
        return outStrBuild.toString();
    }

    @Override
    public boolean appendRhymeToSink(Object out) throws Exception {

        switch (clientOptions.outBatch) {
            case ONE:
                sink.sink((String) out);
                break;
            case ALL:
                sink.appendRhyme((String) out);
                if (nrOfOutputtedEntries == clientOptions.fromTopTill - 1) {
                    sink.sink();
                }
                break;
        }
        if (nrOfOutputtedEntries == clientOptions.fromTopTill - 1) {
            nrOfOutputtedEntries = 0;
            RhymesClient.prL3("Reached --fromTopTill = "+clientOptions.fromTopTill);
            return false;
        }
        super.appendRhymeToSink(out);
        return true;
    }


    @Override
    public void processOutput() {

    }
}
