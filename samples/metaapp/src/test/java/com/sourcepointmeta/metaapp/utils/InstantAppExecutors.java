package com.sourcepointmeta.metaapp.utils;


import com.sourcepointmeta.metaapp.AppExecutors;

import java.util.concurrent.Executor;

public class InstantAppExecutors extends AppExecutors {

    private static final Executor instant = new Executor() {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    };

    public InstantAppExecutors() {
        super(instant, instant, instant);
    }
}
