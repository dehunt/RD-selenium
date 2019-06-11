package rdSeleniumTests;

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class rdPublisherBookManagement {
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userPub, userPubPassword, testEpub2Path, testEpub3Path, testPdfPath;
	String testEpub2, testEpub3, testPdf;
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
		userPub = testVars.getProperty("publisherUserName");
		userPubPassword = testVars.getProperty("publisherUserPassword");
		testEpub2Path = testVars.getProperty("testEpub2Path");
		testEpub3Path = testVars.getProperty("testEpub3Path");
		testPdfPath = testVars.getProperty("testPdfPath");
		impWait = Integer.parseInt(testVars.getProperty("impWait"));
		expWait = Integer.parseInt(testVars.getProperty("expWait"));
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Web portal, log in as static publisher
	@Test (groups = { "baseline", "login"})
	public void publisherLogin() throws Exception {
		String userPubEmail = userPub;// + "@datalogics.com";
		zRdUtil.portalLogin(driver, objMap, rdqaWeb, userPubEmail, userPubPassword);
		zRdUtil.bypassTutorial(driver, objMap);
	}

	// Web portal, upload a epub 2
	@Test (groups = { "baseline", "uploadBook", "publisher"}, dependsOnMethods={"publisherLogin"})
	public void uploadEpub2() throws Exception {
		SimpleDateFormat simpDate = new SimpleDateFormat("MMdd-HHmmss");
		Date dateNow = new Date();
		testEpub2 = "sign_" + simpDate.format(dateNow);
		bookUpload(testEpub2Path, testEpub2, "Arthur Conan Doyle", "The guy in the dining room with a large metal candlestick");
	}
	
	// Web portal, upload a epub 3
	@Test (groups = { "baseline", "uploadBook", "publisher"}, dependsOnMethods={"publisherLogin"})
	public void uploadEpub3() throws Exception {
		SimpleDateFormat simpDate = new SimpleDateFormat("MMdd-HHmmss");
		Date dateNow = new Date();
		testEpub3 = "moby_" + simpDate.format(dateNow);
		bookUpload(testEpub3Path, testEpub3, "Herman Melville", "Some guy that was really angry with some whale.");
	}
	
	// Web portal, upload a pdf
	@Test (groups = { "baseline", "uploadBook", "publisher"}, dependsOnMethods={"publisherLogin"})
	public void uploadPdf() throws Exception {
		SimpleDateFormat simpDate = new SimpleDateFormat("MMdd-HHmmss");
		Date dateNow = new Date();
		testPdf = "daisy_" + simpDate.format(dateNow);
		bookUpload(testPdfPath, testPdf, "Henry James", "Something with a victorian looking woman on the cover");
	}
	
	// Web portal, edit the epub 2
	@Test (groups = { "baseline", "editBook", "publisher"}, dependsOnMethods={"uploadEpub2"})
	public void editEpub2() throws Exception {
		testEpub2 = bookEdit(testEpub2, "-EDITED");
	}
	
	// Web portal, edit the epub 3
	@Test (groups = { "baseline", "editBook", "publisher"}, dependsOnMethods={"uploadEpub3"})
	public void editEpub3() throws Exception {
		testEpub3 = bookEdit(testEpub3, "-EDITED");
	}
	
	// Web portal, edit the pdf
	@Test (groups = { "baseline", "editBook", "publisher"}, dependsOnMethods={"uploadPdf"})
	public void editPdf() throws Exception {
		testPdf = bookEdit(testPdf, "-EDITED");
	}
	
	// Web portal, delete the epub 2
	@Test (groups = { "baseline", "deleteBook", "publisher"}, dependsOnMethods={"editEpub2"})
	public void deleteEpub2() throws Exception {
		bookDelete(testEpub2);
	}
	
	// Web portal, delete the epub 3
	@Test (groups = { "baseline", "deleteBook", "publisher"}, dependsOnMethods={"editEpub3"})
	public void deleteEpub3() throws Exception {
		bookDelete(testEpub3);
	}
	
	// Web portal, delete the pdf
	@Test (groups = { "baseline", "deleteBook", "publisher"}, dependsOnMethods={"editPdf"})
	public void deletePdf() throws Exception {
		bookDelete(testPdf);
	}
	
	// Webdriver shutdown
	// This will run after the test class, and will exit the webdriver
	@AfterClass
	public void quitDriver() throws InterruptedException{
		zRdUtil.exitDriver(driver);
	}
	
	// bookUpload
	// Arguments: 	String path			file path
	//				String bookname		name of book file and what to use for book name field
	//				String author		what to set for author field
	//				String desc			what to set for book description field
	public void bookUpload(String path, String bookName, String author, String desc) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.libUi.loading")));
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.libUi.bookUpload")));
		
		// click book upload link
		driver.findElement(objMap.getLoc("rd.libUi.bookUpload")).click();
		
		// select file to upload
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.bookContent")));
		WebElement contentSection = driver.findElement(objMap.getLoc("rd.upload.bookContent"));
//		contentSection.findElement(By.cssSelector(".file-chooser-input")).sendKeys(path);
		contentSection.findElement(objMap.getLoc("rd.upload.fileSelector")).sendKeys(path);

		
		// fill out book info
		// Because all the change buttons use the same css selector, first find the book data section and store it in a WebElement
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.bookDataSection")));
		WebElement bookData = driver.findElement(objMap.getLoc("rd.upload.bookDataSection"));
		bookData.findElement(objMap.getLoc("rd.upload.changeButton")).click();
		
		driver.findElement(objMap.getLoc("rd.upload.bookName")).sendKeys(bookName);
		driver.findElement(objMap.getLoc("rd.upload.bookAuthor")).sendKeys(author);
		driver.findElement(objMap.getLoc("rd.upload.bookDesc")).sendKeys(desc);
		
		// leave book permission as restricted
		
		// leave comment visibility as creator and owner
		
		// if not already ticked, then tick the content rights checkbox
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.acceptUsage")));
		if (!driver.findElement(objMap.getLoc("rd.upload.acceptUsage")).isSelected()) {
			driver.findElement(objMap.getLoc("rd.upload.acceptUsage")).click();
		}
		
		// click create (it'll take a bit to finish)
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.submit")));
		WebElement element = driver.findElement(objMap.getLoc("rd.upload.submit"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		Thread.sleep(500);
		driver.findElement(objMap.getLoc("rd.upload.submit")).click();
			
		// return to My Bookshelf. Need to wait for both the popup window to dismiss AND for the page to load
//		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".btn.btn-primary.confirm-button")));
		wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.upload.submit")));
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.bookDetails.backButton")));
		Thread.sleep(500);
		driver.findElement(objMap.getLoc("rd.libUi.myBookshelf")).click();
	}
	
	// bookEdit
	// Arguments:	String bookName		name of book to search for
	//				String toAdd		the string of text to add to fields
	// Returns:		String				this is the updated book name, now that it was edited
	// NOTE: the xpath to locate the book details button has to be created on the fly, and so isn't in the objectmap
	public String bookEdit(String bookName, String toAdd) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.libUi.loading")));
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.libUi.bookUpload")));
		Thread.sleep(2000);
		
		// find the book and click the details icon
//		if (driver.findElements(By.xpath("//p[text()='"+bookName+"']/../..//*[@id='details-btn']")).size() != 0) {
//			driver.findElement(By.xpath("//p[text()='"+bookName+"']/../..//*[@id='details-btn']")).click();
//		}
//		else {
//			driver.findElement(objMap.getLoc("rd.libUi.lastPage")).click();
//			driver.findElement(By.xpath("//p[text()='"+bookName+"']/../..//*[@id='details-btn']")).click();
//		}
		driver.findElement(By.xpath("//p[text()='"+bookName+"']/../..//*[@id='details-btn']")).click();
		
		// open the book settings
		wait.until(ExpectedConditions.visibilityOfElementLocated(objMap.getLoc("rd.bookDetails.bookContent")));
		driver.findElement(objMap.getLoc("rd.bookDetails.bookSettings")).click();
		
		// add the received string to the fields
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.bookDataSection")));
		WebElement bookData = driver.findElement(objMap.getLoc("rd.upload.bookDataSection"));
		bookData.findElement(objMap.getLoc("rd.upload.changeButton")).click();
		driver.findElement(objMap.getLoc("rd.upload.bookName")).sendKeys(toAdd);
		driver.findElement(objMap.getLoc("rd.upload.bookAuthor")).sendKeys(toAdd);
		driver.findElement(objMap.getLoc("rd.upload.bookDesc")).sendKeys(toAdd);
		
		// if not already ticked, then tick the content rights checkbox
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.acceptUsage")));
		if (!driver.findElement(objMap.getLoc("rd.upload.acceptUsage")).isSelected()) {
			driver.findElement(objMap.getLoc("rd.upload.acceptUsage")).click();
		}
		
		// click save changes
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.submit")));
		WebElement element = driver.findElement(objMap.getLoc("rd.upload.submit"));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
		Thread.sleep(500);
		driver.findElement(objMap.getLoc("rd.upload.submit")).click();
		
		// return to bookshelf
		wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.upload.submit")));
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.libUi.myBookshelf")));
		Thread.sleep(500);
		driver.findElement(objMap.getLoc("rd.libUi.myBookshelf")).click();
		bookName = bookName+toAdd;
		return bookName;
	}
	
	// bookDelete
	// Arguments:	String bookName		what book to search for to delete
	// NOTE: the xpath to locate the book details button has to be created on the fly, and so isn't in the objectmap
	public void bookDelete(String bookName) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(objMap.getLoc("rd.libUi.loading")));
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.libUi.bookUpload")));
		
		// find the book and click the details icon
		driver.findElement(By.xpath("//p[text()='"+bookName+"']/../..//*[@id='details-btn']")).click();
		
		// open the book settings
		wait.until(ExpectedConditions.visibilityOfElementLocated(objMap.getLoc("rd.bookDetails.bookContent")));
		driver.findElement(objMap.getLoc("rd.bookDetails.bookSettings")).click();
		
		// click delete, then confirm
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.bookContent")));
		driver.findElement(objMap.getLoc("rd.upload.deleteButton")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(objMap.getLoc("rd.upload.fadeIn")));
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.upload.deleteConfirmButton")));
		driver.findElement(objMap.getLoc("rd.upload.deleteConfirmButton")).click();
	}
}
