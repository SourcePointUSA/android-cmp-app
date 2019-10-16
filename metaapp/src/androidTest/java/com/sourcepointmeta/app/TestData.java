package com.sourcepointmeta.app;

import com.sourcepointmeta.app.database.entity.Website;

import java.util.Arrays;
import java.util.List;

public class TestData {

    private static final Website WEBSITE_ENTITY = new Website(22,"mobile.demo",false);
    private static final Website WEBSITE_ENTITY2 = new Website(808,"AndroidTesting",false);

    public static final List<Website> WEBSITES = Arrays.asList(WEBSITE_ENTITY, WEBSITE_ENTITY2);

}
