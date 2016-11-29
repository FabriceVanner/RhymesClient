package output;

import client.ClientOptions;
import client.PhEntriesStructure;
import phonetic_entities.PhEntry;

import java.util.Set;

import static client.ClientOptions.OutFilterOption;

/**
 * Created by Fabrice Vanner on 09.09.2016.
 */
public interface Output {

    /**
     * Filter, Grouping etc options functions to be performed on the output
     * options maybe represented as enums
     */
    void addOption(OutFilterOption option);
    void setOptions(Set<ClientOptions.OutFilterOption> options);
    void removeOption(ClientOptions.OutFilterOption option);


    void init(ClientOptions clientOptions, PhEntriesStructure phEntriesStructure);
    void init(ClientOptions clientOptions);
    void addToOutput(PhEntry entry, float similarity) throws Exception;
    void openSink() throws Exception;
    void closeSink() throws Exception;
    void setQueryEntry(PhEntry phEntry);

   // void sendRhymeToSink(PhEntry entry);
    void processOutput();


}
