package wiktionaryParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Fab on 26.11.2015.
 *
 *
 * this class  parses the phonetics of eachEntry word-article of the german wiktionary xml-dumpfile with jax and regexces
 * get dumps at https://dumps.wikimedia.org/dewiktionary/
 */
public class XMLDumpParser {

    static int i = 0;
    /* article names containing following tokens are meta-articles */
    private static String[] metaArticleIndicator = {"MediaWiki:", "Vorlage", "Wiktionary", "Benutzer", "Hilfe", "Diskussion", "Verzeichnis:", "MediaWiki","Kategorie:","Verzeichnis Diskussion:","Kategorie Diskussion:","Datei:","Flexion:"};
    /** the outputDelimiter to use in the output file between eachEntry word and its phonetic*/
    private static String delimiter ="    ";
    private static String logFileName="dumpParsing.log";


    public static void main(String argv[]) {
    XMLDumpParser xmlDumpParser = new XMLDumpParser();
        String fileName = "S:\\FabriceDocs\\Studieren\\Bachelor\\Archiv\\dewiktionary-20150407-pages-meta-current.xml";
        //  String fileName="S:\\FabriceDocs\\Studieren\\Bachelor\\Archiv\\new 2.xml";
        //  String fileName="S:\\FabriceDocs\\Studieren\\Bachelor\\Archiv\\dewiktionary-20150407-pages-meta-current_Ausschnitt.xml";
        xmlDumpParser.parseXMLDump(fileName,"parsedDump.txt",delimiter, metaArticleIndicator,logFileName);
    }

    /**
     *
     * @param inputXMLDumpFileName
     * @param outputFileName
     */
    public static void parseXMLDump(String inputXMLDumpFileName,String outputFileName ){
        parseXMLDump(inputXMLDumpFileName,outputFileName,delimiter, metaArticleIndicator,logFileName);
    }

    /**
     *
     * @param inputXMLDumpFileName
     * @param outputFileName
     * @param outputDelimiter The outputDelimiter between eachEntry word and its IPA String in the output file
     * @param skipTexts if one of these tokens appears in the beginning of the articlename, the article won't be parsed
     * @param logFileName
     */
    public static void parseXMLDump(String inputXMLDumpFileName, String outputFileName,String outputDelimiter, String[] skipTexts, String logFileName){
        try {
         //   MediawikiParser mwp = new MediawikiParser();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            final BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName, true));
            BufferedWriter bufLogWriter = new BufferedWriter(new FileWriter(logFileName, true));
            DefaultHandler handler = new DefaultHandler() {
                boolean skip = false;
                Boolean nowInXmlElement_1 = false;
                Boolean nowInXmlElement_2 = false;
                String xmlElementName_1 = "title";
                String xmlElementName_2 = "text";

                StringBuilder elementBuffer_1 = new StringBuilder();
                StringBuilder elementBuffer_2 = new StringBuilder();

                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) throws SAXException {


                    //if (qName.equalsIgnoreCase("text xml:space=\"preserve\"")) {
                    //if (!skip) {
                    if (qName.equalsIgnoreCase(xmlElementName_1)) {
                        skip = false;
                        elementBuffer_1.setLength(0);
                        nowInXmlElement_1 = true;
                        nowInXmlElement_2 = false;
                    } else if (qName.equalsIgnoreCase(xmlElementName_2)) {
                        elementBuffer_2.setLength(0);
                        nowInXmlElement_2 = true;
                        nowInXmlElement_1 = false;
                    }

                }

                public void endElement(String uri, String localName,
                                       String qName) throws SAXException {
                    if (!skip) {
                        if (qName.equalsIgnoreCase(xmlElementName_1)) {
                            endElementPrepareBuffer(qName, elementBuffer_1, nowInXmlElement_1, false, false, false,false);
                            nowInXmlElement_1 = false;

                        } else if (qName.equalsIgnoreCase(xmlElementName_2)) {
                            endElementPrepareBuffer(qName, elementBuffer_2, nowInXmlElement_2, false, true, true,false);
                            nowInXmlElement_2 = false;


                        }
                    }

                }

                /**
                 *
                 * @param qName das aktuelle xml-tag in dem sich der parser befindet
                 * @param buffer der gefüllte buffer
                 * @param nowInXmlElement
                 * @param writeDirectToFile
                 * @param filter
                 * @param writeFilteredOutToFile
                 * @param printProgress to CONSOLE
                 */
                private void endElementPrepareBuffer(String qName, StringBuilder buffer, Boolean nowInXmlElement, boolean writeDirectToFile, boolean filter, boolean writeFilteredOutToFile,boolean printProgress) {
                    final String content = buffer.toString().trim();
                    int contLength = content.length();
                    int substrEnd = 10;
                    String printStr = "";
                    if (contLength > substrEnd) {
                        printStr = content.substring(0, substrEnd) + "...";
                    } else {
                        printStr = content;
                    }
                    if(printProgress) {
                        System.out.print("<" + qName + ">");
                        System.out.print(printStr);
                        System.out.println("</" + qName + ">");
                    }
                    if (writeDirectToFile) {
                        // .... deal with content
                        try {
                            bw.write(content);
                            bw.newLine();
                        } catch (java.io.IOException ioE) {

                        }
                    }
                    WiktionArticle wa = null;
                    if (filter) {
                        String title = elementBuffer_1.toString().trim();
                        //title.toLowerCase();
                        try {
                            wa = RegExParser.filterAndGetMatches(title, content, bufLogWriter,false,true,false,"Deutsch");
                        } catch (java.io.IOException ioE) {
                        }
                        if (writeFilteredOutToFile) {
                            try {
                                String strToWrite = "";
                                if (wa == null) {
                                    strToWrite = title + outputDelimiter + "NO_IPA";
                                } else {
                                    strToWrite = title + outputDelimiter + wa.LautschriftSimple;
                                    bw.write(strToWrite);
                                    bw.newLine();
                                }
                            } catch (java.io.IOException ioE) {
                            }
                        }
                    }
                    i++;
                    if (i >= 500) {
                        //throw new SAXException(new BreakParsingException());

                    }
                    nowInXmlElement = false;
                }

                /**
                 * wird beliebig oft pro zeile aufgerufen
                 * @param ch
                 * @param start
                 * @param length
                 * @throws SAXException
                 */
                public void characters(char ch[], int start, int length) throws SAXException {
                    String str = new String(ch, start, length);
                    if (!skip) {
                        if (nowInXmlElement_1) {
                            for (String txt : skipTexts) {
                                if (str.startsWith(txt)) {
                                    skip = true;
                                }
                            }
                            if (str.length() == 0) {
                                skip = true;
                            } else {
                                str = str.trim();

                                int endSub = 2 >= str.length() - 1 ? str.length() : 2;
                                String substr = str.substring(0, endSub);
                                try {
                                    Integer.parseInt(substr);
                                    skip = true;
                                } catch (NumberFormatException nfe) {

                                }
                            }
                            if (!skip) {
                                // System.out.println("Characters got called with: <"+ new String(ch, start, length)+">");
                                //System.out.println("found xmlElementName_1" +xmlElementName_1+ "<"+ new String(ch, start, length)+">");
                                //    nowInXmlElement_1 = false;
                                elementBuffer_1.append(str);

                            }
                        } else if (nowInXmlElement_2) {
                            if (!skip) {
                                elementBuffer_2.append(str);
                            }
                        }
                    }
                }
            };

            saxParser.parse(inputXMLDumpFileName, handler);

            if (bw != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e2) {
                }
            }
            if (bufLogWriter != null) {
                try {
                    bw.flush();
                    bw.close();
                } catch (IOException e2) {
                }
            }

        }catch (SAXException e) {
            //     if (e.Cause instanceof BreakParsingException) {
            System.out.println("SAXException "+e.getMessage().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}



