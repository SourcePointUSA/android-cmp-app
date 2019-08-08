package com.sourcepoint.cmplibrary;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SourcePointClientBuilderTest {
    SourcePointClientBuilder sourcePointClientBuilder;

    @Before
    public void setUp() throws ConsentLibException {
        sourcePointClientBuilder = new SourcePointClientBuilder(808, "siteName", true);//mock(SourcePointClientBuilder.class);
    }


    @Test
    public void setMmsDomain() throws Exception{
        sourcePointClientBuilder.setMmsDomain("mmsDomain");
        Field member = null;

        member = SourcePointClientBuilder.class.getDeclaredField("mmsDomain");

        member.setAccessible(true);
        SourcePointClientBuilder sourcePointClientBuilder1 = sourcePointClientBuilder;

        assertEquals("mmsDomain", member.get(sourcePointClientBuilder1).toString());
    }

    @Test
    public void setCmpDomain() throws Exception{
        sourcePointClientBuilder.setCmpDomain("cmpDomain");
        Field member = null;

        member = SourcePointClientBuilder.class.getDeclaredField("cmpDomain");

        member.setAccessible(true);
        SourcePointClientBuilder sourcePointClientBuilder1 = sourcePointClientBuilder;

        assertEquals("cmpDomain", member.get(sourcePointClientBuilder1).toString());
    }

    @Test
    public void setMessageDomain() throws Exception {
        sourcePointClientBuilder.setMessageDomain("messageDomain");
        Field member = null;

        member = SourcePointClientBuilder.class.getDeclaredField("messageDomain");

        member.setAccessible(true);
        SourcePointClientBuilder sourcePointClientBuilder1 = sourcePointClientBuilder;

        assertEquals("messageDomain", member.get(sourcePointClientBuilder1).toString());
    }

    @Test
    public void setStagingCampaign() throws Exception {
        sourcePointClientBuilder.setStagingCampaign(true);
        Field member = null;

        member = SourcePointClientBuilder.class.getDeclaredField("stagingCampaign");

        member.setAccessible(true);
        SourcePointClientBuilder sourcePointClientBuilder1 = sourcePointClientBuilder;

        assertEquals(true, member.get(sourcePointClientBuilder1));
    }
}