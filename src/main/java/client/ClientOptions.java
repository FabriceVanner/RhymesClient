package client;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static client.ClientOptions.ClientOperation.*;
import static client.StringsAndStuff.*;
import static client.Utils.checkFile;

/**
 * http://www.programcreek.com/java-api-examples/index.php?api=org.kohsuke.args4j.Argument
 *
 * Created by Fabrice Vanner on 26.11.2016.
 * eigene Annotations: http://args4j.kohsuke.org/apidocs/org/kohsuke/args4j/spi/Setter.html#asAnnotatedElement()
 *
 * Validation:
 * https://github.com/jinahya/args4j-with-bean-validation-example/blob/master/src/main/java/com/github/jinahya/example/Opts.java
 *
 * @depends @forbids   Enum Switch
 * http://args4j.kohsuke.org/apidocs/org/kohsuke/args4j/Option.html
 *
 * custom handler
 * https://grysz.com/2016/03/10/implement-custom-option-handler-in-args4j/
 *
 * https://grysz.com/2016/03/03/multi-value-option-handler-in-args4j/
 */


/**
 * Clients arguments and options-holder for Commandline Interface.
 */
public class ClientOptions {


    /*  MAIN OPERATIONS*/
    @Option(name = "-ci", aliases = {"--clientInterface"},usage = CI_H)
    ClientInterface clientInterface = ClientInterface.CONSOLE;

    @Option(name = "-co", aliases = {"--clientOperation"},usage = CO_H )
    ClientOperation clientOperation = QUERY;

    /**TODO: wahrscheinlich kaputt... */
    @Option(name="-dfp", aliases = {"--dictFilePath"}, usage =DFP_H)
    public void ipaDictFilePath(String ipaDictFilePath){
        if (checkFile(ipaDictFilePath)){
            this.ipaDictFilepath = ipaDictFilePath;
        }else{
            RhymesClient.prErr("Falling back to default filepath...");
        }
    }
    /** The dict-file is expected by default to be in the same folder as the client's-jar-file.*/
    public String ipaDictFilepath = ipaDictFilenameDefault;
    public final static String ipaDictFilenameDefault = "ipaDict.txt";

    @Option(name="-xmldfp", aliases = {"--xmlDumpFilePath"}, usage =XMLDFP_H)
    public void xmlDumpFilePath(String xmlDumpFilePath)throws CmdLineException{
        if (checkFile(xmlDumpFilePath))this.xmlDumpFilePath = xmlDumpFilePath;
        else throw new CmdLineException("xml-filepath not valid");
    }
    public String xmlDumpFilePath = "";

    @Option(name = "-h", aliases = {"--help","-?"}, usage = "Affiche l'aide")
    public boolean help;

    @Option(name = "-v", aliases = {"--version"}, usage = "Affiche la version")
    public boolean version;


    @Option(name = "-qo", aliases = {"--queryOperation"},usage = QO_H )
    QueryOperation queryOperation = QueryOperation.ONE_VS_ALL;




    /** QUERY OPTIONS
     * these options affect which entries of the dictionary db will be processed
     * */
    @Option(name = "-fi", aliases = {"--fromIndex"},usage ="QUERY the database starting at Index" )
    public int fromIndex = 0;

    @Option(name = "-ti", aliases = {"--tillIndex"},usage ="QUERY the database till Index" )
    public int tillIndex = -1;

    @Option(name = "-ee", aliases = {"--eachEntry"},usage ="QUERY eachEntry database Index" )
    public int eachEntry = 1;

    @Option(name = "-lt", aliases = {"--lowThreshold"},usage ="low similarity threshold 0.0 - 1.0" )
    public float lowThreshold = 0.9f;




    /** OUTPUT OPTIONS
     *  these Options only affect what of the results per entry will be outputted, they don't reduce the number of entries to be processed by the algorithm
     * */
    @Option(name = "-ht", aliases = {"--highThreshold"},usage ="high similarity threshold 0.0 - 1.0" )
    public float highThreshold = 1.0f;

    @Option(name = "-ftt", aliases = {"--fromTopTill"},usage ="outputs the first <int> entries (no Limit = -1)" )
    public int fromTopTill = 30;

    @Option(name = "-risn", aliases = {"--revIpaSearchNeighboursUpAndDown"},usage ="search by reversed ipa-string. Returns <int> neighbour-entries up and <int> neighbour-entries below the index of the searched word" )
    public int revIpaSearchNeighboursUpAndDown = 10;




    /** FORMAT-OPTIONS*/
    public OutFormatType outFormatType = OutFormatType.STRING;

    /**number of chars to be written in one line of the CONSOLE after that linebreak is needed, -1= no limit */
    public int consoleWidth=-1;
    public String outputDelimiter = "\n";
    public String outputDelimiterForGrouped = ", ";

    @Option(name = "-f", aliases = {"--filter"},usage = F_H)
    public void fillRemOutputOption(OutFilterOption oo ){
        fillRemEnumFromSet(oo, outFilterOptions);
    }

    @Option(name = "-mf",aliases ={"--multiFormat"},usage = F_H)
    public void parseOutputOptions(String enumStrings){
        parseEnumStrToSet(enumStrings, outFilterOptions,OutFilterOption.class);
    }
    public Set<OutFilterOption> outFilterOptions =new HashSet<OutFilterOption>();

    /**number of chars which should be eqal at the end
     * choose wisely: 4 would already exclude wordsArrLi like "aus" but safes performance and 3 would group often too much... */
    public int minCharCountForGroupingOnEqualWordEnds= 4;

    @Option(name = "-od",aliases = {"--outDelimiting"},usage = OD_H)
    public OutDelimiting outDelimiting = OutDelimiting.DELIM;

    @Option(name = "-pd", aliases = {"--printDetail"}, usage = PD_H)
    private void printDetail(boolean printDetail){
        this.printDetail = !printDetail;
    }
    public boolean printDetail = false;

    @Option(name = "-pe", aliases = {"--printErrors"}, usage = PD_H)
    private void printErrors(boolean printErrors){
        this.printErrors = !printErrors;
    }
    public boolean printErrors = false;

    public boolean printOutInfosAboutChoosenOptions = false;
    public boolean printPerformance = false;



    /** OUTPUT OPTIONS*/
    @Option(name = "-os", aliases = {"--outputSink"},usage = OS_H )
    OutputSink outputSink = OutputSink.SYSOUT;
    OutBatch outBatch=OutBatch.RHYMES;

    public String exportToDBFilename = "rhymes.db";
    public String exportToSerHM_Filename ="wordIndexHM.ser";

    private String outputDelimiterForExportToDB = "\n";
    public int exportStartAtEntryIndex = 0;
    public int exportStopAtEntryIndex = 100;
    public boolean exportToSerHM = true;






    /** INPUT */
    // receives other command line parameters than options
    @Argument(multiValued = true,usage =ARGS_H )
    public ArrayList<String> wordsArrLi = new ArrayList<>();
    void setWordsArrLi(ArrayList<String> wordsArrLi){
        this.wordsArrLi = wordsArrLi;
       words= (String[])wordsArrLi.toArray();

    }
    //public ArrayList<String> wordsArrLi =null;
    public String srcWord = "";
    public String[]words;

    public <E extends Enum>void fillRemEnumFromSet(E e,Set s){
        if(s.contains(e))s.remove(e);
        else s.add(e);
    }


    public <E extends Enum>void parseEnumStrToSet(String enumsString,Set set,Class<E> type){
        for(String enumString : enumsString.split(",")){
            try {
               set.add(E.valueOf(type,enumString));
            }catch (IllegalArgumentException iaE){
                System.err.println(enumString +" is not an ENUM of type "+ type);
            }
        }
    }

    public String getOptionInfo(){
        return "";
    }

    private void doubleCheck()throws CmdLineException{

        if((clientOperation == PARSE_XMLDUMP)&&(xmlDumpFilePath.equals(""))){
            throw new CmdLineException("No valid XMLDump-filepath");
        }
        if(clientOperation!=PARSE_XMLDUMP&& (!checkFile(ipaDictFilepath))){
            throw new CmdLineException("No valid ipaDictFilepath");
        }
        if(queryOperation==QueryOperation.ALL_VS_ALL){
            if(wordsArrLi !=null&& wordsArrLi.size()!=0)throw new CmdLineException("Don't provide wordsArrLi, if you gonna check do all against all Query anyway");
        }
    }

    private void setClientInterface(){
        // erkennen ob shell-mode gefordert ist
        if((clientOperation==QUERY&& queryOperation!=QueryOperation.ALL_VS_ALL)||
                clientOperation==REV_IPA_SEARCH||clientOperation==PRINT_IPA){
            if(wordsArrLi ==null|| wordsArrLi.size()==0){
                clientInterface=ClientInterface.SHELL;
            }else{
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
    private void constructDictfilePath(String clientFolderPath, String ipaDictTextfileName) {
        ipaDictFilepath = clientFolderPath;
        if (!(clientFolderPath.charAt(RhymesClient.getClientsFolderPath().length() - 1) == '/')) {
            ipaDictFilepath += "/";/** TODO: funktioniert nur auf win wegen slash*/
        }
        ipaDictFilepath += ipaDictTextfileName;
    }


    public void eval(String[]args){
        constructDictfilePath(RhymesClient.getClientsFolderPath(),ipaDictFilenameDefault );
        if (args.length < 1 || args.length == 1 && args[0] == "") {
            clientInterface = ClientInterface.SHELL;
            if(!checkFile(ipaDictFilepath))System.err.println("No ipaDictFile found");
            return;
        }

        CmdLineParser cmdLineParser = new CmdLineParser(this);
        //cmdLineParser.setUsageWidth(80);
        try {
            cmdLineParser.parseArgument(args);
            if(help){
                cmdLineParser.printUsage(System.out);
                System.out.println("Usage in Windows-Commandline: for correct displaying of unicode try activating codepage: chcp 65001");
                return;
            }

            doubleCheck();
            setClientInterface();



        } catch (CmdLineException e) {
            //cmdLineParser.setUsageWidth(80);
            e.getMessage();
//            e.getCause().toString();
            e.getLocalizedMessage();
            System.err.println("Exception");
            //cmdLineParser.printUsage(System.out);
        }



    }
    public enum OutFormatType {STRING, STR_ARR}
    public enum OutFilterOption {EQU_ENDS, PLURALS};
    public enum OutDelimiting {GROUP, DELIM, LINE}
    public enum OutBatch{RHYME,RHYMES,NROFRHYMES}
    //PROCESS_OUTPUT_IN_BATCHES


    public enum OutputSink{SYSOUT,SQLLITE}
    public enum ClientOperation {QUERY, REV_IPA_SEARCH, PARSE_XMLDUMP, PRINT_IPA}
    public enum ClientInterface {CONSOLE, SHELL}
    public enum QueryOperation {ONE_VS_ONE, ONE_VS_SOME, ONE_VS_ALL, ALL_VS_ALL}



}
