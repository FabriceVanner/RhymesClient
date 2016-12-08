package client;

import com.google.common.collect.Multimap;
import learning.StopWatch;
import org.kohsuke.args4j.CmdLineException;
import os_specifics.OSSpecificProxy;
import output.*;
import phonetic_entities.PhEntry;
import wiktionaryParser.XMLDumpParser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
    public static long lastTime=0;
    //public static Stack<StopWatch> timeStack = new Stack<>();
    public static HashMap<String,StopWatch> stopWatchMap= new HashMap<>();


    public static void main(String[] args) {

        //mainNewInConstruction(args);
        setJarFilenameAndClientPath();
        ClientOptions clientOptions = new ClientOptions();
        rC.clientOptions = clientOptions;
        startSW("Main Method");
        startSW("Eval ClientOptions");
        try {
            clientOptions.eval(args);
        } catch (CmdLineException e) {
            return;
        }
        stopSW("Eval ClientOptions");
        if(clientOptions.help)return;
        if (clientOptions.clientInterface == CONSOLE) {
            if (rC.init(clientOptions)) try {
                rC.runTask(clientOptions);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stopSW("Main Method");
            return;
        }


        RhymesClient.prL1("No arguments: Starting Shell.\n");
        if (!rC.init(clientOptions)) return;
        //RhymesClientShell.clientOptions = clientOptions;
        //RhymesClientShell.rC = rC;
        //RhymesClientShell.main();
        mainLoop(args);
    }

    public static void mainLoop(String[] args) {
        Scanner command = new Scanner(System.in);

        clientOptions.argsContainCommand=true;
        while(true){
            System.out.print("RhymesClient: ");
            String inputStr = command.nextLine();

            if(inputStr.equals("exit")) {
                prL2("Ending");
                break;
            }

            args = inputStr.split(" ");


            try {
                clientOptions.eval(args);
            } catch (CmdLineException e) {
                clientOptions.argsContainCommand=true;
                continue;
            }

            try {
                rC.runTask(clientOptions);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        command.close();
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

    /**
     *  @param message
     *
     */
    public static void prErr(String message) {
        if(clientOptions.verbose>-1) System.err.println(getClientFileName() + message);
    }

    /**
     * regular output;
     * @param message
     */
    public static void pr(String message) {
        if(clientOptions.verbose>0){
            System.out.print(message);
        }
    }
    /**
     * regular output;
     * @param message
     */
    public static void prln(String message) {
        if(clientOptions.verbose>0){
            System.out.println(message);
        }
    }


    /**
     * 1.LevelMeta
     * @param message
     */
    public static void prL1(String message) {
        if(clientOptions.verbose>1){
            System.out.print(message);
        }
    }



    /**
     * 2.LevelMeta
     * @param message
     */
    public static void prL2(String message) {
        if(clientOptions.verbose>2){
            System.out.print(message);
        }
    }

    /**
     * 3.LevelMeta
     * @param message
     */
    public static void prL3(String message) {
        if(clientOptions.verbose>3){
            System.out.println(message);
        }
    }

    /**
     * debug
     * @param message
     */
    public static void prDebug(String message) {
        if(clientOptions.verbose>4){
            System.out.println("DEBUG:\t" + message);
        }
    }




    /**
     * prints out the current time if clientOptions.printPerformance==true
     */
    public static void prTime(String str) {
        if (clientOptions.printPerformance) {

            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            //RhymesClient.prL3(sdf.format(cal.getTime()) + " - " + str);
            long thisTime = (Calendar.getInstance().getTimeInMillis())-lastTime;
            System.out.println("TIME "+thisTime+"ms\t" + (int)(thisTime/1000)+"sec \t"+ str);
            //System.out.println("#TIMECALL; LASTCALL TILL NOW: "+(thisTime)+" ms");
            //lastTime=thisTime;

        }
    }
    public static void startSW(String str) {
        if (clientOptions.printPerformance) {
            stopWatchMap.put(str, new StopWatch());
            //timeStack.push(new StopWatch("Starting Opt.-Eval"));
        }
    }

    public static void stopSW(String str){
        if (clientOptions.printPerformance) {
            System.out.println("#### StopWatch: " + str + " took " + stopWatchMap.get(str).getElapsedTimeStr()+"  ####");
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
        RhymesClient.prL1("Loaded dict-file.\n ");
        return true;
    }

    /**
     * runs the task indicated by the Clientargs
     *
     * @param clientOpts
     */
    public void runTask(ClientOptions clientOpts) throws SQLException {
        switch (clientOpts.outputSink){
            case SYSOUT:
                sink = new SysOutSink();
                break;
            case SQLLITE:
                sink = new DBSink();
                break;
        }

        switch (clientOpts.outFormatType){
            case STRING:
                output = new StringOutput(sink);
        }
        RhymesClient.prL1("\nClient Operation = "+ clientOpts.clientOperation+", ");

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
                //prTime("Starting Query-Task");
                startSW("Query-Task");
                runQueryTask(clientOpts);
                stopSW("Query-Task");
                prL3("Nr of stopped Entry-Calculations because of low Thresshold:" + phEntriesStructure.stoppedCalculatingEntriesCount);
                //prTime("Ended Query-Task. Nr of stopped Entry-Calculations because of low Thresshold:" + phEntriesStructure.stoppedCalculatingEntriesCount);
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
        if (clientOptions.clientInterface!= ClientOptions.ClientInterface.SHELL) RhymesClient.prL1(infoSysOut + "\n");
        sysOutQuery = (returnSurroundingReversedIPA(clientOptions.words[0], clientOptions.revIpaSearchNeighboursUpAndDown));
        RhymesClient.pr(sysOutQuery);
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
                if (phEntry != null) RhymesClient.pr(phEntry.toString());
            }
        }
    }


    /**
     * the heart of the client, the phonetic search
     */
    private void runQueryTask(ClientOptions clientOptions)throws SQLException {
        Multimap<Float, PhEntry> mp = null;
        if((clientOptions.clientInterface == CONSOLE))RhymesClient.prL1(clientOptions.getOptionInfo()+"\n");
        output.init(clientOptions,phEntriesStructure);
        output.openSink();
        PhEntry queryEntry;
        RhymesClient.prL1("Starting Query-Operation = "+ clientOptions.queryOperation+" ...\n");
        try {
            switch (clientOptions.queryOperation) {
                case ONE_VS_ALL:
                    RhymesClient.startSW("Query "+clientOptions.queryOperation.toString());
                    queryEntry= phEntriesStructure.getEntry(clientOptions.srcWord, true);
                    List<PhEntry> entries = Utils.getSubList(phEntriesStructure.getEntries(), clientOptions.fromIndex, clientOptions.tillIndex, clientOptions.eachEntry);
                    mp = phEntriesStructure.calcSimilaritiesTo(clientOptions.srcWord, 100000, entries, clientOptions.lowThreshold);
                    RhymesClient.stopSW("Query "+clientOptions.queryOperation.toString());
                    if (mp == null) return;
                    phEntriesStructure.outputResult(mp, output, clientOptions,queryEntry, true);
                    break;
                case ONE_VS_SOME:
                    queryEntry= phEntriesStructure.getEntry(clientOptions.srcWord, true);
                    mp = phEntriesStructure.calcSimilaritiesTo(clientOptions.srcWord, clientOptions.words, 100000);
                    if (mp == null) return;
                    phEntriesStructure.outputResult(mp, output, clientOptions,queryEntry, true);
                    break;
                case ALL_VS_ALL: //EXPORT
                    phEntriesStructure.queryAllEntries(clientOptions, output);
                    break;
            }
            RhymesClient.prL1("...finished Query.\n");
        }catch (NoSuchElementException nsee){
            RhymesClient.prErr("Could not find Entry in DB: <" + clientOptions.srcWord + ">");
        }
        // ((DBSink)sink).checkResults();
        output.closeSink();
        RhymesClient.prL1("\n");
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
