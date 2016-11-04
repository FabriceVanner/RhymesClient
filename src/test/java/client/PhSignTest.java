package client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import phonetic_entities.PhSign;

/** 
* PhSign Tester. 
* 
* @author <Authors name> 
* @since <pre>Jun 17, 2015</pre> 
* @version 1.0 
*/ 
public class PhSignTest { 
    PhSign sign=null;
@Before
public void before() throws Exception {
    sign = PhSignDefs.getBasicPhSign('a');
} 

@After
public void after() throws Exception { 
} 

/** 
* 
*
*/ 
@Test
public void testCalcSimilarity() throws Exception { 

    PhSign sign1 = PhSignDefs.getBasicPhSign('a');
    PhSign sign2 = PhSignDefs.getBasicPhSign('æ');
    PhSign sign3 = PhSignDefs.getBasicPhSign('ɑ');


    Assert.assertEquals(1.f, sign.calcSimilarity(sign1),0.f);
    Assert.assertNotEquals(1.f, sign.calcSimilarity(sign2), 0.f);
    Assert.assertNotEquals(sign.calcSimilarity(sign2), sign.calcSimilarity(sign3), 0.f);

}





} 
