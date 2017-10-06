package operational_entities;

/**
 * Created by Fabrice Vanner on 21.12.2016.
 */
public class WordPair {
    public String wordOne;
    public String wordTwo;
    public float toleratedDifference;
    /** The score that shall be reached to ??? */
    public float destinationScore;

    public WordPair(String wordOne, String wordTwo, float toleratedDifference, float destinationScore) {
        this.wordOne = wordOne;
        this.wordTwo = wordTwo;
        this.toleratedDifference = toleratedDifference;
        this.destinationScore = destinationScore;
    }

    public WordPair(String wordOne, String wordTwo) {
        this.wordOne = wordOne;
        this.wordTwo = wordTwo;
    }

    public String toString(){
        return wordOne +" : "+wordTwo+" \ttoleratedDiff = "+toleratedDifference + "\tdestinationScore = "+destinationScore;
    }
}
