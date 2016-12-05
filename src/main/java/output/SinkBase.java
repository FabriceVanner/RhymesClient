package output;

import client.ClientOptions;

/**
 * Created by Fabrice Vanner on 04.12.2016.
 */
public abstract class SinkBase implements Sink {
    protected String[]rhymes;
    protected int rhymesArrIndex =0;
    protected ClientOptions clientOptions;
    protected String word;

    public void init(ClientOptions clientOptions) {
        rhymes = new String[clientOptions.fromTopTill];
        this.clientOptions = clientOptions;
    }

    public void appendRhyme(String str) {
        rhymes[rhymesArrIndex]=str;
        rhymesArrIndex++;
    }

    public void setQueryWord(String str){
        this.word = str;
    }

    public void sink()throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<rhymesArrIndex;i++){
            stringBuilder.append(rhymes[i]);
        }
        sink(stringBuilder.toString());
        rhymesArrIndex = 0;
    }

}
