package output;

import client.ClientArgs;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */
public class SysOutSink implements Sink {

    @Override
    public void init(ClientArgs clientArgs) {

    }

    @Override
    public void setQueryWord(String word) {
    }

    @Override
    public void setRhymes(String str) {

    }


    @Override
    public void sink() {

    }

    @Override
    public void sink(String str) {
        System.out.println(str);
    }

    @Override
    public void sink(String[][] str) {
        System.out.println(str.toString());
    }


}
