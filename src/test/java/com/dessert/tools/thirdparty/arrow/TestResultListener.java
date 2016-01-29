package com.dessert.tools.thirdparty.arrow;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import com.dessert.tools.PropertiesDataProvider;

/**
 * @author renta     Capture from Netease
 * 
 */
public class TestResultListener extends TestListenerAdapter {

	private static Logger logger = Logger.getLogger(TestResultListener.class.getName());
	protected ITestContext testContext = null;           // new added
	String  browser = null;

	@Override
	public void onStart(ITestContext testContext) {     // new added
		this.testContext = testContext;
		browser = String.valueOf(testContext.getCurrentXmlTest().getParameter("browserName"));
		super.onStart(testContext);
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		super.onTestFailure(tr);
		logger.warn(tr.getName() + "Test case performs failed !");
		WebDriver webDriver = (WebDriver) testContext.getAttribute("SELENIUM_DRIVER");  // get the driver which is set in @BeforeClass method
		saveScreenShot(tr, webDriver, browser);
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		super.onTestSkipped(tr);
		WebDriver webDriver = (WebDriver) testContext.getAttribute("SELENIUM_DRIVER");   // get the driver which is set in @BeforeClass method
		logger.warn(tr.getName() + "Test case skipped !");
		saveScreenShot(tr, webDriver,browser);

	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		super.onTestSuccess(tr);
		logger.info(tr.getName() + "Test case performs successfully !");
	}

	@Override
	public void onTestStart(ITestResult tr) {
		super.onTestStart(tr);
		logger.info(tr.getName() + "Test case starts to run !");
	}

	@Override
	public void onFinish(ITestContext testContext) {
		super.onFinish(testContext);

		// List of test results which we will delete later
		ArrayList<ITestResult> testsToBeRemoved = new ArrayList<ITestResult>();
		// collect all id's from passed test
		Set<Integer> passedTestIds = new HashSet<Integer>();
		for (ITestResult passedTest : testContext.getPassedTests().getAllResults()) {
			logger.info("Test cases which are passed = " + passedTest.getName());
			passedTestIds.add(getId(passedTest));
		}

		// Eliminate the repeat methods
		Set<Integer> skipTestIds = new HashSet<Integer>();
		for (ITestResult skipTest : testContext.getSkippedTests().getAllResults()) {
			logger.info("Test cases which are skipped = " + skipTest.getName());
			// id = class + method + dataprovider
			int skipTestId = getId(skipTest);

			if (skipTestIds.contains(skipTestId) || passedTestIds.contains(skipTestId)) {
				testsToBeRemoved.add(skipTest);
			} else {
				skipTestIds.add(skipTestId);
			}
		}

		// Eliminate the repeat failed methods
		Set<Integer> failedTestIds = new HashSet<Integer>();
		for (ITestResult failedTest : testContext.getFailedTests().getAllResults()) {
			logger.info("The cases which are failed = " + failedTest.getName());
			// id = class + method + dataprovider
			int failedTestId = getId(failedTest);

			// if we saw this test as a failed test before we mark as to be
			// deleted
			// or delete this failed test if there is at least one passed
			// version
			if (failedTestIds.contains(failedTestId) || passedTestIds.contains(failedTestId) || skipTestIds.contains(failedTestId)) {
				testsToBeRemoved.add(failedTest);
			} else {
				failedTestIds.add(failedTestId);
			}
		}

		// finally delete all tests that are marked
		for (Iterator<ITestResult> iterator = testContext.getFailedTests().getAllResults().iterator(); iterator.hasNext();) {
			ITestResult testResult = iterator.next();
			if (testsToBeRemoved.contains(testResult)) {
				logger.info("移除重复失败的用例 = " + testResult.getName());
				iterator.remove();
			}
		}

	}

	private int getId(ITestResult result) {
		int id = result.getTestClass().getName().hashCode();
		id = id + result.getMethod().getMethodName().hashCode();
		id = id + (result.getParameters() != null ? Arrays.hashCode(result.getParameters()) : 0);
		return id;
	}

	private void saveScreenShot(ITestResult tr, WebDriver driver, String browser) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		String mDateTime = formatter.format(new Date());
		String fileName = mDateTime + "_" + tr.getName();                        //getName一般来说就是测试方法的名字
		String filePath = "";
		try {
			// 这里可以调用不同框架的截图功能
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			filePath = "target/test-screenshots/"+ fileName + ".jpg";
			File destFile = new File(filePath);
			FileUtils.copyFile(screenshot, destFile);
			logger.info("["+fileName + "] 截图成功，图片保存在：" + "[ " + filePath + " ]");

		} catch (Exception e) {
			filePath = "["+fileName+"]" + " ,截图失败，原因是：" + e.getMessage();
			logger.error(filePath);
		}
			
	    // 把截图写入到Html报告中方便查看
		if(System.getProperty("jenkins.project.url")!=null){      //如果由服务器执行构建
			Reporter.log("<img src=\""+System.getProperty("jenkins.project.url")+"ws/" + filePath + "\"/>");
		}else{
//			Reporter.log("<img src=\"file:///"+System.getProperty("user.dir") + "/" + filePath + "\"/>");
			Reporter.log("<img src=\"../../" + filePath + "\"/>");
		}
	}

}
