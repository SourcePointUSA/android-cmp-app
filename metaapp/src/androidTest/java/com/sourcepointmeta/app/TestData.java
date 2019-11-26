package com.sourcepointmeta.app;

import com.sourcepointmeta.app.database.entity.Property;

import java.util.Arrays;
import java.util.List;

public class TestData {

    private static final Property PROPERTY_ENTITY = new Property(22,2372,"mobile.demo","privacyManagerId",false,false,"authId");
    private static final Property PROPERTY_ENTITY_2 = new Property(808,2372,"AndroidTesting","privacyManagerID",false,false,"authId");

    public static final List<Property> PROPERTIES = Arrays.asList(PROPERTY_ENTITY, PROPERTY_ENTITY_2);

}
