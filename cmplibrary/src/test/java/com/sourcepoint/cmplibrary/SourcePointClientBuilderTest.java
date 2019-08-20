package com.sourcepoint.cmplibrary;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SourcePointClientBuilderTest {
    private SourcePointClientBuilder sourcePointClientBuilder;

    @Before
    public void setUp() throws ConsentLibException {
        sourcePointClientBuilder = new SourcePointClientBuilder(123, "example.com", true);//mock(SourcePointClientBuilder.class);
    }

    private Field getDeclareFieldAccess(String fieldName) throws Exception{
        Field member = SourcePointClientBuilder.class.getDeclaredField(fieldName);
        member.setAccessible(true);
        return member;
    }


    @Test
    public void setMmsDomain() throws Exception{
        sourcePointClientBuilder.setMmsDomain("mmsDomain");
        Field localMember = getDeclareFieldAccess("mmsDomain");
        assertEquals("mmsDomain", localMember.get(sourcePointClientBuilder).toString());
    }

    @Test
    public void setCmpDomain() throws Exception{
        sourcePointClientBuilder.setCmpDomain("cmpDomain");
        Field localMember = getDeclareFieldAccess("cmpDomain");
        assertEquals("cmpDomain", localMember.get(sourcePointClientBuilder).toString());
    }

    @Test
    public void setMessageDomain() throws Exception {
        sourcePointClientBuilder.setMessageDomain("messageDomain");
        Field localMember = getDeclareFieldAccess("messageDomain");
        assertEquals("messageDomain", localMember.get(sourcePointClientBuilder).toString());
    }

    @Test
    public void setStagingCampaign() throws Exception {
        sourcePointClientBuilder.setStagingCampaign(true);
        Field localMember = getDeclareFieldAccess("stagingCampaign");
        assertEquals(true, localMember.get(sourcePointClientBuilder));
    }
}