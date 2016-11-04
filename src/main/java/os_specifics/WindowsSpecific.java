package os_specifics;

import client.RhymesClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Created by Fabrice Vanner on 29.08.2016.
 */
public class WindowsSpecific implements OSSpecific {
    public  String getMatcherGroup(Matcher getMatcher, String groupTo){
        //return getMatcher.group(groupTo); //unCOMMENT IF WINDOWS VERSION
        return"";
    }

    public  void setJarfilenameAndClientPath(StringBuilder clientFileName,StringBuilder clientsFolderPath) {
        try{
            String absClientFilePath = RhymesClient.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            String[]tmp=absClientFilePath.split("/");
            clientFileName.append( tmp[tmp.length-1] +":\t");
            clientsFolderPath.append(absClientFilePath.substring(1,absClientFilePath.length()- tmp[tmp.length-1].length()-1));
        }catch(Exception ex){}
    }

    /**

     */
    public  Scanner getScanner(String dictTextFilePath) throws FileNotFoundException{
        File f = new File(dictTextFilePath);
        if(!f.exists() || f.isDirectory() || !f.canRead()) {
            throw new FileNotFoundException("Can't resolve (or read) dictFilePath:\n "+dictTextFilePath+"\nThe dict-file is expected by default to be in the same folder as the client's-jar-file.\n");
        }
        Scanner scanner;

            scanner = new Scanner (new BufferedInputStream(new FileInputStream(f)));


        return scanner;
    }

}
