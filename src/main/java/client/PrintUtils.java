package client;

import static phonetic_entities.PhPart.floatDecPlaces;
import static phonetic_entities.PhPart.floatMinPlaces;

/**
 * Created by Fabrice Vanner on 06/10/2017.
 */
public class PrintUtils {

    /**
     * intended for multiline info-printing to sysout
     * adds the rhymesArrIndex-var similarity of this Part to a one dimensional string array
     * @param similarity
     * @param out
     * @param basicArrayFieldsCntVerticalOffset
     * @return
     */
    public static String[] addVerticalSimilarity(float similarity, String[] out, int basicArrayFieldsCntVerticalOffset){
        float decRoundFactor=1f;
        for(int i = 0; i< floatDecPlaces; i++) decRoundFactor*=10f;
        if (similarity != -1) {
            String similarityAsStr = Float.toString(Math.round(similarity * decRoundFactor) / decRoundFactor);//runden
            if (similarityAsStr.length() > (floatMinPlaces + floatDecPlaces)) {
                similarityAsStr = similarityAsStr.substring(0, (floatMinPlaces + floatDecPlaces));
            } else if(similarityAsStr.length()<(floatMinPlaces + floatDecPlaces)){
                int diff = (floatMinPlaces + floatDecPlaces -similarityAsStr.length());
                for (int j =0;j<diff;j++){similarityAsStr+='0';}//zeropadding
            }
            for (int j = 0; j < floatMinPlaces + floatDecPlaces; j++)
                out[j+basicArrayFieldsCntVerticalOffset] += similarityAsStr.charAt(j);//float untereindander charweise schreiben
        }
        return out;
    }
}
