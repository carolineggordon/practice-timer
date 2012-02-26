package net.johnpwood.android.standuptimer.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import android.R.string;
import android.test.suitebuilder.TestSuiteBuilder;
import android.util.Log;

public class AllTests extends TestSuite {
	public static final String TAG = "Timer AllTests";
	
    public static Test suite() {
    	Log.i(TAG, "Got to AllTests class");
        return new TestSuiteBuilder(AllTests.class).includeAllPackagesUnderHere().build();
    }
}
