package testessentials.mockito.testng.listener;

import org.testng.ITestResult;

public class InstanceFinder {
    public static Object find(ITestResult testResult) {
        Object instance = FixtureListener.getFixture();
        if (instance == null) {
            instance = testResult.getInstance();
        }
        return instance;
    }
}
