package output;

import phonetic_entities.PhEntry;

/**
 * Created by Fabrice Vanner on 09.09.2016.
 */
public class StringOut extends OutputBase {
    private int nrOfCharsInLine = 0;

    /**
     * @param sink where the formatted Output goes
     */
    public StringOut(Sink sink) {
        super(sink);
    }

    @Override
    Object formatOutput(PhEntry entry, float similarity, boolean groupWithPrecedor) {
        StringBuilder out  = new StringBuilder();
        //  delimiter in die Konsole schreiben
        if (nrOfOutputtedEntries > 0) {
            if (groupWithPrecedor) {
                out.append(clientArgs.outputDelimiterForGrouped);
            } else {
                out.append(clientArgs.outputDelimiter);
            }
        }
        if (clientArgs.delimiterSeperated) {
            out.append(String.format("# %5f%s\n", similarity, entry.toString(clientArgs.printDetail)));
            return out.toString();
        }
        //  delimiter in die Konsole schreiben
        if (clientArgs.consoleWidth != -1) {
            if (nrOfCharsInLine + entry.getWord().length() > clientArgs.consoleWidth) {
                out.append("\n");
                nrOfCharsInLine = 0;
            }
            nrOfCharsInLine += entry.getWord().length() + clientArgs.outputDelimiter.length();
        }
        out.append(entry.getWord());
        return out.toString();
    }

    @Override
    void sendRhymesToSink(Object out) {
        sink.sink((String)out);
    }



    @Override
    public void processOutput() {

    }
}
