package com.dessert.base;

import java.io.IOException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import com.dessert.pages.base.BasePage;
import com.dessert.tools.AssertUtil;
import com.dessert.tools.CSVDataProvider;
import com.dessert.tools.SeleniumUtil;

/**
 * Including the setUp and tearDown methods to initialize and end driver. <br>
 * 每一个用例都要继承的基础类，主要功能：测试前的初始化，测试后的结束操作以及测试数据的收集。
 * 
 * @author Renta
 * 
 * */
public class TestInitialization {
	private Logger logger = Logger.getLogger(TestInitialization.class.getName());
	protected AssertUtil assertUtil;
	protected SeleniumUtil seleniumUtil = null;
	protected ITestContext testContext = null;
	protected BasePage basePage;
	
	/**
	 * Initialization operations which set the entrance parameters such as browser name, URLs and time limits. <br>
	 * 提供一些初始化的入口变量，如浏览器类型，一些url，等待时间限制等。
	 * 
	 * @param browserName 浏览器的名字, 如firefox,ie ,chrome
	 * @param miUrl 前台地址
	 * @param sellerUrl 特卖地址
	 * @param manageUrl 后台地址
	 * @param pageTimeOut 等待页面加载时间限制
	 * @param picTimeOut 等待图片加载时间限制
	 * @param elementTimeOut 等待普通元素加载时间限制
	 * @param promptWindowTimeOut 等待弹出框加载时间限制
	 * @param context TestNg上下文
	 */
	@Parameters({"browserName", "miUrl","pageTimeOut","picTimeOut","elementTimeOut","promptWindowTimeOut"})
	@BeforeClass
	public void setUp(String browserName, String miUrl, int pageTimeOut, int picTimeOut, int elementTimeOut, int promptWindowTimeOut,  ITestContext context) {
		
		this.seleniumUtil = new SeleniumUtil();
		this.assertUtil = new AssertUtil(this.seleniumUtil);
		this.testContext = context;                  		
		try {																			//此处值传给测试用例
			this.basePage = new BasePage();
			this.basePage.setSelenium(this.seleniumUtil);
			this.basePage.setPageTimeout(pageTimeOut);
			this.basePage.setPicTimeout(picTimeOut);
			this.basePage.setElementTimeout(elementTimeOut);
			this.basePage.setPromptWindowTimeout(promptWindowTimeOut);
			this.basePage.setMiUrl(miUrl);
			this.basePage.getSelenium().launchBrowser(browserName, context);

		} catch (Exception e) {
			this.logger.error("Browser can not be lauched due to some reason, please check the BasePage config !",e);
		}
		// Record driver to context, it will be got in later test
		this.testContext.setAttribute("SELENIUM_DRIVER", seleniumUtil.driver);
		
	}

	/**
	 * Ending operations quit the browser. <br>
	 * 关闭浏览器等测试结束的收尾工作。
	 * 
	 */
	@AfterClass
	public void tearDown() {
		if (seleniumUtil.driver != null) {
			seleniumUtil.quit();
		} else {
			logger.error("No driver object is being created, fail !");
			Assert.fail("No driver object is being created, fail !");
		}
	}
	
	/**
	 * Provide the CSV files data, test class name should be same to corresponding CSV files. <br>
	 * 为测试提供数据, 规则是：测试类的完整路径名要和CSV文件对应。
	 * 
	 * */
	@DataProvider(name = "testData")
	public Object[][] getCSVData(Method m) throws IOException{
			CSVDataProvider csvData = new CSVDataProvider();
//			String testEnv = System.getProperty("testEnv");
			String method = m.toString();
			String classPath = method.substring(method.lastIndexOf(" "), method.lastIndexOf(".test")).trim().replace(".", "/").replace("cases", "data/csv");
			Object[][] allData = csvData.readCSVData(classPath);
			return allData;
		}		
	
	
	}
