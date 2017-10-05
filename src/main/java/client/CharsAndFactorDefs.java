package client;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fab on 16.05.2015.
 */
public final class CharsAndFactorDefs {

//  CHARS AND StRINGS
    private static final Map<Character, String> replacementMap = new HashMap<Character, String>() {{
        put('ã', "a");
        put('ɡ', "g");
        put(':', "ː");
        put('ː', "ː"); // \u02d0
        put('ã', "a");
        put('\'', "ˈ");
        put('ˈ', "ˈ");
        put('ˈ', "ˈ"); //\u02c8
        put('̍', "ˈ");// \u030d
        put('ˌ', "ˌ");
        put('ˌ', "ˌ");//  \u02cc
        put('õ', "õ");
        put('ņ', "n̩");
    }};
    private static final char[] skipableChars = {'\u200B', '(', ')', '1', '2', '4', '[', ']', '@'};
    private static final String toStringEndPadding = "\t";

    private static final char[][] diphtongs={{'a','ɪ'},{'a','ʊ'},{'ɛ','ɪ'},{'ɔ','ɪ'},{'ʊ','ɪ'}};

    public static String getUniCodeStr(char ch) {
        return "\\u" + Integer.toHexString(ch | 0x10000).substring(1);
    }

    public static Map<Character, String> getReplacementMap() {
        return replacementMap;
    }

    public static char[] getSkipableChars() {
        return skipableChars;
    }

    public static String getToStringEndPadding() {
        return toStringEndPadding;
    }


// WEIGHTS

    /**
     * partIndiceWeights are used to put more weight / emphasis on the first parts (the last parts of the word)
     *  because the more they are in front(in the words back) the more important they are for the rhyme
     *
     * the Weight of the index defined partposition, applied by index to the parts-arraylist in CLASS PhPartsStructure
     * [0][...]contains the factors, [1][...] contains the normalisation-sum to apply afterwards
     *
     * results to :
     * partIndiceWeights[0]{1.0,0.5,0.3,0.25 ...}
     * partIndiceWeights[1]{1.0,1.5,1.8,2.05 ...}
     */
    private static int nrOfPartIndiceWeights = 40;
    private static Float[][] partIndiceWeights = new Float[2][nrOfPartIndiceWeights];
    static {
        float sum = 0;
        for (int i = 0; i < nrOfPartIndiceWeights; i++) {
            partIndiceWeights[0][i] = 1.f /(i + 1);
            sum += partIndiceWeights[0][i];
            partIndiceWeights[1][i] = sum;
        }
    }

    public static Float[][] getPartIndiceWeights() {
        return partIndiceWeights;
    }


    /** value to be subtracted fromIndex simi-val if stresses of two parts are not equal*/
    private static final float stressPunishment = 0.0f;

    public static float getStressPunishment() {
        return stressPunishment;
    }


}
