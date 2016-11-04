package os_specifics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;

/**
 * Created by Fabrice Vanner on 30.08.2016.
 */
public class OSSpecificProxy implements OSSpecific {
    OSSpecific specificClass = new WindowsSpecific();
    @Override
    public String getMatcherGroup(Matcher getMatcher, String groupTo) {
        return specificClass.getMatcherGroup(getMatcher,groupTo);
    }

    @Override
    public void setJarfilenameAndClientPath(StringBuilder clientFileName, StringBuilder clientsFolderPath) {
         specificClass.setJarfilenameAndClientPath(clientFileName,clientsFolderPath);

    }

    @Override
    public Scanner getScanner(String dictTextFilePath) throws FileNotFoundException,IOException {
        return specificClass.getScanner(dictTextFilePath);
    }
}
