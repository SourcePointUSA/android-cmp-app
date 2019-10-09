package com.sourcepointmeta.app.utils;


import com.sourcepointmeta.app.AppExecutors;

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
