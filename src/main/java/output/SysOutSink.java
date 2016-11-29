package output;

import client.ClientOptions;

/**
 * Created by Fabrice Vanner on 22.11.2016.
 */
public class SysOutSink implements Sink {

    @Override
    public void init(ClientOptions clientOptions) {

    }

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
