package wiktionaryParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Created by Fab on 02.12.2015.
 */
public class RegExParser {


    public static boolean isLanguage(String articleContent, String languageToParse) {
        Matcher getLanguageMatcher = SqlAndRegExStrings.regexSprache.matcher(articleContent);

        if (getLanguageMatcher.find()) {
            return getLanguageMatcher.group("Sprache").equals(languageToParse);
        }
        return false;
    }


    /**
     * Parses the given article String for Lautschrift, Worttrennung und Reim Lautschrift
     *
     * @param articleContent wiktionary article
     * @return null, if Lautschrift is not there or if Lautschrift="...", else the parsed strings in an WiktionArticle Obj
     */
    public static WiktionArticle filterAndGetMatches(String articleName, String articleContent, BufferedWriter bw, boolean noticeWrongLanguageFailures, boolean writeWholeArticlesOfMissedMatchesOfSameLanguageToBW, boolean parseReimLSAndWorttrennung, String languageToParse) throws IllegalStateException, IOException {
        WiktionArticle wa = null;
        boolean isRightLanguage = isLanguage(articleContent, languageToParse);
        boolean foundIPA = false;
        boolean isDotted = false;
        if (isRightLanguage) {
            Matcher getIpaMatcher = SqlAndRegExStrings.regexIPA.matcher(articleContent);
            foundIPA = getIpaMatcher.find();
            if (foundIPA) {
                String ipa = getIpaMatcher.group("Lautschrift");
                if (ipa.equals("…")) {
                    isDotted = true;
                } else {
                    wa = new WiktionArticle();
                    wa.LautschriftSimple = ipa;
                }
                if (parseReimLSAndWorttrennung) {
                    //         wa.WorttrennungSimple = regexMatcherTrenAusReime.group("Worttrennung");
                    //          wa.ReimLS = regexMatcherTrenAusReime.group("ReimLS");
                }
            }
        }
        if(!isRightLanguage||!foundIPA||isDotted) {
            if (!isRightLanguage) {
                bw.write("WRONG LANGUAGE:\t" + articleName);
            } else {
                if (!foundIPA) {
                    bw.write("NO IPA:\t" + articleName);
                } else if (isDotted) {
                    bw.write("Dotted IPA:\t" + articleName);
                }
                bw.newLine();
                if (writeWholeArticlesOfMissedMatchesOfSameLanguageToBW&&!isDotted) {
                    bw.write(articleContent);
                    bw.newLine();
                    bw.write("#######################################################");
                    bw.newLine();
                }
            }
            bw.newLine();
            return null;
        }


        return wa;
    }


    /**
     * Parses the given article String for Lautschrift, Worttrennung und Reim Lautschrift
     *
     * @param articleContent wiktionary article
     * @return null, if Lautschrift is not there or if Lautschrift="...", else the parsed strings in an WiktionArticle Obj
     */
    public static WiktionArticle filterAndGetMatchesOld(String articleName, String articleContent, BufferedWriter bw, boolean noticeWrongLanguageFailures, boolean writeWholeArticlesOfMissedMatchesToBW, boolean parseReimLSAndWorttrennung, String languageToParse) throws IllegalStateException, IOException {
        WiktionArticle wa = null;
        Matcher regexMatcherTrenAusReime = SqlAndRegExStrings.regexSimplestNewest.matcher(articleContent);
        boolean found = regexMatcherTrenAusReime.find();
        boolean isRightLanguage = false;
        boolean isDotted = false;
        if (found) {
            isRightLanguage = regexMatcherTrenAusReime.group("Sprache").equals(languageToParse);
            if (isRightLanguage) {
                wa = new WiktionArticle();
                wa.LautschriftSimple = regexMatcherTrenAusReime.group("Lautschrift");
                if (wa.LautschriftSimple.equals("…")) {
                    // bw.write(articleName+"\tLautschriftSimple == "+wa.LautschriftSimple);
                    // bw.newLine();
                    // if(returnNullForIllegalIPAEntries){
                    //     return null;
                    isDotted = true;
                } else if (parseReimLSAndWorttrennung) {
                    wa.WorttrennungSimple = regexMatcherTrenAusReime.group("Worttrennung");
                    wa.ReimLS = regexMatcherTrenAusReime.group("ReimLS");
                }
            }
        }
        if (found) {
            if (!isRightLanguage && noticeWrongLanguageFailures || isDotted) {
                bw.write("NO LEGAL IPA FOUND:\t" + articleName);
                if (writeWholeArticlesOfMissedMatchesToBW) {
                    bw.write(articleContent);
                    bw.newLine();
                    bw.write("#######################################################");
                    bw.newLine();
                }
                bw.newLine();
                return null;
            }
        }


        return wa;
    }
}
