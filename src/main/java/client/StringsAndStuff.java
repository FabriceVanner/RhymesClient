package client;
/**
 * Created by Fabrice Vanner on 04.11.2016.
 */
public class StringsAndStuff {
    public static String help = "" +
            "SHELL usage: w <source-word> [<list of wordsArrLi to match against(space-seperated)>]\n" +
            "             o <CONSOLE-option>\t\t\t; type \"?l\" for all available SHELL-commands \n" +
            "\n Usage in Windows-Commandline: for correct displaying of unicode try activating codepage: chcp 65001\n";

    public static final String F_H ="EQU_ENDS\tfilter results out that are (backwards) identicall with the word beeing queried(works not with --REV_IPA_SEARCH) EX: Word to QUERY: <Haus>, <Bootshaus> would be filtered out\n"+
                                    "GROUP_EQU_RHYMES\tnumber of chars which should be eqal at the end\n" +
                                    "PROCESS_OUTPUT_IN_BATCHES\t"         +
                                    "PLURALS\t";
    public static final String CO_H="";//ClientOperation.values().toString();
    public static final String OS_H=""+// OutputSink.values().toString();
                                       "parses the (german-wiktionary) XML-Dump File at given Path into a dictionary txt-file this program can use\n"+
                                        " just print out the according ipa-String(s)\n";
    public static final String CI_H ="";
    public static final String QO_H="";

    public static final String PD_H =" print equality-comparison-details";
    public static final String DFP_H = "full qualified filepath or the dictionary(default=<ClientFolder>//ipaDict.txt";
    public static final String XMLDFP_H = "full qualified filepath to wiktionary XML-Dump";
    public static final String ARGS_H = " <source-word> [<options(space-seperated)>] [<list of wordsArrLi to match against(space-seperated)>]";
    public static final String OD_H = "if rhymes shall be outputted each on one line with rhyme-factor or comma-seperated";
    public static final String VL_H = "set Information-Level, -1 = silent, 0 = errors, 1 = basic output , 2 = status, 3 = advanced status, 4 = debug";

    /*
    public String getInfoString() {
        String out = "";
        out = "Rhymes on " + "<" + srcWord + ">";
        out += "\tsimilarities (" + lowThreshold + " - " + highThreshold + "). Querying each " + eachEntry + ". entry from Index " + fromIndex + " tillIndex " + tillIndex + ". (-1 = end)  OPTIONS:   ";
        +"\t--fromTopTill=" + fromTopTill
        + ClientOperation.values().toString();
        + outputSink.toString();
        + clientOperation.toString();
        + queryOperation.toString();
        //out += queryResult;
        //if (!shellModeOn) System.out.println(out + "\n");
        return out;
    }
*/


}
