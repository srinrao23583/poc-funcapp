package com.demo.FunctionAppTest;

import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("BrowserFuncApp")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request .");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);
        String flowMessage = name + ", Started, ";
        
        try (Playwright playwright = Playwright.create()) {
			flowMessage = flowMessage + "Created PR, ";
			flowMessage = flowMessage + "Path:" + playwright.chromium().executablePath() + ", ";
            Browser browser = playwright.chromium().launch();
            flowMessage = flowMessage + "Launched chromium, ";
            // Create a new page and navigate to a URL
            Page page = browser.newPage();
            page.navigate("https://www.google.com/");
            flowMessage = flowMessage + "Opened Page ";	
            browser.close();
        } catch(Exception e) {
        	flowMessage = flowMessage + "Exception in playright, " + e.getMessage();	
        }
        
        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello 3, " + flowMessage).build();
        }
    }
}
