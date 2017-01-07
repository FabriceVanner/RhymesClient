package client;

import com.google.common.collect.Multimap;
import operational_Entities.WordPair;
import org.kohsuke.args4j.CmdLineException;
import os_specifics.OSSpecificProxy;
import output.*;
import phonetic_entities.PhEntry;
import wiktionaryParser.XMLDumpParser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static client.ClientOptions.ClientInterface.CONSOLE;
import static client.ClientOptions.ipaDictFilenameDefault;

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
    public static RhymesClient rC;// = new RhymesClient();
    public static long lastTime=0;
    //public static Stack<StopWatch> timeStack = new Stack<>();
    public static HashMap<String,StopWatch> stopWatchMap= new HashMap<>();


    public static void main(String[] args) {
        try {
            rC = new RhymesClient(args);
        }catch (CmdLineException cmdE){
            System.out.println(cmdE.getMessage());
            return;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
              prErr(e.getMessage() + " Aborting.");
              return;
        } catch (SQLException e) {
            prErr(e.getMessage() + " Aborting.");
            return;
        }

        if(clientOptions.printHelp)return;
        //mainNewInConstruction(args);

        rC.setSinkAndFormat(clientOptions);
        if (clientOptions.clientInterface == CONSOLE) {
            try {
                rC.runOperation(clientOptions);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return;
        }


        RhymesClient.prL3("No arguments or commands provided. \n");
        try {
            mainShellLoop(args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
    }
    private void init(ClientOptions clientOptions) throws IOException, ClassNotFoundException {
        startSW("new PhEntriesStructure");
        this.phEntriesStructure = new PhEntriesStructure(clientOptions.ipaDictfileFullQualifiedName);
        stopSW("new PhEntriesStructure");
        setSinkAndFormat(clientOptions);
    }


    /**
     *    * initialises the client by loading into memory(dict file etc...)
     * @param args
     * @throws CmdLineException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public RhymesClient(String[]args) throws CmdLineException, IOException, ClassNotFoundException, SQLException {
        setJarFilenameAndClientPath();
        this.clientOptions = new ClientOptions();
        startSW("Eval ClientOptions");
        clientOptions.eval(args);
        stopSW("Eval ClientOptions");
        if(clientOptions.printHelp)return;
        init(clientOptions);
    }

    /**
     * does not evaluate / parse given options
     * initialises the client by loading into memory(dict file etc...)
     * @param clientOptions
     * @throws Exception
     */
    public RhymesClient(ClientOptions clientOptions) throws IOException, ClassNotFoundException, SQLException {
        setJarFilenameAndClientPath();
        this.clientOptions = clientOptions;
        this.clientOptions.constructDictfilePath(getClientsFolderPath(),ipaDictFilenameDefault );
        init(this.clientOptions);
    }

    public static void setClientOptions(ClientOptions clientOptions) {
        RhymesClient.clientOptions = clientOptions;
    }

    /**
     * this method provides the shell-loop to enter commands inside
      * @param args
     */
    public static void mainShellLoop(String[] args) throws ClassNotFoundException {
        Scanner command = new Scanner(System.in);
        clientOptions.argsContainCommand=true;
        prL1("++ RhymesClient Shell ++\n(\"exit\" for exit, \"-reinit\" for reinit \"-h\" for printHelp)\n\n");
        while(true){
            System.out.print("RhymesClient: ");
            String inputStr = command.nextLine();
            if(inputStr.equals("-reinit")){
                try {
                    rC.init(clientOptions);
                } catch (IOException e) {
                    prErr(e.getMessage());
                }
            }
            if(inputStr.equals("exit")) {
                prL2("Ending");
                break;
            }
            args = inputStr.split(" ");
            try {


                clientOptions.eval(args);

      /**TODO:
        - zuerst:
        - alle felder (aus ClientOptions)mit ihrenDefault  Values in eine Map kopieren: Key = feld-name, Value = feld Wert

        danach bei jedem erneuten Parse-Vorgang:
      (     cmdLineParser.parseArgument(args);)
        - alle felder in eine Map kopieren: Key = feld-name, Value = feld Wert
        -  alle keys/vals der Maps miteinander vergleichen, nur die unterschiedlichen behalten
        - ...(?)
         */


            } catch (CmdLineException e) {
                System.out.print("\n");
                prErr(e.getMessage());
                clientOptions.argsContainCommand=true;
                continue;
            }
            if(!clientOptions.argsContainCommand) {
                clientOptions.argsContainCommand = true;
                continue;
            }
                try {
                    rC.runOperation();
                } catch (SQLException e) {
                    prErr(e.getMessage());
                }
        }
        command.close();
    }


    /**
     * sets up the clients folderpath and its jarfilename
     */
    public static void setJarFilenameAndClientPath() {
        StringBuilder clientFileName = new StringBuilder();
        StringBuilder clientsFolderPath = new StringBuilder();
        (new OSSpecificProxy()).setJarfilenameAndClientPath(clientFileName, clientsFolderPath);
        RhymesClient.clientFileName = clientFileName.toString();
        RhymesClient.clientsFolderPath = clientsFolderPath.toString();

    }

    /**
     *  @param message
     *
     */
    public static void prErr(String message) {
        if(clientOptions.verboseLevel >-1) System.err.println(getClientFileName() + message);
    }

    /**
     * regular query output
     * @param message
     */
    public static void pr(String message) {
        if(clientOptions.verboseLevel >0){
            System.out.print(message);
        }
    }
    /**
     * regular output;
     * @param message
     */
    public static void prln(String message) {
        if(clientOptions.verboseLevel >0){
            System.out.println(message);
        }
    }


    /**
     * 1.LevelMeta
     * Messages describing
     * @param message
     */
    public static void prL1(String message) {
        if(clientOptions.verboseLevel >1){
            System.out.print(message);
        }
    }



    /**
     * 2.LevelMeta
     * @param message
     */
    public static void prL2(String message) {
        if(clientOptions.verboseLevel >2){
            System.out.print(message);
        }
    }

    /**
     * 3.LevelMeta
     * @param message
     */
    public static void prL3(String message) {
        if(clientOptions.verboseLevel >3){
            System.out.print(message);
        }
    }

    /**
     * debug
     * @param message
     */
    public static void prDebug(String message) {
        if(clientOptions.verboseLevel >4){
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

    /**
     * starts a new Stop watch running
     * @param name of the stop-watch, needs to be identical with name provided to stopSW(...)
     */
    public static void startSW(String name) {
        if (clientOptions.printPerformance) {
            stopWatchMap.put(name, new StopWatch());
            //timeStack.push(new StopWatch("Starting Opt.-Eval"));
        }
    }

    /**
     *  counter part of startSW, used to stop stopWatch and print results
     * @param name
     */
    public static void stopSW(String name){
        if (clientOptions.printPerformance) {
            System.out.println("#### StopWatch: " + name + " took " + stopWatchMap.get(name).getElapsedTimeStr()+"  ####");
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
     * runs a task with already set clientOptions
     * @throws SQLException
     */
    public void runOperation()throws SQLException{
        runOperation(this.clientOptions);
    }


    public void setSinkAndFormat(ClientOptions clientOpts){
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
    }



    /**
     * runs the task indicated by the Clientargs
     *
     * @param clientOpts
     */
    public void runOperation(ClientOptions clientOpts) throws SQLException {
        RhymesClient.prL1("\nClient Operation = "+ clientOpts.clientOperation+"\t" + "<"+clientOpts.srcWord+">\t");
        switch (clientOpts.clientOperation) {
            case PRINT_IPA:
                printIPA(clientOpts.words);
                break;
            case PARSE_XMLDUMP:
                // parses the phonetics of eachEntry word-article of the wiktionary xml-dumpfile and stores the result as textfile with default filename at clients path
                prTime("Starting to parse Dump Input File " + clientOpts.xmlDumpFilePath + " to Dict OutputFile" + "clientOptions.ipaDictfileFullQualifiedName");
                XMLDumpParser xmlDumpParser = new XMLDumpParser();
                xmlDumpParser.parseXMLDump(clientOpts.xmlDumpFilePath, clientOpts.ipaDictfileFullQualifiedName);
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
                prL3("Nr of stopped Entry-Calculations because of low Thresshold: " + phEntriesStructure.stoppedCalculatingEntriesCount+"\n");
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
                    phEntriesStructure.outputResult(mp, output, clientOptions,queryEntry, clientOptions.skipFirstEntry);
                    break;
                case ONE_VS_ONE:
                    /*TODO*/
                    break;
                case ALL_VS_ALL: //EXPORT
                    phEntriesStructure.queryAllEntries(clientOptions, output);
                    break;
                case ONE_VS_SOME:
                    queryEntry= phEntriesStructure.getEntry(clientOptions.srcWord, true);
                    mp = phEntriesStructure.calcSimilaritiesTo(clientOptions.srcWord, clientOptions.words, 100000);
                    if (mp == null) return;
                    phEntriesStructure.outputResult(mp, output, clientOptions,queryEntry, clientOptions.skipFirstEntry);
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
     * usually used for tests
     * @param wordPair
     */
    public float runOneOnOneQuery(WordPair wordPair)throws NoSuchElementException{
       PhEntry firstEntry =  phEntriesStructure.getEntry(wordPair.wordOne,true);
       return firstEntry.calcSimilarity(phEntriesStructure.getEntry(wordPair.wordTwo,true));
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
