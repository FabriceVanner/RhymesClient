package client;

import output.Output;
import output.OutputToSysOut;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static client.ClientArgs.ClientMode.CONSOLE;
import static client.ClientArgs.ClientOperation.*;
import static client.ClientArgs.OutputOptions.*;
import static client.ClientArgs.QueryOperation.ONE_AGAINST_ALL;
import static client.ClientArgs.QueryOperation.ONE_AGAINST_SEVERAL_GIVEN;

/**
 * Created by Fab on 22.07.2015.
 */

//TODO: mit Commandline-Framework ersetzen

/**
 * Clients handmade arguments and options-Parser for the Commandline Interface.
 */
public class ClientArgs {
    public static String help = "" +
            "++ Help ++\n" +
            "####  NICHT MEHR AKTUELL  !!!! ###"+
            "CONSOLE usage: <source-word> [<options(space-seperated)>] [<list of words to match against(space-seperated)>]\n" +
            "SHELL usage: w <source-word> [<list of words to match against(space-seperated)>]\n" +
            "             o <CONSOLE-option>\t\t\t; type \"?l\" for all available SHELL-commands \n" +
            "++ Console-Options ++\n" +
            "--lowThreshold=<float>\t\t: low similarity threshold 0.0 - 1.0, (defaults to 0.9, -1.0 disables it)\n" +
            "--highThreshold=<float>\t\t: high similarity threshold 0.0 - 1.0, (defaults to 1.0)\n" +
            "--fromIndex=<int>\t\t: QUERY the database fromIndex Index <int> (defaults to 0)\n" +
            "--tillIndex=<int>\t\t: QUERY the database tillIndex Index <int> (defaults to -1 = the end )\n" +
            "--eachEntry=<int>\t\t: QUERY eachEntry <int> database Index (defaults to 1)\n" +
            "-h or -?     \t\t: this help\n" +
            "--printDetail\t\t: print equality-comparison-details\n" +
            "--printErrors\t\t: print errors in loading the database-entry-structure\n" +
            "--revIpaSearchNeighboursUpAndDown=<int>\t\t: search by reversed ipa-string. Returns <int> neighbour-entries up and <int> neighbour-entries below the index of the searched word \n" +
            "--printIPA   \t\t: just print out the according ipa-String(s)\n" +
            "--FILTER_EQU_ENDS\t: filter results out that are (backwards) identicall with the word beeing queried(works not with --REV_IPA_SEARCH) EX: Word to QUERY: <Haus>, <Bootshaus> would be filtered out\n" +
            "--fromTopTill=<int>\t: outputs the first <int> entries (defaults to 30, no Limit = -1)\n" +
            "--delimiterSeperated\t: just outputs comma-seperated words\n" +
            "--printPerformance\t: prints client-performance time measures \n" +
            "--parseXMLDUMPFile=<pathToFile>\t: parses the (german-wiktionary) XML-Dump File at given Path into a dictionary txt-file this program can use\n" +
            "--ipaDictFile=<pathToFile>\t: the file to use for the dictionary(default=<ClientFolder>//ipaDict.txt\n" +
            "\n Usage in Windows-Commandline: for correct displaying of unicode try activating codepage: chcp 65001\n";

    int wordsInd = 0;
    int optionInd = 0;
    public String[] words = new String[10];
    public String srcWord = "";
    public String[] options = new String[5];
    public int maxArgLength = words.length;
    public String ipaDictFilepath;

    /** QUERY OPTIONS*/
    public int fromIndex = 0;
    public int tillIndex = -1;
    public int eachEntry = 1;
    public float lowThreshold = 0.9f;


    /** INPUT OPTIONS*/

    /** The dict-file is expected by default to be in the same folder as the client's-jar-file.*/
    public String ipaDictFilenameDefault = "ipaDict.txt";



    /** OUTPUT / EXPORT OPERATION */
    public Output output = new OutputToSysOut();
    public Set<OutputOptions> outputOptions = new HashSet<>();

    /** OUTPUT OPTIONS*/
    public float highThreshold = 1.0f;
    public int fromTopTill = 30;
    public int revIpaSearchNeighboursUpAndDown = 10;
    public boolean filterEquEnds = false;

    /** whats that?*/
    public boolean filterEquPhSignMArrBase = false;

    /** EX: Query-Word: "Maus" results would be "Haus", "Bootshaus", "Mietshaus","Horrorhaus",... "Applaus", "Staus",
     * now all Rhymes ending with the same last characters (Haus, Bootshaus...), would be grouped togethe*/
    public boolean groupEquRhymes =false;
    /**number of chars which should be eqal at the end
     * choose wisely: 4 would already exclude words like "aus" but safes performance and 3 would group often too much... */
    public int minCharCountForGroupingOnEqualWordEnds= 4;

    public boolean filterPlurals = false;


    /** OUTPUT FORMAT-OPTIONS*/
    /**number of chars to be written in one line of the CONSOLE after that linebreak is needed, -1= no limit */
    public int consoleWidth=-1;
    public boolean printPerformance = false;
    public boolean delimiterSeperated = true;
    private String outputDelimiterForPrintOut = ", ";
    public String outputDelimiter = "\n";
    public String outputDelimiterForGrouped = ", ";
    public boolean printDetail = false;
    public boolean printOutInfosAboutChoosenOptions = false;
    public boolean printErrors = false;


    /** EXPORT OPTIONS*/
    public String exportToDBFilename = "rhymes.db";
    private String outputDelimiterForExportToDB = "\n";
    public int exportStartAtEntryIndex = 0;
    public int exportStopAtEntryIndex = 100;
    public boolean exportToSerHM = false;
    public boolean exportToDB = false;
    public String exportToSerHM_Filename ="wordIndexHM.ser";

    /** WORK MODES */
    public ClientOperation clientTask = QUERY;
    public boolean shellModeOn = false;
    public ClientMode clientMode = CONSOLE;
    public QueryOperation queryOperation = ONE_AGAINST_ALL;
    public int simpleRevIpaSearch = -1;
    public boolean printIPA = false;
    public String parseXMLDUMPFile = "";
    public boolean showHelp = false;


    public String toString() {
        String str = "";
        str = "lowThreshold = " + lowThreshold + ", " +
                "highThreshold = " + highThreshold + ", " +
                "printDetail = " + printDetail + ", " +
                "fromIndex = " + fromIndex + ", " +
                "tillIndex = " + tillIndex + ", " +
                "eachEntry = " + eachEntry + ", " +
                "fromTopTill = " + fromTopTill + ", \n" +

                "delimiterSeperated = " + delimiterSeperated + ", " +
                "printErrors = " + printErrors + ", \n" +
             //   "ipaDictFilepath = " + ipaDictFilepath + ", " +
                "filterEquEnds = " + filterEquEnds + ", " +
                "groupEquRhymes =" + groupEquRhymes +", "+
                "filterPlurals = "+ filterPlurals +"\n"
        +       "EnumArray: outputOptions[]"+  outputOptions.toString()  ;

        //       "String words[] = "+ Arrays.toString(words)+ ", "+
        //       "int wordsInd = "+wordsInd+", "+
        //       "String options[] = " +Arrays.toString(options)+", "+
        //       "int optionInd = " + optionInd;
        return str;
    }

    public ClientArgs(String[] args) throws IllegalArgumentException {

        constructDictfilePath(RhymesClient.getClientsFolderPath(), ipaDictFilenameDefault);

        evalCommandLineArgs(args);
    }

    public ClientArgs() {
        constructDictfilePath(RhymesClient.getClientsFolderPath(), ipaDictFilenameDefault);
    }

    public ClientArgs(String word, boolean filterEquEnds, int fromTopTill) throws IllegalArgumentException {
        constructDictfilePath(RhymesClient.getClientsFolderPath(), ipaDictFilenameDefault);
        setArgsForBasicQuery(word, filterEquEnds, fromTopTill);
    }

    public void setArgsForBasicQuery(String word, boolean filterEquWordEnd, int fromTopTill) {
        this.srcWord= word;
        this.filterEquEnds = filterEquWordEnd;
        this.fromTopTill = fromTopTill;
    }

    public String getInfoString() {
        String out = "";
        //out = "\n" + RhymesClient.getClientFileName() + "Rhymes on " + "<" + srcWord + ">";
        out = "Rhymes on " + "<" + srcWord + ">";
        out += "\tsimilarities (" + lowThreshold + " - " + highThreshold + "). Querying each " + eachEntry + ". entry from Index " + fromIndex + " tillIndex " + tillIndex + ". (-1 = end)  OPTIONS:   ";
        if (filterEquEnds) out += "\t--FILTER_EQU_ENDS";
        if (fromTopTill != -1) out += "\t--fromTopTill=" + fromTopTill;
        //out += queryResult;
        //if (!shellModeOn) System.out.println(out + "\n");
        return out;
    }

    /**
     * adds client-folderpath and dictfilename, if necessary adds a slash
     *
     * @param clientFolderPath
     * @param ipaDictTextfileName
     */
    private void constructDictfilePath(String clientFolderPath, String ipaDictTextfileName) {
        ipaDictFilepath = clientFolderPath;
        if (!(clientFolderPath.charAt(RhymesClient.getClientsFolderPath().length() - 1) == '/')) {
            ipaDictFilepath += "/";/** TODO: funktioniert nur auf win wegen slash*/
        }
        ipaDictFilepath += ipaDictTextfileName;
    }

    private boolean checkFile(String newPath) {
        File f = new File(newPath);
        if (!(f.exists() && !f.isDirectory())) {
            RhymesClient.prErr("Can't resolve (or read) filePath: < " + newPath+" >");
            return false;
        } else {
            return true;
        }
    }

    public void resetWordsAndOptions() {
        words = new String[10];
        wordsInd = 0;
        options = new String[10];
        optionInd = 0;
        srcWord ="";
        options = new String[5];
    }

    /**
     * splits up the command line args into an options and src-word array
     *
     * @param arg
     */
    public void splitArgsInTokensAndOptions(String arg) {
        arg = arg.toLowerCase();
        if ((arg.equals("-?")) || (arg.equals("-h"))) {
            this.showHelp = true;
            return;
        }

        if (arg.startsWith("--")) {
            this.options[optionInd] = arg;
            optionInd++;
            return;
        }
        if (srcWord == "") {
            srcWord = arg;
            return;
        }else {
            (this.words[wordsInd]) = arg;
            wordsInd++;
        }
    }

    /**
     * TODO: mit Commandline-Framework ersetzen
     *
     * @param option - the option as String
     */
    public void parseOptionToIVar(String option) {
        option = option.toLowerCase();


        try {
            if (option != null) {
                if (option.startsWith("lowThreshold".toLowerCase())) {
                    this.lowThreshold = Float.valueOf(option.split("=")[1]);
                }
                else if (option.startsWith("highThreshold".toLowerCase()))
                    this.highThreshold = Float.valueOf(option.split("=")[1]);
                else if (option.startsWith("printDetail".toLowerCase())) {
                    this.printDetail = !this.printDetail;
                } else if (option.startsWith("fromTopTill".toLowerCase()))
                    this.fromTopTill = Integer.valueOf(option.split("=")[1]);
                else if (option.startsWith("filterEquEnds".toLowerCase())) {
                    this.filterEquEnds = !this.filterEquEnds; //TODO: replace the usage of this boolean by the outputOption enum
                } else if (option.startsWith("groupEquRhymes".toLowerCase())) {
                    this.groupEquRhymes = !groupEquRhymes;
                } else if (option.startsWith("fromIndex".toLowerCase()))
                    this.fromIndex = Integer.valueOf(option.split("=")[1]);
                else if(option.startsWith("filterPlurals".toLowerCase())) {
                    this.filterPlurals = !filterPlurals;
                }else if (option.startsWith("tillIndex".toLowerCase()))
                    this.tillIndex = Integer.valueOf(option.split("=")[1]);
                else if (option.startsWith("eachEntry".toLowerCase()))
                    this.eachEntry = Integer.valueOf(option.split("=")[1]);
                else if (option.startsWith("printErrors".toLowerCase()))
                    this.printErrors = !this.printErrors;
                else if (option.startsWith("revIpaSearchNeighboursUpAndDown".toLowerCase()))
                    this.revIpaSearchNeighboursUpAndDown = Integer.valueOf(option.split("=")[1]);
                else if (option.startsWith("printIPA".toLowerCase())) this.printIPA = !this.printIPA;
                else if (option.startsWith("delimiterSeperated".toLowerCase()))
                    this.delimiterSeperated = !this.delimiterSeperated;
                else if (option.startsWith("printPerformance".toLowerCase()))
                    this.printPerformance = !this.printPerformance;
                else if (option.startsWith("parseXMLDUMPFile".toLowerCase())) {
                    String newPath = option.split("=")[1];
                    if (checkFile(newPath)) this.parseXMLDUMPFile = newPath;
                } else if (option.startsWith("ipaDictFile".toLowerCase())) {
                    /**TODO: wahrscheinlich kaputt... */
                    String newPath = option.split("=")[1];
                    if (checkFile(newPath)){
                        this.ipaDictFilepath = newPath;
                    }else{
                        RhymesClient.prErr("Falling back to default filepath...");
                    }
                } else {
                    RhymesClient.prErr(RhymesClient.getClientFileName() + "Can't resolve arg: " + option);
                    throw new IllegalArgumentException();
                }
            }
        } catch (ArrayIndexOutOfBoundsException aiOOBEx) {
            RhymesClient.prErr("You promised to deliver a value for " + option + " and now you don't. This can't be right. Maybe you need to quote the Path. I'll ignore this option.");
        }

    }


    private void evalCommandLineArgs(String[] args) throws IllegalArgumentException {
        clientMode = ClientMode.CONSOLE;
        if (args.length < 1 || args.length == 1 && args[0] == "") {
            clientMode = ClientMode.SHELL;
            /**TODO: wenn man den dict-file-path als argument mitgibt, wird schon nicht mehr in die shell gestartet...  */
        }

        if (args.length > this.maxArgLength) {

            //    RhymesClient.prErr("Only at least 1 and a max of " + this.maxArgLength + " argument words are allowed");
            //   throw new IllegalArgumentException();
            return;
        }


        for (int i = 0; i < args.length; i++) {
            splitArgsInTokensAndOptions(args[i]);
        }

        for (int i = 0; i < optionInd; i++) {
            parseOptionToIVar(options[i].substring(2));
        }

        temporaryConvertBooleansToEnums();

/* too much or too little arguments...
        if (parseXMLDUMPFile.equals("")) {
            if (wordsInd == 0) {
                //RhymesClient.prErr("Enter at least one word (or --parseXMLDUMPFile) to process");
                //throw new IllegalArgumentException();
            }
        } else {
            if (wordsInd != 0) {
                RhymesClient.prErr("Can't process Query and parse XMLDump in one command");
                throw new IllegalArgumentException();
            }
        }
        */


    }

    //TODO: replace the usage of these booleans by the enums completely:
    /**temporary Method to convert boolean to enum options */
    public void temporaryConvertBooleansToEnums(){
        if (printIPA) {
            clientTask = PRINT_IPA;
        } else if (simpleRevIpaSearch > -1) {
            clientTask = REV_IPA_SEARCH;
        } else if (!parseXMLDUMPFile.equals("")) {
            clientTask = PARSE_XMLDUMP;
        } else if (exportToDB) {
            clientTask = EXPORT;
            outputDelimiter = outputDelimiterForExportToDB;
        } else {
            clientTask = QUERY;
            // one against all test
            if (srcWord != "") {
                queryOperation = ONE_AGAINST_ALL;
            }
            else if(wordsInd >= 1) {
                queryOperation = ONE_AGAINST_SEVERAL_GIVEN;

            }
        }

        if(filterEquEnds){
            outputOptions.add(FILTER_EQU_ENDS);
        }
        if(groupEquRhymes){
            outputOptions.add(GROUP_EQU_RHYMES);
        }
        if(filterPlurals){
            outputOptions.add(FILTER_PLURALS);
        }

        if(!filterEquEnds){
            outputOptions.remove(FILTER_EQU_ENDS);
        }
        if(!groupEquRhymes){
            outputOptions.remove(GROUP_EQU_RHYMES);
        }
        if(!filterPlurals){
            outputOptions.remove(FILTER_PLURALS);
        }

    }


    public enum ClientOperation {QUERY, REV_IPA_SEARCH, PARSE_XMLDUMP, PRINT_IPA, EXPORT}

    public enum ClientMode {CONSOLE, SHELL}

    public enum QueryOperation {ONE_ON_ONE, ONE_AGAINST_SEVERAL_GIVEN, ONE_AGAINST_ALL}

    public enum OutputOptions {FILTER_EQU_ENDS, GROUP_EQU_RHYMES, PROCESS_OUTPUT_IN_BATCHES, FILTER_PLURALS};

}
