package output;

import client.ClientArgs;
import client.ClientArgs.OutputOptions;
import client.PhEntriesStructure;
import phonetic_entities.PhEntry;

import java.util.*;

/**
 * Created by Fabrice Vanner on 30.09.2016.
 *
 * TODO:
 *     Filter / Group-Option: groupRhymesWithEquWordEndsEX: Query-Word: "Maus" results would be "Haus", "Bootshaus", "Mietshaus","Horrorhaus",... "Applaus", "Staus",
 * now all Rhymes ending with the same last characters (Haus, Bootshaus...), would be grouped together
 *
 */
public abstract class OutputBase implements Output {
    Set<OutputOptions> options = new HashSet<>();
    List<PhEntry>entries = new ArrayList<>(); // usefull if there is some filtering or grouping to be performed
    ClientArgs clientArgs;
    PhEntry filterEntry;
    PhEntry precedorEntry;//the entry added one iteration before
    float precedorEntrySimilarity;
    int nrOfOutputtedEntries=0;
    PhEntriesStructure phEntriesStructure;

    public void addOption(OutputOptions option){
        options.add(option);
    }

    public void addOptions(Set<OutputOptions> options){
        this.options = options;
    }

    public void removeOption(OutputOptions option){
        options.remove(option);
    }

    /**
     * An entry entered here will be filtered and grouped and whatever option has been set in addOption()
     * before it is send to sendToOutputSink
     * @param entry
     * @param similarity
     */
    public void addToOutput(PhEntry entry, float similarity) {
        boolean group=false;

        for (OutputOptions option : options){
            switch (option){
                case FILTER_EQU_ENDS:
                    if(isEqualEndingTo(this.filterEntry,entry))return; //return ohne den Entry zum "output-sink" zu schicken
                    break;
                case FILTER_PLURALS:
                    if(isEqualEndingTo(this.filterEntry,entry,-1,true))return;
                    break;
                case GROUP_EQU_RHYMES:
                    if(precedorEntry==null)break;
                    //if(isEqualEndingTo(precedorEntry,entry,clientArgs.minCharCountForGroupingOnEqualWordEnds))group=true; // works but gets a lot false positives
                    if(haveEqualConcatinattedWordEnds(precedorEntry,entry))group = true;
                    break;
            }
        }
        sendToOutputSink(entry, -1, group);
        nrOfOutputtedEntries++;
        precedorEntry = entry;
        precedorEntrySimilarity = similarity;
    }


    /**
     * every Entry forwarded here will be sinked immediately (but what about batched output option?)
     * @param entry
     * @param similarity
     * @param groupWithPrecedor
     */
    abstract void sendToOutputSink(PhEntry entry, float similarity, boolean groupWithPrecedor);

    public void setFilterEntry(PhEntry entry){
        filterEntry = entry;

    }

    public void init(ClientArgs clientArgs, PhEntry filterEntry){
        this.clientArgs = clientArgs;
        this.filterEntry = filterEntry;
       // if(options.contains(OutputOptions.FILTER_EQU_ENDS))setFilterEntry();
    }
    public void init(ClientArgs clientArgs, PhEntry filterEntry, PhEntriesStructure phEntriesStructure){
        this.clientArgs = clientArgs;
        this.filterEntry = filterEntry;
        this.phEntriesStructure = phEntriesStructure;
        // if(options.contains(OutputOptions.FILTER_EQU_ENDS))setFilterEntry();
    }



    abstract void initOutput();


    /**
     EX: Query-Word: "Maus" results would be "Haus", "Bootshaus", "Mietshaus","Horrorhaus",... "Applaus", "Staus",
     * now all Rhymes ending with the same last characters (Haus, Bootshaus...), would be grouped togethe
     * TODO: workes, but expensive
     * TODO:  folgende werden fälchlicherweise gruppiert: query <Maus> result: Feldmaus, Schmaus
     * TODO: was ist mit zusammengesetzten Wörtern mit leerzeichen drinn wie: komm aus      kam aus standen aus ständen aus      stünden aus     halfen aus
     * auch gruppieren?
     *
     hälfen aus
     * @param precedor
     * @param entry
     * @return
     */
    private boolean haveEqualConcatinattedWordEnds(PhEntry precedor, PhEntry entry){
        String commonSuffix = longestCommonSuffix(new String[]{precedor.getWord(),entry.getWord()});

        // when the smallest common suffix is at least as long as:
        if(commonSuffix.length()<clientArgs.minCharCountForGroupingOnEqualWordEnds)return false;
        String subString;

        for (int i = 0; commonSuffix.length()-i>=clientArgs.minCharCountForGroupingOnEqualWordEnds; i++){
            subString=commonSuffix.substring(i);
            // and if its an entry in the database itself its obviously a "concatinated-word" to be grouped
            try {
                phEntriesStructure.getEntry(subString, true);
                return true;
            }catch (NoSuchElementException nsee){

            }
        }
        return false;
    }

    private boolean isEqualEndingTo(PhEntry entry1, PhEntry entry2){
       return  isEqualEndingTo(entry1,entry2,-1, false);
    }

    /**
     *
     * * TODO:  folgende werden fälchlicherweise gefiltert: query <Maus> : <Schmaus> etc.....
     *
     * TODO: only little tested and redundand code...
     */
    private boolean isEqualEndingTo(PhEntry entry1, PhEntry entry2, int minLength, boolean equalsIgnorePluralSingularCases){
        String fEW = entry1.getWord();
        String pEW = entry2.getWord();
        if(minLength==-1) minLength = fEW.length() < pEW.length() ? fEW.length() : pEW.length();

        if(equalsIgnorePluralSingularCases) {
            if(fEW.length() < pEW.length()) {
                fEW = fEW.substring(fEW.length() - minLength, fEW.length());
                pEW = pEW.substring(pEW.length() - (minLength+1), pEW.length());
                if (fEW.equalsIgnoreCase(pEW.substring(0, pEW.length() - 1))) return true;
            }

            if(fEW.length() > pEW.length()) {
                fEW = fEW.substring(fEW.length() -(minLength+1), fEW.length());
                pEW = pEW.substring(pEW.length() - minLength, pEW.length());
                if (pEW.equalsIgnoreCase(fEW.substring(0, fEW.length() - 1))) return true;
            }
/*
            if(fEW.length() == pEW.length()) {
                fEW = fEW.substring(fEW.length() - minLength, fEW.length());
                pEW = pEW.substring(pEW.length() - minLength, pEW.length());
                if (fEW.equalsIgnoreCase(pEW))return true;
            }
*/

        }else{
            fEW = fEW.substring(fEW.length() - minLength, fEW.length());
            pEW = pEW.substring(pEW.length() - minLength, pEW.length());
            if (fEW.equalsIgnoreCase(pEW))return true;
        }
        return false;
    }

        /** worked fine*/
    private boolean OLDisEqualEndingTo(PhEntry entry1, PhEntry entry2, int minLength, boolean equalsIgnorePluralSingularCases){
        String fEW = entry1.getWord();
        String pEW = entry2.getWord();
        if(minLength==-1) minLength = fEW.length() < pEW.length() ? fEW.length() : pEW.length();
        fEW = fEW.substring(fEW.length() - minLength, fEW.length());
        pEW = pEW.substring(pEW.length() - minLength, pEW.length());
        if (fEW.equalsIgnoreCase(pEW)) {
            return true;
        }
        return false;
    }



    private String longestCommonSuffix(String[] strs) {
        if(strs==null || strs.length==0){
            return "";
        }

        if(strs.length==1)
            return strs[0];

        //t minLen = strs.length+1;
        int minLen = 100;
        for(String str: strs){
            if(minLen > str.length()){
                minLen = str.length();
            }
        }
        String commonSuffix="";
        boolean stop= false;
        for(int i=0; i<minLen; i++){
            for(int j=0; j<strs.length-1; j++){
                String s1 = strs[j];
                String s2 = strs[j+1];

                int s1Index = s1.length()-(1+i);
                int s2Index = s2.length()-(1+i);
                if(Character.toLowerCase(s1.charAt(s1Index))== Character.toLowerCase(s2.charAt(s2Index))){
                    /**TODO: hier werden teilweise um einen zu früh startende suffixe zurückgegeben */
                    /* TODO: und diese wiederrum fälschlicherweise nicht: query <Maus> result: komm aus, kam aus, standen aus,  ständen aus ,stünden aus,
              *       bei
               *        return s1.substring(s1Index, s1.length()); wird beim Vergleich "kam aus" VS "standen aus"  "m aus" als kleinster suffix ausgegeben !!*/

                    commonSuffix= s1.substring(s1Index, s1.length());
                }else{
                    stop=true;
                    break;
                }
            }
            if(stop)break;
        }
        return commonSuffix;
    }

    //* works:*/
    private String longestCommonPrefix(String[] strs) {
        if(strs==null || strs.length==0){
            return "";
        }

        if(strs.length==1)
            return strs[0];

        int minLen = strs.length+1;

        for(String str: strs){
            if(minLen > str.length()){
                minLen = str.length();
            }
        }

        for(int i=0; i<minLen; i++){
            for(int j=0; j<strs.length-1; j++){
                String s1 = strs[j];
                String s2 = strs[j+1];
                if(s1.charAt(i)!=s2.charAt(i)){
                    return s1.substring(0, i);
                }
            }
        }

        return strs[0].substring(0, minLen);
    }



    /**
     * removes entries with equal word-ending fromIndex map
     *
     * @param similarities      Map with PhEntries, ordered by float-key
     * @param filterEntry       if the word-string of an entry of the map contains this entries word-string it will be filtered out
     * @param filterEquWordBase
     * @param fromTopTill       processes the first <int> entries
     * @return
     *
    public Multimap<Float, PhEntry> filterEqualEndingWordsOut(Multimap<Float, PhEntry> similarities, PhEntry filterEntry, boolean filterEquWordBase, int fromTopTill) {
        int nrOfIterations = 0;
        SortedSet<Float> set = (SortedSet) similarities.keySet();
        Float simi = set.last();
        try {
            while (true) {
                Collection<PhEntry> col = similarities.get(simi);
                Iterator<PhEntry> it2 = col.iterator();
                while (it2.hasNext()) {
                    PhEntry entry = it2.next();
                    if (filterEquWordBase) {
                        String fEW = filterEntry.getWord();
                        String pEW = entry.getWord();
                        int minLength = fEW.length() < pEW.length() ? fEW.length() : pEW.length();
                        fEW = fEW.substring(fEW.length() - minLength, fEW.length());
                        pEW = pEW.substring(pEW.length() - minLength, pEW.length());
                        if (fEW.equalsIgnoreCase(pEW)) {
                            it2.remove();
                            nrOfIterations--;
                        }
                    }
                    nrOfIterations++;
                    if (fromTopTill != -1 && nrOfIterations >= fromTopTill) break;
                }
                if (fromTopTill != -1 && nrOfIterations >= fromTopTill) break;
                simi = set.headSet(simi).last();
            }
        } catch (Exception ex) {
        }
        return similarities;

    }
*/

}
