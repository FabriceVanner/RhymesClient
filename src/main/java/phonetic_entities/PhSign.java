package phonetic_entities;

import client.AttribTypeNotComparableException;
import client.PhAttribTypeDefs;
import client.PhSignDefs;

import java.util.HashMap;
import java.util.Map;

import static client.PhAttribTypeDefs.*;

/**
 * Created by Fab on 18.05.2015.
 * Phonetic Sign (IPA-Alphabet)
 * the smallest entity, representing a single symbole
 * stores all attributes defining the way the sound ("der Laut") is generated in the mouth
 *
 */
public class PhSign {

    protected char sign;
    protected Map<String, Enum> attribs;
    private PhSignDefs.SignType signType;
    private PhSignDefs.PhOtherType phOType;

    public Map<String, Enum> getAttribs() {
        return attribs;
    }

    public PhSignDefs.PhOtherType getPhOType() {
        return phOType;
    }

    private PhSign(char sign) {
        this.sign = sign;
        this.attribs = new HashMap<>();
    }

    public PhSign (char sign, PhSignDefs.PhOtherType type){
        this(sign);
        this.signType = PhSignDefs.SignType.other;
        this.phOType = type;
    }

    public PhSign(char sign, ConsoArtiPlace ConsoArtiLocation, ConsoArtiManner ConsoArtiWay, ConsoVoiced ConsoStimmhaftigkeit){
        this(sign);
        signType = PhSignDefs.SignType.consonant;
        attribs.put(ConsoArtiPlace.class.getSimpleName(), ConsoArtiLocation);
        attribs.put(ConsoArtiManner.class.getSimpleName(), ConsoArtiWay);
        attribs.put(ConsoVoiced.class.getSimpleName(), ConsoStimmhaftigkeit);
    }

    public PhSign(char sign, VowelArtiPlace vowelArtiLocation, VowelOpenness vowelOpenness, VowelRoundedness vowelRoundness){
        this(sign);
        signType = PhSignDefs.SignType.vowel;
        attribs.put(VowelArtiPlace.class.getSimpleName(), vowelArtiLocation);
        attribs.put(VowelOpenness.class.getSimpleName(), vowelOpenness);
        attribs.put(VowelRoundedness.class.getSimpleName(), vowelRoundness);
    }

    public PhSignDefs.SignType getType() {
        return this.signType;
    }

    public String toString(){
        return ""+sign;
    }


    /**
     * actually retrieves  how similar two  phonems sound, which has been calculated sometime before
     * --> saving processing power
     * @param otherSign
     * @return 1.0 = they are equally sounding (or identical) 0.0  = completely different
     * @throws AttribTypeNotComparableException
     */
    public float calcSimilarity(PhSign otherSign)throws AttribTypeNotComparableException {
        if(this.sign == otherSign.sign){
            return 1;
        }
       float simi =  PhSignDefs.getSimilarity(this, otherSign);
        //RhymesClient.prDebug("PhSign: calcSimilarity(): this.sign = " + this.sign + "other.sign = "+otherSign +" --> "+simi );
        return simi;


    }

   /**
     * calculates how similar two  phonems sound:
    *  this method
     * @param otherSign
     * @return 1.0 = they are equally sounding (or identical) 0.0  = completely different
     * @throws AttribTypeNotComparableException
     */
    public float calcSimilarity_OLD(PhSign otherSign)throws AttribTypeNotComparableException {
        if(this.sign == otherSign.sign){
            return 1;
        }
        return PhAttribTypeDefs.calcSimilarity(this.attribs, otherSign.getAttribs());
    }
}

