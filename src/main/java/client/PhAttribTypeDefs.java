package client;

import org.apache.commons.lang.ArrayUtils;
import phonetic_entities.PhAttribType;

import java.util.*;

/**
 * Created by Fab on 20.06.2015.
 */
public class PhAttribTypeDefs {
    private static PhAttribType<VowelArtiPlace> vowelArtiPlace = new PhAttribType<VowelArtiPlace>(1,null) {{
        put(VowelArtiPlace.front, -1.0f);
        put(VowelArtiPlace.nearFront, -0.5f);
        put(VowelArtiPlace.central, 0f);
        put(VowelArtiPlace.nearBack, +0.50f);
        put(VowelArtiPlace.back, +1f);
    }};
    private static PhAttribType<VowelLength> vowelLength = new PhAttribType<VowelLength>(1f, VowelLength.normal) {
        {
            put(VowelLength.Short, -1.0f, '̆');
            put(VowelLength.normal, 0f);
            put(VowelLength.halfLong, 0.5f, 'ˑ');
            put(VowelLength.Long, 1f, 'ː');
        }
    };
    private static PhAttribType<VowelOpenness> vowelOpenness = new PhAttribType<VowelOpenness>(1f, null) {
        {
            put(VowelOpenness.close, -1.5f);
            put(VowelOpenness.nearClose, -1f);
            put(VowelOpenness.closeMid, -0.5f);
            put(VowelOpenness.mid, 0f);
            put(VowelOpenness.openMid, 0.5f);
            put(VowelOpenness.nearOpen, 1f);
            put(VowelOpenness.open, 1.50f);
        }
    };
    private static PhAttribType<VowelRoundedness> vowelRoundedness = new PhAttribType<VowelRoundedness>(1f, null) {
        {
            put(VowelRoundedness.notRound, -0.5f);
            put(VowelRoundedness.round, 0.5f);
            put(VowelRoundedness.notDefined, 0f);
        }
    };
    private static PhAttribType<VowConsoSylabicy> vowConsoSylabicy = new PhAttribType<VowConsoSylabicy>(1f, VowConsoSylabicy.notDefined) {
        {
            put(VowConsoSylabicy.notLow, -1f, '̯');
            put(VowConsoSylabicy.notUp,-1,'̑');
            put(VowConsoSylabicy.notDefined, 0f);
            put(VowConsoSylabicy.sylabicLow, 1f, '̩');
            put(VowConsoSylabicy.sylabicUp, 1f, '̍');
        }
    };
    private static PhAttribType<ConsoVoiced> consoVoiced = new PhAttribType<ConsoVoiced>(1f, null) {
        {
            put(ConsoVoiced.voiced, 1f);
            put(ConsoVoiced.voiceless, -1f);
            put(ConsoVoiced.notDefined, 0f);
        }
    };
    private static PhAttribType<ConsoArtiPlace> consoArtiPlace = new PhAttribType<ConsoArtiPlace>(1f, null) {
        {
            put(ConsoArtiPlace.bilabial, -2f);
            put(ConsoArtiPlace.labiodental, -1.5f);
            put(ConsoArtiPlace.alveolar, -1f);
            put(ConsoArtiPlace.postalveolar, -0.5f);
            put(ConsoArtiPlace.palatal, 0f);
            put(ConsoArtiPlace.velar, 0.5f);
            put(ConsoArtiPlace.uvular, 1f);
            put(ConsoArtiPlace.glottal, 1.5f);
            put(ConsoArtiPlace.notDefined, 0.5f);

        }
    };
    private static PhAttribType<ConsoArtiManner> consoArtiManner = new PhAttribType<ConsoArtiManner>(1f, null,true) {
        {
            put(ConsoArtiManner.stop, 0f);
            put(ConsoArtiManner.nasal, 0f);
            put(ConsoArtiManner.trill, 0f);
            put(ConsoArtiManner.frikativ, 0f);
            put(ConsoArtiManner.approxim, 0f);
            put(ConsoArtiManner.latApproxim, 0f);
            put(ConsoArtiManner.ejective, 0.f);
        }
    };
    private static PhAttribType<VowelNasal> vowelNasal = new PhAttribType<VowelNasal>(1f, VowelNasal.noNasal) {
        {
            put(VowelNasal.hasNasal, 1f, '̃');
            put(VowelNasal.noNasal, -1f);
        }
    };
    /**
     * containing all legit modifiers to phSigns used to precheck processing of chars in the IPA-String
     */
    private static char[] modifierMapKeySets;
    /**
     * all the PhAttribTypes<T extendsEnum> containing  all the attributes used to describe a particular char
     */
    private static Map<String, PhAttribType> allPhAttribTypes = new HashMap<String, PhAttribType>() {{
        put(VowelArtiPlace.class.getSimpleName(), vowelArtiPlace);
        put(VowelLength.class.getSimpleName(), vowelLength);
        put(VowelOpenness.class.getSimpleName(), vowelOpenness);
        put(VowelRoundedness.class.getSimpleName(), vowelRoundedness);
        put(VowConsoSylabicy.class.getSimpleName(), vowConsoSylabicy);
        put(ConsoVoiced.class.getSimpleName(), consoVoiced);
        put(VowelNasal.class.getSimpleName(), vowelNasal);
        put(ConsoArtiPlace.class.getSimpleName(), consoArtiPlace);
        put(ConsoArtiManner.class.getSimpleName(), consoArtiManner);
    }};

    /**
     * baseSignAttribsWeight and modifierAttribsWeight together need to add up to 2.0
     */
    private static float baseSignAttribsWeight = 1.6f;
    private static float modifierAttribsWeight = 0.4f;

    public static float getSignAgainstNullPunishment() {
        return signAgainstNullPunishment;
    }

    /**if one of two parts to be compared has more signs than the other:
     * for eachEntry sign against sign a similarity val is calculated and summed up
     * for sign against null this value (signAgainstNullPunishment) is addedup
     * the sum gets divided by the number of comparisons
     * */
    private static float signAgainstNullPunishment =0.3f;

    public static Map<String, PhAttribType> getAllPhAttribTypes() {
        return allPhAttribTypes;
    }


    public static float getFloat(Enum en) {
        return allPhAttribTypes.get(en.getClass().getSimpleName()).getFloat(en);
    }

    public static char getChar(Enum en) {
        return allPhAttribTypes.get(en.getClass().getSimpleName()).getChar(en);
    }

    public static Enum getEnum(Class en, char ch) {
        return allPhAttribTypes.get(en.getClass().getSimpleName()).getEnum(ch);
    }

    public static Enum getEnum(String key, char ch) {
        return allPhAttribTypes.get(key).getEnum(ch);
    }

    public static Enum getEnum(char ch) throws ModifierNotSuittedException {
        for (String key : allPhAttribTypes.keySet()) {
            if (allPhAttribTypes.get(key).containsEnumFor(ch)) {
                return allPhAttribTypes.get(key).getEnum(ch);
            }
            ;
        }
        throw new ModifierNotSuittedException("modifier: " + ch + " does not exist");
    }

    public static String getPhAttribKey(char ch) throws ModifierNotSuittedException {
        for (String key : allPhAttribTypes.keySet()) {
            if (allPhAttribTypes.get(key).containsEnumFor(ch)) {
                return key;
            }
            ;
        }
        throw new ModifierNotSuittedException("modifier: " + ch + " does not exist");
    }

    public static Map.Entry<String, Enum> getPhAttribAndEnumSet(char ch) throws ModifierNotSuittedException {
        for (String key : allPhAttribTypes.keySet()) {
            PhAttribType phA = allPhAttribTypes.get(key);
            if (phA.containsEnumFor(ch)) {
                return new AbstractMap.SimpleEntry<String, Enum>(key, phA.getEnum(ch));
            }
            ;
        }
        throw new ModifierNotSuittedException("modifier: " + ch + " does not exist");
    }

    /**
     * prechecks if one of given Maps is null, calls appropriate Method
     * compares every attribute of first map to (same type) attribute  of second map(if present)
     * missing attributes will be compared to default vals if possible
     *
     * @param first
     * @param second
     * @return
     */
    public static float calcSimilarity(Map<String, Enum> first, Map<String, Enum> second)  {
        if (first == null && second == null) return 1f;
        Set<String> allKeys = new HashSet<>();

        if (first != null) allKeys.addAll(first.keySet());
        if (second != null) allKeys.addAll(second.keySet());

        int normDiv = 0;
        float sum = 0;
        for (String key : allKeys) {
            Enum thisEnum = first != null ? first.get(key) : null;
            Enum otherEnum = second != null ? second.get(key) : null;
            PhAttribType phA = getAllPhAttribTypes().get(key);
            try {
                sum += phA.calcSimilarity(thisEnum, otherEnum) * phA.getWeight();
                normDiv++;
            } catch (AttribTypeNotComparableException ncE) {
                System.err.println("NotComparableException: " + ncE);
            }
        }
        if (sum == 0) return 0;
        return sum / normDiv;
    }

    /**
     * sets a field arr containing all legit modifiers to phSigns
     * helps quikly sorting out not recognized chars in Ipa String
     * @return returns the field
     */
    public static char[] getModifierMapKeySetsAsCharArr() {
        if (modifierMapKeySets == null) {
            Character[] srcCharArr1 = (Character[]) (getAllPhAttribTypes().get(VowConsoSylabicy.class.getSimpleName()).getCharMap().keySet().toArray(new Character[0]));
            Character[] srcCharArr2 = (Character[]) (getAllPhAttribTypes().get(VowelLength.class.getSimpleName()).getCharMap().keySet().toArray(new Character[0]));
            Character[] srcCharArr3 = (Character[]) (getAllPhAttribTypes().get(VowelNasal.class.getSimpleName()).getCharMap().keySet().toArray(new Character[0]));
            Character[] charArr = (Character[]) ArrayUtils.addAll(srcCharArr1, srcCharArr2);
            charArr = (Character[]) ArrayUtils.addAll(charArr, srcCharArr3);
            modifierMapKeySets = ArrayUtils.toPrimitive(charArr);
        }
        return modifierMapKeySets;
    }


    public static float getBaseSignAttribsWeight() {
        return baseSignAttribsWeight;
    }

    public static float getModifierAttribsWeight() {
        return modifierAttribsWeight;
    }

    public enum VowelNasal {hasNasal, noNasal}

    public enum VowelLength {Short, normal, halfLong, Long}

    public enum VowelArtiPlace {front, nearFront, central, nearBack, back}

    public enum VowelOpenness {close, nearClose, closeMid, mid, openMid, nearOpen, open}

    public enum VowelRoundedness {notRound, round, notDefined}

    public enum ConsoArtiPlace {bilabial, labiodental, alveolar, postalveolar, palatal, velar, uvular, glottal, notDefined}

    public enum ConsoArtiManner {stop, nasal, trill, frikativ, approxim, latApproxim, ejective}

    public enum VowConsoSylabicy {sylabicLow, sylabicUp, notLow,notUp,notDefined}

    public enum ConsoVoiced {voiced, voiceless, notDefined}
}
