package rdSeleniumTests;

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.util.ArrayList;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class rdStuReaderEpub3 {
	// The locators constructed with book names are not in the objectmap
//	String testEpub3 = "moby";								// Book name for the test epub3
//	String testEpub3Path = "//*[text()='" + testEpub3 + "']";
	
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userName, userPassword, testEpub3;
	Integer numNextPage, numPrevPage, impWait, expWait;
	
	// Webdriver setup
	// This will check the "browser" parameter in the testng.xml file and create the correct webdriver.
	// This will also prep the Properties object with most test variables
	@BeforeClass
	@Parameters({"browser"})
	public void setUp(String browser) throws Exception{
		zRdUtil.propLoader(testVars);
		rdqaWeb = testVars.getProperty("rdqaWeb");
		driverPath = testVars.getProperty("driverPath");
		userName = testVars.getProperty("studentUserName");
		userPassword = testVars.getProperty("studentUserPassword");
		numNextPage = Integer.parseInt(testVars.getProperty("numNextPage"));
		numPrevPage = Integer.parseInt(testVars.getProperty("numPrevPage"));
		impWait = Integer.parseInt(testVars.getProperty("impWait"));
		expWait = Integer.parseInt(testVars.getProperty("expWait"));
		testEpub3 = testVars.getProperty("aEpub3");
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Web portal, log in as static user
	@Test (groups = { "baseline", "login"})
	public void userLoginEpub3() throws Exception {
		String userEmail = userName;
		zRdUtil.portalLogin(driver, objMap, rdqaWeb, userEmail, userPassword);
		zRdUtil.bypassTutorial(driver, objMap);
	}
	
	// Web portal, open epub3
	@Test (groups = { "baseline", "openBook", "epub3"}, dependsOnMethods={"userLoginEpub3"})
	public void openEpub3() throws Exception {
		zRdUtil.openBook(driver, objMap, testEpub3);
	}
	
	// Web portal, navigate by table of contents chapter
	// Note: chapter toc-cc IDs are +1 on windows - mac starts at 1234530 and windows at 1234531.
	// This will work for Moby and Sign
	@Test (groups = { "baseline", "bookNav", "epub3"}, dependsOnMethods={"openEpub3"})
	public void chapterNav() throws Exception {
		WebDriverWait tocWait = new WebDriverWait(driver, expWait);
		
		System.out.println("wait for iframe opacity");
		tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
		
		ArrayList<String> toc = new ArrayList<String>();
		toc.add("toc-cc1234570");
		toc.add("toc-cc1234572");
		toc.add("toc-cc1234574");
		toc.add("toc-cc1234576");
		toc.add("toc-cc1234578");
		toc.add("toc-cc1234570");
		for (String i : toc){
			System.out.println("click rd.reader.tocToggle");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
			driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
			Thread.sleep(500);
			System.out.println("click toc entry: " + i);
			tocWait.until(ExpectedConditions.presenceOfElementLocated(By.id(i)));
			driver.findElement(By.id(i)).click();
			
			System.out.println("wait for iframe");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			
			System.out.println("click rd.reader.tocToggle");
			driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
			
			Thread.sleep(500);
		}
	}
	
	// Web portal, click next page epub3
	@Test (groups = { "baseline", "bookNav", "epub3"}, dependsOnMethods={"chapterNav"})
	public void nextPageEpub3() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		for (int i = 0; i < numNextPage; i++) {
			System.out.println("wait for iframe");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
			wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			Thread.sleep(500);
			
			System.out.println("wait for clickable: rd.reader.next");
			wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.reader.next")));
			
			System.out.println("click rd.reader.next");
			driver.findElement(objMap.getLoc("rd.reader.next")).click();
		}
	}
	
	// Web portal, click prev page epub3
	@Test (groups = { "baseline", "bookNav", "epub3"}, dependsOnMethods={"chapterNav"})
	public void prevPageEpub3() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		for (int i = 0; i < numPrevPage; i++) {
			System.out.println("wait for iframe");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
			wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			Thread.sleep(500);
			
			System.out.println("wait for clickable: rd.reader.prev");
			wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.reader.prev")));

			System.out.println("click rd.reader.prev");
			driver.findElement(objMap.getLoc("rd.reader.prev")).click();
		}
	}
	
	// Webdriver shutdown
	// This will run after the test class, and will exit the webdriver
	@AfterClass
	public void quitDriver() throws InterruptedException{
		zRdUtil.exitDriver(driver);
	}
}
