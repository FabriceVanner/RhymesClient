package client;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import output.Output;
import phonetic_entities.PhEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static client.PhSignDefs.*;
import static client.RhymesClient.*;

/**
 * Created by Fab on 11.05.2015.
 * <p>
 * <p>
 * loads database in memory and holds all PhEntries
 * <p>
 * all initial methods for querying the database for rhymes
 */
public class PhEntriesStructure {
    public static SortedMap<Character, Integer> charsNotInMainMap = new TreeMap<>();
    private List<PhEntry> entries;
    private List<PhEntry> entriesRev;
    private Connection conn;
    private Statement st;
    private long dBRowCount;

    /**
     * count of Entries that have been stopped beeing calculated because of too low thresshold
     */
    public int stoppedCalculatingEntriesCount = 0;
    List<PhEntry> errorPhEntryList = new ArrayList<>();

    public int getEntriesRevListSize() {
        return entriesRev.size();
    }

    /**
     * Constructor to use with Dictionary in Database output
     *
     * @param printErrors
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public PhEntriesStructure(boolean printErrors) throws SQLException, ClassNotFoundException, FileNotFoundException {
        this.conn = DBConnection.connectToDatabaseLautschriftOrDie();
        this.st = this.conn.createStatement();
        this.dBRowCount = Utils.getDBRowCount(st);
        this.entries = Utils.getRowsFromDB(st, false);
        //RhymesClient.prTime("Starting initialising Entries");
        startSW("Initialising-Entries");
        initEntries(printErrors);
        stopSW("Initialising-Entries");
        //RhymesClient.prTime("Ended initialising Entries");
    }

    /**
     * Constructor to use with Dictionary in Textfile output
     *
     * @param printErrors
     * @param dictTextFilePath
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public PhEntriesStructure(boolean printErrors, String dictTextFilePath) throws ClassNotFoundException, FileNotFoundException, IOException {
        //RhymesClient.prTime("Starting reading dictFile");
        startSW("Reading-DictFile");
        this.entries = Utils.readDictTextFile(dictTextFilePath, "    ");
      //  RhymesClient.prTime("Ending reading dictFile");
        stopSW("Reading-DictFile");
        //RhymesClient.prTime("Starting initialising Entries");
        startSW("Initialising-Entries2");
        initEntries(printErrors);
        stopSW("Initialising-Entries2");
    }

    /**
     * initialises all entries of this Structure in entries
     * - unicode normalization
     * - the ipa-string gets parsed to PhSignMs and reversed
     * - the phSignMs get grouped into parts
     *
     * @param printIPAErrors errors in parsing the ipa-unicode to PhSignMs to std-err
     */
    private void initEntries(boolean printIPAErrors) {
        float vowelWeight = getSignTypeWeight().get(SignType.vowel);
        float consoWeight = getSignTypeWeight().get(SignType.consonant);

        PhPartsStructure.setWeights(CharsAndFactorDefs.getPartIndiceWeights(), vowelWeight, consoWeight, CharsAndFactorDefs.getStressPunishment());
        boolean removeErrorEntries = true;
        for (Iterator<PhEntry> it = entries.iterator(); it.hasNext(); ) {
            PhEntry phEntry = it.next();
            boolean failed = false;
            try {
                phEntry.normalizeIPA_UNICODE();
                phEntry.IPAToPhSignMs();
                phEntry.setReversed();
                phEntry.phSignMsToPhParts();
            } catch (client.SignNotSuittedException sNSE) {
                failed = true;
                if (printIPAErrors) {
                    System.err.print(sNSE.getMessage());
                }
            } catch (NullPointerException npE) {
                failed = true;
                if (printIPAErrors) System.err.print(npE);
            }
            if (failed) {
                errorPhEntryList.add(phEntry);
                if (printIPAErrors) System.err.print(": " + phEntry.toString());

                if (removeErrorEntries) {
                    it.remove();
                    if (printIPAErrors) prErr(": removed fromIndex Memory.\n");
                }
            }
        }
        this.entriesRev = (List<PhEntry>) ((ArrayList<PhEntry>) this.entries).clone();
        Collections.sort(this.entriesRev);
        if (printIPAErrors) {
            String rauten = "#####################################################";
            prErr("\n" + rauten + "\nList of Filtered entrys due to illegal IPA char-mappings\n" + rauten + "\n" + charsNotInMainmapCount() + "\n");
        }
    }

    public List<PhEntry> getEntries() {
        return entries;
    }

    public List<PhEntry> getEntriesRev() {
        return entriesRev;
    }

    /**
     * Information about the unicode chars which could not have been parsed correctly to PhSigns
     *
     * @return an Info-String
     */
    private String charsNotInMainmapCount() {
        String str = "";
        for (char ch : charsNotInMainMap.keySet()) {
            str += "<\t" + ch + "\t> = <" + CharsAndFactorDefs.getUniCodeStr(ch) + "> appeared " + charsNotInMainMap.get(ch) + " times\n";
        }
        return str;
    }





    public void outputResult(Multimap<Float, PhEntry> similarities, Output output, ClientOptions clientOptions, PhEntry queryEntry, boolean skipFirstEntry) {
        startSW("outputResult()");
        output.setQueryEntry(queryEntry);
        //output.init(clientOptions, queryEntry,this); //TODO: bisschen doppelt gemoppelt
        //output.setOptions(clientOptions.formatOptionses);
        int nrOfIterations = 0;
        SortedSet<Float> set = (SortedSet) similarities.keySet();
        // starts with the highest similarity existing
        Float simi=0.0f;
        if (set.size()>0){
            simi= set.last();
        }
        boolean foundEntries = false;
        try {
            while (true) {
                // check if similarity is within searched thressholds
                if (simi < clientOptions.lowThreshold) {
                    // if simlitarity is already lower than break loop. since similaries are in descending order no higher similarities will follow
                    prL3("Skipping all Entries with Similarity of "+simi+" Because of clientOptions.lowThreshold");
                    break;
                }
                if (simi > clientOptions.highThreshold) {
                    nrOfIterations++;
                    simi = set.headSet(simi).last();
                    prL3("Skipping Entriy with Similarities of "+simi+" Because of clientOptions.highThreshold");
                    continue;
                }
                foundEntries = true;

                if(skipFirstEntry&&nrOfIterations==0){
                    nrOfIterations++;
                    simi = set.headSet(simi).last();
                    prL3("Skipping first Entry");
                    continue;
                }
                // gets the collection for all entries with this similarity
                Collection<PhEntry> col = similarities.get(simi);
                boolean goOn =true; // when the sink has printed enough entries to fullfill "--fromTopTill"
                for (PhEntry phEntry : col) {

                    goOn = output.addToOutput(phEntry, simi);

                    nrOfIterations++;
                    if(!goOn)break;
                  //  if (clientOptions.fromTopTill != -1 && nrOfIterations >= clientOptions.fromTopTill) break;
                }

                if(!goOn)break;
                //TODO hier wird nicht berücksichtigt dass in einer kleinen Datenbank auch weniger einträge als "fromTopTill" vorschreibt sein können --> endlosschleife
                // if (clientOptions.fromTopTill != -1 && nrOfIterations >= clientOptions.fromTopTill) break;
                // gets the next highest similarity below the aktueller wert
                simi = set.headSet(simi).last();
                //    System.out.println("simi = "+simi+"  nrOfIterations = "+ nrOfIterations);
            }
        } catch (Exception ex) {
            prErr(ex.getStackTrace().toString());
        }
        stopSW("outputResult()");
        if (!foundEntries) prL2("No entry matched your thresholds: low = " + clientOptions.lowThreshold + " high= " + clientOptions.highThreshold+"\n");
    }

    /**
     * Exports all iniialised PhEntries to an SQLite Database
     * for every Entry all rhymes get calculated (see EXPORT options )
     * If set, will also create a serialized HashMap for faster row QUERY in Android App
     * (the row indice of a given word will be found by the hashmap, then the rhymes string will be taken from the database)
     *
     * RELEVANT CLIENTARGS options: exportToDB; EQU_ENDS; serializeWordIndexHashMap; queryOpp_ALL_VS_ALL_StopAtEntryIndex
     *
     * @param clientOptions
     * @throws SQLException
     */
    //TODO umbauen,
    public void queryAllEntries(ClientOptions clientOptions, Output output) throws SQLException {
        Multimap<Float, PhEntry> mp;
        List<PhEntry> entries = Utils.getSubList(getEntries(), clientOptions.fromIndex, clientOptions.tillIndex, clientOptions.eachEntry);
        int size = entries.size();
        for (int i = clientOptions.exportStartAtEntryIndex; i < size; i++) {
            PhEntry entry = entries.get(i);
            mp = calcSimilaritiesTo(entry.getWord(), 100000, entries, clientOptions.lowThreshold);
            prL3("###################################################\nRunning Query "+i+"\tfor <"+entry.getWord()+">");
            outputResult(mp, output,clientOptions,entry, true);
            if(clientOptions.queryOpp_ALL_VS_ALL_StopAtEntryIndex !=-1){
                if (i>=clientOptions.queryOpp_ALL_VS_ALL_StopAtEntryIndex){
                    prL3("Finished Querying because of queryOpp_ALL_VS_ALL_StopAtEntryIndex = " + clientOptions.queryOpp_ALL_VS_ALL_StopAtEntryIndex);
                    break;
                }
            }
            prL2("\n");
        }

    }


















    /**
     * looks up the suitable PhEntry for the given word in the entry-list
     *
     * @param entries
     * @param word
     * @param ignoreCase
     * @return
     */
    public PhEntry getEntry(List<PhEntry> entries, String word, boolean ignoreCase)throws NoSuchElementException {
        int wortIndex = Utils.ordinaryIndexSearch(entries, LDBEntryComparisonField.word, word, ignoreCase);
        return entries.get(wortIndex);
    }

    /**
     * looks up the suitable PhEntry for the word in rhymesArrIndex-var entries
     *
     * @param word
     * @param ignoreCase
     * @return
     */
    public PhEntry getEntry(String word, boolean ignoreCase) throws NoSuchElementException {
        return getEntry(entries, word, ignoreCase);
    }



    /**
     * @param srcWord      the source-word (Words db will be compared against this one)
     * @param roundTo      the number of decimal places to round / group the entries in the result map
     * @param lowThreshold if the similarity will be lower than this the calculation may be stopped
     * @return an similarity-ordered map
     */
    public Multimap<java.lang.Float, PhEntry> calcSimilaritiesTo(String srcWord, int roundTo, float lowThreshold)throws NoSuchElementException {
        return calcSimilaritiesTo(srcWord, roundTo, entries, lowThreshold);
    }

    /**
     *
     * @param srcWord
     * @param roundTo the number of decimal places to round / group the entries in the result map
     * @return an similarity-ordered map
     */
    public Multimap<java.lang.Float, PhEntry> calcSimilaritiesTo(String srcWord, String[] testWords, int roundTo) {
        PhEntry phEntry = getEntry(srcWord, true);
        if (phEntry == null) return null;
        List<PhEntry> entries = new ArrayList<>();
        for (int i = 0; i < testWords.length; i++) {
            if (testWords[i] == null || testWords[i].equals("")) continue;
            PhEntry entrie = getEntry(testWords[i], true);
            if (entrie != null) entries.add(entrie);
        }
        if (entries.size() > 0) return calcSimilaritiesTo(phEntry, roundTo, entries, -1.0f);
        else return null;
    }



    /**
     * @param srcWord
     * @param roundTo      the number of decimal places to round / group the entries in the result map
     * @param entries
     * @param lowThreshold if the similarity will be lower than this the calculation may be stopped
     * @return
     */
    public Multimap<java.lang.Float, PhEntry> calcSimilaritiesTo(String srcWord, int roundTo, List<PhEntry> entries, float lowThreshold)throws NoSuchElementException {
        PhEntry phEntry = getEntry(srcWord, true);
        if (phEntry == null) return null;
        return calcSimilaritiesTo(phEntry, roundTo, entries, lowThreshold);
    }

    /**
     * @param srcEntry
     * @param roundTo      the number of decimal places to round / group the entries in the result map
     * @param entries
     * @param lowThreshold if the similarity will be lower than this the calculation may be stopped
     * @return
     */
    public Multimap<java.lang.Float, PhEntry> calcSimilaritiesTo(PhEntry srcEntry, int roundTo, List<PhEntry> entries, float lowThreshold) {
        Multimap<Float, PhEntry> entriesSimilarities = TreeMultimap.create();
        for (PhEntry phEntry : entries) {
            float similarity = 0.0f;

            if (lowThreshold == -1.0f) { // -1.0 bedeutet hier, dass kein low Thresshold gesetzt wurde
                similarity = srcEntry.calcSimilarity(phEntry);
            } else {
                similarity = srcEntry.calcSimilarity(phEntry, lowThreshold);
                if (similarity == -1.0f) { //-1.0 bedeutet hier, dass durch den lowThreshold die berechnung abgebrochen wurde (weil das Ergebnis zu niedrig gewesen wäre)
                    stoppedCalculatingEntriesCount++;
                    continue;
                }
            }

            similarity = Math.round(similarity * roundTo) / (float) roundTo;
            entriesSimilarities.put(similarity, phEntry);
        }
        return entriesSimilarities;
    }

    /**
     * returns a new ordered-list centered around the keyfield value and nrOfNext entries, up / down the ordered indices
     *
     * @param entries
     * @param keyField
     * @param keyFieldValue
     * @param nrOfNext
     * @return
     */
    public List<PhEntry> getSublistOfNext(List<PhEntry> entries, LDBEntryComparisonField keyField, String keyFieldValue, int nrOfNext)throws NoSuchElementException {
        List<PhEntry> sList = null;
        //try {
            int index = Utils.ordinaryIndexSearch(entries, keyField, keyFieldValue, true);
            //if (index == -1) return null;
            //else
                sList = (List) Utils.getSubList(entries, index - nrOfNext, index + nrOfNext, 1);
            /*
        } catch (NoSuchElementException ex) {
            RhymesClient.prErr("Could not find Entry in DB: <" + keyFieldValue + ">");
        }catch(Exception ex){
            System.out.println("LDBStructure.getSublistOfNext(): Exception: " + ex.getMessage());
        }
        */
        return sList;
    }


}



