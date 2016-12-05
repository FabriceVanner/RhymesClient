package MediawikiParser;

import java.util.regex.Pattern;

/**
 * Created by Fab on 10.04.2015.
 */
public  class SqlAndRegExStrings {
    /* returns table in order of koelner code, with eachEntry code only once, starting at codes larger than 10, returns only 10 rows*/
    public static String sql_1 = "WITH ORDERED AS\n" +
            "(\n" +
            "SELECT\n" +
            "    flect\n" +
            ",   id\n" +
            ",   koelnercode\n" +
            ",   ROW_NUMBER() OVER (PARTITION BY koelnercode ORDER BY koelnercode ASC) AS rn\n" +
            "FROM\n" +
            "    woerter\n" +
            ")\n" +
            "SELECT\n" +
            "    flect\n" +
            ",   id\n" +
            ",   koelnercode\n" +
            "FROM\n" +
            "    ORDERED\n" +
            "WHERE\n" +
            "    rn = 1 AND koelnercode >10\n" +
            "LIMIT 20";
    public static String sql_GetAllColsOrderByKC = "SELECT * FROM woerter ORDER BY Koelnercode";
    public static String sql_selAllFrTable = "SELECT * FROM woerter";
    public static String sql_whereIsNull = "SELECT * FROM woerter WHERE KoelnerCodeRev is null";
    public static String sqlPrepared = "UPDATE woerter SET koelnerCodeRev=? WHERE flect=?";
    public static String sqlPrepINSERTINTOLautschriftDb =   "INSERT INTO woerter(wort, ipa, reim, worttrennung)\n" +
                                                            "VALUES\n (?,?,?,?);";
    public static String sqlPrepStSetRow ="";
    public static String sql_getHouse= "";
    public static String getSql_MedWikiJoinTitleAndTextCOUNT = "SELECT Count (*)\n" +
            "\tFROM mediawiki.page \n" +
            "\t\tJOIN  mediawiki.revision ON mediawiki.page.page_id = mediawiki.revision.rev_page\n" +
            "\t\tJOIN  mediawiki.pagecontent ON mediawiki.pagecontent.old_id = mediawiki.revision.rev_text_id";
    public static String sql_MedWikiJoinTitleAndText="SELECT mediawiki.page.page_title, mediawiki.pagecontent.old_text\n" +
                                            "FROM mediawiki.page \n" +
                                                "JOIN  mediawiki.revision ON mediawiki.page.page_id = mediawiki.revision.rev_page\n" +
                                                "JOIN  mediawiki.pagecontent ON mediawiki.pagecontent.old_id = mediawiki.revision.rev_text_id\n"+
                                                "ORDER BY mediawiki.page.page_title  ";
    //   String sql = "UPDATE woerter SET koelnerCode='" + woerter[1][rhymesArrIndex] + "' WHERE flect='" + woerter[0][rhymesArrIndex] + "'";
    public static String sql_CountWhereEmpty = "SELECT count(*) FROM woerter where koelnercodeRev is null";


   public static String zeilenEnde="\\n";

    public static Pattern regexBox = Pattern.compile(
            "==[^(^=]*\\(\\{\\{Sprache\\|   Deutsch   }}\\)\\ ?=="+zeilenEnde +
            "===\\s*\\{\\{Wortart\\|   (?<Wortart>[\\w\\s]*)   \\|\\w*}}.*==="+zeilenEnde +
            "(?s:.)*? " +
            "(?<Box>\\{\\{Deutsch\\ [^\\ ]*\\ Übersicht"+zeilenEnde +
            "    (?:\\|[^"+zeilenEnde+"]*"+zeilenEnde+")*" +
            "\\}\\}"+zeilenEnde+")"
            ,Pattern.COMMENTS);

    public static Pattern regexWortTrennung = Pattern.compile(
            "==[^(^=]*\\(\\{\\{Sprache\\|   Deutsch   }}\\)\\ ?=="+zeilenEnde+
            "===\\s*\\{\\{Wortart\\|   (?<Wortart>[\\w\\s]*)   \\|\\w*}}.*==="+zeilenEnde+
            "(?s:.)*?"+
            "(?:\\{\\{Worttrennung}}"+zeilenEnde+
            ":(?<Worttrennung>[^,]*?)   "+
            "    (?:,\\s\\{\\{   (?<WorttrennungMultiTyp0>[^}]*?)   }}  \\s?   (?<WorttrennungMulti0>[^"+zeilenEnde+"]*?)   )?"+
            "    (?:,\\s\\{\\{   (?<WorttrennungMultiTyp1>[^}]*?)   }}  \\s?   (?<WorttrennungMulti1>[^"+zeilenEnde+"]*?)   )?"+
            "    (?:,\\s\\{\\{   (?<WorttrennungMultiTyp2>[^}]*?)   }}  \\s?   (?<WorttrennungMulti2>[^"+zeilenEnde+"]*?)   )?"+
            "    "+zeilenEnde+")"
            ,Pattern.COMMENTS);
    public static String[] WorttrennungRegExResGroupKeys = new String[]{"Wortart","Lautschrift","Lautschrift0","LautschriftAuch","LautschriftMultiTyp0","LautschriftMulti0","LautschriftMultiTyp1","LautschriftMulti1","LautschriftMultiTyp2","LautschriftMulti2","LautschriftMultiTyp3","LautschriftMulti3"};
    public static    int WorttrennungStartTestsIndex =1;

    public static Pattern regexAussprache = Pattern.compile(
            "==[^(^=]*\\(\\{\\{Sprache\\|   Deutsch   }}\\)\\ ?=="+zeilenEnde+
            "===\\s*\\{\\{Wortart\\|   (?<Wortart>[\\w\\s]*)   \\|\\w*}}.*==="+zeilenEnde+
            "(?s:.)*? "+
            "\\{\\{Aussprache}}"+zeilenEnde+
            ":\\{\\{IPA}}\\ \\{\\{Lautschrift\\|   (?<Lautschrift>[^}]*)   }}"+
            "    (?:,?\\ ?   ~\\ \\{\\{Lautschrift\\|   (?<Lautschrift0>[^}]*)   }})?"+
            "    (?:,\\ ''auch:''\\ \\{\\{Lautschrift\\|(?<LautschriftAuch>[^}]*)   }})?"+
            "    (?:,\\ ?\\{\\{   (?<LautschriftMultiTyp0>[^}]*)   }}\\ \\{\\{Lautschrift\\|   (?<LautschriftMulti0>[^}]*) }})?"+
            "    (?:,\\ ?\\{\\{   (?<LautschriftMultiTyp1>[^}]*)   }}\\ \\{\\{Lautschrift\\|   (?<LautschriftMulti1>[^}]*) }})?"+
            "    (?:,\\ ?\\{\\{   (?<LautschriftMultiTyp2>[^}]*)   }}\\ \\{\\{Lautschrift\\|   (?<LautschriftMulti2>[^}]*) }})?"+
            "    (?:,\\ ?\\{\\{   (?<LautschriftMultiTyp3>[^}]*)   }}\\ \\{\\{Lautschrift\\|   (?<LautschriftMulti3>[^}]*) }})?"+
            "    "+zeilenEnde
            ,Pattern.COMMENTS); // am ende <+"?">
    public static String[] AusspracheRegExResGroupKeys = new String[]{"Wortart","Lautschrift","Lautschrift0","LautschriftAuch","LautschriftMultiTyp0","LautschriftMulti0","LautschriftMultiTyp1","LautschriftMulti1","LautschriftMultiTyp2","LautschriftMulti2","LautschriftMultiTyp3","LautschriftMulti3"};
    public static    int AussprachemultiStartTestsIndex =4;
            //static {        AusspracheRegExResGroupsIFM.put("Wortart", "");        AusspracheRegExResGroupsIFM.put("Lautschrift", "");        AusspracheRegExResGroupsIFM.put("Lautschrift0", "");        AusspracheRegExResGroupsIFM.put("LautschriftAuch", "");        AusspracheRegExResGroupsIFM.put("LautschriftMulti0", "");        AusspracheRegExResGroupsIFM.put("LautschriftMulti1", "");        AusspracheRegExResGroupsIFM.put("LautschriftMulti2", "");        AusspracheRegExResGroupsIFM.put("LautschriftMulti3", "");    }

    public static Pattern regexReime = Pattern.compile(
            "==[^(^=]*\\(\\{\\{Sprache\\|   Deutsch   }}\\)\\ ?=="+zeilenEnde+
            "===\\s*\\{\\{Wortart\\|   (?<Wortart>[\\w\\s]*)   \\|\\w*}}.*==="+zeilenEnde+
            "(?s:.)*?"+
            ":\\{\\{Reime}}\\ \\{\\{Reim\\|(?<ReimLS>[^\\|]*)"
            ,Pattern.COMMENTS);

    public static Pattern regexWortart = Pattern.compile(
            "==[^(^=]*\\(\\{\\{Sprache\\|   Deutsch   }}\\)\\ ?=="+zeilenEnde +
            "===\\s*\\{\\{Wortart\\|   (?<Wortart>[\\w\\s]*)   \\|\\w*}}.*==="+zeilenEnde
            ,Pattern.COMMENTS);

    public static Pattern regexTrenAusReim = Pattern.compile("==[^(^=]*\\(\\{\\{Sprache\\|   Deutsch   }}\\)\\ ?==" + zeilenEnde +
            "(?<allBefore>(?s:.)*?)" +
            "(?:" +
            "\\{\\{Worttrennung}}" + zeilenEnde +
            ":(?<Worttrennung>[^,^" + zeilenEnde + "]*).*" + zeilenEnde +
            "(?s:.)*?" +
            ")?" +
            "\\{\\{Aussprache}}" + zeilenEnde +
            ":\\{\\{IPA}}\\ \\{\\{Lautschrift\\|   (?<Lautschrift>[^}]*)   }}" +
            "(?:" +
            "(?s:.)*?" +
            ":\\{\\{Reime}}\\ \\{\\{Reim\\|(?<ReimLS>[^|^}]*))?",Pattern.COMMENTS);


    //UPDATE woerter SET worttrennung = TRIM(notDefined fromIndex worttrennung)
    //select woerter.ipa, char_length(woerter.ipa)    FROM woerter    where id =45711
}
