package client;

/**
 * Created by Fabrice Vanner on 04.11.2016.
 */
public class StringsAndStuff {
    public static String help = "" +
            "++ Help ++\n" +
            "####  NICHT MEHR AKTUELL  !!!! ###"+
            "CONSOLE usage: <source-word> [<options(space-seperated)>] [<list of words to match against(space-seperated)>]\n" +
            "SHELL usage: w <source-word> [<list of words to match against(space-seperated)>]\n" +
            "             o <CONSOLE-option>\t\t\t; type \"?l\" for all available SHELL-commands \n" +
            "++ Console-Options ++\n" +
            "--printDetail\t\t: print equality-comparison-details\n" +
            "--printErrors\t\t: print errors in loading the database-entry-structure\n" +
            "--revIpaSearchNeighboursUpAndDown=<int>\t\t: search by reversed ipa-string. Returns <int> neighbour-entries up and <int> neighbour-entries below the index of the searched word \n" +
            "--printIPA   \t\t: just print out the according ipa-String(s)\n" +
            "--FILTER_EQU_ENDS\t: filter results out that are (backwards) identicall with the word beeing queried(works not with --REV_IPA_SEARCH) EX: Word to QUERY: <Haus>, <Bootshaus> would be filtered out\n" +
            "--delimiterSeperated\t: just outputs comma-seperated words\n" +
            "--printPerformance\t: prints client-performance time measures \n" +
            "--parseXMLDUMPFile=<pathToFile>\t: parses the (german-wiktionary) XML-Dump File at given Path into a dictionary txt-file this program can use\n" +
            "--ipaDictFile=<pathToFile>\t: the file to use for the dictionary(default=<ClientFolder>//ipaDict.txt\n" +
            "\n Usage in Windows-Commandline: for correct displaying of unicode try activating codepage: chcp 65001\n";
}
