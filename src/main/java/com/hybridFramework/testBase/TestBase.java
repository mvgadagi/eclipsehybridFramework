package com.hybridFramework.testBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.bcel.Repository;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.hybridFramework.excelReader.Excel_reader;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class TestBase {

	public static final Logger logger = Logger.getLogger(TestBase.class.getName());
	// public WebDriver driver;
	public Properties OR;
	public File f1;
	public FileInputStream file;
	public Excel_reader excelReader;

	public static ExtentReports extent;
	public static ExtentTest test;
	static WebDriver driver;
	static {
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss");
		extent = new ExtentReports(System.getProperty("user.dir") + "\\src\\main\\java\\com\\hybridFramework\\report\\"
				+ format.format(calender.getTime()) + ".html", false);

	}

	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.SUCCESS) {
			test.log(LogStatus.PASS, result.getName() + "Test Is Pass");
		}
		else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP,
					result.getName() + "Test Is Skipped because of the reason " + result.getThrowable());
		}
		else if (result.getStatus() == ITestResult.FAILURE) {
			test.log(LogStatus.FAIL,
					result.getName() + "Test Is Failed because of the reason " + result.getThrowable());
		}
		else if (result.getStatus() == ITestResult.STARTED) {
			test.log(LogStatus.INFO, result.getName() + "Test Is Started");
		}

	}

	@BeforeMethod()
	public void beforeMethod(Method result) {
		test = extent.startTest(result.getName());
		test.log(LogStatus.INFO, result.getName() + "Test Is Started");
	}

	@AfterMethod()
	public void afterMethod(ITestResult result) {
		getResult(result);
	}

	@AfterClass(alwaysRun = true)
	public void endTest() {
		extent.endTest(test);
		extent.flush();
	}

	public void getBrowser(String browser) {

		if (System.getProperty("os.name").contains("Window")) {
			if (browser.equalsIgnoreCase("firefox")) {
				System.out.println("Windows is selected");
				System.setProperty("webdriver.gecko.driver",
						System.getProperty("user.dir") + "\\drivers\\geckodriver.exe");
				driver = new FirefoxDriver();
			}
			else if (browser.equalsIgnoreCase("chrome")) {
				System.out.println("Selected Chrome Driver");
				System.out.println(System.getProperty("user.dir"));
				System.setProperty("webdriver.chrome.driver",
						System.getProperty("user.dir") + "\\drivers\\chromedriver.exe");
				driver = new ChromeDriver();

			}
		}

		else if (System.getProperty("os.name").contains("Mac")) {
			if (browser.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.firefox.marionette",
						System.getProperty("user.dir") + "/drivers/geckodriver");
			}
			else if (browser.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", System.getProperty("usr.dir") + "/drivers/chromedriver");
			}
		}

	}

	public void init() throws IOException, InterruptedException {
		loadPropertiesFile();
		getBrowser(OR.getProperty("browser"));
		driver.get(OR.getProperty("url"));
	}

	public void getScreenshot(String imageName) throws IOException {

		if (imageName.equals("")) {
			imageName = "blank";
		}
		File image = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String imageLocation = System.getProperty("user.dir") + "\\src\\main\\java\\com\\hybridFramework\\screenshot\\";
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
		String actualImageName = imageLocation + "_" + formater.format(calendar.getTime()) + ".png";
		File destFile = new File(actualImageName);
		FileUtils.copyFile(image, destFile);

	}

	public void loadPropertiesFile() throws IOException {

		String log4jConfPath = "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);

		OR = new Properties();
		f1 = new File(
				System.getProperty("user.dir") + "\\src\\main\\java\\com\\hybridFramework\\config\\config.properties");
		file = new FileInputStream(f1);
		OR.load(file);
		logger.info("Loaded the Property File : " + f1);

		f1 = new File(
				System.getProperty("user.dir") + "\\src\\main\\java\\com\\hybridFramework\\config\\or.properties");
		file = new FileInputStream(f1);
		OR.load(file);
		logger.info("Loaded the Property File : " + f1);

		f1 = new File(System.getProperty("user.dir")
				+ "\\src\\main\\java\\com\\hybridFramework\\properties\\homepage.properties");
		file = new FileInputStream(f1);
		OR.load(file);
		logger.info("Loaded the Property File : " + f1);
	}

	public WebElement waitForElement(WebDriver driver, long time, WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, time);
		return wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public WebElement waitForElementWithPollingInterval(WebDriver driver, long time, WebElement element) {

		WebDriverWait wait = new WebDriverWait(driver, time);
		wait.pollingEvery(5, TimeUnit.SECONDS);
		wait.ignoring(NoSuchElementException.class);
		return wait.until(ExpectedConditions.elementToBeClickable(element));

	}

	public void impliciteWait(long time) {
		driver.manage().timeouts().implicitlyWait(time, TimeUnit.SECONDS);
	}

	public WebElement getLocator(String locator) throws Exception {
		String[] split = locator.split(":");
		String locatorType = split[0];
		String locatorValue = split[1];

		if (locatorType.toLowerCase().equals("id")) {
			return driver.findElement(By.id(locatorValue));
		}
		else if (locatorType.toLowerCase().equals("name")) {
			return driver.findElement(By.name(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("classname")) || (locatorType.toLowerCase().equals("class"))) {
			return driver.findElement(By.className(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("tagname")) || (locatorType.toLowerCase().equals("tag"))) {
			return driver.findElement(By.tagName(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("linktext")) || (locatorType.toLowerCase().equals("link"))) {
			return driver.findElement(By.linkText(locatorValue));
		}
		else if (locatorType.toLowerCase().equals("partiallinktext")) {
			return driver.findElement(By.partialLinkText(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("cssselector")) || (locatorType.toLowerCase().equals("css"))) {
			return driver.findElement(By.cssSelector(locatorValue));
		}
		else if (locatorType.toLowerCase().equals("xpath")) {
			return driver.findElement(By.xpath(locatorValue));
		}
		else
			throw new Exception("'Unknown locator Type '" + locatorType + "'");
	}

	@SuppressWarnings("unchecked")
	public List<WebElement> getLocators(String locator) throws Exception {
		String[] split = locator.split(":");
		String locatorType = split[0];
		String locatorValue = split[1];

		if (locatorType.toLowerCase().equals("id"))

			return (List<WebElement>) driver.findElement(By.id(locatorValue));

		else if (locatorType.toLowerCase().equals("name"))

			return (List<WebElement>) driver.findElement(By.name(locatorValue));

		else if ((locatorType.toLowerCase().equals("classname")) || (locatorType.toLowerCase().equals("class"))) {
			return (List<WebElement>) driver.findElement(By.className(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("tagname")) || (locatorType.toLowerCase().equals("tag"))) {
			return (List<WebElement>) driver.findElement(By.tagName(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("linktext")) || (locatorType.toLowerCase().equals("link"))) {
			return (List<WebElement>) driver.findElement(By.linkText(locatorValue));
		}
		else if (locatorType.toLowerCase().equals("partiallinktext")) {
			return (List<WebElement>) driver.findElement(By.partialLinkText(locatorValue));
		}
		else if ((locatorType.toLowerCase().equals("cssselector")) || (locatorType.toLowerCase().equals("css"))) {
			return (List<WebElement>) driver.findElement(By.cssSelector(locatorValue));
		}
		else if (locatorType.toLowerCase().equals("xpath")) {
			return (List<WebElement>) driver.findElement(By.xpath(locatorValue));
		}
		else
			throw new Exception("'Unknown locator Type '" + locatorType + "'");
	}

	public WebElement getWebElement(String locator) throws Exception {
		return getLocator(OR.getProperty(locator));
	}

	public List<WebElement> getWebElements(String locator) throws Exception {
		return getLocators(OR.getProperty(locator));
	}

	public String[][] getData(String excelName, String sheetname) {
		String excellocation = System.getProperty("user.dir") + "\\src\\main\\java\\com\\hybridFramework\\data\\"
				+ excelName;
		System.out.println(excellocation);
		excelReader = new Excel_reader();
		return excelReader.getExcelData(excellocation, sheetname);

	}

	public static void main(String[] args) throws IOException, InterruptedException {

		TestBase test = new TestBase();
		test.init();

	}
}
