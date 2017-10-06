package client;

import os_specifics.OSSpecificProxy;
import phonetic_entities.PhEntry;
import phonetic_entities.PhSignM;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static client.CharConstants.getToStringEndPadding;

/**
 * Created by Fab on 31.05.2015.
 */
public class Utils {

    private static String uniCharMeasureLine;

    public static boolean charIsOneOf(char ch, char[] those) {
        for (char diaChar : those) {
            if (ch == diaChar) return true;
        }
        return false;
    }

    /**
     * truncates a string to desired Length
     * @param str
     * @param supposedLength
     * @param backwards
     * @return
     */
    public static String truncStrTo(String str, int supposedLength, boolean backwards) {
        if (str != null) {
            int tmp = str.length() - supposedLength;
            if (tmp > 0) {
                if (backwards) {
                    return str.substring(tmp);
                } else {
                    return str.substring(0, supposedLength);
                }
            }
        }
        return str;
    }


    /**
     * builds a String /  prints chars of a given string seperated with a tab between EACH one (DIACRITICS as well)
     * @param str
     */
    public static String getUniCharsSeperate(String str, boolean addInfoLine, boolean print) {
        char geschLeerzeichen = '\u00A0';
        String out = "";
        if (addInfoLine)             out += "\tstr = <" + str + ">\tLength = " + str.length() + "\n";
        for (int i = 0; i < str.length(); i++) {out += " " + str.charAt(i) + getToStringEndPadding();        }
        out += "\n";
        if (print)             System.out.print(out + "\n");
        return out;
    }

    /**
     * creates a measure-line meant to be printed in sysout to measure the length of a string for discovering or comparing hidden unicode-signs/ diacritics
     * @return
     */
    public static String getUniCharsMeasureLine() {
        if (uniCharMeasureLine == null) {
            String measure = "";
            for (int i = 0; i < 20; i++) {
                measure += String.format("%02d", i) + getToStringEndPadding();
            }
            uniCharMeasureLine = measure + "\n";
        }
        return uniCharMeasureLine;
    }

    /**
     * reads the text-file with dictionary Data in output
     * @param dictTextFilePath
     * @param delimiter between word and phonetic-representation on each text-file line
     * @return suitable PhEntries for the PhEntriesStructure
     * @throws FileNotFoundException
     */
    public static List<PhEntry> readDictTextFile(String dictTextFilePath, String delimiter) throws FileNotFoundException,IOException{
        List<PhEntry> entries = new ArrayList<PhEntry>();
        Scanner scanner = (new OSSpecificProxy()).getScanner( dictTextFilePath);
        RhymesClient.prL1("Loading dict-File");
        scannerToEntries(delimiter, entries, scanner);
        RhymesClient.prL1("loaded dict-file.\n ");
        return entries;
    }

    private static void scannerToEntries(String delimiter, List<PhEntry> entries, Scanner scanner) {
        String word = "";
        StringBuilder ipa = new StringBuilder();
        int i=0;
        while (scanner.hasNextLine()) {
            Scanner scanner2 = new Scanner(scanner.nextLine());
            scanner2.useDelimiter(delimiter);

            if (!scanner2.hasNext()) {
                continue;
            }
            word = scanner2.next();
            if (!scanner2.hasNext()) {
                continue;
            }
            ipa.append(scanner2.next());
            entries.add(new PhEntry(word, ipa.toString()));
            ipa.setLength(0);
            if(i%10000.0==0) System.out.print(".");

            i++;
        }
    }

    /**
     * collects the dictionary-entries from Index the database
     * @param st
     * @param printProgress
     * @return
     * @throws SQLException
     */
    public static List<PhEntry> getRowsFromDB(Statement st, boolean printProgress) throws SQLException {
        ArrayList<PhEntry> ldbEntries = new ArrayList<>();
        ResultSet rs = st.executeQuery("Select * from Index woerter ORDER BY wort");
        if (printProgress) {
            int i = 0;
            while (rs.next()) {
                String wort = rs.getString("wort");
                if (i % 1000 == 0) System.out.print(i + ", ");
                ldbEntries.add(new PhEntry(rs.getLong("id"), wort, rs.getString("ipa"), rs.getString("reim"), rs.getString("worttrennung")));
                i++;
            }
            System.out.println();
        } else {
            while (rs.next()) {
                ldbEntries.add(new PhEntry(rs.getLong("id"), rs.getString("wort"), rs.getString("ipa"), rs.getString("reim"), rs.getString("worttrennung")));
            }
        }
        rs.close();
        return ldbEntries;
    }

    public static void closeConnStatement(Statement st) {
        try {
            if (!st.isClosed()) st.close();
        } catch (SQLException ex) {
        }
        ;
    }

    static long getDBRowCount(Statement st) throws SQLException {
        ResultSet rs = st.executeQuery("Select Count(*) fromIndex woerter");
        rs.next();
        long dBRowCount = rs.getLong("count");
        rs.close();
        return dBRowCount;
    }

    /**
     * creates a new List ArrayList containing a reduced set of original list
     * @param entries
     * @param from index
     * @param till index
     * @param each first, second... entry to be copied to sublist
     * @return
     */
    public static List<PhEntry> getSubList(List<PhEntry> entries, int from, int till, int each) {
        ArrayList<PhEntry> sList = new ArrayList<>();
        if (from < 0) from = 0;
        if (till >= entries.size()) till = entries.size() - 1;
        else if (till == -1) till = entries.size();
        for (int i = from; i < till; i++) {
            if ((i - from) % each == 0) {
                sList.add(entries.get(i));
            }
        }
        return sList;
    }


    /**
     * builds a String /  prints chars of a given string seperated with a tab between EACH one (DIACRITICS as well)
     */
    private String getUniCharsSeperate(ArrayList<PhSignM> phSEPArr,boolean printOther, boolean addInfoLine, boolean printSysOut, boolean addTabsBetwSigns,boolean addTabsBetwSignAndModifiers) {
        String out = "";
        if (addInfoLine)             out += "\t ArraySize = " + phSEPArr.size() + "\n";
        for (PhSignM phSign : phSEPArr) {
            if(printOther ||( !printOther && !phSign.getClass().getSimpleName().equals("PhOtherExt"))){
                out += " "+phSign.getSignWithModifiers(addTabsBetwSignAndModifiers);
                if (addTabsBetwSigns) out+= getToStringEndPadding();
            }
        }
        out += "\n";
        if (printSysOut)             System.out.print(out + "\n");
        return out;
    }

    public int countTextFileLines(String textFilePath){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(textFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int lines = 0;
        try {
            while (reader.readLine() != null) lines++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    /**
     * search for different types of fields in the PhEntries Object
     *
     * @param entries
     * @param keyField
     * @param keyFieldValue
     * @param ignoreCase
     * @return
     */
    public static int ordinaryIndexSearch(List<PhEntry> entries, PhSignDefs.LDBEntryComparisonField keyField, String keyFieldValue, boolean ignoreCase) throws NoSuchElementException {
        if (ignoreCase) keyFieldValue = keyFieldValue.toLowerCase();
        for (int i = 0; i < entries.size(); i++) {
            PhEntry phEntry = entries.get(i);
            String entryVal = "";
            if (keyField.toString().equalsIgnoreCase("word")) entryVal = phEntry.getWord();
            else if (keyField.toString().equals("ipa")) entryVal = phEntry.getIpa();
            else if (keyField.toString().equals("ipaRev")) entryVal = phEntry.getIpaRev();

            if (keyFieldValue.equalsIgnoreCase(entryVal)) return i;
        }
        throw new NoSuchElementException("Could not find Entry in DB: <" + keyFieldValue + ">") ;
        //return -1;
    }
    public static boolean checkFile(String newPath) {
        File f = new File(newPath);
        if (!(f.exists() && !f.isDirectory())) {
            final String pathErrorMess = "Can't resolve (or read) filePath: < " + newPath + " >";
            RhymesClient.prErr(pathErrorMess);
            return false;
        } else {
            return true;
        }
    }




}
