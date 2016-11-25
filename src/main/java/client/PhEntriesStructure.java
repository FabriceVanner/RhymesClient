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
     * Constructor to use with Dictionary in Database format
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
        RhymesClient.prTime("Starting initialising Entries");
        initEntries(printErrors);
        RhymesClient.prTime("Ended initialising Entries");
    }

    /**
     * Constructor to use with Dictionary in Textfile format
     *
     * @param printErrors
     * @param dictTextFilePath
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public PhEntriesStructure(boolean printErrors, String dictTextFilePath) throws ClassNotFoundException, FileNotFoundException, IOException {
        RhymesClient.prTime("Starting reading dictFile");
        this.entries = Utils.readDictTextFile(dictTextFilePath, "    ");
        RhymesClient.prTime("Ending reading dictFile");
        RhymesClient.prTime("Starting initialising Entries");
        initEntries(printErrors);
        RhymesClient.prTime("Ended initialising Entries");
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
                    if (printIPAErrors) System.err.print(": removed fromIndex Memory.\n");
                }
            }
        }
        this.entriesRev = (List<PhEntry>) ((ArrayList<PhEntry>) this.entries).clone();
        Collections.sort(this.entriesRev);
        if (printIPAErrors) {
            String rauten = "#####################################################";
            System.err.print("\n" + rauten + "\nList of Filtered entrys due to illegal IPA char-mappings\n" + rauten + "\n" + charsNotInMainmapCount() + "\n");
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





/**TODO: completely replacing similaritiesToString() and other EXPORT and filter methods with this method:*/
    public void outputResult(Multimap<Float, PhEntry> similarities, PhEntry queryEntry, boolean skipFirstEntry, ClientArgs clientArgs) {
        Output output = clientArgs.output;
        //TODO: folgendes wieder reinkommentieren:
        //output.init(clientArgs, queryEntry,this); //TODO: bisschen doppelt gemoppelt
        //output.setOptions(clientArgs.outputOptions);

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
                if (simi < clientArgs.lowThreshold) {
                    // if simlitarity is already lower than break loop. since similaries are in descending order no higher similarities will follow
                    break;
                }
                if (simi > clientArgs.highThreshold) {
                    nrOfIterations++;
                    simi = set.headSet(simi).last();
                    continue;
                }
                foundEntries = true;
                // gets the collection for all entries with this similarity
                Collection<PhEntry> col = similarities.get(simi);
                for (PhEntry phEntry : col) {

                    output.addToOutput(phEntry, simi);

                    nrOfIterations++;

                    if (clientArgs.fromTopTill != -1 && nrOfIterations >= clientArgs.fromTopTill) break;
                }

                //TODO hier wird nicht berücksichtigt dass in einer kleinen Datenbank auch weniger einträge als "fromTopTill" vorschreibt sein können --> endlosschleife
                if (clientArgs.fromTopTill != -1 && nrOfIterations >= clientArgs.fromTopTill) break;
                // gets the next highest similarity below the aktueller wert
                simi = set.headSet(simi).last();
                //    System.out.println("simi = "+simi+"  nrOfIterations = "+ nrOfIterations);
            }
        } catch (Exception ex) {
        }
        if (!foundEntries) RhymesClient.prErr("No entry matched your thresholds: low = " + clientArgs.lowThreshold + " high= " + clientArgs.highThreshold);
    }


    /**
     * TODO whole method to be replaced by outputResult() which works with OutputBase
     * works fromIndex the last entry-set through the given multimap:
     * iterates trough all entries with same similarity then gets the next highest similarity and works through that collection
     *
     * @param similarities
     * @param printToSysOut
     * @param skipFirstEntry TODO: the first entry usually is the word with 1.0 similarity --> the word itself
     * @param clientArgs
     * @return when printing out to CONSOLE, "" will be retourned, else the whole result as a HUGE string will be retourned
     */
    public String similaritiesToString(Multimap<Float, PhEntry> similarities, boolean printToSysOut, boolean skipFirstEntry, ClientArgs clientArgs) {
        String out = "";
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
                if (simi < clientArgs.lowThreshold) {
                    // if simlitarity is already lower than break loop. since similaries are in descending order no higher similarities will follow
                    break;
                }
                if (simi > clientArgs.highThreshold) {
                    nrOfIterations++;
                    simi = set.headSet(simi).last();
                    continue;
                }
                foundEntries = true;
                // gets the collection for all entries with this similarity
                Collection<PhEntry> col = similarities.get(simi);
                for (PhEntry phEntry : col) {
                    if (!clientArgs.delimiterSeperated) {
                        out += String.format("# %5f%s\n", simi, phEntry.toString(clientArgs.printDetail));
                    } else {
                        out += phEntry.getWord() + clientArgs.outputDelimiter;
                    }
                    nrOfIterations++;

                    if (clientArgs.fromTopTill != -1 && nrOfIterations >= clientArgs.fromTopTill) break;
                }
                if (printToSysOut) {
                    System.out.print(out);
                    out = "";
                }

                //TODO hier wird nicht berücksichtigt dass in einer kleinen Datenbank auch weniger einträge als "fromTopTill" vorschreibt sein können --> endlosschleife
                if (clientArgs.fromTopTill != -1 && nrOfIterations >= clientArgs.fromTopTill) break;
                // gets the next highest similarity below the aktueller wert
                simi = set.headSet(simi).last();
                //    System.out.println("simi = "+simi+"  nrOfIterations = "+ nrOfIterations);
            }
        } catch (Exception ex) {
        }

        if (!foundEntries)
            RhymesClient.prErr("No entry matched your thresholds: low = " + clientArgs.lowThreshold + " high= " + clientArgs.highThreshold);
        if (printToSysOut) {
            System.out.println();
            return "";
        }
        out += "\n";
        return out;
    }

/** TODO: STUB */
    public void groupRhymesWithEquWordEnds(){
/*
    mit fertigen String ergebnissen arbeiten? oder mit Multimap ?
    gibt es einen Eintrag vor mir?
    endet er genauso wie ich? (anzahl der identischen chars festlegen)
    schreib ihn (in die gleiche Zeil/ in die glleiche arrayrow)
     wenn nicht, mach einen Absatz (nächste array-row)

 */


    }


    /**
     * removes entries with equal word-ending fromIndex map
     *
     * @param similarities      Map with PhEntries, ordered by float-key
     * @param filterEntry       if the word-string of an entry of the map contains this entries word-string it will be filtered out
     * @param filterEquWordBase
     * @param fromTopTill       processes the first <int> entries
     * @return
     */
    public Multimap<java.lang.Float, PhEntry> filterEqualEndingWordsOut(Multimap<Float, PhEntry> similarities, PhEntry filterEntry, boolean filterEquWordBase, int fromTopTill) {
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
     * looks up the suitable PhEntry for the word in i-var entries
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



