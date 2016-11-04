package client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import phonetic_entities.PhAttribType;

/** 
* PhAttribTypeTester.
* 
* @author <Authors name> 
* @since <pre>Jun 20, 2015</pre> 
* @version 1.0 
*/ 
public class PhAttribTypeTest {

    PhAttribType<PhAttribTypeDefs.VowelArtiPlace> phAVowelArti;
    PhAttribType<PhAttribTypeDefs.VowelOpenness> phAVowelOpenness;
    PhAttribType<PhAttribTypeDefs.VowelLength> phAVowelLength;
    @Before
public void before() throws Exception {
        phAVowelArti = PhAttribTypeDefs.getAllPhAttribTypes().get(PhAttribTypeDefs.VowelArtiPlace.class.getSimpleName());
        phAVowelOpenness =  PhAttribTypeDefs.getAllPhAttribTypes().get(PhAttribTypeDefs.VowelOpenness.class.getSimpleName());
        phAVowelLength = PhAttribTypeDefs.getAllPhAttribTypes().get(PhAttribTypeDefs.VowelLength.class.getSimpleName());
} 

@After
public void after() throws Exception { 
}





    /**
* 
* Method: calcSimilarity(T first, T second) 
* 
*/ 
@Test
public void testCalcSimilarity() throws Exception {
    float hintenVorneSimi = phAVowelArti.calcSimilarity(PhAttribTypeDefs.VowelArtiPlace.back, PhAttribTypeDefs.VowelArtiPlace.front);
    float hintenHintenSimi = phAVowelArti.calcSimilarity(PhAttribTypeDefs.VowelArtiPlace.back, PhAttribTypeDefs.VowelArtiPlace.back);
    float hintenZentralSimi = phAVowelArti.calcSimilarity(PhAttribTypeDefs.VowelArtiPlace.back, PhAttribTypeDefs.VowelArtiPlace.central);
    float zentralHintenSimi = phAVowelArti.calcSimilarity(PhAttribTypeDefs.VowelArtiPlace.central, PhAttribTypeDefs.VowelArtiPlace.back);

    float nullNull= phAVowelArti.calcSimilarity(null, null);

    Assert.assertEquals(1,nullNull,0f);
    Assert.assertEquals(hintenZentralSimi,zentralHintenSimi, 0f);

    Assert.assertNotEquals(1f,hintenZentralSimi,0f);
    Assert.assertNotEquals(0f, hintenZentralSimi, 0f);
    Assert.assertEquals(1f,hintenHintenSimi, 0f);
    Assert.assertEquals(0f,hintenVorneSimi, 0f);


    float vowOpenOffenGeschl = phAVowelOpenness.calcSimilarity(PhAttribTypeDefs.VowelOpenness.open, PhAttribTypeDefs.VowelOpenness.close);
    Assert.assertEquals(0f,vowOpenOffenGeschl,0f);


}

@Test (expected = AttribTypeNotComparableException.class)
public void testCS_NotDefaultComparable_VS_Null()throws Exception{
    phAVowelOpenness.calcSimilarity(PhAttribTypeDefs.VowelOpenness.close,null);
}

@Test
public void testCS_DefaultComparableAttrib_VS_Null()throws Exception{
    phAVowelLength.calcSimilarity(PhAttribTypeDefs.VowelLength.halfLong,null);
}




} 
