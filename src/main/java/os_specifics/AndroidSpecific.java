package os_specifics;

import os_specifics.OSSpecific;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Created by Fabrice Vanner on 29.08.2016.
 */
public class AndroidSpecific implements OSSpecific {
   // Context c;
    public  String getMatcherGroup(Matcher getMatcher, String groupTo){
        //stub
        return "";
    }
    public  void setJarfilenameAndClientPath(StringBuilder clientFileName,StringBuilder clientsFolderPath){
        clientsFolderPath.append("");
        }

    @Override
    public Scanner getScanner(String dictTextFilePath) throws FileNotFoundException,IOException{
        Scanner scanner;
        InputStream ims;

          /*  AssetManager assetManager = c.getAssets();
          ims = assetManager.open("ipaDict.txt");
          scanner = new Scanner (new BufferedInputStream(ims));
*/

        return null; //scanner;
    }
}
