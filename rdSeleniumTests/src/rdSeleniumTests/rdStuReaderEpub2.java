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

public class rdStuReaderEpub2 {
	// The locators constructed with book names are not in the objectmap
//	String testEpub2 = "sign";								// Book name for the test epub2
//	String testEpub2Path = "//*[contains(text(), '" + testEpub2 + "')]";
	
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userName, userPassword, testEpub2, testEpub2Path;
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
		testEpub2 = testVars.getProperty("aEpub2");
		testEpub2Path = "//*[contains(text(), '" + testEpub2 + "')]";
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Web portal, log in as static user
	@Test (groups = { "baseline", "login"})
	public void userLoginEpub2() throws Exception {
		String userEmail = userName;// + "@datalogics.com";
		zRdUtil.portalLogin(driver, objMap, rdqaWeb, userEmail, userPassword);
		zRdUtil.bypassTutorial(driver, objMap);
	}
	
	// Web portal, open epub2
	@Test (groups = { "baseline", "openBook", "epub2"}, dependsOnMethods={"userLoginEpub2"})
	public void openEpub2() throws Exception {
		zRdUtil.openBook(driver, objMap, testEpub2);
	}
	
	// Web portal, navigate by table of contents chapter
	// Note: chapter toc-cc IDs are +1 on windows - mac starts at 1234530 and windows at 1234531.
	// This will work for Moby and Sign
	@Test (groups = { "baseline", "bookNav", "epub2"}, dependsOnMethods={"openEpub2"})
	public void chapterNav() throws Exception {
		WebDriverWait tocWait = new WebDriverWait(driver, expWait);
		
		System.out.println("wait for iframe opacity");
		tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
		
		ArrayList<String> toc = new ArrayList<String>();
		toc.add("toc-cc1234548");
		toc.add("toc-cc1234550");
		toc.add("toc-cc1234552");
		toc.add("toc-cc1234554");
		toc.add("toc-cc1234556");
		toc.add("toc-cc1234548");
		for (String i : toc){
			System.out.println("click rd.reader.tocToggle");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
			driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
			Thread.sleep(250);
			System.out.println("click toc entry: " + i);
			tocWait.until(ExpectedConditions.presenceOfElementLocated(By.id(i)));
			driver.findElement(By.id(i)).click();
			
			System.out.println("wait for iframe");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			tocWait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
			
			System.out.println("click rd.reader.tocToggle");
			driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
			
			Thread.sleep(250);
		}
	}
	
	// Web portal, click next page epub2
	@Test (groups = { "baseline", "bookNav", "epub2"}, dependsOnMethods={"chapterNav"})
	public void nextPageEpub3() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		for (int i = 0; i < numNextPage; i++) {
			System.out.println("wait for iframe");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
			wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			Thread.sleep(250);
			
			System.out.println("wait for clickable: rd.reader.next");
			wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.reader.next")));
			
			System.out.println("click rd.reader.next");
			driver.findElement(objMap.getLoc("rd.reader.next")).click();
		}
	}
	
	// Web portal, click prev page epub2
	@Test (groups = { "baseline", "bookNav", "epub2"}, dependsOnMethods={"chapterNav"})
	public void prevPageEpub3() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		for (int i = 0; i < numPrevPage; i++) {
			System.out.println("wait for iframe");
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
			wait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			Thread.sleep(250);
			
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