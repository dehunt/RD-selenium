package rdSeleniumTests;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class rdPublisherContacts {
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userPub, userPubPassword, stuConAlpha, stuConBravo;
	Integer impWait, expWait;
	
	// Webdriver setup
	// This will check the "browser" parameter in the testng.xml file and create the correct webdriver.
	// This will also prep the Properties object with most test variables
	// Finally it will check that users for the test exist, and create them (if required)
	@BeforeClass
	@Parameters({"browser"})
	public void setUp(String browser) throws Exception{
		zRdUtil.propLoader(testVars);
		rdqaWeb = testVars.getProperty("rdqaWeb");
		driverPath = testVars.getProperty("driverPath");
		userPub = testVars.getProperty("publisherUserName");
		userPubPassword = testVars.getProperty("publisherUserPassword");
		impWait = Integer.parseInt(testVars.getProperty("impWait"));
		expWait = Integer.parseInt(testVars.getProperty("expWait"));
		stuConAlpha = testVars.getProperty("studentContactAlpha");
		stuConBravo = testVars.getProperty("studentContactBravo");
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Web portal, log in as static publisher
	@Test (groups = { "baseline", "login"})
	public void publisherLogin() throws Exception {
		String userEmail = userPub;// + "@datalogics.com";
		zRdUtil.portalLogin(driver, objMap, rdqaWeb, userEmail, userPubPassword);
		zRdUtil.bypassTutorial(driver, objMap);
	}
	
	// Web portal, publisher add a student as a contact
	@Test (groups = { "baseline", "contacts", "publisher"}, dependsOnMethods={"publisherLogin"})
	public void addFirstStudentContact() throws Exception {
		addContact(stuConAlpha);//+"@datalogics.com");
	}
	
	// Web portal, publisher add a student as a contact
	@Test (groups = { "baseline", "contacts", "publisher"}, dependsOnMethods={"publisherLogin"})
	public void addSecondStudentContact() throws Exception {
		addContact(stuConBravo);//+"@datalogics.com");
	}
	
	// Web portal, publisher remove student as contact
	@Test (groups = { "baseline", "contacts", "publisher"}, dependsOnMethods={"addFirstStudentContact"})
	public void removeFirstStudentContact() throws Exception {
		removeContact(stuConAlpha);//+"@datalogics.com");
	}
	
	// Web portal, publisher remove student as contact
	@Test (groups = { "baseline", "contacts", "publisher"}, dependsOnMethods={"addSecondStudentContact"})
	public void removeSecondStudentContact() throws Exception {
		removeContact(stuConBravo);//+"@datalogics.com");
	}
	
	// Webdriver shutdown
	// This will run after the test class, and will exit the webdriver
	@AfterClass
	public void quitDriver() throws InterruptedException{
		zRdUtil.exitDriver(driver);
	}
	
	// addContact
	// Arguments:	String email
	// receives the email string and adds it as a contact
	public void addContact(String email) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.people.tab")));
		driver.findElement(objMap.getLoc("rd.people.tab")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.people.new")));
		WebElement contactSection = driver.findElement(objMap.getLoc("rd.people.contacts"));
		contactSection.findElement(objMap.getLoc("rd.people.new")).click();
		wait.until((ExpectedConditions.visibilityOfElementLocated(objMap.getLoc("rd.people.email"))));
		driver.findElement(objMap.getLoc("rd.people.email")).sendKeys(email);
		driver.findElement(objMap.getLoc("rd.upload.submit")).click();
		Thread.sleep(500);
		driver.findElement(objMap.getLoc("rd.libUi.logo")).click();
	}
	
	// removeContact
	// Arguments:	String email
	// receives the email string, then locates and removes that contact
	// NOTE: This xpath must be built on the fly with the provided email, and so isn't included in the objectmap
	public void removeContact(String email) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.people.tab")));
		driver.findElement(objMap.getLoc("rd.people.tab")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.people.new")));
		if (!driver.findElement(By.xpath("//div[text()='"+email+"']/../../td/input[@type='checkbox']")).isSelected()) {
			driver.findElement(By.xpath("//div[text()='"+email+"']/../../td/input[@type='checkbox']")).click();
		}
//		driver.findElement(By.className("delete-button")).click();
		driver.findElement(objMap.getLoc("rd.people.deleteButton")).click();
//		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn.btn-danger.confirm-button")));
//		driver.findElement(By.cssSelector(".btn.btn-danger.confirm-button")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.people.confirmDelete")));
		driver.findElement(objMap.getLoc("rd.people.confirmDelete")).click();
		Thread.sleep(500);
		driver.findElement(objMap.getLoc("rd.libUi.logo")).click();
	}
}
