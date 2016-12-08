package output;

import client.ClientOptions;
import client.ClientOptions.OutFilterOption;
import client.PhEntriesStructure;
import client.RhymesClient;
import phonetic_entities.PhEntry;

import java.sql.SQLException;
import java.util.*;

import static client.ClientOptions.OutDelimiting.GROUP;

/**
 * Created by Fabrice Vanner on 30.09.2016.
 *
 * TODO:
 *     Filter / Group-Option: groupRhymesWithEquWordEndsEX: Query-Word: "Maus" results would be "Haus", "Bootshaus", "Mietshaus","Horrorhaus",... "Applaus", "Staus",
 * now all Rhymes ending with the same last characters (Haus, Bootshaus...), would be grouped together
 *
 */
public abstract class OutputBase implements Output {
    Set<ClientOptions.OutFilterOption> options = new HashSet<>();
    List<PhEntry>entries = new ArrayList<>(); // usefull if there is some filtering or grouping to be performed
    ClientOptions clientOptions;
    PhEntry queryEntry;
    PhEntry precedorEntry;//the entry added one iteration before
    float precedorEntrySimilarity;
    int nrOfOutputtedEntries=0;
    PhEntriesStructure phEntriesStructure;
    protected Sink sink;

    public void addOption(ClientOptions.OutFilterOption option){
        options.add(option);
    }

    public void setOptions(Set<OutFilterOption> options){
        this.options = options;
    }

    public void removeOption(ClientOptions.OutFilterOption option){
        options.remove(option);
    }

    /**
     * An entry entered here will be filtered and grouped and whatever option has been set in addOption()
     * before it is send to appendRhymeToSink
     * @param entry
     * @param similarity
     */
    public boolean addToOutput(PhEntry entry, float similarity)throws Exception {
        boolean group=false;
        for (OutFilterOption option : options){
            switch (option){
                case EQU_ENDS:
                    if(isEqualEndingTo(this.queryEntry,entry)) {
                        RhymesClient.prL2("\t - FILTERED: EQU_ENDS\n" );
                        return true; //return ohne den Entry zum "output-sink" zu schicken
                    }
                    break;
                case PLURALS:
                    if(isEqualEndingTo(this.queryEntry,entry,-1,true)){
                        RhymesClient.prL2("\t - FILTERED: PLURALS\n" );
                        return true;
                    }
                    break;
            }
        }
        if(clientOptions.outDelimiting==GROUP){
            if(precedorEntry!=null){
                //if(isEqualEndingTo(precedorEntry,entry,clientOptions.minCharCountForGroupingOnEqualWordEnds))group=true; // works but gets a lot false positives
                if(haveEqualConcatinattedWordEnds(precedorEntry,entry))group = true;
            }
        }

        Object out= formatOutput(entry, -1, group);
        boolean goOn= true;
        goOn = appendRhymeToSink(out);
        precedorEntry = entry;
        precedorEntrySimilarity = similarity;
        return goOn;
    }


    abstract Object formatOutput(PhEntry entry, float similarity, boolean groupWithPrecedor);

    public void flushSink(){
        sink.flush();
    }

    /**
     * every Entry forwarded here will be sinked immediately (but what about batched output option?)
     */
    public boolean appendRhymeToSink(Object out) throws Exception{
        nrOfOutputtedEntries++;
        return true;
    }

    public void setQueryEntry(PhEntry entry){
        queryEntry = entry;
        this.sink.setQueryWord(this.queryEntry.getWord());
    }

    public void init(ClientOptions clientOptions){
        this.clientOptions = clientOptions;
        this.queryEntry = queryEntry;
       // if(options.contains(OutFilterOption.EQU_ENDS))setQueryEntry();
        this.sink.init(clientOptions);
    }
    public void init(ClientOptions clientOptions, PhEntriesStructure phEntriesStructure){
        this.clientOptions = clientOptions;
        this.phEntriesStructure = phEntriesStructure;
        this.sink.init(clientOptions);
        // if(options.contains(OutFilterOption.EQU_ENDS))setQueryEntry();
    }


    /**
     *
      * @param sink where the formatted Output goes
     */
    public OutputBase(Sink sink){
        this.sink = sink;
    }

    public void initSink(){
            sink.init(clientOptions);
    };
    public void openSink() throws SQLException {
        sink.openSink();
    }
    public void closeSink() throws SQLException {
        sink.closeSink();
    }



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
        if(commonSuffix.length()< clientOptions.minCharCountForGroupingOnEqualWordEnds)return false;
        String subString;

        for (int i = 0; commonSuffix.length()-i>= clientOptions.minCharCountForGroupingOnEqualWordEnds; i++){
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
     * @param queryEntry       if the word-string of an entry of the map contains this entries word-string it will be filtered out
     * @param filterEquWordBase
     * @param fromTopTill       processes the first <int> entries
     * @return
     *
    public Multimap<Float, PhEntry> filterEqualEndingWordsOut(Multimap<Float, PhEntry> similarities, PhEntry queryEntry, boolean filterEquWordBase, int fromTopTill) {
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
                        String fEW = queryEntry.getWord();
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
