package com.sourcepointmeta.app;

import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.database.entity.TargetingParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticTestData {

    private static TargetingParam targetingParam = new TargetingParam("MyPrivacyManager","false");
    private  static TargetingParam targetingParam1 = new TargetingParam( "CMP","false");
    private static  List<TargetingParam> targetingParameter = Arrays.asList(targetingParam, targetingParam1);

    private static final Property PROPERTY_ENTITY = new Property(22,2372,"mobile.demo","privacyManagerID",false,false ,"authId",targetingParameter);

    private static TargetingParam targetingParam2 = new TargetingParam("MyPrivacyManager","true");
    private  static TargetingParam targetingParam3 = new TargetingParam( "CMP","true");
    private static  List<TargetingParam> targetingParameter1 = Arrays.asList(targetingParam2, targetingParam3);

    private static  List<TargetingParam> targetingParameter2 = new ArrayList<>();

    private static final Property PROPERTY_ENTITY_2 = new Property(123,2331,"example.com","privacyManagerID",false,true,"authId", targetingParameter1);

    private static final Property PROPERTY_ENTITY_3 = new Property(123,2331,"example.com","privacyManagerID",false,true,"authId",targetingParameter2);

    public static final List<Property> PROPERTIES = Arrays.asList(PROPERTY_ENTITY, PROPERTY_ENTITY_2, PROPERTY_ENTITY_3);

}
