package rdSeleniumTests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class zRdUtil {
	static String propPath = "resources/config.properties";

	// bypassTutorial
	// Arguments: driver
	public static void bypassTutorial(WebDriver driver, zObjectMap objMap) throws Exception{
		WebDriverWait tutWait = new WebDriverWait(driver, 5);
		tutWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.libUi.tutorial")));
		if (driver.findElement(objMap.getLoc("rd.libUi.tutorial")).isDisplayed()){
			System.out.println("Tutorial window found");
			driver.findElement(objMap.getLoc("rd.libUi.tutClose")).click();
			tutWait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.libUi.tutorial")));
		}
		else{
			System.out.println("Tutorial window not found");
		}
	}
	
	// captureScreenshot
	// captures a screenshot and copies it to the copyFile location
	// look at a program called imagemagick to compare output screenshots against known good ones
	public static void captureScreenshot(WebDriver driver) throws IOException{
		File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		DateTimeFormatter ymdh = DateTimeFormatter.ofPattern("YYYY-MM-dd-HH");
		LocalDateTime datetime = LocalDateTime.now();
		try {
			FileUtils.copyFile(src, new File("/Users/deh/Documents/workspace/rdSeleniumTests/testreports/"+ymdh.format(datetime)+"/"+System.currentTimeMillis()+".png"));
		}
		finally {
			
		}
	}
	
	// checkTitle
	// checks that the page title matches the two passed strings
	public static void checkTitle(WebDriver driver, String alpha, String bravo) {
		System.out.println("Checking if page title contains: " + alpha + " AND " + bravo);
		bravo=bravo.toLowerCase();
		Assert.assertTrue(driver.getTitle().toLowerCase().contains(alpha) && driver.getTitle().toLowerCase().contains(bravo));
	}
	
	// exitDriver
	// exits the driver
	// Arguments:	WebDriver driver (the webdriver to exit)
	public static void exitDriver(WebDriver driver) throws InterruptedException{
		System.out.println("Exiting WebDriver instance");
		Thread.sleep(2000);
		driver.quit();
	}
	
	// openBook
	// Arguments: driver, objectmap
	//			  bookName = the name of the book to open
	public static void openBook(WebDriver driver, zObjectMap objMap, String bookName) throws Exception {
		WebDriverWait libWait = new WebDriverWait(driver, 10);
//		System.out.println("wait for backdrop fade: class modal-backdrop fade");
//		libWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal-backdrop.fade")));
		String bookPath = "//*[contains(text(), '" + bookName + "')]";
		System.out.println("wait for invisibility: rd.reader.load");
		libWait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.load")));
		System.out.println("wait for clickable: bookPath");
		libWait.until(ExpectedConditions.elementToBeClickable(By.xpath(bookPath)));
		System.out.println("click bookPath");
		driver.findElement(By.xpath(bookPath)).click();
		// wait for the book to load, check that the page title matches the book
		libWait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.block")));
		zRdUtil.checkTitle(driver, "readynamic", bookName);
	}
	
	// propLoader
	// loads config.properties file
	// Arguments:	Properties incProp
	public static void propLoader (Properties incProp) {
		System.out.println("Popuating Properties object");
		InputStream input = null;

		try {
			input = new FileInputStream(propPath);
			incProp.load(input);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (input != null) {
				try{
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	// setDriver
	// sets up the WebDriver
	// Arguments: WebDriver driver
	//			  String browser 	(set in testng.xml, e.g. parameter name="browser" value="chrome")
	//			  String driverPath (set in config.properties, pulled in with propLoader)
	//			  Integer impWait	(implicit wait time, set in config.properties, also pulled in with propLoader
	// Returns:	  WebDriver driver
	public static WebDriver setDriver (WebDriver driver, String browser, String driverPath, Integer impWait) throws Exception {
		//chrome driver setup
    	if (browser.equalsIgnoreCase("chrome")) {
        	System.setProperty("webdriver.chrome.driver", driverPath+"chromedriver");
        	ChromeOptions options = new ChromeOptions();
        	options.addArguments("--start-fullscreen");
    		driver = new ChromeDriver(options);
    	}
    	//firefox driver setup
    	else if (browser.equalsIgnoreCase("firefox")) {
    		System.setProperty("webdriver.gecko.driver", driverPath+"geckodriver");
    		driver = new FirefoxDriver();
    		driver.manage().window().maximize();
    	}
    	//driver setup fails
    	else{
    		throw new Exception("error: browser incorrect");
    	}
    	// Set implicit wait time
    	driver.manage().timeouts().implicitlyWait(impWait, TimeUnit.SECONDS);
    	return driver;
	}
	
	// userLogin
	// Arguments: driver, objectmap
	//            rdqaWeb = the web address for the rd server
	//            userEmail (not userName) and the password
	public static void portalLogin(WebDriver driver, zObjectMap objMap, String rdqaWeb, String userEmail, String password) throws Exception{
		driver.get(rdqaWeb + "/portal");
//		zRdUtil.captureScreenshot(driver);
		
		driver.findElement(objMap.getLoc("rd.login.username")).sendKeys(userEmail);
		driver.findElement(objMap.getLoc("rd.login.password")).sendKeys(password);
		driver.findElement(objMap.getLoc("rd.login.submit")).click();
		zRdUtil.checkTitle(driver, "readynamic", "portal");
	}

	// elementExist
	// Arguments
	
	public static void elementExistXpath(WebDriver driver, zObjectMap objMap, String element) {
		
		
	}

//	// clickLinkByHref
//	// loops through a page's elements to find the specified href element
//	public static void clickLinkByHref(WebDriver driver, String href) {
//		List<WebElement> anchors = driver.findElements(By.tagName("a"));
//		Iterator<WebElement> i = anchors.iterator();
//		
//		while(i.hasNext()) {
//			WebElement anchor = i.next();
//			if(anchor.getAttribute("href").contains(href)) {
//				anchor.click();
//				break;
//			}
//		}
//	}
	
	// === not currently used, but I don't want to delete ===
	
//	// fluentWait
//	// causes the driver to wait until a condition is true, and returns the web element.
//	// Can take a predicate (returns boolean only) or a function (returns any object)
//	public static WebElement fluentWait(WebDriver driver, final By locator){
//		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
//				.withTimeout(30, TimeUnit.SECONDS)
//				.pollingEvery(3, TimeUnit.SECONDS)
//				.ignoring(NoSuchElementException.class);
//		
//		WebElement retElement = wait.until((new Function<WebDriver, WebElement>() {
//			public WebElement apply(WebDriver driver) {
//				return driver.findElement(locator);
//			}
//		}));
//		return retElement;
//	}
//	
//	// waitUntilDisplayed
//	// waits until the element is displayed
//	public static void waitUntilDisplayed(WebDriver driver, final WebElement element) {
//		WebDriverWait wait = new WebDriverWait(driver, 20);
//		ExpectedCondition elementDisplayed = new ExpectedCondition<Boolean>() {
//			public Boolean apply(WebDriver arg0) {
//				try{
//					element.isDisplayed();
//					return true;
//				}
//				catch (NoSuchElementException e){
//					return false;
//				}
//				catch (StaleElementReferenceException f){
//					return false;
//				}
//			}
//		};
//		wait.until(elementDisplayed);
////		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//	}
	
}
