package com.dff.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestListener implements ITestListener {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String jiraUrl = "https://innovationdays.atlassian.net"; // Example: "https://yourcompany.atlassian.net"
    private final String apiToken = "9f5d1ea6-131d-4ddc-a273-e50872ecd3e4"; // Your Jira API token
    private final String projectKey = "TES"; // Example: "PROJ"

    private static String getTestName(ITestResult result) {
        return result.getMethod().getMethodName();
    }

    private void updateZephyrTestCase(ITestResult result, String status) {
        Method method = result.getMethod().getConstructorOrMethod().getMethod();
        if (method.isAnnotationPresent(ZephyrTestCase.class)) {
            String testCaseKey = method.getAnnotation(ZephyrTestCase.class).value();
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(jiraUrl + "/rest/atm/1.0/testcase/" + testCaseKey + "/testresult"))
                        .header("Authorization", "Bearer " + apiToken)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(
                                "{\"status\":\"" + status + "\", \"projectKey\":\"" + projectKey + "\"}"))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("Test case " + testCaseKey + " updated with status " + status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onFinish(ITestContext iTestContext) {
        //Do tier down operations for ExtentReports reporting!
    }

    public void onTestStart(ITestResult iTestResult) {
    }

    public void onTestSuccess(ITestResult iTestResult) {
        updateZephyrTestCase(iTestResult, "Pass");
    }


    public void onTestFailure(ITestResult iTestResult) {
        updateZephyrTestCase(iTestResult, "Fail");
    }

    public void onTestSkipped(ITestResult iTestResult) {
        updateZephyrTestCase(iTestResult, "Skip");
    }
}