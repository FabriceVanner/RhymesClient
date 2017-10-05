package client;

import phonetic_entities.PhPart;
import phonetic_entities.PhSignM;

import java.util.ArrayList;

import static client.CharsAndFactorDefs.getToStringEndPadding;

/**
 * Created by Fab on 31.05.2015.
 *
 * contains all parts of an entry
 */
public class PhPartsStructure {

    public ArrayList<PhPart> getPhParts() {
        return phParts;
    }

    private ArrayList<PhPart> phParts;
    /**the indices at which position in the "string */
    private int[] vowelPartsIndices = null;
    private int[] consoPartsIndices = null;
    private static Float[][] partIndiceWeights;
    /** the importance of vowels / consos for the rhyme calculation*/
    private static float vowelWeight;
    private static float consoWeight;


    public int[] getConsoPartsIndices() {
        return consoPartsIndices;
    }
    public int[] getVowelPartsIndices() {
        return vowelPartsIndices;
    }

    /**
     * resolves/groups the Signs array into parts, sets vowel and Conso indices
     * @param phSignMs the source signs
     * @throws SignNotSuittedException
     */
    public PhPartsStructure(ArrayList<PhSignM> phSignMs) throws client.SignNotSuittedException {
        phParts = phSignMsToPhParts(phSignMs);
        if (phParts == null)
            throw new client.SignNotSuittedException("no partable signs in Arraylist<" + phSignMs.toString() + ">");
        vowelPartsIndices = collectTypeIndices(phParts, PhSignDefs.SignType.vowel);
        consoPartsIndices = collectTypeIndices(phParts, PhSignDefs.SignType.consonant);
    }

    /**
     *
     * @param partIndiceWeights
     * @param vowelWeight
     * @param consoWeight
     * @param partStressNotEqualPunishment
     */
    public static void setWeights(Float[][] partIndiceWeights, float vowelWeight, float consoWeight, float partStressNotEqualPunishment) {
        PhPartsStructure.vowelWeight = vowelWeight;
        PhPartsStructure.consoWeight = consoWeight;
        PhPartsStructure.partIndiceWeights = partIndiceWeights;
        PhPart.setPartStressNotEqualPunishment(partStressNotEqualPunishment);
    }


    /**
     * Calculates similarities for all contained vowel and conso- parts. Applies DefStrings signTypeWeights and DefStrings partIndiceWeights
     *
     * @param others to those  will be compared
     * @return 0.0-1.0
     */
    public float calcSimilarity(PhPartsStructure others) {
        float similarity = 0;
        if (vowelWeight != 0f)
            similarity += vowelWeight * calcShiftSimilarity(this.vowelPartsIndices, others.getPhParts(), others.getVowelPartsIndices());
        if (consoWeight != 0f)
            similarity += consoWeight * calcShiftSimilarity(this.consoPartsIndices, others.getPhParts(), others.getConsoPartsIndices());

        return similarity;
    }

    /**
     *
     *  Calculates similarities for all contained vowel and conso- parts. Applies DefStrings signTypeWeights and DefStrings partIndiceWeights
     *   STOPS COMPARISON IF RESULT IS LOWER THAN THRESHOLD: PERFORMANCE INCREASE
     * @param others
     * @param lowThreshold
     * @return -1.0 if comparison has been stopped, because of low thresshold
     */
    public float calcSimilarity(PhPartsStructure others, float lowThreshold) {
        float vowelSimilarity = 0.0f;
        float consoSimilarity = 0.0f;

        // vowel similarity is calculated first, since it is much more important. only when vowel similarity is higher than the lowThreshold
        // the consonant similarities are calculated as well
        if (vowelWeight != 0f) {
            if(ClientOptions.debug){
                RhymesClient.prDebug(" DEBUG in PhPartsStructure.calcSimilarity():\n");
                vowelSimilarity = vowelWeight * calcShiftSimilarity(this.vowelPartsIndices, others.getPhParts(), others.getVowelPartsIndices(),lowThreshold, this.phParts);
            }else{
                vowelSimilarity = vowelWeight * calcShiftSimilarity(this.vowelPartsIndices, others.getPhParts(), others.getVowelPartsIndices());
            }

            if (vowelSimilarity + consoWeight < lowThreshold){
                return -1.0f;
            }
        }

        if (consoWeight != 0f) {
            if (ClientOptions.debug) {
                RhymesClient.prDebug(" DEBUG in PhPartsStructure.calcSimilarity():\n");
                consoSimilarity = consoWeight * calcShiftSimilarity(this.consoPartsIndices, others.getPhParts(), others.getConsoPartsIndices(), lowThreshold, this.phParts);
            } else {
                consoSimilarity += consoWeight * calcShiftSimilarity(this.consoPartsIndices, others.getPhParts(), others.getConsoPartsIndices());
            }

        }
        return vowelSimilarity + consoSimilarity;
    }






    @Override
    public String toString() {
        return toString(this.phParts);
    }

    /**
     * intended for multiline info-printing to sysout
     * method for visuallizing more detailed the grouping of vowels consonants
     * @param parts
     * @return the parts with Det infos on several lines, seperated by tabs
     */
    public String toString(ArrayList<PhPart> parts) {
        if (parts == null) return "";
        boolean printVowelSimilarities = this.vowelPartsIndices != null;
        boolean printConsoSimilarities = this.consoPartsIndices != null;
        String[] outArr;
        //3 werte sind reserviert für [0] buchstaben, [1] vokal od. Conso, [2] emphasis,
        // [3,etc]vertikal similarity float
        outArr = new String[PhPart.floatDecPlaces + PhPart.floatMinPlaces + 3];
        for (int i = 0; i < outArr.length; i++) outArr[i] = "";//init
        String padding = getToStringEndPadding();
        for (PhPart part : parts) { // info arrays der parts auf die zeilen schreiben + padding
            if (part.getType() == null) {
                System.err.println("PhParts.toString(): part.getType()==null");
                System.err.println(part.getPhSignMArr().toString());
                continue;
            }
            boolean printSimilarities = false;
            if (((part.getType() == PhSignDefs.SignType.vowel) && printVowelSimilarities) ||
                    ((part.getType() == PhSignDefs.SignType.consonant) && (printConsoSimilarities))) {
                printSimilarities = true;
            }
            String[] prtStrArr = part.toStringArr(printSimilarities);
            for (int i = 0; i < prtStrArr.length; i++) {
                outArr[i] += "{" + prtStrArr[i] + "}" + padding;
            }
        }
        String out = "";
        for (int i = 0; i < outArr.length; i++) out += outArr[i] + "\n";
        return out;
    }

    /**
     * collects the indices where parts of given type (vowel / consonant / other) are stored in the array
     * @param phParts
     * @param type vowel / consonant / other
     * @return
     */
    private int[] collectTypeIndices(ArrayList<PhPart> phParts, PhSignDefs.SignType type) {
        int typePartCount = 0;
        for (PhPart phPart : phParts) if (phPart.getType() == type) typePartCount++;
        int[] indices = null;
        if (typePartCount > 0) {  // falls es vowels gab, array mit ihren indices füllen
            indices = new int[typePartCount];
            int j = 0;
            for (int i = 0; i < phParts.size(); i++) {
                PhPart phP = phParts.get(i);
                if (phP.getType() == type) {
                    indices[j] = i;
                    j++;
                }
            }
        }
        return indices;
    }


    /**
     * treats PhSigns of Type others, checks if they refer to stress information on the next syllable and if so sets the stress
     * to the current index
     * @param phSignM
     * @param phParts
     * @param recentPhPart
     */
    private void treatOthers(PhSignM phSignM, ArrayList<PhPart> phParts, PhPart recentPhPart) {
        int stress = -1;
        PhSignDefs.PhOtherType phOT = phSignM.getPhSign().getPhOType();
        if (phOT == PhSignDefs.PhOtherType.primaryStress) {
            stress = 1;
        } else if (phOT == PhSignDefs.PhOtherType.secondaryStress) {
            stress = 2;
        }
        if (stress != -1) {
            recentPhPart.setStress(stress);
            if (recentPhPart.getType() != PhSignDefs.SignType.vowel) { // falls das hier kein vowel ist, beim nächsten (bzw. derzeit letzten) Part(ist zwangsläufig ein vowel, da ja immer abwechselnd) stress setzen
                if (phParts.size() != 0) {
                    phParts.get(phParts.size() - 1).setStress(stress);
                }
            }
        }
    }

    /**
     * resolves the given phSignMs into parts by splitting them according to vowels, consos, applies any stress information
     *
     * @param phSignMs
     */
    private ArrayList<PhPart> phSignMsToPhParts(ArrayList<PhSignM> phSignMs) {
        ArrayList<PhPart> phParts = new ArrayList<>();
        PhPart recentPhPart = new PhPart();
        for (int i = 0; i < phSignMs.size(); i++) {
            PhSignM phSignM = phSignMs.get(i);
            PhSignDefs.SignType phSignMSignType = (phSignM.getPhSign().getType());
            if (phSignMSignType == PhSignDefs.SignType.other) {
                treatOthers(phSignM, phParts, recentPhPart);
                continue;
            }
            try {  // aktuelles Zeichen hat anderen Typ als aktueller  Part:
                if ((phSignMSignType != recentPhPart.getType())) {
                    if (recentPhPart.getPhSignMArr().size() > 0) {   //abschließen dieses Parts und neuen erzeugen
                        phParts.add(recentPhPart);
                        recentPhPart = new PhPart();
                    }
                }
                recentPhPart.addPhSignM(phSignM, phSignMSignType);

            } catch (NullPointerException npE) {
                System.err.println("Type was null");
            }
            if (i == phSignMs.size() - 1) {
                recentPhPart.isLastPart(true);
            }
        }
        if (recentPhPart.getPhSignMArr().size() > 0) phParts.add(recentPhPart);
        if (phParts.size() == 0) return null;
        return phParts;
    }





    /**
     * calculates similarity for all Parts at the given Indices, applies partIndiceWeights array as well
     * shifts parts against eachEntry other
     *
     * @param thisIndices
     * @param otherParts
     * @param othersIndices
     * @return 0.0-1.0
     */
    private float calcShiftSimilarity(int[] thisIndices, ArrayList<PhPart> otherParts, int[] othersIndices) {
        if (thisIndices == null ^ othersIndices == null) return 0;
        else if (thisIndices == null && othersIndices == null) return 1;

        int maxShifts = 1;//in one direction

        float[] shiftingResults = new float[(maxShifts * 2) + 2];// +2 because there are 2 nulldurchgänge
        for (int i = 0; i < shiftingResults.length; i++) shiftingResults[i] = 0f;//init
        for (int direction = 0; direction < 2; direction++) {
            for (int shift = 0; shift < maxShifts; shift++) {
                if (direction == 1 && shift == 0) continue;// den 0er durchgang nicht zweimal ausrechnen
                int directedShift = shift - (direction * (2 * shift));
                int shiftingResultsIndex = shift + direction * maxShifts;
                float tmpSum = 0.0f;
                for (int i = 0; i < thisIndices.length; i++) {
                    int shiftedI = i - directedShift;
                    float tmp = 0.0f;
                    float tmp2 = 0.0f;
                    PhPart firstPart = phParts.get(thisIndices[i]);
                    PhPart secondPart = null;
                    if (shiftedI < othersIndices.length && shiftedI >= 0) {
                        secondPart = otherParts.get(othersIndices[shiftedI]);
                        tmp = firstPart.calcSimilarity(secondPart);
                        tmp2 = tmp * partIndiceWeights[0][i];
                    }
                    tmpSum += tmp2;
                }
                tmpSum /= partIndiceWeights[1][thisIndices.length - 1];//normalising
                shiftingResults[shiftingResultsIndex] = tmpSum;
            }
        }
        float similarity = 0;
        for (float tmp : shiftingResults) {
            if (tmp > similarity) similarity = tmp;
        }
        return similarity;
    }




    /**
     * TODO: #############still buggy##############
     * THIS VERSION STOPS COMPARISON IF RESULT IS LOWER THAN THRESHOLD: PERFORMANCE INCREASE
     * calculates similarity for all Parts at the given Indices, applies partIndiceWeights array as well
     * shifts parts against eachEntry other
     *
     * calculates similarity for all Parts at the given Indices, applies partIndiceWeights array as well
     * shifts parts against eachEntry other
     * this method will be called twice: once for all vowel parts and once for all consonant parts
     *
     * @param thisPartsIndices
     * @param otherParts
     * @param othersIndices
     * @param thisParts
     * @return 0.0-1.0
     */
    private float calcShiftSimilarity(int[] thisPartsIndices, ArrayList<PhPart> otherParts, int[] othersIndices, float lowThreshold, ArrayList<PhPart> thisParts) {
        if (thisPartsIndices == null ^ othersIndices == null) return 0;
        else if (thisPartsIndices == null && othersIndices == null) return 1;
        int maxShifts = 2;//in one direction leading thus to 5 positions where each parts similarity is calculated against the other
        float highestResult=0.0f;
         // DOUBLE FOR LOOP FOR DIRECTED SHIFT
        for (int direction = 0; direction < 2; direction++) {
            for (int shift = 0; shift < maxShifts+1; shift++) {
                if (direction == 1 && shift == 0) continue;// den 0er durchgang nicht zweimal ausrechnen
                int directedShift = shift - (direction * (2 * shift)); /* directed shift means: if maxShifts is 2 then the two arrays are shifted +2 in one direction and -2 in the other direction */
                float tmpSumOfPartsSimilaritiesPositionalWeighted = 0.0f;
                // FOR LOOP for calculating all parts against each other
                for (int i = 0; i < thisPartsIndices.length; i++) {
                    tmpSumOfPartsSimilaritiesPositionalWeighted+=calcPartsSimilarity(i,directedShift,thisPartsIndices,thisParts,othersIndices,otherParts,lowThreshold);
                    // abbrechen wenn es schon ein größeres Ergebnis gibt, oder das bisherige ergebnis kleiner als der Thresshold ist
                    /*TODO: folgende kondition wird nie erfüllt - warum nicht?*/
                    float innerTmpSumPositionalWeightedNormalized = tmpSumOfPartsSimilaritiesPositionalWeighted / (partIndiceWeights[1][i]);
                    if ((innerTmpSumPositionalWeightedNormalized) < lowThreshold || highestResult>=tmpSumOfPartsSimilaritiesPositionalWeighted) {//      RhymesClient.prDebug("breaking loop because of --lt: at index "+ rhymesArrIndex+ " of "+thisPartsIndices[rhymesArrIndex]);
                        break;
                    }
                }
                float outerTmpSumPositionalWeightedNormalized = tmpSumOfPartsSimilaritiesPositionalWeighted / partIndiceWeights[1][thisPartsIndices.length - 1];//normalising
                if(outerTmpSumPositionalWeightedNormalized>highestResult) highestResult=outerTmpSumPositionalWeightedNormalized;
            }
        }
        return highestResult;
    }

        private float calcPartsSimilarity(int i, int directedShift, int[]thisPartsIndices, ArrayList<PhPart>thisParts, int[] othersIndices, ArrayList<PhPart> otherParts, float lowThreshold){
            int shiftedIndex = i - directedShift;
            float tmpPartSimilarity;
            float tmpPartSimilarityPositionallyWeighted = 0.0f;
            PhPart firstPart = thisParts.get(thisPartsIndices[i]);
            PhPart secondPart;
            if (shiftedIndex < othersIndices.length && shiftedIndex >= 0) {
                secondPart = otherParts.get(othersIndices[shiftedIndex]);
                tmpPartSimilarity = firstPart.calcSimilarity(secondPart);
                tmpPartSimilarityPositionallyWeighted = tmpPartSimilarity * partIndiceWeights[0][i];
            }
            return tmpPartSimilarityPositionallyWeighted;

        }
}