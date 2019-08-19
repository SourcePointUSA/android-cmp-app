package com.sourcepoint.cmplibrary;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class EncodedParamTest {

    @Test
    public void EncodeParam() throws Exception{
        String attrName = "attrName";
        String attrValue = "attrValue";
        String encodeValue = "UTF-8";
        EncodedParam encodedParam = mock(EncodedParam.class) ;

        Method method = EncodedParam.class.getDeclaredMethod("encode",String.class,String.class);
        method.setAccessible(true);
        assertEquals(URLEncoder.encode(attrValue,encodeValue),method.invoke(encodedParam,attrName,attrValue));

        Field member = EncodedParam.class.getDeclaredField("value");
        member.setAccessible(true);
        EncodedParam encodedParam1 = new EncodedParam(attrName,attrValue);

        assertNotNull(member.get(encodedParam1).toString());
        assertEquals(member.get(encodedParam1).toString(),encodedParam1.toString());
    }

    @Test
    public void Encode() throws Exception{
        String attrName = "attrName";
        String attrValue = "attrValue";
        String encodeValue = "UTF-8";
        EncodedParam encodedParam = mock(EncodedParam.class) ;

        Method method = EncodedParam.class.getDeclaredMethod("encode",String.class,String.class);
        method.setAccessible(true);
        assertEquals(URLEncoder.encode(attrValue,encodeValue),method.invoke(encodedParam,attrName,attrValue));
    }
}