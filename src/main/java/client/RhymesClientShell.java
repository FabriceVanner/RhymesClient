package client;

import asg.cliche.Command;
import asg.cliche.Param;

import java.io.IOException;
import java.sql.SQLException;

import static asg.cliche.ShellFactory.createConsoleShell;

/**
 * Created by Fabrice Vanner on 03.02.2016.
 *
 * provides an interactive SHELL for the client, to prevent loading the database for every QUERY
 */
public class RhymesClientShell {
    //public static ClientOptions clientOptions;
    public static RhymesClient rC;
    public boolean validate;

    @Command(description="looks for rhymes to given word(s). Actually behaves like the command line interface. You might type options here too")
    public void word(@Param(name="word(s)", description="the word(s)")String arg){
        String args[] = arg.split(" ");
        rC.clientOptions.srcWord = args[0];
        rC.clientOptions.eval(args);
        if(rC.clientOptions.help)return;
        try {
            rC.runTask(rC.clientOptions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Command(description="processes the command as would the command line version")
    public void command(@Param(name="command", description="the word(s)")String arg){
        String args[] = arg.split(" ");
        /*TODO:
        clientOptions.resetWordsAndOptions();
        */
        rC.clientOptions.eval(args);
        if(rC.clientOptions.help)return;
        try {
            rC.runTask(rC.clientOptions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Command(description="sets one option accoardingly")
    public void option(@Param(name="option", description="the option") String arg){
        String args[] = arg.split(" ");
        if(!args[0].startsWith("-")) System.out.println("Not an Option: "+ args[0]);
        rC.clientOptions.eval(args);
    }
    @Command(description="Prints all the set options")
    public void optionsstate(){
     //   System.out.println(clientOptions.toString());
    }

    @Command(description="reinitialises rhyme-SHELL, reloads dict-file")
    public void init(){
        rC.init(rC.clientOptions);
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
