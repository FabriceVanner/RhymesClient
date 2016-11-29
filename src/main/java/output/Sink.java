package output;

import client.ClientOptions;

import java.sql.SQLException;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */
public interface Sink {
    void init(ClientOptions clientOptions);
    void openSink() throws Exception,SQLException;
    void closeSink()throws Exception,SQLException;
    void setQueryWord(String word);
    void setRhymes(String str);
    void sink();
    void sink(String str) throws Exception;
    void sink(String[][] str);

}
