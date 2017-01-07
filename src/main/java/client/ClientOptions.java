package client;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static client.ClientOptions.ClientOperation.*;
import static client.ClientOptions.OutDelimiting.GROUP;
import static client.ClientOptions.OutFilterOption.EQU_ENDS;
import static client.ClientOptions.OutFilterOption.PLURALS;
import static client.ClientOptions.OutputSink.SQLLITE;
import static client.ClientOptions.QueryOperation.ALL_VS_ALL;
import static client.StringsAndStuff.*;
import static client.Utils.checkFile;

/**
 * http://www.programcreek.com/java-api-examples/index.php?api=org.kohsuke.args4j.Argument
 * <p>
 * Created by Fabrice Vanner on 26.11.2016.
 * eigene Annotations: http://args4j.kohsuke.org/apidocs/org/kohsuke/args4j/spi/Setter.html#asAnnotatedElement()
 * <p>
 * Validation:
 * https://github.com/jinahya/args4j-with-bean-validation-example/blob/master/src/main/java/com/github/jinahya/example/Opts.java
 *
 * @depends @forbids   Enum Switch
 * http://args4j.kohsuke.org/apidocs/org/kohsuke/args4j/Option.html
 * <p>
 * custom handler
 * https://grysz.com/2016/03/10/implement-custom-option-handler-in-args4j/
 * <p>
 * https://grysz.com/2016/03/03/multi-value-option-handler-in-args4j/
 */


/**
 * Clients arguments and options-holder for Commandline Interface.
 */
public class ClientOptions implements Cloneable {



    CmdLineParser cmdLineParser=null;

    // Overriding clone() method of Object class
    public Object clone()throws CloneNotSupportedException{
        return (ClientOptions)super.clone();
    }


    @Option(name = "-ci", aliases = {"--clientInterface"}, usage = CI_H)
    public void clientInterface(ClientInterface clientInterface) {
        this.clientInterface = clientInterface;
        if (this.clientInterface == ClientInterface.SHELL) argsContainCommand = false;
    }

    ClientInterface clientInterface = ClientInterface.CONSOLE;
    /**this is used to evaluate if shell or console mode shall be used */
    boolean argsContainCommand = true;

     /* HELP & VERBOSE Options...*/

    @Option(name = "-h", aliases = {"--printHelp", "-?"}, usage = "Affiche l'aide")
    public boolean printHelp;

    @Option(name = "-v", aliases = {"--version"}, usage = "Affiche la version")
    public boolean version;

    @Option(name = "-vl", aliases = {"--verboseLevel"}, usage = VL_H)
    public int verboseLevel = 2;

    @Option(name = "-pe", aliases = {"--printErrors"}, usage = PD_H)
    private void printErrors(boolean ignoreMe) {
        this.printErrors = !this.printErrors;
    }

    public boolean printErrors = false;

    public boolean printOutInfosAboutChoosenOptions = false;

    @Option(name = "-pp", aliases = {"--printPerformance"}, usage = PP_H)
    private void printPerformance(boolean ignoreMe) {
        this.printPerformance = !this.printPerformance;
    }

    public boolean printPerformance = false;


/* FILE LOCATIONS....*/

    /**
     * TODO: wahrscheinlich kaputt...
     */
    @Option(name = "-dfp", aliases = {"--dictFilePath"}, usage = DFP_H)
    public void ipaDictFilePath(String ipaDictFilePath) {
        if (checkFile(ipaDictFilePath)) {
            this.ipaDictfileFullQualifiedName = ipaDictFilePath;
        } else {
            RhymesClient.prErr("Falling back to default filepath...");
        }
    }

    /**
     * The dict-file is expected by default to be in the same folder as the client's-jar-file.
     */
    public String ipaDictfileFullQualifiedName = ipaDictFilenameDefault;
    public static String ipaDictFilenameDefault = "ipaDict.txt";
    //public final static String ipaDictFilenameDefault = "ipaDict-Test.txt";/**TODO: uncomment*/

    @Option(name = "-xmldfp", aliases = {"--xmlDumpFilePath"}, usage = XMLDFP_H)
    public void xmlDumpFilePath(String xmlDumpFilePath) throws CmdLineException {
        if (checkFile(xmlDumpFilePath)) this.xmlDumpFilePath = xmlDumpFilePath;
        else throw new CmdLineException("xml-filepath not valid");
    }

    public String xmlDumpFilePath = "";



 /*  MAIN OPERATIONS*/

    @Option(name = "-co", aliases = {"--clientOperation"}, usage = CO_H)
    ClientOperation clientOperation = QUERY;


    @Option(name = "-qo", aliases = {"--queryOperation"}, usage = QO_H)
    QueryOperation queryOperation = QueryOperation.ONE_VS_ALL;

    /** QUERY OPTIONS
     * these options affect which entries of the dictionary db will be processed
     * */
    @Option(name = "-fi", aliases = {"--fromIndex"}, usage = "QUERY the database starting at Index")
    public int fromIndex = 0;

    @Option(name = "-ti", aliases = {"--tillIndex"}, usage = "QUERY the database till Index")
    public int tillIndex = -1;

    @Option(name = "-ee", aliases = {"--eachEntry"}, usage = "QUERY <eachEntry> of the database Index.")
    public int eachEntry = 1;

    @Option(name = "-lt", aliases = {"--lowThreshold"}, usage = "low similarity threshold 0.0 - 1.0")
    public float lowThreshold = 0.9f;


    /** OUTPUT OPTIONS
     *  these Options only affect what of the results per entry will be outputted, they don't reduce the number of entries to be processed by the algorithm
     * */
    @Option(name = "-ht", aliases = {"--highThreshold"}, usage = "high similarity threshold 0.0 - 1.0")
    public float highThreshold = 1.0f;

    @Option(name = "-ftt", aliases = {"--fromTopTill"}, usage = "outputs the first <int> entries (no Limit = -1). If this value is too low, option --lowThresshold will have no effect on the output(but still on the calculation speed)")
    public int fromTopTill = 30;

    @Option(name = "-risn", aliases = {"--revIpaSearchNeighboursUpAndDown"}, usage = "search by reversed ipa-string. Returns <int> neighbour-entries up and <int> neighbour-entries below the index of the searched word")
    public int revIpaSearchNeighboursUpAndDown = 10;

    public boolean skipFirstEntry = true;
    public boolean skipFirstEntryOnALL_VS_ALL = true;


    /** FORMAT-OPTIONS*/
    public OutFormatType outFormatType = OutFormatType.STRING;

    /**number of chars to be written in one line of the CONSOLE after that linebreak is needed, -1= no limit */
    public int consoleWidth = -1;
    public String outputDelimiter = "\n";
    public String outputDelimiterForGrouped = ", ";

    @Option(name = "-f", aliases = {"--filter"}, usage = F_H)
    public void fillRemOutputOption(OutFilterOption oo) {
        fillRemEnumFromSet(oo, outFilterOptions);
    }

    @Option(name = "-mf", aliases = {"--multiFilter"}, usage = MF_H)
    public void parseOutputOptions(String enumStrings) {
        parseEnumStrToSet(enumStrings, outFilterOptions, OutFilterOption.class);
    }

    public Set<OutFilterOption> outFilterOptions = new HashSet<OutFilterOption>();

    /**number of chars which should be equal at the end
     * choose wisely: 4 would already exclude wordsArrLi like "aus" but safes performance and 3 would group often too much... */
    public int minCharCountForGroupingOnEqualWordEnds = 4;

    @Option(name = "-od", aliases = {"--outDelimiting"}, usage = OD_H)
    public OutDelimiting outDelimiting = OutDelimiting.DELIM;

    @Option(name = "-pd", aliases = {"--printDetail"}, usage = PD_H)
    private void printDetail(boolean ignoreMe) {
        this.printDetail = !this.printDetail;
    }

    public boolean printDetail = false;


    /** OUTPUT SINK OPTIONS*/
    @Option(name = "-os", aliases = {"--outputSink"}, usage = OS_H)
    OutputSink outputSink = OutputSink.SYSOUT;

    /** print output in Batches; ONE = print rhymes one by one; ALL = print all rhymes*/
    public OutBatch outBatch = OutBatch.ALL;

    public String exportToDBFilename = "rhymes.db";
    public String exportToSerHM_Filename = "wordIndexHM.ser";

    public int exportStartAtEntryIndex = 0;

    /** number of entries to be sinked, -1 = no limit*/
    public int queryOpp_ALL_VS_ALL_StopAtEntryIndex = -1;
    public boolean exportToSerHM = true;


    /** INPUT */
    // receives other command line parameters than options
    @Argument(multiValued = true, usage = ARGS_H)
    public ArrayList<String> wordsArrLi = new ArrayList<>();

    void setWordsArrLi(ArrayList<String> wordsArrLi) {
        this.wordsArrLi = wordsArrLi;
        words = (String[]) wordsArrLi.toArray();

    }

    //public ArrayList<String> wordsArrLi =null;
    public String srcWord = "";
    public String[] words;

    public <E extends Enum> void fillRemEnumFromSet(E e, Set s) {
        if (s.contains(e)) s.remove(e);
        else s.add(e);
    }


    /**the sign(s) to use to seperate concatinated ENUM options on the commandline*/
    private final String enumParserDelimiter = ",";

    /**
     * parses multiple delimiter seperated ENUM OPTIONs
     * @param enumsString
     * @param set
     * @param type
     * @param <E>
     */
    public <E extends Enum> void parseEnumStrToSet(String enumsString, Set set, Class<E> type) {
        for (String enumString : enumsString.split(enumParserDelimiter)) {
            try {
                set.add(E.valueOf(type, enumString));
            } catch (IllegalArgumentException iaE) {
                System.err.println(enumString + " is not an ENUM of type " + type);
            }
        }
    }

    /** META OPTIONS: */

    @Option(name = "-e", aliases = {"--Export"}, usage = E_H)
    private void export(boolean ignoreMe) {
        this.exportAll = !exportAll;
    }
    boolean exportAll = false;


    /**MetaOption: sets a couple of options accordingly*/
    public static boolean debug =false;

    void setDebugOptions(){
        verboseLevel=5;
        ipaDictFilenameDefault = "ipaDict-Test.txt";
        RhymesClient.prErr(" - DEBUG OPTIONS ACTIVATED - ");
    }



    /**
     * setting client to exportAll
     */
    private void setExportOptions()
    {
        outFilterOptions = new HashSet<>();
        outFilterOptions.add(EQU_ENDS);
        outFilterOptions.add(PLURALS);
        outDelimiting = GROUP;
        verboseLevel = 4;
        outputSink = SQLLITE;
        queryOperation = ALL_VS_ALL;
    }


    /*#############################################################################################################*/

    public String getOptionInfo() {
        return "";
    }

    private void doubleCheck() throws CmdLineException {

        if ((clientOperation == PARSE_XMLDUMP) && (xmlDumpFilePath.equals(""))) {
            throw new CmdLineException("No valid XMLDump-filepath");
        }
        if (clientOperation != PARSE_XMLDUMP && (!checkFile(ipaDictfileFullQualifiedName))) {
            throw new CmdLineException("No valid ipaDictfileFullQualifiedName");
        }
        if (queryOperation == ALL_VS_ALL) {
            if (wordsArrLi != null && wordsArrLi.size() != 0)
                throw new CmdLineException("No need to provide source words, if you gonna  do ALL_VS_ALL Query anyway");
        }
    }

    /**
     * evaluates if shell or console mode is needed
     */
    private void setClientInterfaceAndSetSourceWord() {
        if(exportAll){
            return;
        }
        // erkennen ob shell-mode gefordert ist
        if ((clientOperation == QUERY && queryOperation != ALL_VS_ALL) || clientOperation == REV_IPA_SEARCH || clientOperation == PRINT_IPA) {
            if ((wordsArrLi == null || wordsArrLi.size() == 0)) {
                clientInterface = ClientInterface.SHELL;
                argsContainCommand = false;
            } else {
                clientInterface = ClientInterface.CONSOLE;
                srcWord = wordsArrLi.get(0);
            }
        }
    }


    /**
     * adds client-folderpath and dictfilename, if necessary adds a slash
     *
     * @param clientFolderPath
     * @param ipaDictTextfileName
     */
    public void constructDictfilePath(String clientFolderPath, String ipaDictTextfileName) {
        ipaDictfileFullQualifiedName = clientFolderPath;
        if (!(clientFolderPath.charAt(RhymesClient.getClientsFolderPath().length() - 1) == '/')) {
            ipaDictfileFullQualifiedName += "/";/** TODO: funktioniert nur auf win wegen slash*/
        }
        ipaDictfileFullQualifiedName += ipaDictTextfileName;
    }




    public void reEvaluate(){

    }

    /**
     * setss up DictfilePath and checks file, checks for shell mode, starts args-parser
     * @param args from main
     * @throws CmdLineException
     */
    public void eval(String[] args) throws CmdLineException {
        if(debug) setDebugOptions();
        constructDictfilePath(RhymesClient.getClientsFolderPath(), ipaDictFilenameDefault);

        if (args.length < 1 || args[0].equals("")) {
            clientInterface = ClientInterface.SHELL;
            argsContainCommand = false;
            if (!checkFile(ipaDictfileFullQualifiedName)) RhymesClient.prErr("No ipaDictFile found");
            return;
        }
        if(cmdLineParser==null) {
            cmdLineParser = new CmdLineParser(this);
            cmdLineParser.setUsageWidth(140);
        }
        cmdLineParser.parseArgument(args);


        if (printHelp) {
            cmdLineParser.printUsage(System.out);
            argsContainCommand = false;
            System.out.println("\nOptions with multiple Suboptions should be seperated by <"+enumParserDelimiter+">. Example: -mf OPTION_1,OPTION_2");
            System.out.println("\nUsage in Windows-Commandline: for correct displaying of unicode try activating codepage: chcp 65001");
            return;
        }
        if(exportAll) setExportOptions();

        if(debug) setDebugOptions();/** unfortunately needed two times... */
        doubleCheck();
        setClientInterfaceAndSetSourceWord();

        /*
        List<Field> privateFields = new ArrayList<>();
        Field[] allFields = ClientOptions.class.getDeclaredFields();
        for (Field field : allFields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                privateFields.add(field);
            }
        }
*/
        //cmdLineParser.setUsageWidth(80);
        //RhymesClient.prErr( e.getMessage());
//            e.getCause().toString();
        //RhymesClient.prErr(e.getLocalizedMessage());
        //RhymesClient.prErr(e.toString());
        //cmdLineParser.printUsage(System.out);

    }

    public enum OutFormatType {STRING, STR_ARR}

    public enum OutFilterOption {EQU_ENDS, PLURALS}

    ;

    public enum OutDelimiting {GROUP, DELIM, FACTOR_LINE}

    public enum OutBatch {ONE, ALL, NR_OF_RHYMES}
    //PROCESS_OUTPUT_IN_BATCHES


    public enum OutputSink {SYSOUT, SQLLITE}

    public enum ClientOperation {QUERY, REV_IPA_SEARCH, PARSE_XMLDUMP, PRINT_IPA}

    public enum ClientInterface {CONSOLE, SHELL}

    public enum QueryOperation {ONE_VS_ONE, ONE_VS_SOME, ONE_VS_ALL, ALL_VS_ALL}


}
