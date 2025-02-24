package com.mongodb.jdbc.smoke.test;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Flow;

/**
 * Main class for the smoke test jar.
 * This let us build the driver and test jar on a specific system and test it in multiple platform.
 */
public class Main {

    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        // Specify the packages or classes containing your tests
                        org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage("com.mongodb.jdbc.smoketest")
                )
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.execute(request, listener);

        try (PrintWriter writer = new PrintWriter(System.out)) {
            listener.getSummary().printTo(writer);
           List<TestExecutionSummary.Failure> failures= listener.getSummary().getFailures();
           for (TestExecutionSummary.Failure failure : failures) {
            System.out.println(failure.getException().getMessage());
           }
        }
    }
}
