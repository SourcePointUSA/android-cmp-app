package com.sourcepoint.cmplibrary;

/**
 * @link https://github.com/SourcePointUSA/android-cmp-app/issues/25
 */
class ConsentLibNoOP extends ConsentLib {
    ConsentLibNoOP(ConsentLibBuilder builder) throws ConsentLibException.BuildException {
        super(builder);
    }

    @Override
    public void run() { /* No op */ }
}
