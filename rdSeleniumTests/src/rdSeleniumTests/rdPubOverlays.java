package rdSeleniumTests;

import java.sql.Time;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.omg.CORBA.TIMEOUT;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class rdPubOverlays {
	String testChapter = "toc-cell-content-cc1234532";
	
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userName, userPassword, testEpub2;
	Integer impWait, expWait;
	
	// Webdriver setup
	// This will check the "browser" parameter in the testng.xml file and create the correct webdriver.
	// This will also prep the Properties object with most test variables
	@BeforeClass
	@Parameters({"browser"})
	public void setUp(String browser) throws Exception{
		zRdUtil.propLoader(testVars);
		rdqaWeb = testVars.getProperty("rdqaWeb");
		driverPath = testVars.getProperty("driverPath");
		userName = testVars.getProperty("publisherUserName");
		userPassword = testVars.getProperty("publisherUserPassword");
		impWait = Integer.parseInt(testVars.getProperty("impWait"));
		expWait = Integer.parseInt(testVars.getProperty("expWait"));
		testEpub2 = testVars.getProperty("pubEpub2");
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Web portal, log in as static user
	@Test (groups = { "baseline", "login"})
	public void publisherLogin() throws Exception {
		String userEmail = userName + "@datalogics.com";
		zRdUtil.portalLogin(driver, objMap, rdqaWeb, userEmail, userPassword);
		zRdUtil.bypassTutorial(driver, objMap);
	}
	
	@Test (groups = {"baseline", "overlay"}, dependsOnMethods = {"publisherLogin"})
	public void overlaysOpenEpub2() throws Exception {
		zRdUtil.openBook(driver, objMap, testEpub2);
	}
	
	@Test (groups = {"baseline", "overlay"}, dependsOnMethods = {"overlaysOpenEpub2"})
	public void overlaysNavigateToChapter() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		System.out.println("wait for iframe opacity");
		wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
		System.out.println("click rd.reader.tocToggle");
		wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
		driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
		Thread.sleep(250);
		System.out.println("click toc entry: " + testChapter);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id(testChapter)));
		driver.findElement(By.id(testChapter)).click();
		
		System.out.println("wait for iframe");
		wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
		wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
		
		System.out.println("click rd.reader.tocToggle");
		driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
		Thread.sleep(250);
	}
	
	@Test (groups = {"baseline", "overlay"}, dependsOnMethods = {"overlaysNavigateToChapter"})
	public void overlaysCreateTextOverlay() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		
		// first switch to the Iframe that contains the #document content
		// returning back is done with WebDriver.SwitchTo().DefaultContent()
		driver.switchTo().frame(driver.findElement(objMap.getLoc("rd.reader.contentFrame")));
		
		WebElement element = driver.findElement(By.xpath("//*[contains(text(),'Miss Morstan entered the room with a firm step')]"));

		// This ~should~ work to click and drag, but doesn't. It seems to be a known bug in chromedriver.
		// 		See also: https://github.com/SeleniumHQ/selenium/issues/3269
//		Actions builder = new Actions(driver);
//		Action action = builder.moveToElement(element, 0, 0)
//				.clickAndHold()
//				.moveToElement(element, 10, 10)
//				.release()
//				.build();
//		action.perform();
		
		// simple double click does work at least. This can be used to highlight a word.
//		action.doubleClick(element).perform();
//		action.moveToElement(element, 0, 0).doubleClick().build().perform();		

	}
	
	// Webdriver shutdown
	// This will run after the test class, and will exit the webdriver
	@AfterClass
	public void quitDriver() throws InterruptedException{
		zRdUtil.exitDriver(driver);
	}
}
