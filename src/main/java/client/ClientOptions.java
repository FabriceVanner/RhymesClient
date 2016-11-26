package client;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class ClientOptions {
    @Option(name = "-h", aliases = {"--help","-?"}, usage = "Affiche l'aide")
    public boolean help;

    @Option(name = "-v", aliases = {"--version"}, usage = "Affiche la version")
    public boolean version;

    /** QUERY OPTIONS*/
    @Option(name = "-fi", aliases = {"--fromIndex"},usage ="QUERY the database starting at Index" )
    public int fromIndex = 0;

    @Option(name = "-ti", aliases = {"--tillIndex"},usage ="QUERY the database till Index" )
    public int tillIndex = -1;

    @Option(name = "-ee", aliases = {"--eachEntry"},usage ="QUERY eachEntry database Index" )
    public int eachEntry = 1;

    @Option(name = "-lt", aliases = {"--lowThreshold"},usage ="low similarity threshold 0.0 - 1.0" )
    public float lowThreshold = 0.9f;

    /** OUTPUT OPTIONS*/
    @Option(name = "-ht", aliases = {"--highThreshold"},usage ="high similarity threshold 0.0 - 1.0" )
    public float highThreshold = 1.0f;

    @Option(name = "-ftt", aliases = {"--fromTopTill"},usage ="outputs the first <int> entries (no Limit = -1)" )
    public int fromTopTill = 30;

    @Option(name = "-risn", aliases = {"--revIpaSearchNeighboursUpAndDown"},usage ="search by reversed ipa-string. Returns <int> neighbour-entries up and <int> neighbour-entries below the index of the searched word" )
    public int revIpaSearchNeighboursUpAndDown = 10;

    @Option(name = "-fee", aliases = {"--filterEquEnds"},usage ="low similarity threshold 0.0 - 1.0" )
    public boolean filterEquEnds = false;


    public Set<OutputOption> outputOptions=new HashSet<>();

    @Option(name = "-oO",aliases ={"--outputOptions"})
    public void setOutputOptions(String enumStrings){
            for(String enumString : enumStrings.split(",")){
                try {
                    outputOptions.add(OutputOption.valueOf(enumString));
                }catch (IllegalArgumentException iaE){
                    System.err.println(enumString +" is not an OutputOption");
                }
            }
    }



    //@Option(name = "-e", aliases = "--elapse", usage = "elapses in", handler = MultiEnumOptionHandler.class)
    //private List<Integer> elapsesIn = new ArrayList<>();

    enum Coin { PENNY,NICKEL,DIME,QUARTER }

    @Option(name = "-coin")
    public Coin coin;


    // receives other command line parameters than options
    @Argument
    public List<String> arguments = new ArrayList<String>();

    public void eval(String[]args){
        CmdLineParser cmdLineParser = new CmdLineParser(this);
        try {
            cmdLineParser.parseArgument(args);
            if(help)cmdLineParser.printUsage(System.out);

            //if(arguments.isEmpty()&&)
            if(tillIndex==3)System.out.println("entered 3");

            System.out.println("OO = "+outputOptions.toString());

        } catch (CmdLineException e) {
            //cmdLineParser.setUsageWidth(80);
            e.getMessage();
            e.getCause().toString();
            e.getLocalizedMessage();
            System.out.println("Exception");
            //cmdLineParser.printUsage(System.out);
        }



    }
    public enum OutputOption {FILTER_EQU_ENDS, GROUP_EQU_RHYMES, PROCESS_OUTPUT_IN_BATCHES, FILTER_PLURALS};

}
