package output;

import client.ClientArgs;

import java.sql.SQLException;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */
public interface Sink {
    void init(ClientArgs clientArgs) throws SQLException;

    public void setQueryWord(String word);
    public void setRhymes(String str);
    public void sink();
    public void sink(String str);
    public void sink(String[][] str);

}
