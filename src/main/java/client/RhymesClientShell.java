package client;

import asg.cliche.Command;
import asg.cliche.Param;

import java.io.IOException;

import static asg.cliche.ShellFactory.createConsoleShell;

/**
 * Created by Fabrice Vanner on 03.02.2016.
 *
 * provides an interactive SHELL for the client, to prevent loading the database for every QUERY
 */
public class RhymesClientShell {
    public static ClientArgs clientArgs;
    public static RhymesClient rC;
    public boolean validate;

    @Command(description="looks for rhymes to given word(s). Actually behaves like the command line interface. You might type options here too")
    public void word(@Param(name="word(s)", description="the word(s)")String arg){
        String args[] = arg.split(" ");
        clientArgs.resetWordsAndOptions();
        for (int i = 0; i < args.length; i++) {
            clientArgs.splitArgsInTokensAndOptions(args[i]);
        }
        rC.runTask(clientArgs);
    }
    @Command(description="sets one option accoardingly")
    public void option(@Param(name="option", description="the option") String arg){
        clientArgs.options = new String[10];
        clientArgs.optionInd = 0;
        try {
            clientArgs.parseOptionToIVar(arg);
            clientArgs.temporaryConvertBooleansToEnums();
        }catch (java.lang.IllegalArgumentException ex){

        }
    }
    @Command(description="Prints all the set options")
    public void optionsstate(){
        System.out.println(clientArgs.toString());
    }

    @Command(description="reinitialises rhyme-SHELL, reloads dict-file")
    public void init(){
        rC.init(clientArgs);
    }


    public static void main() {
        try {
            createConsoleShell("RhymesClient", "++ RhymesClient(type \"h\" for help) ++", new RhymesClientShell()).commandLoop();
        } catch (IOException e) {
        }
    }

    @Command(description="prints help")
    public void help(){
        System.out.println(StringsAndStuff.help);
    }

    @Command(description="validates every OptionChange by a Console Message")
    public void validate(){
        this.validate = !this.validate;
        /** TODO*/
    }


    @Command(description="print Number of Loaded Entries in Database")
    public void count(){
        System.out.println(rC.phEntriesStructure.getEntriesRevListSize());
    }



}
