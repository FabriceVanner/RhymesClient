package phonetic_entities;

import client.PhAttribTypeDefs;
import client.PrintUtils;
import client.RhymesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static client.PhSignDefs.*;

/**
 * Created by Fab on 20.05.2015.
 *
 */
public class PhPart {
   private ArrayList<PhSignM> phSignMArr = new ArrayList<>();
   private int startIndex;
    /**stores last calced similarity to use for print outs...*/
    private float similarity=0;
    private SignType type;
    private int stress=-1;

    /**
     * if phonetic stress (betonung) on this part ain't the same set a punishment: will be substracted on the similarity calculation
     * @param partStressNotEqualPunishment
     */
    public static void setPartStressNotEqualPunishment(float partStressNotEqualPunishment) {
        PhPart.partStressNotEqualPunishment = partStressNotEqualPunishment;
    }

    private static float partStressNotEqualPunishment;
    public void isLastPart(boolean isLastPart) {
        this.isLastPart = isLastPart;
    }

    private boolean isLastPart=false;
    public final static int floatMinPlaces = 2;
    public final static int floatDecPlaces =2;

    public ArrayList<PhSignM> getPhSignMArr() {
        return phSignMArr;
    }


    public SignType getType() {
        return type;
    }




    public String toMultilineString(boolean printSimilarity){
        String out = "";
        String[] multiLineStrArr = toMultilineStringArr(printSimilarity);
        for (int i = 0; i < multiLineStrArr.length; i++) out += multiLineStrArr[i] + "\n";
        return out;
    }

    /**
     * intended for multiline info-printing to sysout
     * infos of this part, its signs, their types, emphasis...
     * eachEntry dimension shall be printed as one seperate line
     * @return two dimensional string arr. part infos are stored on the second dimension
     * @param printSimilarity additionally prints the similarity to the array
     */
    public String[] toMultilineStringArr(boolean printSimilarity){
        String[] out;
        int basicArrayFieldsCnt = 3;//sign + signType + Emphasis
        int outLength;
        if (printSimilarity==true) {
            outLength = basicArrayFieldsCnt + floatMinPlaces + floatDecPlaces;
        }else{
            outLength = basicArrayFieldsCnt;
        }
        out = new String[outLength];
        for(int i=0; i<out.length;i++)out[i]=""; //initialisieren
        int spacesCount=0;

        String additSpace="";
        for(int j=0;j<phSignMArr.size();j++){
            PhSignM phSMP = phSignMArr.get(j);
            String signWithMod =phSMP.getSignWithModifiers(false);
            Map modAttribs=phSMP.getModAttribs();
            if(modAttribs!=null&&modAttribs.get(PhAttribTypeDefs.VowelLength.class.getSimpleName())!=null){additSpace+=" ";}
            spacesCount++;//=signWithMod.length();
            out[0] += signWithMod;
            if(printSimilarity) {
                if (j == 0) {
                    out = PrintUtils.addVerticalSimilarity(this.similarity, out, basicArrayFieldsCnt);
                } else { // falls es mehr als ein Sign gibt, blanks füllen
                    for(int k = 0; k< floatMinPlaces + floatDecPlaces; k++){
                        out[k+basicArrayFieldsCnt]+=" ";
                    }
                }
            }
        }

        String spaces = "";
        for(int i=0;i<spacesCount-1;i++){spaces+=" ";}//leerzeichen zusammensetzen

        out[1] += type.toString().charAt(0)+spaces+additSpace; //  Conso / vokal info
        if(stress==1){
            out[2]+="S"+spaces+additSpace; // stress info
        }else if(stress==2){
            out[2]+="s"+spaces+additSpace; // stress info
        }else{
            out[2]+=" "+spaces+additSpace;
        }
        if(similarity==-1){additSpace+=" ";}
       for(int i=3;i<outLength;i++){out[i]+=additSpace;} // spaces in float zeilen schreiben
        return out;
    }

    @Override
    public String toString() {
        if(phSignMArr==null)return "";
        return phSignMArr.toString();
    }

    /**
     * array of Phonems, + ....
     * @return
     */
    public String simpleInfoString(){
        if(phSignMArr==null)return "";
        String out = "{"+phSignMArr +", rhymesArrIndex=" + startIndex +", "+ (type.toString().charAt(0))+", S="+stress+"}";
        return out;
    }

     /**
      * * if phonetic stress (betonung) on this part ain't the same set a punishment: will be substracted on the similarity calculation
     *used in calcSimilarity()
     * @param stress
     */
    public void setStress(int stress) {
        this.stress = stress;
    }

    public PhPart(){

    }

    public PhPart(SignType type, PhSignM firstPHSignM, short startIndex) {
       phSignMArr.add(firstPHSignM);
       this.type = type;
       this.startIndex = startIndex;
    }

    /**
     * adds a phonem to the end of this part
     * @param phSign
     * @param type
     * @throws NullPointerException
     */
    public void addPhSignM(PhSignM phSign, SignType type)throws NullPointerException{
        if(this.type==null){
            if(type==null)throw new NullPointerException("Type Should not be null");
            this.type = type;
        }
        phSignMArr.add(phSign);
    }



    /**
     * calcs similarity for parts (of which one has more than one PhSignMs)
     * while keeping the signs of eachEntry part in correct order, all possible combinations are tried
     * by moving them indexwise against each other
     * then all  similarities for each shifting position are calculated and the highest similarity is retourned
     * @param otherPhSignMArr
     * @return he highest similarity of all legit combinations
     */
    private float calcShiftSimilarities(List<PhSignM> otherPhSignMArr){
        float similarity = 0f;
        /*
            one part is moved against the other(in terms of sign-location)
            phSignMArr is placed on the upper arrayrow; otherPhSignMArr on the lower
        */
        PhSignM[][]carrier= new PhSignM[2][this.phSignMArr.size()+ 2*(otherPhSignMArr.size()-1)];
        ArrayList<Float> similarities = new ArrayList<>();
        for(int i=0;i<phSignMArr.size();i++){carrier[0][i+otherPhSignMArr.size()-1]=phSignMArr.get(i);}//init obere reihe

        for(int k=0;k<(carrier[0].length-(otherPhSignMArr.size()-1));k++){
            for (int j=0;j<k;j++){carrier[1][j]=null;}// vorherige durchgänge in unterer reihe nullen
            for (int i=0;i<otherPhSignMArr.size();i++){carrier[1][i+k]=otherPhSignMArr.get(i);}// init untere reihe

            float addUpSimilarities=0.0f;
            int comparisonCount=0;// alle vergleiche hochzählen, fungiert als divisor
            for(int i=0;i<carrier[0].length;i++){
                PhSignM thisPhSignM = carrier[0][i];
                PhSignM otherPhSignM = carrier[1][i];
                if ((thisPhSignM == null)&&(otherPhSignM==null)){continue;}
                comparisonCount++;

                if(thisPhSignM==null||otherPhSignM==null){
                    if (thisPhSignM==null
                            && this.phSignMArr.size()<otherPhSignMArr.size() //falls das andere Zeichen das letzte  des letzten Parts ist
                            && (otherPhSignMArr.size()-1+this.phSignMArr.size())<=i
                            && this.isLastPart){
                        addUpSimilarities+=1.0;
                    }else{
                        addUpSimilarities+= PhAttribTypeDefs.getSignAgainstNullPunishment();
                    }
                }else{
                    addUpSimilarities+= thisPhSignM.calcSimilarity(otherPhSignM);
                }
            }
            similarities.add(addUpSimilarities/comparisonCount);//normalisieren
        }
        for(float tmp : similarities){if(tmp>similarity) similarity = tmp;}//größte zurück geben
        return similarity;
    }

    /**
     * calculates the similarities of the parts
     * @param otherPart
     * @return
     */
    public float calcSimilarity(PhPart otherPart){
        float similarity=0.0f;
        ArrayList<PhSignM> otherPhSignMArr = otherPart.getPhSignMArr();
        // if notDefined parts only contain one sign, simple operation:
        if(this.phSignMArr.size()==1&&otherPhSignMArr.size()==1){
            similarity= phSignMArr.get(0).calcSimilarity(otherPhSignMArr.get(0));
        }else{
            similarity= calcShiftSimilarities(otherPhSignMArr);
        }
        if(this.stress!=otherPart.stress){
            similarity-=partStressNotEqualPunishment;
        }
        this.similarity = similarity; // zwischenspeicherung zum ausprinten
        otherPart.similarity=similarity;// zwischenspeicherung zum ausprinten
        RhymesClient.prDebug("PhPart: calcSimilarity(): "+this.toString() + " VS " + otherPart.toString() + " = "+similarity);
        return similarity;
    }

}
