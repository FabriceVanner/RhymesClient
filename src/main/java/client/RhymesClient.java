package client;

import com.google.common.collect.Multimap;
import os_specifics.OSSpecificProxy;
import output.*;
import phonetic_entities.PhEntry;
import wiktionaryParser.XMLDumpParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import static client.ClientOptions.ClientInterface.CONSOLE;

/**
 * Created by Fab on 11.05.2015.
 * The DBExport Class to be executed
 * TODO: EXPORT jar bug: Running in IntelliJ works fine, but exported artifact-jar calculates (on same JRE and same Computer) other -WRONG- results. float-rounding?!
 */

public class RhymesClient {
    PhEntriesStructure phEntriesStructure;

    private static String clientFileName;
    public static String clientsFolderPath;
    public Output output;// = new StringOutput(new SysOutSink());
    public Sink sink=null;
    public static ClientOptions clientOptions;
    public static RhymesClient rC = new RhymesClient();

    public static void main(String[] args) {
        shellAndClient(args);
        //mainNewInConstruction(args);

    }


    /**
     * if args are present commandline client is used. else, SHELL is started
     *
     * @param args
     */
    public static void shellAndClient(String[] args) {
        setJarFilenameAndClientPath();
        ClientOptions clientOptions = new ClientOptions();
        rC.clientOptions = clientOptions;
        clientOptions.eval(args);

        if(clientOptions.help)return;

        if (clientOptions.clientInterface == CONSOLE) {
            if (rC.init(clientOptions)) try {
                rC.runTask(clientOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }


        System.out.print("No arguments: Starting Shell. ");

        if (!rC.init(clientOptions)) return;
        System.out.println("Loaded dict-file. ");
        RhymesClientShell.clientOptions = clientOptions;
        RhymesClientShell.rC = rC;
        RhymesClientShell.main();
    }


    /**
     * sets up the clients folderpath and its jarfilename
     */
    public static void setJarFilenameAndClientPath() {
        StringBuilder clientFileNameSB = new StringBuilder();
        StringBuilder clientsFolderPathSB = new StringBuilder();
        (new OSSpecificProxy()).setJarfilenameAndClientPath(clientFileNameSB, clientsFolderPathSB);
        clientFileName = clientFileNameSB.toString();
        clientsFolderPath = clientsFolderPathSB.toString();

    }

    public void set() {

    }

    public static void prErr(String str) {
        System.err.println(getClientFileName() + str);

    }

    /**
     * prints out the current time if clientOptions.printPerformance==true
     */
    public static void prTime(String str) {
        if (clientOptions.printPerformance) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            System.out.println(sdf.format(cal.getTime()) + " - " + str);
        }
    }

    public static String getClientFileName() {
        return clientFileName;
    }

    public static void setClientFileName(String clientFileName) {
        RhymesClient.clientFileName = clientFileName;
    }

    public static String getClientsFolderPath() {
        return clientsFolderPath;
    }

    public static void setClientsFolderPath(String clientsFolderPath) {
        RhymesClient.clientsFolderPath = clientsFolderPath;
    }

    /**
     * initialises the client by loading into memory(dict file etc...)
     *
     * @param clientOptions
     * @return if it worked
     */
    public boolean init(ClientOptions clientOptions) {
        try {
            this.phEntriesStructure = new PhEntriesStructure(clientOptions.printErrors, clientOptions.ipaDictFilepath);
        } catch (ClassNotFoundException cnfE) {
            prErr(cnfE.toString());
            return false;
        } catch (FileNotFoundException fnfE) {
            prErr(fnfE.getMessage() + " Aborting.");
            return false;

        } catch (IOException fnfE) {
            prErr(fnfE.getMessage() + " Aborting.");
            return false;
        }
        return true;
    }

    /**
     * runs the task indicated by the Clientargs
     *
     * @param clientOpts
     */
    public void runTask(ClientOptions clientOpts) throws Exception {
        switch (clientOpts.outputSink){
            case SYSOUT:
                sink = new SysOutSink();
            case SQLLITE:
                sink = new DBSink();
        }

        switch (clientOpts.outFormatType){
            case STRING:
                output = new StringOutput(sink);
        }


        switch (clientOpts.clientOperation) {
            case PRINT_IPA:
                printIPA(clientOpts.words);
                break;
            case PARSE_XMLDUMP:
                // parses the phonetics of eachEntry word-article of the wiktionary xml-dumpfile and stores the result as textfile with default filename at clients path
                prTime("Starting to parse Dump Input File " + clientOpts.xmlDumpFilePath + " to Dict OutputFile" + "clientOptions.ipaDictFilepath");
                XMLDumpParser xmlDumpParser = new XMLDumpParser();
                xmlDumpParser.parseXMLDump(clientOpts.xmlDumpFilePath, clientOpts.ipaDictFilepath);
                prTime("Finished Parsing");
                break;
            case REV_IPA_SEARCH:
                runRevIpaSearchTask(clientOpts);
                break;
            case QUERY:
                prTime("Starting Query-Task");
                runQueryTask(clientOpts);
                prTime("Ending Query-Task. Nr of stopped Entry-Calculations because of low Thresshold:" + phEntriesStructure.stoppedCalculatingEntriesCount);
                break;
            /*
            case EXPORT:
                //clientOptions.output=new StringOutput(new DBSink());
                runExportTask(clientOpts);
                break;
                */
        }
    }


    private void runRevIpaSearchTask(ClientOptions clientOptions) {
        String infoSysOut = clientOptions.getOptionInfo();
        String sysOutQuery = "";
        if (clientOptions.clientInterface!= ClientOptions.ClientInterface.SHELL) System.out.print(infoSysOut + "\n");
        sysOutQuery = (returnSurroundingReversedIPA(clientOptions.words[0], clientOptions.revIpaSearchNeighboursUpAndDown));
        System.out.println(sysOutQuery);
    }




    /**
     * additional function: simply looks up and prints the IPA(s) of the src wordsArrLi
     *
     * @param srcWords
     */
    private void printIPA(String[] srcWords) {
        for (String word : srcWords) {
            PhEntry phEntry;
            if (word != null) {
                phEntry = phEntriesStructure.getEntry(word, true);
                if (phEntry != null) System.out.println(phEntry.toString());
            }
        }
    }


    /**
     * the heart of the client, the phonetic search
     */
    private void runQueryTask(ClientOptions clientOptions)throws Exception,SQLException,NoSuchElementException {
        Multimap<Float, PhEntry> mp = null;
        if((clientOptions.clientInterface == CONSOLE))System.out.println(clientOptions.getOptionInfo()+"\n");
        output.init(clientOptions,phEntriesStructure);
        output.openSink();
        PhEntry queryEntry;
        try {
            switch (clientOptions.queryOperation) {
                case ONE_VS_ALL:
                    queryEntry= phEntriesStructure.getEntry(clientOptions.srcWord, true);
                    List<PhEntry> entries = Utils.getSubList(phEntriesStructure.getEntries(), clientOptions.fromIndex, clientOptions.tillIndex, clientOptions.eachEntry);
                    mp = phEntriesStructure.calcSimilaritiesTo(clientOptions.srcWord, 100000, entries, clientOptions.lowThreshold);
                    if (mp == null) return;
                    phEntriesStructure.outputResult(mp, output, clientOptions,queryEntry);
                    break;
                case ONE_VS_SOME:
                    queryEntry= phEntriesStructure.getEntry(clientOptions.srcWord, true);
                    mp = phEntriesStructure.calcSimilaritiesTo(clientOptions.srcWord, clientOptions.words, 100000);
                    if (mp == null) return;
                    phEntriesStructure.outputResult(mp, output, clientOptions,queryEntry);
                    break;
                case ALL_VS_ALL: //EXPORT
                    phEntriesStructure.queryAllEntries(clientOptions, output);
                    break;
            }

        }catch (NoSuchElementException nsee){
            this.prErr("Could not find Entry in DB: <" + clientOptions.srcWord + ">");
        }
        output.openSink();
        System.out.println();
    }




    /**
     * additional function: returns the surrounding entries (by index) of the given srcWord in the unicode-ordered list
     *
     * @param srcWord  the srcWord
     * @param nrOfNext how many entries upwards and downwards of the index(of the sourceword) to print
     * @return a formatted string
     */
    private String returnSurroundingReversedIPA(String srcWord, int nrOfNext)throws NoSuchElementException {
        int wortIndex = Utils.ordinaryIndexSearch(phEntriesStructure.getEntries(), PhSignDefs.LDBEntryComparisonField.word, srcWord, true);
        //if (wortIndex == -1) {
        //    return "";
       // }
        PhEntry phEntry1 = this.phEntriesStructure.getEntries().get(wortIndex);
        List<PhEntry> entries = this.phEntriesStructure.getSublistOfNext(this.phEntriesStructure.getEntriesRev(), PhSignDefs.LDBEntryComparisonField.ipaRev, phEntry1.getIpaRev(), nrOfNext);
        String out = "";
        for (PhEntry phEntry : entries) {
            out += phEntry.toString() + "\n";
        }
        return out;
    }


}
