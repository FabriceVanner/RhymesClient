package output;

import phonetic_entities.PhEntry;

/**
 * Created by Fabrice Vanner on 09.09.2016.
 */
public class OutputToSysOut extends OutputBase {
    private int nrOfCharsInLine = 0;


    @Override
    public void sendToOutputSink(PhEntry entry, float similarity, boolean groupWithPrecedor) {
        //  delimiter in die Konsole schreiben
        if (nrOfOutputtedEntries > 0) {
            if (groupWithPrecedor) {
                System.out.print(clientArgs.outputDelimiterForGrouped);
            } else {
                System.out.print(clientArgs.outputDelimiter);
            }
        }

        if (clientArgs.delimiterSeperated) {
            System.out.print(String.format("# %5f%s\n", similarity, entry.toString(clientArgs.printDetail)));
            return;
        }
        //  delimiter in die Konsole schreiben
        if (clientArgs.consoleWidth != -1) {
            if (nrOfCharsInLine + entry.getWord().length() > clientArgs.consoleWidth) {
                System.out.println();
                nrOfCharsInLine = 0;
            }
            nrOfCharsInLine += entry.getWord().length() + clientArgs.outputDelimiter.length();
        }
        System.out.print(entry.getWord());
    }

    @Override
    void initOutput() {
    }

    @Override
    public void processOutput() {

    }
}
