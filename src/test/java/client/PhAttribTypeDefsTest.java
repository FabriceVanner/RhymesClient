package client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static client.PhAttribTypeDefs.*;

/** 
* PhAttribDefs Tester.
* 
* @author <Authors name> 
* @since <pre>Jun 20, 2015</pre> 
* @version 1.0 
*/ 
public class PhAttribTypeDefsTest {

    private float mapsToCalcSimi(Enum e1ToMap1, Enum e2ToMap1, Enum e1ToMap2, Enum e2ToMap2)throws Exception{
        Map<String,Enum> attribsMap1 = new HashMap<String,Enum>();
        Map<String,Enum> attribsMap2 = new HashMap<String,Enum>();

        if (e1ToMap1!=null)attribsMap1.put(e1ToMap1.getClass().getSimpleName(), e1ToMap1);
        if (e2ToMap1!=null)attribsMap1.put(e2ToMap1.getClass().getSimpleName(), e2ToMap1);
        if (e1ToMap2!=null)attribsMap2.put(e1ToMap2.getClass().getSimpleName(), e1ToMap2);
        if (e2ToMap2!=null)attribsMap2.put(e2ToMap2.getClass().getSimpleName(), e2ToMap2);
        return calcSimilarity(attribsMap1, attribsMap2);
    }

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Attrib:      Types       Value
     * 1            same        same
     * @throws Exception
     */
    @Test
    public void testCS_1() throws Exception {
        //same type, same val
        Assert.assertEquals(1.f, mapsToCalcSimi(VowelArtiPlace.central, null, VowelArtiPlace.central, null), 0f);

        //also same type, same val
        Assert.assertEquals(1.f, mapsToCalcSimi(VowelOpenness.openMid, null, VowelOpenness.openMid, null), 0f);
    }

    /**
     * Attrib:      Types       Value
     * 1            Same        Opposite
     * @throws Exception
     */
    @Test
    public void testCS_2() throws Exception {
        //same type opposite vals
        Assert.assertEquals(0.f, mapsToCalcSimi(VowelArtiPlace.back, null, VowelArtiPlace.front, null),0f);
        //same type opposite vals
        Assert.assertEquals(0.f, mapsToCalcSimi( VowelOpenness.open, null,  VowelOpenness.close, null),0f);
    }


    /**
     * Attrib:      Types       Value
     * 1            same        diff
     *
     * @throws Exception
     */
    @Test
    public void testCS_3() throws Exception {
        //same type diff vals
        float val1 = mapsToCalcSimi(VowelArtiPlace.back, null, VowelArtiPlace.central, null);
        Assert.assertTrue(val1<1&&val1>0);

        // same thing, diff order:
        float val2 = mapsToCalcSimi(VowelArtiPlace.central, null, VowelArtiPlace.back, null);
        Assert.assertEquals(val1,val2,0);
    }


    /**
     * Attrib:      Types       Value
     * 1            same        same
     * 2            same        same
     *
     * @throws Exception
     */
    @Test
    public void testCS_4() throws Exception {
        //same types, same vals
        Assert.assertEquals(1.f, mapsToCalcSimi(VowelArtiPlace.central, VowelOpenness.mid, VowelArtiPlace.central, VowelOpenness.mid), 0f);

        //also same types, same vals
        Assert.assertEquals(1.f, mapsToCalcSimi(VowelOpenness.openMid, VowelRoundedness.notRound, VowelOpenness.openMid, VowelRoundedness.notRound), 0f);
    }

    /**
     * Attrib:      Types       Value
     * 1            same        opp
     * 2            same        opp
     * @throws Exception
     */
    @Test
    public void testCS_5() throws Exception {
        //same types notDefined opposite vals
        Assert.assertEquals(0.f, mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.open, VowelArtiPlace.front, VowelOpenness.close), 0f);
    }

    /**
     * Attrib:      Types       Vals
     * 1            same        same
     * 2            same        opp
     * @throws Exception
     */
    @Test
    public void testCS_6() throws Exception {
        //same types: first Ones: same vals, second ones opposites vals
        float firstOnesSameSecOnesOpposite = mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.open, VowelArtiPlace.back, VowelOpenness.close);
        Assert.assertTrue(firstOnesSameSecOnesOpposite<1&&firstOnesSameSecOnesOpposite>0);

        // same thing, diff order
        float firstOnesSameSecOnesOppositeDiffOrder = mapsToCalcSimi(VowelOpenness.close, VowelArtiPlace.back, VowelArtiPlace.back, VowelOpenness.open);
        Assert.assertEquals(firstOnesSameSecOnesOpposite,firstOnesSameSecOnesOppositeDiffOrder,0);

        //
    }

    /**
     * Attrib:      Types       Vals
     * 1            same        same
     * 2            same        opp
     * @throws Exception
     */
    @Test
    public void testCS_7() throws Exception {
        //same types: first Ones: same vals, second ones opposites vals
        float halfSimi = mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.open, VowelArtiPlace.back, VowelOpenness.close);
        Assert.assertEquals(0.5,halfSimi,0.f);
        Assert.assertTrue(halfSimi<1&&halfSimi>0);
    }


    /**
     * Attrib:      Types       Vals
     * 1            same        same
     * 2            same        opp
     * ------VS--------
     * 1            same        same
     * 2            same        diff
     * @throws Exception
     */
    @Test
    public void testCS_8() throws Exception {
        float halfSimi = mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.open, VowelArtiPlace.back, VowelOpenness.close);
        float moreThanHalfSim = mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.nearOpen, VowelArtiPlace.back, VowelOpenness.nearClose);
        Assert.assertTrue(halfSimi < moreThanHalfSim);
    }

    /**
     * Attrib:      Types       Vals
     * 1            same        same
     * 2            same        opp
     * ------VS--------
     * 1            same        same
     * 2            same        littleDiff
     * @throws Exception
     */

    @Test
    public void testCS_9()throws Exception{
        float moreThanHalfSim = mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.nearOpen, VowelArtiPlace.back, VowelOpenness.nearClose);

        float evenMoreThanMoreThanHalfSim = mapsToCalcSimi(VowelArtiPlace.back, VowelOpenness.openMid, VowelArtiPlace.back, VowelOpenness.closeMid);
        Assert.assertTrue(moreThanHalfSim<evenMoreThanMoreThanHalfSim);

    }


    /**
     * Attrib:      Types           Vals
     * 1            diff:notdefC
     */
    @Test
    public void testCS_10() throws Exception{
        //same type, same val
        Assert.assertEquals(0.f, mapsToCalcSimi(VowelOpenness.nearClose, null, VowelArtiPlace.central, null), 0f);
    }

    /**
     * Attrib:      Types            Vals
     * 1            diff: defC,null
     */
    @Test
    public void testCS_11()throws Exception{
         mapsToCalcSimi(VowelLength.halfLong, null, null, null);
    }

    /**
     * Attrib:      Types            Vals
     * 1            diff: defC,null
     * VS
     * 1            same,defC       halfLong,normal(def)
     */
    @Test
    public void testCS_12()throws Exception{
        float diffToDefComp =  mapsToCalcSimi(VowelLength.halfLong,null,null,null);
        float diffToDefComp2 =  mapsToCalcSimi(VowelLength.halfLong,null,VowelLength.normal,null);
        Assert.assertEquals(diffToDefComp,diffToDefComp2,0.0 );
    }


    /**
     * Attrib       Types           Vals
     * 1            same            diff
     * 2            diff(not defC)
     * VS
     * 1            same            diff
     * @throws Exception
     */
    @Test
    public void testCS_13()throws Exception {
        //same type, same val
        float onlyVowDiffMatters1 = mapsToCalcSimi(VowelOpenness.nearClose, VowelArtiPlace.central, VowelOpenness.nearOpen, VowelRoundedness.round);
        float onlyVowDiffMatters2 = mapsToCalcSimi(VowelOpenness.nearClose,null,VowelOpenness.nearOpen,null);
        Assert.assertEquals(onlyVowDiffMatters1,onlyVowDiffMatters2 , 0f);

    }

    /**
     * Attrib       Types           Vals
     * 1            same            same
     * 2            diff:defC,null
     * VS
     * 1            diff:defC,null
     * @throws Exception
     */
    @Test
    public void testCS_14()throws Exception{
        float onlyDefCompMatters = mapsToCalcSimi(VowelOpenness.nearClose, VowelLength.halfLong, VowelOpenness.nearClose, null);
        float onlyDefCompMatters2 =  mapsToCalcSimi(VowelLength.halfLong,null,null,null);
        Assert.assertNotEquals(onlyDefCompMatters,onlyDefCompMatters2,0f);
    }

    /**
     *
     *
     *Attrib       Types           Vals
     * 1           diff(not defC)
     * 2            diff(not defC)
     * @throws Exception
     */
    @Test
    public void testCS_15() throws Exception{
        float nothingsToCompare =  mapsToCalcSimi(VowelOpenness.nearClose, VowelArtiPlace.back, VowelRoundedness.round, null);
        Assert.assertEquals(0f,nothingsToCompare,0);
//        float firstSameType_SecDiff_NotDefComparableTypes = mapsToCalcSimi(VowelOpenness.nearClose, VowelLength.halfLong, VowelOpenness.nearOpen, null);
    }




}