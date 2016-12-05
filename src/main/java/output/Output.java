package output;

import client.ClientOptions;
import client.PhEntriesStructure;
import phonetic_entities.PhEntry;

import java.sql.SQLException;
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
    boolean addToOutput(PhEntry entry, float similarity) throws Exception;
    void openSink() throws SQLException;
    void flushSink();
    void closeSink() throws SQLException;
    void setQueryEntry(PhEntry phEntry);

   // void appendRhymeToSink(PhEntry entry);
    void processOutput();


}
