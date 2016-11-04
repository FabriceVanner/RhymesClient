package os_specifics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

public interface OSSpecific
{
/*
    private static String OS = null;
    public static String getOsName()
    {
        if(OS == null) { OS = System.getProperty("os.name"); }
        return OS;
    }
    public static boolean isWindows()
    {
        return getOsName().startsWith("Windows");
    }
*/
     String getMatcherGroup(Matcher getMatcher, String groupTo);

      void setJarfilenameAndClientPath(StringBuilder clientFileName,StringBuilder clientsFolderPath);


      Scanner  getScanner(String dictTextFilePath) throws FileNotFoundException,IOException;



}