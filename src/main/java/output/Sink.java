package output;

import client.ClientOptions;

import java.sql.SQLException;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */
public interface Sink {
    void init(ClientOptions clientOptions);
    void openSink() throws SQLException;
    void closeSink()throws SQLException;
    void setQueryWord(String word);
    void setRhymes(String str);
    //void setRhyme(String str);
    void appendRhyme(String str);
    void sink()throws Exception;
    void sink(String str) throws Exception;
    void sink(String[][] str);
    void flush();

}
