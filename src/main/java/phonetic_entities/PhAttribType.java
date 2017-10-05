package phonetic_entities;

import client.AttribTypeNotComparableException;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fab on 01.06.2015.
 */
public class PhAttribType<T extends Enum> {
    private float maxVal = -100;
    private float minVal = 100;

    private boolean comparableToDefault;
    private float valRange;

    private float weight;
    private Map<T, Float> valMap;
    private DualHashBidiMap<Character, T> charMap;
    private T defaultVal;
    private boolean hasBlackWhiteSimilarity;

    public PhAttribType(float weight, T defaultVal) {
        this(weight,defaultVal,false);
    }

    /**
     *
     * @param weight the Attrib-types Weight
     * @param defaultVal default value to use, if Attrib is not present; if set to null it means: this Attrib-type is not comparable to a default value
     * @param hasBlackWhiteSimilarity means that comparisons of this attribute can't be done and expressed in steps, its either the saame or not
     */
    public PhAttribType(float weight, T defaultVal, boolean hasBlackWhiteSimilarity) {
        valMap = new HashMap<>();
        charMap = new DualHashBidiMap<>();
        this.weight = weight;
        this.defaultVal = defaultVal;
        this.comparableToDefault= defaultVal ==null?false:true;
        this.hasBlackWhiteSimilarity = hasBlackWhiteSimilarity;
    }


    public PhAttribType(Map<T, Float> valMap, float weight) {
        this.weight = weight;
        this.valMap = valMap;
    }

    public PhAttribType(Map<T, Float> valMap, float weight, DualHashBidiMap<Character, T> charMap) {
        this.weight = weight;
        this.valMap = valMap;
        this.charMap = charMap;
    }
    public float getValRange() {
        return valRange;
    }

    private void calcSetValRange(float val) {
        if (val > maxVal) maxVal = val;
        if (val < minVal) minVal = val;
        valRange = maxVal - minVal;
    }

    public void put(T attribProp, float val) {
        valMap.put(attribProp, val);
        calcSetValRange(val);
    }

    public void put(T attribProp, float val, char ch) {
        put(attribProp, val);
        charMap.put(ch, attribProp);
    }

    public DualHashBidiMap<Character, T> getCharMap() {
        return charMap;
    }

    public void setCharMap(DualHashBidiMap<Character, T> charMap) {
        this.charMap = charMap;
    }


    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Map<T, Float> getValMap() {
        return valMap;
    }

    public float getFloat(T t) {
        return valMap.get(t);
    }

    public char getChar(T t) {
        return charMap.inverseBidiMap().get(t);
    }

    public T getEnum(char ch) {
        return charMap.get(ch);
    }

    public boolean containsEnumFor(char ch) {
        return charMap.containsKey(ch);
    }


    public float calcSimilarity(T first, T second)throws AttribTypeNotComparableException {
        if (first == second) return 1; //gibt auch bei 2*null 1 zurück
        if(this.hasBlackWhiteSimilarity)return 0;

        if ((first == null) ^(second == null)){
            if(this.comparableToDefault){
                if (first == null) first = defaultVal;
                if (second == null) second = defaultVal;
            }else{
                throw new AttribTypeNotComparableException("first or second Argument is null and an Attribute of this type is not comparable To a Default value" );//+charMap.get(charMap.keySet().iterator().next()).getClass().getSimpleName() +
            }
        }

        float diff = this.valMap.get(first) - this.valMap.get(second);
        float similarity=0;
        diff = Math.abs(diff);
        if (this.valRange < 0) System.err.println("PhAttrib(T="+ defaultVal +"): valrange = "+valRange+"");
            similarity = 1 - diff / this.valRange;
        return similarity;
    }


    public void setValMap(Map<T, Float> valMap) {
        this.valMap = valMap;
    }
}
