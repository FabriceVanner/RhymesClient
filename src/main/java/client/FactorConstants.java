package client;

/**
 * Created by Fabrice Vanner on 05/10/2017.
 */
public class FactorConstants {


// WEIGHTS

    static {
        float sum = 0;
        for (int i = 0; i < FactorConstants.nrOfPartIndiceWeights; i++) {
            FactorConstants.partIndiceWeights[0][i] = 1.f /(i + 1);
            sum += FactorConstants.partIndiceWeights[0][i];
            FactorConstants.partIndiceWeights[1][i] = sum;
        }
    }

    public static void initPartIndiceWeights(){
        float sum = 0;
        for (int i = 0; i < FactorConstants.nrOfPartIndiceWeights; i++) {
            partIndiceWeights[0][i] = 1.f /(i + 1);
            sum += partIndiceWeights[0][i];
            partIndiceWeights[1][i] = sum;
        }
    }

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
    static int nrOfPartIndiceWeights = 40;
    private static Float[][] partIndiceWeights = new Float[2][nrOfPartIndiceWeights];

    public static Float[][] getPartIndiceWeights() {
      //  initPartIndiceWeights();
        return partIndiceWeights;
    }

    /** value to be subtracted fromIndex simi-val if stresses of two parts are not equal*/
    private static final float stressPunishment = 0.0f;

    public static float getStressPunishment() {
        return stressPunishment;
    }


    public static float getVowelSignTypeWeight() {
        return vowelSignTypeWeight;
    }

    public static float getConsonantTypeWeight() {
        return consonantTypeWeight;
    }

    public static float getOtherSignTypeWeight() {
        return otherSignTypeWeight;
    }

    //vowel and Conso should add up to 1.0
    //the Weights of the SignType Enums vowels, Consos... used to weight PhSignMs in  PhPartsStructure
   public static float vowelSignTypeWeight =  0.9f;
   public static float consonantTypeWeight = 0.1f;
   public static float otherSignTypeWeight = 0.0f;;

    /** the number of shifts allowed to search for optimale reim deckung / vergleich*/
    public static int maxPhPartsShifts = 2;

    public static void setPartIndiceWeights(Float[][] partIndiceWeights) {
        FactorConstants.partIndiceWeights = partIndiceWeights;
    }
}
