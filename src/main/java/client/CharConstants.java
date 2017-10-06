package client;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fab on 16.05.2015.
 */
public final class CharConstants {

//  CHARS AND StRINGS
    private static final Map<Character, String> replacementMap = new HashMap<Character, String>() {{
        put('ã', "a");
        put('ɡ', "g");
        put(':', "ː");
        put('ː', "ː"); // \u02d0
        put('ã', "a");
        put('\'', "ˈ");
        put('ˈ', "ˈ");
        put('ˈ', "ˈ"); //\u02c8
        put('̍', "ˈ");// \u030d
        put('ˌ', "ˌ");
        put('ˌ', "ˌ");//  \u02cc
        put('õ', "õ");
        put('ņ', "n̩");
    }};
    private static final char[] skipableChars = {'\u200B', '(', ')', '1', '2', '4', '[', ']', '@'};
    private static final String toStringEndPadding = "\t";

    private static final char[][] diphtongs={{'a','ɪ'},{'a','ʊ'},{'ɛ','ɪ'},{'ɔ','ɪ'},{'ʊ','ɪ'}};

    public static String getUniCodeStr(char ch) {
        return "\\u" + Integer.toHexString(ch | 0x10000).substring(1);
    }

    public static Map<Character, String> getReplacementMap() {
        return replacementMap;
    }

    public static char[] getSkipableChars() {
        return skipableChars;
    }

    public static String getToStringEndPadding() {
        return toStringEndPadding;
    }




}
