package output;

import client.ClientArgs;
import client.PhEntriesStructure;
import phonetic_entities.PhEntry;

import java.util.Set;

/**
 * Created by Fabrice Vanner on 09.09.2016.
 */
public interface Output {

    /**
     * Filter, Grouping etc options functions to be performed on the output
     * options maybe represented as enums
     */
    void addOption(ClientArgs.OutputOptions option);
    void setOptions(Set<ClientArgs.OutputOptions> options);
    void removeOption(ClientArgs.OutputOptions option);


    void init(ClientArgs clientArgs, PhEntry queryEntry, PhEntriesStructure phEntriesStructure);
    void init(ClientArgs clientArgs, PhEntry filterEntry);
    void addToOutput(PhEntry entry, float similarity);

   // void sendRhymesToSink(PhEntry entry);
    void processOutput();


}
