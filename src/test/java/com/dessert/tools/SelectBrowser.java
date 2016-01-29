package com.dessert.tools;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.ITestContext;

/**
 * Description: choosing browsers based on different OS platforms automatically. <br>
 * 
 * @author renta 
 * @version 1.0.1
 * */

public class SelectBrowser {
	
	private Logger logger;
	private String driverConfgFilePath;
	private String chromeDriver_Win;
	private String chromeDriver_Linux; 
	private String chromeDriver_Mac; 
	private String ghostDriver_Win;
	private String ieDriver;
	private String firefoxPath_Win;
	private String firefoxUA_Win;
	private String firefoxProfile;
	private String firefoxNativeEvent;
	
	
	public SelectBrowser(ITestContext context){
		this.logger = Logger.getLogger(SelectBrowser.class.getName());
		/* get the path of driver.properties file from testng.xml file*/
		this.driverConfgFilePath = context.getCurrentXmlTest().getParameter("driverConfgFilePath");
		/*Fetch the values in driver.properties*/
		this.chromeDriver_Win = PropertiesDataProvider.getTestData(driverConfgFilePath, "chromedriver_win");
		this.chromeDriver_Linux = PropertiesDataProvider.getTestData(driverConfgFilePath, "chromedriver_linux");
		this.chromeDriver_Mac = PropertiesDataProvider.getTestData(driverConfgFilePath, "chromedriver_mac");
		this.ghostDriver_Win = PropertiesDataProvider.getTestData(driverConfgFilePath, "ghostdriver_win");
		this.ieDriver = PropertiesDataProvider.getTestData(driverConfgFilePath, "iedriver");
		this.firefoxPath_Win = PropertiesDataProvider.getTestData(driverConfgFilePath, "firefox_path");
		this.firefoxUA_Win = PropertiesDataProvider.getTestData(driverConfgFilePath,"firefox_ua");
		this.firefoxProfile = PropertiesDataProvider.getTestData(driverConfgFilePath,"firefox_profile");		
		this.firefoxNativeEvent = PropertiesDataProvider.getTestData(driverConfgFilePath,"firefox_native_event");
	}
	
	/**
	 * Launch browser by name.
	 * 
	 * @param browser  with values in ie, firefox, chrome or ghost
	 * @return  WebDriver
	 */
	public WebDriver selectExplorerByName(String browser) {
		Properties props = System.getProperties();    								 // get System properties
		String currentOS = props.getProperty("os.name");                   // get OS name
		this.logger.info("Launching browser [" + browser + "] on Current OS [" + currentOS + "]");
		
		if (currentOS.toLowerCase().contains("win")) {
			if (browser.equalsIgnoreCase("ie")) {
				System.setProperty("webdriver.ie.driver", this.ieDriver);
				DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
				ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				return new InternetExplorerDriver(ieCapabilities);
			} 
			else if (browser.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", this.chromeDriver_Win);
				return new ChromeDriver();
			} 
			else if (browser.equalsIgnoreCase("firefox")) {
				System.setProperty("webdriver.firefox.bin", this.firefoxPath_Win);
//				String userDir = System.getProperty("user.dir");
//				File profileDir = new File(userDir+"\\src\\test\\resources\\com\\juanpi\\driver\\firefox\\win\\fp.webtest");
//				FirefoxProfile profile = new FirefoxProfile(profileDir);
				
				ProfilesIni pi = new ProfilesIni();
				FirefoxProfile profile = pi.getProfile(this.firefoxProfile);
				profile.setPreference("general.useragent.override", this.firefoxUA_Win);
				profile.setEnableNativeEvents(Boolean.parseBoolean(this.firefoxNativeEvent));
				this.logger.info("[Firefox] browser is selected !");
				return new FirefoxDriver(profile);
			} 
			else if(browser.equalsIgnoreCase("ghost")){
				DesiredCapabilities ghostCapabilities = new DesiredCapabilities();
				ghostCapabilities.setJavascriptEnabled(true);                       
				ghostCapabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, this.ghostDriver_Win);
			    return new PhantomJSDriver(ghostCapabilities);
				
			} else {
				logger.error("The [" + browser + "]" + " explorer is not applicable  for  [" + currentOS + "] OS");
				Assert.fail("The [" + browser + "]" + " explorer does not apply to  [" + currentOS + "] OS");
			}

		} else if (currentOS.toLowerCase().contains("linux")) {

			if (browser.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", this.chromeDriver_Linux);
				return new ChromeDriver();

			} else if (browser.equalsIgnoreCase("firefox")) {
				return new FirefoxDriver();
			} else {
				logger.error("The [" + browser + "]" + " explorer does not apply to  [" + currentOS + "] OS");
				Assert.fail("The [" + browser + "]" + " explorer does not apply to  [" + currentOS + "] OS");
			}

		} else if (currentOS.toLowerCase().contains("mac")) {
			if (browser.equalsIgnoreCase("chrome")) {
				System.setProperty("webdriver.chrome.driver", this.chromeDriver_Mac);
				return new ChromeDriver();
			} else if (browser.equalsIgnoreCase("firefox")) {
//				String userDir = System.getProperty("user.dir");
//				File profileDir = new File(userDir+"\\src\\test\\resources\\com\\juanpi\\driver\\firefox\\win\\fp.webtest");
//				FirefoxProfile profile = new FirefoxProfile(profileDir);
				
				ProfilesIni pi = new ProfilesIni();
				FirefoxProfile profile = pi.getProfile(this.firefoxProfile);
				profile.setPreference("general.useragent.override", this.firefoxUA_Win);
				profile.setEnableNativeEvents(Boolean.parseBoolean(this.firefoxNativeEvent));
				this.logger.info("[Firefox] browser is selected !");
				return new FirefoxDriver(profile);
			} else {
				logger.error("The [" + browser + "]" + " explorer does not apply to  [" + currentOS + "] OS");
				Assert.fail("The [" + browser + "]" + " explorer does not apply to  [" + currentOS + "] OS");
			}

		} else
			logger.error("The [" + currentOS + "] is not supported for this automation frame,please change the OS(Windows,MAC or LINUX)");
			Assert.fail("The [" + currentOS + "] is not supported for this automation frame,please change the OS(Windows,MAC or LINUX)");
		return null;
	}

}
