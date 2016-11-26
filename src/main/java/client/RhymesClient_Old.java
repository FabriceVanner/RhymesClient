package client;

import com.google.common.collect.Multimap;
import os_specifics.OSSpecificProxy;
import output.StringOut;
import output.SysOutSink;
import phonetic_entities.PhEntry;
import wiktionaryParser.XMLDumpParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.NoSuchElementException;

import static client.ClientArgs.ClientMode.SHELL;
import static output.DBExport.*;

/**
 * Created by Fab on 11.05.2015.
 * The DBExport Class to be executed
 * TODO: EXPORT jar bug: Running in IntelliJ works fine, but exported artifact-jar calculates (on same JRE and same Computer) other -WRONG- results. float-rounding?!
 */

public class RhymesClient_Old {
    PhEntriesStructure phEntriesStructure;

    private static String clientFileName;
    public static String clientsFolderPath;

    public static ClientArgs clientArgs;
    public static RhymesClient_Old rC = new RhymesClient_Old();

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

        //ClientArgs clientArgs;
        try {
            clientArgs = new ClientArgs(args);
        } catch (IllegalArgumentException iAE) {
            return;
        }

        if (clientArgs.showHelp) {
            System.out.println(StringsAndStuff.help);
            return;
        }

        if (clientArgs.clientMode != SHELL) {
            if (rC.init(clientArgs)) rC.runTask(clientArgs);
            return;
        }


        System.out.print("No arguments: Starting Shell. ");

        if (!rC.init(clientArgs)) return;
        System.out.println("Loaded dict-file. ");
        RhymesClientShell.clientArgs = clientArgs;
    //TODO: uncomment me    RhymesClientShell.rC = rC;
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
     * prints out the current time if clientArgs.printPerformance==true
     */
    public static void prTime(String str) {
        if (clientArgs.printPerformance) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            System.out.println(sdf.format(cal.getTime()) + " - " + str);
        }
    }

    public static String getClientFileName() {
        return clientFileName;
    }

    public static void setClientFileName(String clientFileName) {
        RhymesClient_Old.clientFileName = clientFileName;
    }

    public static String getClientsFolderPath() {
        return clientsFolderPath;
    }

    public static void setClientsFolderPath(String clientsFolderPath) {
        RhymesClient_Old.clientsFolderPath = clientsFolderPath;
    }

    /**
     * initialises the client by loading into memory(dict file etc...)
     *
     * @param clientArgs
     * @return if it worked
     */
    public boolean init(ClientArgs clientArgs) {
        try {
            this.phEntriesStructure = new PhEntriesStructure(clientArgs.printErrors, clientArgs.ipaDictFilepath);
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
     * @param clientArgs
     */
    public void runTask(ClientArgs clientArgs) {

        switch (clientArgs.clientTask) {
            case PRINT_IPA:
                printIPA(clientArgs.words);
                break;
            case PARSE_XMLDUMP:
                // parses the phonetics of eachEntry word-article of the wiktionary xml-dumpfile and stores the result as textfile with default filename at clients path
                prTime("Starting to parse Dump Input File " + clientArgs.parseXMLDUMPFile + " to Dict OutputFile" + "clientArgs.ipaDictFilepath");
                XMLDumpParser xmlDumpParser = new XMLDumpParser();
                xmlDumpParser.parseXMLDump(clientArgs.parseXMLDUMPFile, clientArgs.ipaDictFilepath);
                prTime("Finished Parsing");
                break;
            case REV_IPA_SEARCH:
                runRevIpaSearchTask(clientArgs);
                break;
            case QUERY:
                clientArgs.output=new StringOut(new SysOutSink());
                prTime("Starting Query-Task");
                runQueryTask(clientArgs);
                prTime("Ending Query-Task. Nr of stopped Entry-Calculations because of low Thresshold:" + phEntriesStructure.stoppedCalculatingEntriesCount);
                break;
            case EXPORT:
                //clientArgs.output=new StringOut(new DBSink());
                runExportTask(clientArgs);
                break;
        }
    }


    private void runRevIpaSearchTask(ClientArgs clientArgs) {
        String infoSysOut = clientArgs.getInfoString();
        String sysOutQuery = "";
        if (!clientArgs.shellModeOn) System.out.print(infoSysOut + "\n");
        sysOutQuery = (returnSurroundingReversedIPA(clientArgs.words[0], clientArgs.simpleRevIpaSearch));
        System.out.println(sysOutQuery);
    }




    /**
     * additional function: simply looks up and prints the IPA(s) of the src words
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

    private void runExportTask(ClientArgs clientArgs) {
        try {

            setDBFileName(clientArgs.exportToDBFilename);
            setDBFilePath(clientsFolderPath);
            setPhEntriesStructure(phEntriesStructure);
            getConn();
            if (clientArgs.exportStartAtEntryIndex == 0) {
                createNewDatabase(null);
                createTable(clientArgs.fromTopTill);
            }

            exportToDB(clientArgs);
            closeConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * the heart of the client, the phonetic search
     */
    private void runQueryTask(ClientArgs clientArgs) {
        Multimap<Float, PhEntry> mp = null;
        String queryOut = "";
        try {
            switch (clientArgs.queryOperation) {
                case ONE_AGAINST_ALL:
                    List<PhEntry> entries = Utils.getSubList(phEntriesStructure.getEntries(), clientArgs.fromIndex, clientArgs.tillIndex, clientArgs.eachEntry);
                    mp = phEntriesStructure.calcSimilaritiesTo(clientArgs.srcWord, 100000, entries, clientArgs.lowThreshold);
                    break;
                case ONE_AGAINST_SEVERAL_GIVEN:
                    mp = phEntriesStructure.calcSimilaritiesTo(clientArgs.srcWord, clientArgs.words, 100000);
                    break;
            }

        }catch (NoSuchElementException nsee){
            this.prErr("Could not find Entry in DB: <" + clientArgs.srcWord + ">");
        }
        if (mp == null) return;
        /*
        if (clientArgs.FILTER_EQU_ENDS) {
            PhEntry phE = phEntriesStructure.getEntry(clientArgs.srcWord, true);
            mp = phEntriesStructure.filterEqualEndingWordsOut(mp, phE, clientArgs.FILTER_EQU_ENDS, clientArgs.fromTopTill);
        }
        */
        if(!(clientArgs.clientMode == SHELL))System.out.println(clientArgs.getInfoString()+"\n");
        PhEntry phE = phEntriesStructure.getEntry(clientArgs.srcWord, true);
        phEntriesStructure.outputResult(mp, phE, true, clientArgs);
        System.out.println();
        //if(!(clientArgs.clientMode ==SHELL))System.out.println(clientArgs.getInfoString());

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
