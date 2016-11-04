package phonetic_entities;

import client.AttribTypeNotComparableException;
import client.CharsAndFactorDefs;
import client.ModifierNotSuittedException;
import client.PhAttribTypeDefs;

import java.util.HashMap;
import java.util.Map;

import static client.PhAttribTypeDefs.getChar;
import static client.PhAttribTypeDefs.getPhAttribAndEnumSet;
import static client.PhSignDefs.*;

/**
 * Created by Fab on 25.05.2015.
 */
public class PhSignM {
    public Map<String, Enum> getModAttribs() {
        return modAttribs;
    }

    protected Map<String, Enum> modAttribs;
    private PhSign phSign;


    public PhSignM(char sign) throws client.SignNotSuittedException {
        this.phSign = getBasicPhSign(sign);
    }

    public PhSignM(char sign, String modifiers) throws client.SignNotSuittedException, ModifierNotSuittedException {
        this(sign);
        if (modifiers.length() != 0) modifiersToPhAttrib(modifiers);
    }

    public PhSign getPhSign() {
        return phSign;
    }

    /**
     * reconstructs the modifiers of this Sign to a printable String
     * @param addTabs puts a tab inbetween eachEntry modifier
     * @return
     */
    public String reconstructModifiers(boolean addTabs) {
        if (modAttribs == null) return "";

        String out = "";
        for (String key : modAttribs.keySet()) {
            Enum en = modAttribs.get(key);
            out += getChar(en);
            if (addTabs) out += "\t";
        }
        ;
        return out;
    }

    public SignType getType() {
        return this.phSign.getType();
    }

    public void modifiersToPhAttrib(String modifiers) throws ModifierNotSuittedException {
        for (char modifier : modifiers.toCharArray()) modifierToPhAttrib(modifier);
    }

    /**
     * converts a modifier-char to a key-val-pair in the map of this PhSign
     * @param modifier
     * @throws ModifierNotSuittedException
     */
    protected void modifierToPhAttrib(char modifier) throws ModifierNotSuittedException {
        if (modAttribs == null) modAttribs = new HashMap<>();
        Map.Entry<String, Enum> entry = getPhAttribAndEnumSet(modifier);
        modAttribs.put(entry.getKey(), entry.getValue());
    }

    public String getSignWithModifiers(boolean addTabs) {
        String out = "" + phSign.sign;
        if (addTabs) out += CharsAndFactorDefs.getToStringEndPadding();
        out += reconstructModifiers(addTabs);
        return out;
    }
    @Override
    public String toString() {
        return getSignWithModifiers(false);
    }

    private float calcModifierSimilarities(PhSignM otherPhSignM)throws AttribTypeNotComparableException {
        if(modAttribs==null && otherPhSignM.getModAttribs()==null)return 1f;

        return PhAttribTypeDefs.calcSimilarity(this.modAttribs, otherPhSignM.getModAttribs());
    }

    public float calcSimilarity(PhSignM otherPhSignM) {
        float similarity = 0;
        int normDiv = 0;
        try { // basiszeichen-attribs vergleichen
            similarity += this.phSign.calcSimilarity(otherPhSignM.getPhSign()) * PhAttribTypeDefs.getBaseSignAttribsWeight();
            normDiv++;
        }catch (AttribTypeNotComparableException ncE){
            System.out.println("ncE = " + ncE);
        }
        try {// evtl. vorhandene modifier-attribs vergleichen
            similarity += this.calcModifierSimilarities(otherPhSignM) * PhAttribTypeDefs.getModifierAttribsWeight();
            normDiv++;
        }catch (AttribTypeNotComparableException ncE){
            System.out.println("ncE = " + ncE);
        }
        if(similarity==0)return 0;
        return (similarity/normDiv);
    }







    ;


}
