package com.sourcepointmeta.app;

import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticTestData {

    private static TargetingParam targetingParam = new TargetingParam("MyPrivacyManager","false");
    private  static TargetingParam targetingParam1 = new TargetingParam( "CMP","false");
    private static  List<TargetingParam> targetingParameter = Arrays.asList(targetingParam, targetingParam1);

    private static final Website WEBSITE_ENTITY = new Website(22,"mobile.demo",false, "authId",targetingParameter);

    private static TargetingParam targetingParam2 = new TargetingParam("MyPrivacyManager","true");
    private  static TargetingParam targetingParam3 = new TargetingParam( "CMP","true");
    private static  List<TargetingParam> targetingParameter1 = Arrays.asList(targetingParam2, targetingParam3);

    private static  List<TargetingParam> targetingParameter2 = new ArrayList<>();

    private static final Website WEBSITE_ENTITY2 = new Website(123,"example.com",false,"authId", targetingParameter1);

    private static final Website WEBSITE_ENTITY3= new Website(123,"example.com",false,"authId",targetingParameter2);

    public static final List<Website> WEBSITES = Arrays.asList(WEBSITE_ENTITY, WEBSITE_ENTITY2,WEBSITE_ENTITY3);

}
