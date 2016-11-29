package output;

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
        StringBuilder out  = new StringBuilder();
        //  delimiter in die Konsole schreiben
        switch (clientOptions.outDelimiting){
            case LINE:
                out.append(String.format("# %5f%s\n", similarity, entry.toString(clientOptions.printDetail)));
                return out.toString();
             case DELIM:
                // line breaks einfügen, wenn zu lang...
                if (clientOptions.consoleWidth != -1) {
                    if (nrOfCharsInLine + entry.getWord().length() > clientOptions.consoleWidth) {
                        out.append("\n");
                        nrOfCharsInLine = 0;
                    }
                    nrOfCharsInLine += entry.getWord().length() + clientOptions.outputDelimiter.length();
                    out.append(clientOptions.outputDelimiter);
                }
                break;
            case GROUP:
                if (nrOfOutputtedEntries > 0) {
                    if (groupWithPrecedor) {
                        out.append(clientOptions.outputDelimiterForGrouped);
                    } else {
                        out.append(clientOptions.outputDelimiter);
                    }
                }
                break;
        }
        out.append(entry.getWord());
        return out.toString();
    }

    @Override
    void sendRhymeToSink(Object out)throws Exception {
        sink.sink((String)out);
    }



    @Override
    public void processOutput() {

    }
}
