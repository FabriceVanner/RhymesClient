package client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import phonetic_entities.PhSign;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static client.PhAttribTypeDefs.*;

/**
 * Created by Fab on 20.06.2015.
 */
public class PhSignDefs {
    private static final Map<Character, PhSign> basicPhSigns = new HashMap<>();
    private static final Map<Character,PhSign> basicPhSignVowels = new HashMap<>();
    private static final Map<Character,PhSign> basicPhSignConsos = new HashMap<>();
    private static final Map<Character,PhSign> basicPhSignOthers = new HashMap<>();
    private static  Table<PhSign,PhSign,Float> hbVowelTable;
    private static  Table<PhSign,PhSign,Float> hbConsoTable;
    private static  Table<PhSign,PhSign,Float> hbOtherTable ;

    static{

    }

    /**
     * vowel and Conso should add up to 1.0

        private static Map<SignType, Float> signTypeWeights = new HashMap<SignType, Float>() {{
        put(SignType.vowel, 0.9f);
        put(consonant, 0.10f);
        put(SignType.other, 0.0f);
    }};
     */

    static {
        PhSignDefs.basicPhSignConsos.put('ʔ', new PhSign('ʔ', ConsoArtiPlace.glottal, ConsoArtiManner.stop, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('b', new PhSign('b', ConsoArtiPlace.bilabial, ConsoArtiManner.stop, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('ç', new PhSign('ç', ConsoArtiPlace.palatal, ConsoArtiManner.frikativ, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('c', new PhSign('c', ConsoArtiPlace.palatal, ConsoArtiManner.stop, ConsoVoiced.voiceless)); // aus intern.
        PhSignDefs.basicPhSignConsos.put('d', new PhSign('d', ConsoArtiPlace.alveolar, ConsoArtiManner.frikativ, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('f', new PhSign('f', ConsoArtiPlace.labiodental, ConsoArtiManner.frikativ, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('g', new PhSign('g', ConsoArtiPlace.velar, ConsoArtiManner.stop, ConsoVoiced.voiced)); // 0067, was ist mit // U+0261
        PhSignDefs.basicPhSignConsos.put('h', new PhSign('h', ConsoArtiPlace.glottal, ConsoArtiManner.frikativ, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('j', new PhSign('j', ConsoArtiPlace.palatal, ConsoArtiManner.approxim, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('k', new PhSign('k', ConsoArtiPlace.velar, ConsoArtiManner.stop, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('l', new PhSign('l', ConsoArtiPlace.alveolar, ConsoArtiManner.latApproxim, ConsoVoiced.voiced)); //"l̩
        PhSignDefs.basicPhSignConsos.put('m', new PhSign('m', ConsoArtiPlace.bilabial, ConsoArtiManner.nasal, ConsoVoiced.voiced));//"m̩"
        PhSignDefs.basicPhSignConsos.put('n', new PhSign('n', ConsoArtiPlace.alveolar, ConsoArtiManner.nasal, ConsoVoiced.voiced));   // "n̩
        PhSignDefs.basicPhSignConsos.put('ŋ', new PhSign('ŋ', ConsoArtiPlace.velar, ConsoArtiManner.nasal, ConsoVoiced.voiced)); // [ŋ̍] oder [ŋ̩] (silbisch)
        PhSignDefs.basicPhSignConsos.put('p', new PhSign('p', ConsoArtiPlace.bilabial, ConsoArtiManner.stop, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('r', new PhSign('r', ConsoArtiPlace.alveolar, ConsoArtiManner.trill, ConsoVoiced.voiced));// aus intern. Ipa:
        PhSignDefs.basicPhSignConsos.put('ʀ', new PhSign('ʀ', ConsoArtiPlace.uvular, ConsoArtiManner.trill, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('ʁ', new PhSign('ʁ', ConsoArtiPlace.uvular, ConsoArtiManner.frikativ, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('s', new PhSign('s', ConsoArtiPlace.alveolar, ConsoArtiManner.frikativ, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('ʃ', new PhSign('ʃ', ConsoArtiPlace.postalveolar, ConsoArtiManner.frikativ, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('t', new PhSign('t', ConsoArtiPlace.alveolar, ConsoArtiManner.stop, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('ʦ', new PhSign('ʦ', ConsoArtiPlace.alveolar, ConsoArtiManner.ejective, ConsoVoiced.voiceless)); //t͡s
        PhSignDefs.basicPhSignConsos.put('ʧ', new PhSign('ʧ', ConsoArtiPlace.postalveolar, ConsoArtiManner.ejective, ConsoVoiced.voiceless)); //t͡ʃ
        PhSignDefs.basicPhSignConsos.put('v', new PhSign('v', ConsoArtiPlace.labiodental, ConsoArtiManner.frikativ, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('χ', new PhSign('χ', ConsoArtiPlace.uvular, ConsoArtiManner.frikativ, ConsoVoiced.voiceless));
        PhSignDefs.basicPhSignConsos.put('z', new PhSign('z', ConsoArtiPlace.alveolar, ConsoArtiManner.frikativ, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('ʒ', new PhSign('ʒ', ConsoArtiPlace.postalveolar, ConsoArtiManner.frikativ, ConsoVoiced.voiced));
        PhSignDefs.basicPhSignConsos.put('ʤ', new PhSign('ʤ', ConsoArtiPlace.postalveolar, ConsoArtiManner.ejective, ConsoVoiced.voiced)); //d͡ʒ

        PhSignDefs.basicPhSignVowels.put('a', new PhSign('a', VowelArtiPlace.front, VowelOpenness.open, VowelRoundedness.notRound));//ã // aː
        PhSignDefs.basicPhSignVowels.put('æ', new PhSign('æ', VowelArtiPlace.front, VowelOpenness.nearOpen, VowelRoundedness.notRound));
        PhSignDefs.basicPhSignVowels.put('ɑ', new PhSign('ɑ', VowelArtiPlace.back, VowelOpenness.open, VowelRoundedness.notRound));// aus internationaler ipa..
        PhSignDefs.basicPhSignVowels.put('ɐ', new PhSign('ɐ', VowelArtiPlace.central, VowelOpenness.nearOpen, VowelRoundedness.notDefined)); //ɐ̯
        PhSignDefs.basicPhSignVowels.put('e', new PhSign('e', VowelArtiPlace.front, VowelOpenness.closeMid, VowelRoundedness.notRound)); //[eː]
        PhSignDefs.basicPhSignVowels.put('ɛ', new PhSign('ɛ', VowelArtiPlace.front, VowelOpenness.openMid, VowelRoundedness.notRound)); //ɛː
        PhSignDefs.basicPhSignVowels.put('ə', new PhSign('ə', VowelArtiPlace.central, VowelOpenness.mid, VowelRoundedness.notDefined));
        PhSignDefs.basicPhSignVowels.put('i', new PhSign('i', VowelArtiPlace.front, VowelOpenness.close, VowelRoundedness.notRound)); //iː  // [i̯]
        PhSignDefs.basicPhSignVowels.put('ɪ', new PhSign('ɪ', VowelArtiPlace.nearFront, VowelOpenness.nearClose, VowelRoundedness.notRound));
        PhSignDefs.basicPhSignVowels.put('o', new PhSign('o', VowelArtiPlace.back, VowelOpenness.closeMid, VowelRoundedness.round)); //[oː]
        PhSignDefs.basicPhSignVowels.put('ɔ', new PhSign('ɔ', VowelArtiPlace.back, VowelOpenness.openMid, VowelRoundedness.round));
        PhSignDefs.basicPhSignVowels.put('ø', new PhSign('ø', VowelArtiPlace.front, VowelOpenness.closeMid, VowelRoundedness.round)); // [øː]
        PhSignDefs.basicPhSignVowels.put('œ', new PhSign('œ', VowelArtiPlace.front, VowelOpenness.openMid, VowelRoundedness.round));
        PhSignDefs.basicPhSignVowels.put('u', new PhSign('u', VowelArtiPlace.back, VowelOpenness.close, VowelRoundedness.round)); //[uː]
        PhSignDefs.basicPhSignVowels.put('ʊ', new PhSign('ʊ', VowelArtiPlace.nearBack, VowelOpenness.nearClose, VowelRoundedness.round));
        PhSignDefs.basicPhSignVowels.put('y', new PhSign('y', VowelArtiPlace.front, VowelOpenness.close, VowelRoundedness.round));//yː
        PhSignDefs.basicPhSignVowels.put('ʏ', new PhSign('ʏ', VowelArtiPlace.nearFront, VowelOpenness.nearClose, VowelRoundedness.round));


        PhSignDefs.basicPhSignOthers.put('ˈ', new PhSign('ˈ', PhSignDefs.PhOtherType.primaryStress));
        PhSignDefs.basicPhSignOthers.put('ˌ', new PhSign('ˌ', PhSignDefs.PhOtherType.secondaryStress));
        PhSignDefs.basicPhSignOthers.put(' ', new PhSign(' ', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put(' ', new PhSign(' ', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put('.', new PhSign('.', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put(',', new PhSign(',', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put(';', new PhSign(';', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put('\u200B', new PhSign('\u200B', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put('_', new PhSign('_', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put('-', new PhSign('-', PhSignDefs.PhOtherType.delimit));
        PhSignDefs.basicPhSignOthers.put('X', new PhSign('X', PhSignDefs.PhOtherType.unclassified));

    //folgendes ist nur übergangsweise, kann wahrscheinlich weg:
        basicPhSigns.putAll(basicPhSignConsos);
        basicPhSigns.putAll(basicPhSignVowels);
        basicPhSigns.putAll(basicPhSignOthers);
    // ... .
        preCalculateSimilaritiesForSigns();
    }

    /**Map containing all signs that are used to describe IPA-Wise german language */
    public static Map<Character, PhSign> getBasicPhSigns() {
        return basicPhSigns;
    }

    /**
     * @param sign
     * @return the PhSign associated with the given Character
     * @throws SignNotSuittedException
     */
    public static PhSign getBasicPhSign(char sign) throws SignNotSuittedException {
        PhSign phS = getBasicPhSigns().get(sign);
        if (phS == null)
            throw new SignNotSuittedException("<\t" + sign + "\t>\t utf8= " + CharConstants.getUniCodeStr(sign) + "\t  not in basicPhSigns;");
        return phS;
    }

    public static void preCalculateSimilaritiesForSigns(){
        hbVowelTable =  compareAndCalcAllPhSigns(basicPhSignVowels);
        hbConsoTable = compareAndCalcAllPhSigns(basicPhSignConsos);
        hbOtherTable =  compareAndCalcAllPhSigns(basicPhSignOthers);
    }


    /**
     * compares all PhSigns one against eachEntry other
     * @param map
     * @return
     */
    public static  Table<PhSign,PhSign,Float> compareAndCalcAllPhSigns(Map<Character, PhSign> map){
        //Table<PhSign,PhSign,Float>hbTable = new HashBasedTable<>();
        Table<PhSign,PhSign,Float>hbTable =  HashBasedTable.create();
        Iterator<Character> it1= map.keySet().iterator();
        while (it1.hasNext()) {
            char ch1 = it1.next();
            Iterator<Character> it2 = map.keySet().iterator();
            while (it2.hasNext()) {
                char ch2 = it2.next();
                PhSign phSign1 = map.get(ch1);
                PhSign phSign2 = map.get(ch2);

                float simi = PhAttribTypeDefs.calcSimilarity(phSign1.getAttribs(),phSign2.getAttribs());
                hbTable.put(phSign1, phSign2, simi);
            }
        }
        return hbTable;
    }

    /**
     * retrieving precalculated similarity values of each vocal phonem against each vocal phonem
     * and each consonant
     * @param phSign1
     * @param phSign2
     * @return
     */
    public static float getSimilarity(PhSign phSign1, PhSign phSign2){
       PhSignDefs.SignType st1 = phSign1.getType();
       //PhSignDefs.SignType st2 = phSign2.getType();
        //if(st1 != st2){            return 0.0f;        }
        switch (st1){
            case consonant:
                return hbConsoTable.get(phSign1,phSign2);
            case vowel:
                return hbVowelTable.get(phSign1,phSign2);
            case other:
                return hbOtherTable.get(phSign1,phSign2);
        }
        return 0.0f;
    }



    /**
     * the Weights of the SignType Enums vowels, Consos...
    public static Map<SignType, Float> getSignTypeWeight() {
        return signTypeWeights;
    }
     */

    /**Detailed Definition of enum SignType "other" (than Consonants or vowels) of Phonems occuring in an IPA-String */
    public enum PhOtherType {primaryStress, secondaryStress, delimit, unclassified}

    /** the main types of Phonems, "other" is described in more detail in the enum PhOtherType*/
    public enum SignType {vowel, consonant, other}

    /** */
    public enum LDBEntryComparisonField {word, ipaRev}

}
