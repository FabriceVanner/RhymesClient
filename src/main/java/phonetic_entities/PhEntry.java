package phonetic_entities;

import client.PhAttribTypeDefs;
import client.PhPartsStructure;
import client.PhSignDefs;
import client.Utils;

import java.text.Normalizer;
import java.util.ArrayList;

import static client.CharsAndFactorDefs.*;
import static client.PhEntriesStructure.*;

/**
 * Created by Fab on 11.05.2015.
 */
public class PhEntry implements Comparable<PhEntry> {



    private long dBID;
    private String word;
    private String ipa;
    private String rhyme;
    private String worttrennung;
    private String ipaRev = "";
    private ArrayList<PhSignM> phSignMs;
    private ArrayList<PhSignM> phSignMsRev;
    private PhPartsStructure phPartsStructure;
    private static PhSignDefs.LDBEntryComparisonField fieldToCompareTo = PhSignDefs.LDBEntryComparisonField.ipaRev;

    public PhEntry(String word) {
        this(0, word, "", "", "");
    }

    public PhEntry(String word, String ipa) {
        this(0, word, ipa, "", "");
    }

    public PhEntry(long dBid, String word, String ipa, String rhyme, String worttrennung) {
        this.dBID = dBid;
        this.word = word;
        this.ipa = ipa;
        this.rhyme = rhyme;
        this.worttrennung = worttrennung;
    }

    public PhPartsStructure getPhParts() {
        return phPartsStructure;
    }

    public void setReversed() {
        ipaRev = new StringBuilder(ipa).reverse().toString();
        phSignMsRev = new ArrayList<>();
        for (int i = phSignMs.size() - 1; i >= 0; i--) {
            phSignMsRev.add(phSignMs.get(i));
        }
    }

    public void normalizeIPA_UNICODE() {
        ipa = Normalizer.normalize(ipa, Normalizer.Form.NFKD);
        ipa = Normalizer.normalize(ipa, Normalizer.Form.NFKC);// eliminiert probleme mit c und ̧
    }

    /**
     * normalizes a single char accarding to a replacement map in CharsAndFactorDefs
     *
     * @param ch
     * @return
     */
    public char normalizeSingleChar(char ch) {
        if ((getReplacementMap().containsKey(ch))) {
            String repl = getReplacementMap().get(ch);
            if (repl.length() == 2) {
                ipa = ipa.replace((CharSequence) ("" + ch), (CharSequence) (repl));
            }
            ch = repl.charAt(0);
        }
        return ch;
    }

    /**
     * checks if the char of the IPA-rhymesArrIndex-var at given index is a modifier (accoarding to ModifierMap in PhAttribTypeDefs)
     * if so appends it to param modifiers and returns true
     *
     * @param modifIndex index of the potential Modifier in the IPA-rhymesArrIndex-var, usualy one ahead of actual index
     * @param modifiers  expexts an initialised StringBuilder string
     * @return TRUE If this was a modifier
     */
    private boolean treatModifiers(int modifIndex, StringBuilder modifiers) {
        if (modifIndex < ipa.length()) {
            char ch = ipa.charAt(modifIndex);
            ch = normalizeSingleChar(ch);
            if (Utils.charIsOneOf(ch, PhAttribTypeDefs.getModifierMapKeySetsAsCharArr())) {
                modifiers.append(ch);
                return true;
            }
        }
        return false;
    }

    /**
     * parses the IPA to suitable PhSigns, if possible
     *
     * @throws client.SignNotSuittedException
     */
    public void IPAToPhSignMs() throws client.SignNotSuittedException {
        phSignMs = new ArrayList<>();
        for (int i = 0; i < ipa.length(); i++) {
            char ch = ipa.charAt(i);
            if (Utils.charIsOneOf(ch, getSkipableChars())) continue;
            ch = normalizeSingleChar(ch);
            try {
                StringBuilder modifiers = new StringBuilder("");
                int indexSkip = 0;
                if (treatModifiers(i + 1, modifiers)) {
                    indexSkip++;
                    if (treatModifiers(i + 2, modifiers)) indexSkip++;
                }
                try {
                    phSignMs.add(new PhSignM(ch, modifiers.toString()));
                } catch (client.ModifierNotSuittedException mNSE) {
                    phSignMs.add(new PhSignM(ch));
                }
                i += indexSkip;
            } catch (client.SignNotSuittedException sNSE) {
                Integer I = 1;
                if (charsNotInMainMap.containsKey(ch)) {
                    I = charsNotInMainMap.get(ch);
                    I += 1;
                }
                charsNotInMainMap.put(ch, I);
                phSignMs.add(new PhSignM('X'));
            }
        }
        if (phSignMs.size() == 0)
            throw new client.SignNotSuittedException("None of the contained ipa= <" + ipa + "> chars were represented in the PhSigns-Map.");

    }


    public void phSignMsToPhParts() throws client.SignNotSuittedException {
        this.phPartsStructure = new PhPartsStructure(phSignMsRev);
    }

    /**
     * sorts according to reversed IPA String
     *
     * @param otherldbPhEntry
     * @return
     */
    @Override
    public int compareTo(PhEntry otherldbPhEntry) {
        switch (fieldToCompareTo) {
            case ipaRev:
                return this.ipaRev.compareTo(otherldbPhEntry.ipaRev);
            case word:
                return this.word.compareTo(otherldbPhEntry.word);
        }
        System.out.println("LDBEntry.compareTo: Can't compare to that yet");
        return -999999999;
    }

    public void setFieldToCompareTo(PhSignDefs.LDBEntryComparisonField fieldToCompareTo) {
        this.fieldToCompareTo = fieldToCompareTo;
    }


    public float calcSimilarity(PhEntry otherPhEntry) {
        return this.phPartsStructure.calcSimilarity(otherPhEntry.getPhParts());
    }

    public float calcSimilarity(PhEntry otherPhEntry, float lowThreshold) {
        return this.phPartsStructure.calcSimilarity(otherPhEntry.getPhParts(), lowThreshold);
    }


    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean detail) {
        String out = "";
        out += String.format("%20s\t\t%s", "<" + word + ">", "<" + ipa + ">");
        if (detail && phPartsStructure != null) {
            out += "\n";
            out += phPartsStructure.toString();
        }
        return out;
    }


    /**
     * prints out the String rhymesArrIndex-vars ipa and ipaRev with measures
     */
    public void printSeperateStrings() {
        Utils.getUniCharsSeperate(ipa, true, true);
        Utils.getUniCharsSeperate(ipaRev, true, true);
    }


    public String getWord() {
        return word;
    }

    public String getIpa() {
        return ipa;
    }


    public String getIpaRev() {
        return ipaRev;
    }

}