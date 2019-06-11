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

public class rdStuReaderPdfToBitmap {
	// The locators constructed with book names are not in the objectmap
//	String testPdf = "war";								// Book name for the test pdf
//	String testPdfPath = "//*[contains(text(), '" + testPdf + "')]";
	
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userName, userPassword, testPdf;
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
		
		testPdf = testVars.getProperty("aPdfToBitmap");
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Web portal, log in as static user
	@Test (groups = { "baseline", "login"})
	public void userLoginPdf() throws Exception {
		String userEmail = userName;
		zRdUtil.portalLogin(driver, objMap, rdqaWeb, userEmail, userPassword);
		zRdUtil.bypassTutorial(driver, objMap);
	}
	
	// Web portal, open pdf
	@Test (groups = { "baseline", "openBook", "pdf"}, dependsOnMethods={"userLoginPdf"})
	public void openPdf() throws Exception {
		zRdUtil.openBook(driver, objMap, testPdf);
	}
	
	// Web portal, navigate by table of contents chapter
	// Note: chapter toc-cc IDs are +1 on windows - mac starts at 1234530 and windows at 1234531.
	// This will work for Moby and Sign, War chapters are under a heading that needs to be expanded
	@Test (groups = { "baseline", "bookNav", "pdf"}, dependsOnMethods={"openPdf"})
	public void chapterNavPdf() throws Exception {
		WebDriverWait tocWait = new WebDriverWait(driver, expWait);
		
		System.out.println("wait for pdf page to load");
		tocWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.page.visible.loaded"))).isDisplayed();
//		tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
		
		// then iterate through chapters
		ArrayList<String> toc = new ArrayList<String>();
		toc.add("toc-cell-content-c22");
		toc.add("toc-cell-content-c24");
		toc.add("toc-cell-content-c26");
		toc.add("toc-cell-content-c28");
		toc.add("toc-cell-content-c30");
		toc.add("toc-cell-content-c22");
		for (String i : toc){
			System.out.println("click rd.reader.tocToggle");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
			driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
			Thread.sleep(500);
			System.out.print("Expand chapter selection");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(By.id("toc-cell-content-c21")));
			driver.findElement(By.id("toc-cell-content-c21")).click();
			System.out.println("click toc entry: " + i);
			tocWait.until(ExpectedConditions.presenceOfElementLocated(By.id(i)));
			driver.findElement(By.id(i)).click();
			
			System.out.println("wait for pdf page to load");
			tocWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.page.visible.loaded"))).isDisplayed();
//			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.contentFrame"))).getAttribute("style").contains("opacity: 1");
			
			System.out.println("click rd.reader.tocToggle");
			driver.findElement(objMap.getLoc("rd.reader.tocToggle")).click();
			tocWait.until(ExpectedConditions.presenceOfElementLocated(objMap.getLoc("rd.reader.chDrawer")));
			
			Thread.sleep(500);
		}
	}
		
	// Web portal, click next page pdf
	@Test (groups = { "baseline", "bookNav", "pdf"}, dependsOnMethods={"chapterNavPdf"})
	public void nextPagePdf() throws Exception {
		
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		
		for (int i = 0; i < numNextPage; i++) {
			
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
//			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.load")));
			wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.reader.next")));
			Thread.sleep(250);
			
			driver.findElement(objMap.getLoc("rd.reader.next")).click();
		}
	}
	
	// Web portal, click prev page Pdf
	@Test (groups = { "baseline", "bookNav", "pdf"}, dependsOnMethods={"chapterNavPdf"})
	public void prevPagePdf() throws Exception {
		
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		
		for (int i = 0; i < numPrevPage; i++) {
			
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.spin")));
//			wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.reader.load")));
			wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.reader.prev")));
			Thread.sleep(250);
			
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
