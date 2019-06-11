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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class rdAdminConsole {
	String userPubTest;									// test publisher that will be created this run
	String userPubTestEmail;							// test publisher's email
	String userPubTestPassword;							// test publisher's password
	String userPubTestId;								// this is the RD ID number for the generated test publisher
	
	String userStuTest;									// test student that will be created this run
	String userStuTestEmail;							// test student's email
	String userStuTestPassword;							// test student's password
	String userStuTestId;								// this is the RD ID number for the generated test student
	
	String userPayTest;									// test payer that will be created this run
	String userPayTestEmail;							// test payer's email
	String userPayTestPassword;							// test payer's password
	String userPayTestId;								// this is the RD ID number for the generated test payer
	
	String userAdminTest;								// test admin that will be created this run
	String userAdminTestEmail;							// test admin's email
	String userAdminTestPassword;						// test admin's password
	String userAdminTestId;								// this is the RD ID number for the generated test admin
	
	WebDriver driver;
	zObjectMap objMap = new zObjectMap();
	Properties testVars = new Properties();
	String rdqaWeb, driverPath, userAdmin, userAdminPassword;
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
		userAdmin = testVars.getProperty("adminUserLogin");
		userAdminPassword = testVars.getProperty("adminUserPassword");
		impWait = Integer.parseInt(testVars.getProperty("impWait"));
		expWait = Integer.parseInt(testVars.getProperty("expWait"));
		
		driver = zRdUtil.setDriver(driver, browser, driverPath, impWait);
	}
	
	// Admin console, log in as admin
	@Test (groups = { "baseline", "login"})
	public void adminLogin() throws Exception {
		driver.get(rdqaWeb + "/admin");
//		zRdUtil.captureScreenshot(driver);
		
		driver.findElement(objMap.getLoc("rd.login.username")).sendKeys(userAdmin);
		driver.findElement(objMap.getLoc("rd.login.password")).sendKeys(userAdminPassword);
		driver.findElement(objMap.getLoc("rd.login.submit")).click();
		zRdUtil.checkTitle(driver, "readynamic", "dashboard");
	}
	
	// Admin console, create publisher user
	// NOTE: Confirming the user email requires a link built with the user ID - this xpath IS NOT in the objectmap table
	@Test (groups = { "baseline", "createUser", "publisher"}, dependsOnMethods={"adminLogin"})
	public void adminCreateUserPub() throws Exception {
		// prep test username - it's just qaPub + a timestamp
		SimpleDateFormat simpDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now = new Date();
		
		// test user and password
		userPubTest = "qaPub_" + simpDate.format(now);
		userPubTestEmail = userPubTest + "@datalogics.com";
		userPubTestPassword = "password";
		
		userPubTestId = createUser("qapub", userPubTest, userPubTestEmail, userPubTestPassword, "publisher");//, "student");
	}
	
	// Admin console, Edit publisher user
	// NOTE: Requires the previously created publisher user
	@Test (groups = { "baseline", "editUser", "publisher"}, dependsOnMethods={"adminCreateUserPub"})
	public void adminEditUserPub() throws Exception {
		editUser(userPubTestId);
	}
	
	// Admin console, delete publisher user
	// NOTE: Requires the previously created publisher user
	@Test (groups = { "baseline", "deleteUser", "publisher"}, dependsOnMethods={"adminEditUserPub"})
	public void adminDeleteUserPub() throws Exception {
		deleteUser(userPubTestId);
	}
	
	// Admin console, create student user
	// NOTE: Confirming the user email requires a link built with the user ID - this xpath IS NOT in the objectmap table
	@Test (groups = { "baseline", "createUser", "student"}, dependsOnMethods={"adminLogin"})
	public void adminCreateUserStu() throws Exception {
		// prep test username - it's just qaStu + a timestamp
		SimpleDateFormat simpDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now = new Date();
		
		// test user and password
		userStuTest = "qaStu_" + simpDate.format(now);
		userStuTestEmail = userStuTest + "@datalogics.com";
		userStuTestPassword = "password";
		userStuTestId = createUser("qastu", userStuTest, userStuTestEmail, userStuTestPassword, "student");//, "none");
	}
	
	// Admin console, Edit student user
	// NOTE: Requires the previously created student user
	@Test (groups = { "baseline", "editUser", "student"}, dependsOnMethods={"adminCreateUserStu"})
	public void adminEditUserStu() throws Exception {
		editUser(userStuTestId);
	}
	
	// Admin console, delete student user
	// NOTE: Requires the previously created student user
	@Test (groups = { "baseline", "deleteUser", "student"}, dependsOnMethods={"adminEditUserStu"})
	public void adminDeleteUserStu() throws Exception {
		deleteUser(userStuTestId);
	}
	
//	// Admin console, create payer user
//	// NOTE: Confirming the user email requires a link built with the user ID - this xpath IS NOT in the objectmap table
//	@Test (groups = { "baseline", "createUser", "payer"}, dependsOnMethods={"adminLogin"})
//	public void adminCreateUserPay() throws Exception {
//		// prep test username - it's just qaPayer + a timestamp
//		SimpleDateFormat simpDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
//		Date now = new Date();
//		
//		// test user and password
//		userPayTest = "qaPayer_" + simpDate.format(now);
//		userPayTestEmail = userPayTest + "@datalogics.com";
//		userPayTestPassword = "password";
//		userPayTestId = createUser("qapay", userPayTest, userPayTestEmail, userPayTestPassword, "payer");//, "none");
//	}
//	
//	// Admin console, Edit payer user
//	// NOTE: Requires the previously created payer user
//	@Test (groups = { "baseline", "editUser", "payer"}, dependsOnMethods={"adminCreateUserPay"})
//	public void adminEditUserPay() throws Exception {
//		editUser(userPayTestId);
//	}
//	
//	// Admin console, delete payer user
//	// NOTE: Requires the previously created payer user
//	@Test (groups = { "baseline", "deleteUser", "payer"}, dependsOnMethods={"adminEditUserPay"})
//	public void adminDeleteUserPay() throws Exception {
//		deleteUser(userPayTestId);
//	}
	
	// Admin console, create admin user
	// NOTE: Confirming the user email requires a link built with the user ID - this xpath IS NOT in the objectmap table
	@Test (groups = { "baseline", "createUser", "admin"}, dependsOnMethods={"adminLogin"})
	public void adminCreateUserAdmin() throws Exception {
		// prep test username - it's just qaAdmin + a timestamp
		SimpleDateFormat simpDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date now = new Date();
		
		// test user and password
		userAdminTest = "qaAdmin_" + simpDate.format(now);
		userAdminTestEmail = userAdminTest + "@datalogics.com";
		userAdminTestPassword = "password";
		userAdminTestId = createUser("admin", userAdminTest, userAdminTestEmail, userAdminTestPassword, "admin");//, "none");
	}
	
	// Admin console, Edit admin user
	// NOTE: Requires the previously created admin user
	@Test (groups = { "baseline", "editUser", "admin"}, dependsOnMethods={"adminCreateUserAdmin"})
	public void adminEditUserAdmin() throws Exception {
		editUser(userAdminTestId);
	}
	
	// Admin console, delete admin user
	// NOTE: Requires the previously created admin user
	@Test (groups = { "baseline", "deleteUser", "admin"}, dependsOnMethods={"adminEditUserAdmin"})
	public void adminDeleteUserAdmin() throws Exception {
		deleteUser(userAdminTestId);
	}
	
	// Webdriver shutdown
	// This will run after the test class, and will exit the webdriver
	@AfterClass
	public void quitDriver() throws InterruptedException{
		zRdUtil.exitDriver(driver);
	}
	
	// createUser
	// Arguments: String qaType (Stu, Pub, Pay, Admin)
	//            String userName, String userEmail, String userPassword
	//            String roleA (REQUIRED), String roleB (REQUIRED - set to "none" if no second role)
	// returns:   String userId (this ID must be set to user*TestId by the test that calls this method
	public String createUser(String qaType, String userName, String userEmail, String userPassword, String role) throws Exception {//, String roleB) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		String userId;
		
		// navigate to new user creation page
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.userMenu")));
		driver.findElement(objMap.getLoc("rd.admin.userMenu")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.users")));
		driver.findElement(objMap.getLoc("rd.admin.users")).click();
		zRdUtil.checkTitle(driver, "readynamic", "user");
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.newUser")));
		driver.findElement(objMap.getLoc("rd.admin.newUser")).click();
		zRdUtil.checkTitle(driver, "readynamic", "new user");
		
		// fill out new user form
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.nUser.full")));
		driver.findElement(objMap.getLoc("rd.nUser.full")).sendKeys(userName);
		driver.findElement(objMap.getLoc("rd.nUser.email")).sendKeys(userEmail);
		// the eula is a checkbox - first see if it's already ticked. If it isn't, then tick it.
		// 		Note - this apparently won't work for internet explorer. You have to send the Space key instead of using a click.
		if (!driver.findElement(objMap.getLoc("rd.nUser.eula")).isSelected()) {
			driver.findElement(objMap.getLoc("rd.nUser.eula")).click();
		}
		// roles can have multiple selections. Here we need to select student and publisher
//		Select selector = new Select(driver.findElement(objMap.getLoc("rd.nUser.roles")));
//		selector.selectByVisibleText(roleA);
//		if (roleB.toLowerCase() != "none") {
//			selector.selectByVisibleText(roleB);
//		}
		// roles are now a drop down menu. Select the proper role with that.
		Select selector = new Select(driver.findElement(objMap.getLoc("rd.nUser.roles")));
		selector.selectByVisibleText(role);
		
		driver.findElement(objMap.getLoc("rd.nUser.password")).sendKeys(userPassword);
		driver.findElement(objMap.getLoc("rd.nUser.passconf")).sendKeys(userPassword);
		driver.findElement(objMap.getLoc("rd.admin.commit")).click();
		zRdUtil.checkTitle(driver, "readynamic", qaType);
		
		// finally, confirm user's email.
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.vUser.id")));
		userId = driver.findElement(objMap.getLoc("rd.vUser.id")).getText();
		String urlEdit = "//*[contains(@href,'/admin/users/" + userId + "/edit')]";
		driver.findElement(By.xpath(urlEdit)).click();
		zRdUtil.checkTitle(driver, "readynamic", "edit user");
		String urlConf = "//*[contains(@href,'/admin/users/" + userId + "/confirm_email')]";
		driver.findElement(By.xpath(urlConf)).click();
		driver.findElement(objMap.getLoc("rd.admin.commit")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.dash")));
		driver.findElement(objMap.getLoc("rd.admin.dash")).click();
		
		Thread.sleep(500);
		
		return userId;
	}
	
	// editUser
	// Arguments: String userId (e.g. userPubTestId, from createUser)
	public void editUser(String userId) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		
		// navigate to user page
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.userMenu")));
		driver.findElement(objMap.getLoc("rd.admin.userMenu")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.users")));
		driver.findElement(objMap.getLoc("rd.admin.users")).click();
		zRdUtil.checkTitle(driver, "readynamic", "user");
		
		// view the user and click the edit button
		driver.findElement(By.cssSelector("a[href='/admin/users/" + userId)).click();
		driver.findElement(By.cssSelector("a[href='/admin/users/" + userId + "/edit")).click();;
		zRdUtil.checkTitle(driver, "readynamic", "edit user");
		
		// append edit to the full name and update
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.nUser.full")));
		driver.findElement(objMap.getLoc("rd.nUser.full")).sendKeys("-EDITED");
		driver.findElement(objMap.getLoc("rd.admin.commit")).click();
		
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.dash")));
		driver.findElement(objMap.getLoc("rd.admin.dash")).click();
		Thread.sleep(500);
	}
	
	// deleteUser
	// Arguments: String userId (e.g. userPubTestId, from createUser)
	public void deleteUser(String userId) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, expWait);
		
		// navigate to user page
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.userMenu")));
		driver.findElement(objMap.getLoc("rd.admin.userMenu")).click();
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.users")));
		driver.findElement(objMap.getLoc("rd.admin.users")).click();
		zRdUtil.checkTitle(driver, "readynamic", "user");
		
		// view the user
		driver.findElement(By.cssSelector("a[href='/admin/users/" + userId)).click();
		// click the delete button - for whatever reason, the href for delete isn't followed by /delete
		// first cancel, then accept
		driver.findElement(By.cssSelector("a[href='/admin/users/" + userId)).click();
		driver.switchTo().alert().dismiss();
		driver.findElement(By.cssSelector("a[href='/admin/users/" + userId)).click();
		driver.switchTo().alert().accept();
		
		wait.until(ExpectedConditions.elementToBeClickable(objMap.getLoc("rd.admin.dash")));
		driver.findElement(objMap.getLoc("rd.admin.dash")).click();
		Thread.sleep(500);
	}
}
