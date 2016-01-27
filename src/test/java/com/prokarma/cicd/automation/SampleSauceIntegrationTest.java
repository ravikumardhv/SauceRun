package com.prokarma.cicd.automation;

/**
 * @author Ross Rowe
 */

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.prokarma.cicd.testng.listener.TestListener;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;


/**
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser combinations.
 *
 * @author Ross Rowe
 */
@Listeners({SauceOnDemandTestListener.class,TestListener.class})
public class SampleSauceIntegrationTest implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

	
    /**
     * Constructs a {@link com.saucelabs.common.SauceOnDemandAuthentication} instance using the supplied user name/access key.  To use the authentication
     * supplied by environment variables or from an external file, use the no-arg {@link com.saucelabs.common.SauceOnDemandAuthentication} constructor.
     */
    public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication("prokarma_1", "6c9e437c-cc92-4b1d-8084-51dd369a7051");

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{
                new Object[]{"internet explorer", "11", "Windows 8.1"},
                new Object[]{"safari", "6", "OSX 10.8"},
        };
    }

    /**
     * /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String browser, String version, String os) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
        if (version != null) {
            capabilities.setCapability(CapabilityType.VERSION, version);
        }
        capabilities.setCapability(CapabilityType.PLATFORM, os);
        capabilities.setCapability("name", "Sauce Sample Test");
        webDriver.set(new RemoteWebDriver(
                new URL("http://" + authentication.getUsername() + ":" + authentication.getAccessKey() + "@ondemand.saucelabs.com:80/wd/hub"),
                capabilities));
        sessionId.set(((RemoteWebDriver) getWebDriver()).getSessionId().toString());
        return webDriver.get();
    }

    /**
     * Runs a simple test verifying the title of the amazon.com homepage.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @throws Exception if an error occurs during the running of the test
     */
    @Test(dataProvider = "hardCodedBrowsers")
    public void checkPageTitle(String browser, String version, String os,ITestContext context) throws Exception {
        WebDriver driver = createDriver(browser, version, os);
        System.out.println("SauceOnDemandSessionID="+getSessionId() +"job-name="+context.getName());
        driver.get("http://192.168.13.19:8081/CloudSoleAngular");
        assertEquals(driver.getTitle(), "CloudSole Angular");
        driver.quit();
    }
    
    
    /**
     * Runs a simple test verifying the title of the add a task in todo app.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @throws Exception if an error occurs during the running of the test
     */
    @Test(dataProvider = "hardCodedBrowsers")
    public void checkCanUserAddATask(String browser, String version, String os,ITestContext context) throws Exception {
        WebDriver driver = createDriver(browser, version, os);
        System.out.println("SauceOnDemandSessionID="+getSessionId() +"job-name="+context.getName());
        driver.get("http://192.168.13.19:8081/CloudSoleAngular");
        assertEquals(driver.getTitle(), "CloudSole Angular");
        WebElement todoInputText = driver.findElement(By.xpath(".//*[@id='wrapper']/div/div/div[2]/div[2]/div[2]/form/div/input"));
        WebElement addTodoButton= driver.findElement(By.xpath(".//*[@id='wrapper']/div/div/div[2]/div[2]/div[2]/button[1]"));
        
        assertEquals(addTodoButton.isEnabled(), false);
        System.out.println("Add to button is enable "+addTodoButton.isEnabled());
        
        String enteredText = "Test me";
        todoInputText.clear();
		todoInputText.sendKeys(enteredText);
		System.out.println("Add to button is enable "+addTodoButton.isEnabled());
		assertEquals(addTodoButton.isEnabled(), true);
        addTodoButton.click();
        
        WebElement enteredElementData = driver.findElement(By.cssSelector(".table>tbody>tr:last-child>td:last-child"));
        String availabeText = enteredElementData.getText();
        assertEquals(availabeText, enteredText);
        driver.quit();
    }
    
    

    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        System.out.println("WebDriver" + webDriver.get());
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     *
     * @return the {@link SauceOnDemandAuthentication} instance containing the Sauce username/access key
     */
    @Override
    public SauceOnDemandAuthentication getAuthentication() {
        return authentication;
    }
}

