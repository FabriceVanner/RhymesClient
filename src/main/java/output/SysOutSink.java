package output;

import client.RhymesClient;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */
public class SysOutSink extends SinkBase {


    @Override
    public void openSink() {

    }

    @Override
    public void closeSink() {

    }

    @Override
    public void setQueryWord(String word) {
    }

    @Override
    public void setRhymes(String str) {

    }

    @Override
    public void sink(String str) {
        RhymesClient.pr(str);
    }

    @Override
    public void sink(String[][] str) {
        System.out.println(str.toString());
    }

    @Override
    public void flush() {

    }


}
