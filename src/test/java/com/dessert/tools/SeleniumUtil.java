package com.dessert.tools;

import java.awt.AWTException;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.dessert.tools.thirdparty.arrow.TestResultListener;

/**
 * Description: encapsulating the basic Selenium APIs as well as providing convenient operations for Page Class invoking. <p>
 * Company: Qimi Wuhan
 * 
 * @author Renta 
 * @version  1.0.1
 * */

public class SeleniumUtil{
	
	private final int ELEMENT_WAIT_INTERVAL = 300;
	public Logger logger;
	public ITestResult itestResult;
	public WebDriver driver;
	
	/**
	 * Default constructor with no parameters
	 */
	public SeleniumUtil(){
		this.logger = Logger.getLogger(SeleniumUtil.class.getName());
	}
	
	/**
	 * Launch specified type of browser.
	 *  
	 * @param browserName name of browser 
	 * @param context the testNg context
	 * @return WebDriver object
	 */
	public WebDriver launchBrowser(String browserName, ITestContext context) {
		SelectBrowser browser = new SelectBrowser(context);
		this.driver = browser.selectExplorerByName(browserName);
		try {
			this.logger.info("Browser is launched successfully !");
			maxWindow();
		} catch (TimeoutException e) {
			this.logger.warn("Fail to launch browser in time limit. Please check configurations for browser !");
			e.printStackTrace();
		}
		return this.driver;
	}
	
	/**
	 * Maximize current window
	 * */
	public void maxWindow() {
		driver.manage().window().maximize();
	}
	
	/**
	 * Set size of current window.<p>
	 * It's used for 2 perposes: <p>
	 * 1. In combination with some tools which are used to compare images such as sikuli.<p>
	 * 2. Capturing pages in different sizes and testing the front page style.
	 * 	   For example, set the window to mobile size "320x480" to test its style.
	 * 
	 * @param width 
	 * @param height
	 * */
	public void setBrowserSize(int width, int height) {
		this.logger.info("Setting the window size to width: "+width+" height: "+height);
		driver.manage().window().setSize(new Dimension(width, height));
	}

	/**
	 * Find element with parameter By.
	 * 
	 * @param by with type of By
	 * @return WebElement
	 * */
	public WebElement findElementBy(By by) {
		this.logger.info("Finding element ["+ by +"]...");
		WebElement element = driver.findElement(by);
		this.logger.info("Found element ["+ getElementLocator(element, ">") +"]");
		return element;
	}
	
	/**
	 * Find a list of elements with parameter By.
	 * 
	 * @param by with type of By
	 * @return list of WebElement
	 * */
	public List<WebElement> findElementsBy(By by) {
		this.logger.info("Finding elements ["+ by +"]...");
		List<WebElement> elements =  driver.findElements(by);
		this.logger.info("Found list of elements ["+ elements +"]");	
		return elements;
	}
	
	/**
	 * Find a list of elements by an element.
	 * 
	 *  @param element element which is referring to
	 *  @param by with type of By
	 *  @return list of WebElement
	 * */
	public List<WebElement> findElementsBy(WebElement element, By by) {
		this.logger.info("Father element ["+ getElementLocator(element, ">") + "] is finding sub elements [" + by +"]...");	
		List<WebElement> elements =  element.findElements(by);
		this.logger.info("Found list of elements ["+ elements +"]");	
		return elements;
	}
	
	/**
	 * Find a list of elements by an element.
	 * 
	 * @param element element which is referring to
	 * @param by with type of String matches xpath syntax
	 * @return list of WebElement
	 * */
	public List<WebElement> findElementsBy(WebElement element, String by) {
		this.logger.info("Father element ["+ getElementLocator(element, ">") + "] is finding sub elements [" + by +"]...");	
		List<WebElement> elements =  element.findElements(By.xpath(by));
		this.logger.info("Found list of elements ["+ elements +"]");			
		return elements;
	}
	
	/**
	 * Find element by an element.
	 * 
	 * @param element element which is referring to
	 * @param by with type of By
	 * @return WebElement
	 * */
	public WebElement findElementBy(WebElement element, By by) {
		this.logger.info("Father element ["+ getElementLocator(element, ">") + "] is finding sub element [" + by +"]...");	
		WebElement ele= element.findElement(by);
		this.logger.info("Found element ["+ getElementLocator(ele, ">") +"]");
		return ele;
	}
	
	/**
	 * Find element by an element.
	 * 
	 * @param element element which is referring to
	 * @param by with type of String matches xpath syntax
	 * @return WebElement
	 * */
	public WebElement findElementBy(WebElement element, String by) {
		this.logger.info("Father element ["+ getElementLocator(element, ">") + "] is finding sub element [" + by +"]...");	
		WebElement ele= element.findElement(By.xpath(by));
		this.logger.info("Found element ["+ getElementLocator(ele, ">") +"]");
		return ele;
	}

	/**  
	 * The waiting condition should be reached.<p>
	 * 需要达成的等待条件
	 * 
	 * Ex:<hr>
	 * waitForCondition(<br>
	 * &nbsp;&nbsp;new ExpectedCondition<Boolean>(){<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;public Boolean apply(WebDriver driver) {<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return driver.findElement(By by).isDisplayed();<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;}<br>
	 * }, timeout);
	 * 
	 * @param booleanCondition the expected condition
	 * @param timeOutInMilliseconds the time limit program should wait for condition to be reached.
	 * @see SeleniumUtil#waitForElementToLoad
	 * @see SeleniumUtil#waitForSelectToLoadSpecifiedText
	 * */
	public void waitForCondition(ExpectedCondition<Boolean> booleanCondition, int timeOutInMilliseconds, String message) {
		try{
			new WebDriverWait(driver, timeOutInMilliseconds / 1000)
				.ignoring(StaleElementReferenceException.class)
				.until(booleanCondition);
		} catch(TimeoutException toe){
			if(toe.getCause() != null){
				Assert.fail(message + "Exceptions occur during the waiting time: " + toe.getCause());
			} else {
				Assert.fail(message);
			}
		}
	}
	
	/**
	 * Wait within limited time until target element is loaded. <p>
	 * 在一定时间内等待元素被显示
	 * 
	 * @param element 目标元素
	 * @param timeOut 最大等待时间
	 * */
	public void waitForElementToLoad(final WebElement element, final int timeOut) {
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						boolean rtn = false;
						rtn = element.isDisplayed();
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);          //默认等待0.5秒，当页面跳转时通常慢些，增加时间，适当减少请求次数
						return rtn;
					}
				}, 	timeOut, "Waiting over time but the element ["+ getElementLocator(element, ">") +"] is still not displayed.");
		this.logger.info("Element [" + getElementLocator(element, ">") + "] is displayed.");
	}
	
	/**
	 * Wait within limited time until target element is loaded and displayed. <p>
	 * 等待一段时间被查找出的子元素被显示出来
	 * 
	 * @param elements 目标元素列表
	 * @param timeOut 最大等待时间
	 * */
	public void waitForElementToLoad(final WebElement father, final String child, final int timeOut) {
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						boolean rtn = true;
						List<WebElement> elements = findElementsBy(father, child);
						if(elements.size() > 0){
							for(WebElement ele:elements){
								if(ele.isDisplayed()){
									continue;
								}else{
									rtn = false;
									break;
								}
							}
						}else{
							rtn = false;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
				}, 	timeOut, "Waiting over time but the element ["+ getElementLocator(father, ">")+ " ->" + child +"] is still not displayed.");
		this.logger.info("Element [" + getElementLocator(father, ">")+ " ->" + child + "] is loaded and displayed.");
	}
	
	/**
	 * Wait within limited time until text of target element is displayed.<p>
	 * 等待一段时间直到目标元素的text被显示出来
	 * 
	 * @param element 目标元素
	 * @param timeOut 最大等待时间
	 * */
	public void waitForTextToDisplay(final WebElement element, final int timeOut) {
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						boolean rtn = false;
						String text = element.getText().trim();
						rtn = !text.equals("");
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);               
						return rtn;
					}
				}, 	timeOut, "Waiting over time but text of the element ["+ getElementLocator(element, ">") +"] is still not displayed.");
		this.logger.info("Text of element [" + getElementLocator(element, ">") + "] is displayed.");
	}
	
	/**
	 * Wait within limited time until text of target element is of correct value.<p>
	 * 等待目标元素包含指定的文本
	 * 
	 * @param element 目标元素
	 * @param timeOut 最大时间
	 * @param expectText 期望文本值
	 * */
	public void waitForTextToBeGot(final WebElement element, final int timeOut, final String expectText) {
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						boolean rtn = false;
						String text = element.getText().trim();
						rtn = text.equals(expectText);
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
				}, 	timeOut, "Waiting over time but text of the element ["+ getElementLocator(element, ">") +"] is still not displayed.");
		this.logger.info("Text of element [" + getElementLocator(element, ">") + "] is displayed.");
	}
	
	/**
	 * Wait within limited time until expected element is in the list.<p>
	 * 等待目标元素列表中包含指定的文本值
	 * 
	 * @param elements 目标元素列表
	 * @param timeOut 最大时间
	 * @param expectText 期望文本值
	 * */
	public void waitForTextToBeGot(final List<WebElement> elements, final int timeOut, final String expectText) {
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver driver) {
						boolean rtn = false;
						for(WebElement ele: elements){
							String text = ele.getText().trim();
							if(text.contains(expectText)){
								rtn = true;
								logger.info("Element with text [" + expectText +"] is found in element list !");
								break;
							}else{
								logger.info("Element with text [" + expectText +"] is still not found in this round. Continue finding...");
								continue;
							}
						}
					waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
					return rtn;
					}
				}, 	timeOut, "Waiting over time but the element with expected text is not in element list ["+ elements +"].");
		this.logger.info("The element with expected text is in element list ["+elements+"]");
	}
	
	/** 
	 *  Wait until the specified option text is loaded in Selection field. <p>
	 *  等待下拉选择框中包含指定元素
	 *  
	 *  @param select 目标下拉框
	 * @param timeOut 最大时间
	 * @param expectText 期望文本值
	 *  
	 */
	public void waitForSelectToLoadSpecifiedText(final WebElement select, final int timeOut, final String specifiedText){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver arg0) {
						boolean rtn = false;
						By optionBy = By.xpath(String.format("./option[contains(text(), '%s')]",specifiedText));
						if(findElementsBy(select, optionBy).size() > 0){
								rtn = true;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
				}, timeOut, "Waiting over time but option text ["+ specifiedText +"] of dropdown [" + getElementLocator(select, ">") + "] is not displayed.");
			this.logger.info("Option text ["+ specifiedText +"] of dropdown [" + getElementLocator(select, ">") + "] is displayed.");
		}
	
	/** 
	 *  Wait until the specified option text is loaded in Selection field. <p>
	 *  等待下拉选择框中指定索引的选项存在
	 *  
	 *  @param select 目标下拉框
	 * @param timeOut 最大时间
	 * @param expectText 期望文本值
	 *  
	 */
	public void waitForSelectToLoadIndexOfOption(final WebElement select, final int timeOut, final int index){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					@Override
					public Boolean apply(WebDriver arg0) {
						boolean rtn = false;
						By optionBy = By.xpath(String.format("./option[%s]",index));
						if(findElementsBy(select, optionBy).size() > 0){
							rtn = true;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
				}, timeOut, "Waiting over time but option["+ index +"] of dropdown [" + getElementLocator(select, ">") + "] is not displayed.");
		this.logger.info("Option["+ index +"] of dropdown [" + getElementLocator(select, ">") + "] is displayed.");
	}
	
	/** 
	 *  Wait within limited time until record line of table is loaded, by confirming the td node contains specified text.<p>
	 *  等待目标table中包含指定的元素文本
	 *  
	 *  @param table 目标table
	 *  @param timeOut 最大时间
	 *  @param linePattern tr符合条件
	 *  @param colPattern td符合条件
	 *  @param textOwner 包含指定文本值的节点名
	 *  @param text 指定文本
	 */
	public void waitForTableToLoadSpecifiedRecord(final WebElement table, final int timeOut, final String linePattern, final String colPattern, final String textOwner, final String text){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					public Boolean apply(WebDriver arg0) {
						boolean rtn = false;
//						By optionBy = By.xpath(String.format("./tbody/tr["+linePattern+"][./td["+colPattern+"]//"+textOwner+"[text()='%s']]",text));
						By optionBy = By.xpath(String.format("./tbody/tr["+linePattern+"][./td["+colPattern+"]//"+textOwner+"[contains(text(), '%s')]]",text));
						logger.info("Finding elements with pattern like ["+String.format("./tbody/tr["+linePattern+"][./td["+colPattern+"]//"+textOwner+"[contains(text(), '%s')]]", text) + "]");
						if(findElementsBy(table, optionBy).size() > 0){
							rtn = true;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
			}, timeOut, "Waiting over time but record line which contains text ["+text+"] of table [" + getElementLocator(table, ">") + "] is not loaded.");
		this.logger.info("Record line which contains text ["+text+"] of table [" + getElementLocator(table, ">") + "] is loaded.");
	}
	
	/** 
	 *  Wait within limited time until record line of div is loaded, by confirming the sub div node contains specified text.<p>
	 *  等待指定的div元素加载出来
	 *  
	 *  @param div the Div elements
	 *  @param timeOut time limit
	 *  @param extraPattern pattern adds to existing filters, including the '[' and ']' symbols.
	 *  @param linkText text of llink
	 */
	public void waitForDivisionToLoadSpecifiedRecord(final WebElement div, final int timeOut, final String textOwner, final String extraPattern, final String linkText){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					public Boolean apply(WebDriver arg0) {
						boolean rtn = false;
						By optionBy = By.xpath(String.format("./.[.//"+textOwner+"[contains(text(), '%s')]]"+ extraPattern, linkText));
						if(findElementsBy(div, optionBy).size() > 0){
							rtn = true;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
				}
			}, timeOut, "Waiting over time but record line which contains text ["+linkText+"] of table [" + getElementLocator(div, ">") + "] is not loaded.");
		this.logger.info("Record line which contains text ["+linkText+"] of table [" + getElementLocator(div, ">") + "] is loaded.");
	}
	
	/**
	 * Waits within limited time until image attribute is changed after finishing uploaded. <p>
	 * 上传图片后，等待图片元素的属性变成指定值，目前已有一下三种情况：<br/>
	 * 1. s1.juancdn.com/bao/151124/e/8/56543ca092be59c46a8b4567_800x800.jpg      <br/>
	 * 2. s1.juancdn.com/secret/download?path=/utils/151228/5/7/56809b2692be5983ab8b4590_160x80.jpg&timestamp=1452494997&sign=94e6aaf1c7927aeabe0393511c38a7c8        <br/>
	 * 3. s2.juancdn.com/bao/151125/5/7/56552b4692be597da88b45da_160x80.jpg?iopcmd=thumbnail&type=8&height=80|iopcmd=convert&Q=88&dst=jpg      <br/>
	 * 
	 * 
	 * 
	 * @param element 目标元素
	 * @param attributeName name of attribute
	 * @param specifiedAttribute attribute value expected to be
	 * @param timeOut time limit 
	 */
	public void waitForImgAttributeChanged(final WebElement element, final String attributeName, final String specifiedAttribute, final int timeOut){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					public Boolean apply(WebDriver arg0) {
						boolean rtn = false;
						String[] attributeSplit = getAttributeText(element, attributeName).split("_");
						String attribute;
						if(attributeSplit.length>1){               
							attribute =  attributeSplit[1];
						}else{
							attribute =  attributeSplit[0];
						}
						
						if(attribute.indexOf(".") != -1){
							attribute = attribute.substring(0, attribute.indexOf(".")+4);                //仅支持扩展名为3位的，如.jpg, .png， 不支持jpeg
						}
						
						if(attribute.equals(specifiedAttribute)){
							rtn = true;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
			}, timeOut, "Waiting over time but attribute ["+attributeName+"] value of element ["+ getElementLocator(element, ">") +"] is not changed to ["+specifiedAttribute+"] after image is uploaded .");
		this.logger.info("Attribute ["+attributeName+"] value of element ["+ getElementLocator(element, ">") +"] is changed to ["+specifiedAttribute+"] after image is uploaded."  );
	}
	
	/**
	 * Waits within limited time until images' attribute are changed after finishing uploaded. <p>
	 * 上传图片后，等待一组图片元素的属性变化
	 * 
	 * @param element 目标元素列表
	 * @param attributeName name of attribute
	 * @param expectedAttribute attribute value expected to be
	 * @param timeOut time limit 
	 */
	public void waitForImgsAttributeChanged(final List<WebElement> elements, final String attributeName, final String expectedAttribute, final int timeOut){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					public Boolean apply(WebDriver arg0) {
						boolean rtn = true;
						String[] attributes = new String[elements.size()];
						for(int i = 0; i < elements.size(); i++){
							String[] attributeSplit = getAttributeText(elements.get(i), attributeName).split("_");
							if(attributeSplit.length>1){
								attributes[i] =  attributeSplit[1];
							}else{
								attributes[i] =  attributeSplit[0];
							}
						}
							
						for(String attr: attributes){
							if(attr.indexOf(".") != -1){
								attr = attr.substring(0, attr.indexOf(".")+4);                //仅支持扩展名为3位的，如.jpg, .png， 不支持jpeg
							}
							if(!attr.equals(expectedAttribute)){
								rtn = false;
								break;
							}
						}
					waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
					return rtn;
				}
			}, timeOut, "Waiting over time but attribute ["+attributeName+"] value of element ["+ elements +"] is not changed to ["+expectedAttribute+"] after image is uplaoded !.");
		this.logger.info("Attribute ["+attributeName+"] value of element ["+ elements +"] is changed to ["+expectedAttribute+"] after image is uplaoded !"  );
	}
	
	/**
	 * Waits within limited time until attribute value is changed. <p>
	 * 等待一个元素的属性改变
	 * 
	 * @param element
	 * @param attributeName
	 * @param expectedAttribute
	 * @param timeOut
	 */
	public void waitForAttributeChanged(final WebElement element, final String attributeName, final String expectedAttribute, final int timeOut){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					public Boolean apply(WebDriver arg0) {
						boolean rtn = false;
						String attributeValue = getAttributeText(element, attributeName).trim();
						if(attributeValue.equals(expectedAttribute)){              
							rtn = true;
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
				}, timeOut, "Waiting over time but attribute ["+attributeName+"] value of element ["+ getElementLocator(element, ">") +"] is not changed to ["+expectedAttribute+"].");
		this.logger.info("Attribute ["+attributeName+"] value of element ["+ getElementLocator(element, ">") +"] is changed to ["+expectedAttribute+"]"  );
	}
	
	/**
	 * Waits within limited time until a list of attributes' values are changed. <p>
	 * 等待一组元素的属性改变
	 * 
	 * @param element
	 * @param attributeName
	 * @param expectedAttribute
	 * @param timeOut
	 */
	public void waitForAttributesChanged(final List<WebElement> elements, final String attributeName, final String expectedAttribute, final int timeOut){
		waitForCondition(
				new ExpectedCondition<Boolean>(){
					public Boolean apply(WebDriver arg0) {
						boolean rtn = true;
						String[] attributes = new String[elements.size()];
						for(int i = 0; i < elements.size(); i++){
							attributes[i] = getAttributeText(elements.get(i), attributeName).trim();
						}
						
						for(String attr: attributes){
							if(!attr.equals(expectedAttribute)){
								rtn = false;
								break;
							}
						}
						waitForFixedMilliseconds(ELEMENT_WAIT_INTERVAL);
						return rtn;
					}
			}, timeOut, "Waiting over time but attribute ["+attributeName+"] value of element ["+ elements +"] is not changed to ["+expectedAttribute+"].");
		this.logger.info("Attribute ["+attributeName+"] value of element ["+ elements +"] is changed to ["+expectedAttribute+"]"  );
	}
	
	
	/**
	 * Get table element which matches specified pattern. <p>
	 * 等待table元素满足指定的匹配模式<br/>
	 * 
	 * Ex: self::table[./tbody/---]]
	 * 
	 * @param table 目标table
	 * @param tablePattern 匹配模式
	 * @return table
	 */
	public WebElement getSpecifiedTable(WebElement table, String tablePattern){
		By optionBy = By.xpath("self::table[./tbody/"+tablePattern +"]");
		WebElement tb = findElementBy(table,optionBy);
		return tb;
	}
	
	/** 
	 *  Get the record line (tr element) which matches pattern of table element. <p>  
	 *  
	 *  The table format is like ".//table/tbody/tr[]".
	 *  
	 *  @param table table element
	 *  @param  linePattern the pattern will match, with values such as "1", "last()", "location()>3" and so on
	 *  @return  element of tr type
	 */
	public WebElement getSingleRecordOfTable(WebElement table, String linePattern){
		By optionBy = By.xpath("./tbody/tr["+linePattern+"]");
		WebElement tr = findElementBy(table,optionBy);
		return tr;
	}
	
	/** 
	 *  Get the record lines (tr element) which matches pattern of table element. <p>  
	 *  The table format is like ".//table/tbody/tr[-]".
	 *  
	 *  @param table table element
	 *  @param  linePattern the pattern will match, with values such as "1", "last()", "location()>3" and so on
	 *  @return  list of record lines
	 */
	public List<WebElement> getRecordLinesOfTable(WebElement table, String linePattern){
		By optionBy = By.xpath("./tbody/tr["+linePattern+"]");
		this.logger.info("Finding elements with pattern like [table/tbody/tr["+linePattern+"]]");
		List<WebElement> trs = findElementsBy(table,optionBy);
		return trs;
	}

	/**
	 *  Get the cell (td element) which matches patterns of table element. <p>  
	 *  The table format is like ".//table/tbody/tr[]/td".
	 *  
	 * @param table table element without any pattern
	 * @param linePattern pattern for tr[] element
	 * @param colPattern pattern for td[] element
	 * @return cell of the table
	 */
	public WebElement getCellOfTable(WebElement table, String linePattern, String colPattern){
		By optionBy = By.xpath("./tbody/tr["+linePattern+"]/td["+colPattern+"]");
		return findElementBy(table,optionBy);
	}
	
	/**
	 *  Get the head (th element) which matches patterns of table element. <p>  
	 *  The table format is like ".//table/tbody/tr[]/th".
	 *  
	 * @param table table element without any pattern
	 * @param linePattern pattern for tr[] element
	 * @param colPattern pattern for th[] element
	 * @return head of the table
	 */
	public WebElement getHeadOfTable(WebElement table, String linePattern, String colPattern){
		By optionBy = By.xpath("./tbody/tr["+linePattern+"]/th["+colPattern+"]");
		return findElementBy(table,optionBy);
	}
	
	/**
	 *  Get the cells (td element) which matches patterns of table element. <p>  
	 *  The table format is like ".//table/tbody/tr[]/td".
	 *  
	 * @param table table element without any pattern
	 * @param linePattern pattern for tr[] element
	 * @param colPattern pattern for td[] element
	 * @return cells of single table
	 */
	public List<WebElement> getCellsOfTable(WebElement table, String linePattern, String colPattern){
		By optionBy = By.xpath("./tbody/tr["+linePattern+"]/td["+colPattern+"]");
		return findElementsBy(table,optionBy);
	}
	
	/**
	 * Emulate click operation.
	 * 
	 * @param element
	 * @param timeOut
	 */
	public void click(WebElement element, int timeOut) {

		try {
			clickTheClickable(element, System.currentTimeMillis(), timeOut);
		} catch (StaleElementReferenceException e) {
			this.logger.error("The element you clicked:[" + getElementLocator(element, ">") + "] is no longer exist!");
			Assert.fail("The element you clicked:[" + getElementLocator(element, ">") + "] is no longer exist!");
		} catch (Exception e) {
			this.logger.error("Failed to click element [" + getElementLocator(element, ">") + "]");
			Assert.fail("Failed to click element [" + getElementLocator(element, ">") + "]",e);
		}
		this.logger.info("Click on element [" + getElementLocator(element, ">") + "]");
	}

	/** 
	 * Re-click element if fails until specified time limit 
	 * 
	 * @param element
	 * @param startTime
	 * @param timeOut
	 */
	public void clickTheClickable(WebElement element, long startTime, int timeOut) throws Exception {
		try {
			element.click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeOut) {
				this.logger.warn(getElementLocator(element, ">") + " is unclickable !");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.warn(getElementLocator(element, ">") + " is unclickable, trying again...");
				clickTheClickable(element, startTime, timeOut);
			}
		}
	}

	/**
	 * Get current page title.
	 * 
	 */
	public String getTitle() {
		return driver.getTitle();
	}

	/**
	 * Get element text.
	 * 
	 */
	public String getText(WebElement element) {
		return element.getText().trim();
	}

	/**
	 * Get the attribute according to attribute name
	 * 
	 * @param attributeName 
	 */
	public String getAttributeText(WebElement element, String attributeName) {
		this.logger.info("Getting the ["+attributeName+"] attribute value of element ["+getElementLocator(element, ">")+"]...");
		String attributeText = element.getAttribute(attributeName).trim();
		this.logger.info("Got the ["+attributeName+"] attribute value of element ["+getElementLocator(element, ">")+"]: "+attributeText);
		return attributeText;
	}
	
	/**
	 * Get the attribute values of a list of elements.
	 * 
	 * @param attributeName
	 */
	public List<String> getMultipleAttributesText(List<WebElement> elements, String attributeName) {
		List<String> allAttrValues = new ArrayList<String>();
		String eleAttr;
		this.logger.info("Getting the ["+attributeName+"] attribute valuea of list of elements ["+ elements+"]...");
		for(WebElement ele : elements){
			eleAttr = getAttributeText(ele,attributeName);
			allAttrValues.add(eleAttr);
		}
		return allAttrValues;
	}
	
	/**
	 * Get the text of a list of elements.
	 * 
	 */
	public List<String> getMultipleTexts(List<WebElement> elements) {
		List<String> allTexts = new ArrayList<String>();
		String eleText;
		this.logger.info("Getting the texts of list of elements ["+ elements+"]...");
		for(WebElement ele:elements){
			eleText = getText(ele);
			allTexts.add(eleText);
		}
		return allTexts;
	}

	/**
	 * Clear the value in field.
	 * 
	 */
	public void clear(WebElement element) {
		try {
			element.clear();
		} catch (Exception e) {
			this.logger.error("Failed to clear [" + getElementLocator(element, ">") + "] contents!");
		}
		this.logger.info("Clear the content in [" + getElementLocator(element, ">") + "].");
	}

	/**
	 * Before entering content into field, clear content first.
	 * 
	 */
	public void typeAfterClear(WebElement element, String content) {
		try {
			element.clear();
			this.logger.info("Inputting：[" + content + "] into [" + getElementLocator(element, ">") + "]");
			element.sendKeys(content);
			this.logger.info("Input ["+ content +"] completed. ");
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("Failed to type the [" + content + "] to [" + getElementLocator(element, ">") + "]");
			Assert.fail("Failed to type the [" + content + "] to [" + getElementLocator(element, ">") + "]");
		}
		
	}
	
	/**
	 *  Click on element directly without clearing content.
	 *  
	 */
	public void typeDirectly(WebElement element, String content) {
		try {
			element.sendKeys(content);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("Failed to type the [" + content + "] to [" + getElementLocator(element, ">") + "]");
			Assert.fail("Failed to type the [" + content + "] to [" + getElementLocator(element, ">") + "]");
		}
		this.logger.info("Inputting：[" + content + "] into [" + getElementLocator(element, ">") + "]");
	}
	
	/**
	 * Press combination keys on keyboard like Ctrl+A, Ctrl+C.
	 * 
	 * @param element 
	 * @param key  functional keys on keyboard, like Ctrl, Alt
	 * @param keyword alphabetic, numbers on keyboard
	 */
	public void pressFunctionalKey(WebElement onElement, Keys key, String keyword) {
		Actions builder = new Actions(driver);
		builder.sendKeys(onElement, Keys.chord(key, keyword)).build().perform();
	}

	/**
	 * Press key on board.<br/>
	 * 敲击键盘上的按键
	 * 
	 * @param onElement
	 * @param key
	 */
	public void pressKey(WebElement onElement, Keys key) {
		Actions builder = new Actions(driver);
		builder.sendKeys(onElement, key).build().perform();
	}
	
	/**
	 * Press key on board.
	 * 
	 * @param onElement
	 * @param key
	 */
	public void pressKey(WebElement onElement, String key) {
		Actions builder = new Actions(driver);
		builder.sendKeys(onElement, key).build().perform();
	}
	
	/**
	 * Focus on element. Right click it first and then press ESC key.
	 * 
	 * @param onElement
	 */
	public void PutfocusOn(WebElement onElement){
		Actions builder = new Actions(driver);
		Actions rightClick = builder.contextClick(onElement);
		waitForFixedMilliseconds(5000);
		rightClick.sendKeys(Keys.ESCAPE).build().perform();;
	}
	
	/**
	 * When AJAX is trying to read large JSON files, this method works.
	 * 
	 */
	public void waitForAjaxDone(int timeOut) {
		try {
			(new WebDriverWait(driver, timeOut)).until(new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver d) {
					JavascriptExecutor js = (JavascriptExecutor) d;
					return (Boolean) js.executeScript("return jQuery.active == 0");    // Jquery.active = 0 means the AJAX activity is completed
				}
			}); 
		} catch (TimeoutException e) {
			this.logger.error("Time out!! " + timeOut + " seconds passed  but the jquery state ：[!=0]");
			new TestResultListener().onTestFailure(itestResult);
			quit();
		}

	}

	/**
	 * To verify if the text got is as expected.
	 * 
	 * @param actual
	 * @param expected
	 */
	public void isTextCorrect(String actual, String expected) {
		try {
			Assert.assertEquals(actual, expected);
		} catch (AssertionError e) {
			this.logger.error("The expected text is [" + expected + "] but found [" + actual + "] instead.");
			Assert.fail("The expected text is [" + expected + "] but found [" + actual + "] instead.");

		}
		this.logger.info("The expected text [" + expected + "] is found.");
	}

	/**
	 * Get the Alert object after waiting some moment.<p>
	 * 等待Alert窗口弹出并获取它
	 * 
	 */
	public Alert switchToAlertAfterWait(long waitMillisecondsForAlert){
		final int ONE_ROUND_WAIT = 1000;                       
		Alert alert = null;
		long currentTime = System.currentTimeMillis(); 
		long endTime = currentTime + waitMillisecondsForAlert;
		
		while(currentTime < endTime){
			try {
				alert = driver.switchTo().alert();
				logger.info("Successfully got the alert prompt with content："+ alert.getText());
				break;
			} catch (NoAlertPresentException e) {
				logger.info("Alert prompt is not got. Keep waiting ["+ONE_ROUND_WAIT/1000+"] seconds and try again...");
				pause(ONE_ROUND_WAIT*8);
				currentTime += ONE_ROUND_WAIT*8;        //alert设置等待时间短些
			}
		}
		return alert;
	}               
	
	/**
	 * Accept the Alert window. <p>
	 * 在alert弹出框上点击确定
	 * 
	 * @param waitMillisecondsForAlert
	 */
	public void acceptAlert(long waitMillisecondsForAlert){
		switchToAlertAfterWait(waitMillisecondsForAlert).accept();
	}
	
	/**
	 * Get the text of alert. <p>
	 * 获取alert窗口的文本
	 * 
	 * @param waitMillisecondsForAlert
	 * @return
	 */
	public String getAlertText(int waitMillisecondsForAlert){
		return switchToAlertAfterWait(waitMillisecondsForAlert).getText();
	}
	
	/**
	 * Accept the Alert window recursively if it exists. <p>
	 * 在alert弹出框上点击确定，如果点击确定后又弹出一个框，则再点击确定，直到全部关闭为止.(此方法除非是特殊情况，否则不推荐使用)
	 * 
	 * @param waitMillisecondsForAlert
	 */
	public void acceptAlertIfExist(long waitMillisecondsForAlert){
		while(switchToAlertAfterWait(waitMillisecondsForAlert)!=null){
			switchToAlertAfterWait(waitMillisecondsForAlert).accept();
		}
	}

	/** 
	 * Enter Start and End time to time fields. <p>
	 * 输入开始和结束时间
	 * 
	 * @param start format is as 2015-09-05 00:00 or 2015-09-05 00:00:00
	 * @param end the same format as start
	 * @return  the start time
	 */
	public String inputTimeToControl(String start, WebElement startTime, String end, WebElement endTime){
		typeAfterClear(startTime, start);
		typeAfterClear(endTime, end);
		this.logger.info("Input start time ["+ start + "] to element ["+ getElementLocator(startTime, ">") +"] and end time to element ["+  getElementLocator(endTime, ">") + "].");
		return start;
	}
	
	/**
	 * Pause the case for specified time.<p>
	 * 暂停指定时间
	 * 
	 */
	public void pause(int sleepTime) {
		if (sleepTime <= 0) {
			return;
		}
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			logger.error("Pause interrupted...");
			e.printStackTrace();
		}
	}

	/**
	 * Quit the driver
	 *  
	 */
	public void quit() {
		this.logger.info("Ending this driver...");
		driver.quit();
	}

	/**
	 * Switch to the frame according to frame Id or Name. <p>
	 * 切换到指定id或name的 frame
	 * 
	 * @param nameOrId
	 * 
	 */
	public void inFrame(String nameOrId) {
		driver.switchTo().frame(nameOrId);
	}

	/**
	 * Switch frame according to frame number.
	 * 
	 * @param num
	 */
	public void inFrame(int num) {
		driver.switchTo().frame(num);
	}

	/**
	 * Get out of frame. <p>
	 * 回到最外层document下
	 * 
	 */
	public void defaultFrame() {
		driver.switchTo().defaultContent();
	}
	
	/**
	 * Switch frame according to frame locator
	 * 
	 */
	public void switchFrame(WebElement element) {
		try {
			this.logger.info("Start switching to frame [" + getElementLocator(element, ">") + "]..");
			driver.switchTo().frame(element);
		} catch (Exception e) {
			this.logger.info("Switch to frame [" + getElementLocator(element, ">") + "] failed.");
			Assert.fail("Switch to frame [" + getElementLocator(element, ">") + "] failed");
		}
		this.logger.info("Switch to frame [" + getElementLocator(element, ">") + "] successed");
	}

	/**
	 * Select the option using text.
	 * 
	 * @param element
	 * @param text
	 */
	public void selectByVisibleText(WebElement element, String text) {
		Select s = new Select(element);
		try{
			this.logger.info("Selecting text ["+text+"] of element [" +  getElementLocator(element, ">") +"]...");
			s.selectByVisibleText(text);
			this.logger.info("option value ["+text+"] has been selected.");
		}catch(Exception e){
			this.logger.error("Fail to select text ["+text+"] of element [" +  getElementLocator(element, ">") +"].");
			Assert.fail("Fail to select text ["+text+"] of element [" +  getElementLocator(element, ">") +"].");
		}
		
		}
	
	/**
	 * Select the option using value attribute not the text value.
	 * 
	 * @param element
	 * @param value
	 */
	public void selectByValue(WebElement element, String value) {
		Select s = new Select(element);
		try{
			this.logger.info("Selecting option with attribute ["+value+"] of element [" +  getElementLocator(element, ">") +"]...");
			s.selectByValue(value);
			this.logger.info("option with attribute ["+value+"] has been selected.");
		}catch(Exception e){
			this.logger.error("Fail to select option with attribute ["+value+"] of element [" +  getElementLocator(element, ">") +"].");
			Assert.fail("Fail to select option with attribute ["+value+"] of element [" +  getElementLocator(element, ">") +"].");
		}
		
	}

	/**
	 * Select the option using index. <p>
	 * 根据索引选择元素
	 * 
	 * @param element
	 * @param index
	 */
	public void selectByIndex(WebElement element, int index) {
		Select s = new Select(element);
		try{
			this.logger.info("Selecting option["+index+"] of element [" +  getElementLocator(element, ">") +"]...");
			s.selectByIndex(index);
			this.logger.info("option["+index+"] has been selected.");
		}catch(Exception e){
			this.logger.error("Fail to select option["+index+"] of element [" +  getElementLocator(element, ">") +"].");
			Assert.fail("Fail to select option["+index+"] of element [" +  getElementLocator(element, ">") +"].");
		}
		
	}

	/** Check if the checkbox is selected or not. 
	 * 
	 */
	public boolean isCheckboxSelected(String locator) {
		if (findElementBy(By.xpath("locator")).isSelected() == true) {
			this.logger.info("CheckBox: " + getElementLocator(findElementBy(By.xpath("locator")), ">") + " is checked on. ");
			return true;
		} else{
			this.logger.info("CheckBox: " + getElementLocator(findElementBy(By.xpath("locator")), ">") + " is checked off. ");
		}
		return false;
	}

	/**
	 * Get all the option elements which are now selected. For multiple select field. <p>
	 * 
	 * */
	public List<WebElement> getAllSelectedOptions(By by){
		List<WebElement> options = null;
		Select s = new Select(driver.findElement(by));
			options =  s.getAllSelectedOptions();
			return options;
	}
	
	/**
	 * Get all the option elements belongs to this Select.
	 * 
	 * */
	public List<WebElement> getOptions(WebElement select){
		List<WebElement> options = null;
		Select s = new Select(select);
		options =  s.getOptions();
		return options;
	}

	/**
	 * Execute js code directly.<p>
	 * 执行js代码
	 * 
	 * @param js JavaScript代码
	 * 
	 * */
	public void executeJS(String js) {
		this.logger.info("Executing js code：[" + js + "]...");
		((JavascriptExecutor) driver).executeScript(js);
	}
	
	/**
	 * Executing js method upon to objects. <p>
	 * 
	 * Ex：seleniumUtil.executeJS("arguments[0].click();", seleniumUtil.findElementBy(xxxxxx));
	 * 
	 */
	public void executeJS(String js, Object... args) {
		((JavascriptExecutor) driver).executeScript(js, args);
		this.logger.info("Executing js code：[" + js + "]...");
	}
	
	/**
	 * Get the value of input field. <p>
	 * For some input field which has no value attribute, this method can get is as well.
	 * 
	 * @param attr attribute name
	 * @param attrValue attribute value
	 */
	public String getInputValue(String attr,String attrValue) {
		String value = null;
		switch(attr.toLowerCase()){
		case "name":
			 String jsName = "return document.getElementsByName('"+attrValue+"')[0].value;";
			 value = (String)((JavascriptExecutor) driver).executeScript(jsName);
			 break;
			
		case "id":
			 String jsId = "return document.getElementById('"+attrValue+"').value;";
			 value = (String)((JavascriptExecutor) driver).executeScript(jsId);
			 break;
		
			default:
				this.logger.error("The attribute is not defined:"+attr);
				Assert.fail("The attribute is not defined:"+attr);
		}
		return value;

	}

	/**
	 * Open the specified URL address
	 * 
	 * */
	public void get(String url) {
		driver.get(url);
		this.logger.info("Opening the page:[" + url + "]");
	}
	
	/**
	 * Get current url. <br/>
	 * 获取当前页面的url
	 * 
	 * */
	public String getCurrentUrl() {
		this.logger.info("Curernt page url is:[" + driver.getCurrentUrl() + "]");
		return driver.getCurrentUrl();
	}

	/**
	 * Close the browser.
	 * 
	 */
	public void close() {
		driver.close();
	}

	/**
	 * Refresh current page.
	 * 
	 */
	public void refresh() {
		driver.navigate().refresh();
		this.logger.info("Page is refreshed !");
	}

	/**
	 * Back to previous page.
	 * 
	 */
	public void back() {
		driver.navigate().back();
	}

	/**
	 * Go to the next page.
	 * 
	 */
	public void forward() {
		driver.navigate().forward();
	}

	/**
	 * Emulate the keystroke using Robot. <p>
	 * Robot模拟键盘按一次某键
	 * 
	 * @param key 按下的键
	 * @param hide 是否隐藏按下的键
	 * */
	public void emuKeystroke(int key, boolean hide) {
		try {
			if(hide == true){
				logger.info("Emulates keyboard stroke: *");
			}else{
				logger.info("Emulates keyboard stroke: "+ (char)key);
			}
			Robot robot = new Robot();
			robot.keyPress(key);
			robot.keyRelease(key);
		} catch (AWTException e) {
			logger.error(e.getCause().getMessage());  
		}
		
	}
	
	/**
	 * Emulate the keystroke using Robot. <p>
	 * Robot模拟键盘按一次某键
	 * 
	 * @param button 鼠标按键，对应参考：左键 1024，右键2048，中键4096
	 * */
	public void emuMouseClick(int button) {
		try {
			logger.info("Emulates mouse click: "+ (char)button);
			Robot robot = new Robot();
			robot.mousePress(button);
			robot.mouseRelease(button);
		} catch (AWTException e) {
			logger.error(e.getCause().getMessage());  
		}
		
	}
	
	/**
	 * Emulate mouse move to element operation.
	 * 
	 */
	public void mouseMoveToElement(WebElement element) {
		Actions builder = new Actions(driver);
		try{
			Actions mouse = builder.moveToElement(element);
			mouse.perform();
		}catch(MoveTargetOutOfBoundsException exception){
			logger.error("Can not move to the target element.  " + exception.getMessage());
			
		}
	}
	
	/**
	 * Emulate mouse right-click operation on element.
	 * 
	 */
	public void mouseRightClick(WebElement onElement) {
		Actions builder = new Actions(driver);
		Actions mouse = builder.contextClick(onElement);
		mouse.build().perform();
	}

	/** 
	 * Get element CSS value. 
	 * 
	 */
	public String getCSSValue(WebElement e, String key) {

		return e.getCssValue(key);
	}

	/** 
	 * Implicitly wait provided by WebDriver tool. 
	 * 
	 */
	public void implicitlyWait(int timeOut) {
		driver.manage().timeouts().implicitlyWait(timeOut/1000, TimeUnit.SECONDS);
	}

	/** 
	 * Set the timeout limit for asynchronous scripts, allowing the timeout for asynchronous scripts responding. 
	 * 
	 */
	public void setScriptTimeout(int timeOut) {
		driver.manage().timeouts().setScriptTimeout(timeOut/1000, TimeUnit.SECONDS);
	}

	/**
	 * Wait for page to load completed.
	 * 
	 */
	public void waitForPageLoading(int timeOut) {
		driver.manage().timeouts().pageLoadTimeout(timeOut/1000, TimeUnit.SECONDS);
	}

	/**
	 * Get the element locator by filtering it from printed element object info.
	 * 
	 * @param element  target element
	 * @param separator the separator for fetching the main body of locator, usually as ">", "["
	 * @return main body of element locator
	 */
	public String getElementLocator(WebElement element, String separator) {
		String text = element.toString();
		String expect = null;
		try {
			expect = text.substring(text.indexOf(separator) + 1, text.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("failed to find the string [" + separator + "]");

		}
		return expect;

	}

	/**
	 * For some Windows ui that selenium can not handle, this trick helps.
	 * 
	 */
	public void loginOnWinGUI(String username, String password, String url) {
		driver.get(username + ":" + password + "@" + url);
	}

	/**
	 * Check if element is displayed or not.
	 *  
	 */
	public boolean isDisplayed(WebElement element) {
		boolean isDisplay = false;
		if (element.isDisplayed()) {
			this.logger.info("The element: [" + getElementLocator(element, ">") + "] is displayed");
			isDisplay = true;
		} else if (element.isDisplayed() == false) {
			logger.warn("The element: [" + getElementLocator(element, ">") + "] is not displayed");
			isDisplay = false;
		}
		return isDisplay;
	}
	
	/**
	 * Check if element is enabled or not.
	 *  
	 */
	public boolean isEnabled(WebElement element){
		boolean enable = false;
		if (element.isEnabled()) {
			this.logger.info("The element: [" + getElementLocator(element, ">") + "] is enabled !");
			enable = true;
		} else if (!element.isEnabled()) {
			logger.warn("The element: [" + getElementLocator(element, ">") + "] is not enabled !");
			enable = false;
		}
		return enable;
	}
	
	/**
	 * Verify if element which is found by father element exists or not. <p>
	 * 验证从父节点是否可以查找到子节点
	 * 
	 */
	public  boolean isSubElementExist(WebElement father, String child){
		try{
			findElementBy(father, child);
			return true;
		}catch(NoSuchElementException e){
			return false;
		}
	}
	
	/**
	 * Verify if element which is found by father element is displayed or not.
	 * 
	 */
	public boolean isSubElementDisplayed(WebElement father, String child){
		boolean rtn = true;
		try{
			WebElement element = findElementBy(father, child);
			if(!element.isDisplayed()){
				rtn = false;
			}
		}catch(NoSuchElementException e){
			rtn = false;
		}
		return rtn;
	}
	
	/**
	 * Verify if element is selected or not. 
	 * 
	 */
	public boolean isSelected(WebElement element) {
		boolean flag = false;
		if (element.isSelected() == true) {
			this.logger.info("The element: [" + getElementLocator(element, ">") + "] is selected !");
			flag = true;
		} else if (element.isSelected() == false) {
			this.logger.info("The element: [" + getElementLocator(element, ">") + "] is not selected !");
			flag = false;
		}
		return flag;
	}

	/**
	 *  Get current window width. 
	 * 
	 */
	public double getScreenWidth() {
		return java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}

	/** 
	 * Switch to prompted window after wait for some milliseconds
	 * 
	 * @return   Previous window handle
	 * */
	public String switchToPromptedWindow(Set<String> before, long waitForMilliseconds, String expectedWindowName){
		String foundNewWindow = getPromptedWindow(before, waitForMilliseconds);
		if(foundNewWindow != null){
			String oldWin = getWindowHandle();
			switchToWindow(foundNewWindow);
			return oldWin;
		}
		throw new AssertionError("Time out waiting for " + expectedWindowName + " window to prompted "
			+ "out within " + (waitForMilliseconds/1000) + " seconds. ");
	}
	
	public String switchToPromptedWindow(Set<String> before, long waitForMilliseconds){
		return switchToPromptedWindow(before, waitForMilliseconds, "New Window");
	}
	
	/**
	 * Get the prompted window handle. <br/>
	 * 获取新弹出窗口的句柄
	 * 
	 * @return 有新窗口弹出就返回新窗口句柄，否则返回null
	 * */
	public String getPromptedWindow(Set<String> before, long waitForMilliseconds){
		final long oneRoundWait = 500;
		final long deadLine = System.currentTimeMillis() + waitForMilliseconds;
		
		String foundNewWindow = null;
		while(true){
			List<String> after = new ArrayList<String>(getWindowHandles());
			after.removeAll(before);
			if(after.size() > 0){
				foundNewWindow = after.get(0);
				return foundNewWindow;
			}
			waitForFixedMilliseconds(oneRoundWait);
			if(System.currentTimeMillis() > deadLine){
				return null;
			} 
		}
	}
	
	
	/** 
	 * Get all opened window handlers. 
	 *
	 */
	public Set<String> getWindowHandles(){
		Set<String> hanldes = driver.getWindowHandles();
		logger.info("Number of windows： " + hanldes.size());
		return hanldes;
	}

	/** 
	 * Get current window handler. <br/>
	 * 返回当前窗口句柄
	 * 
	 */
	public String getWindowHandle(){
		return driver.getWindowHandle();
	}
	
	/**
	 *  Switch to the specified window.
	 *  
	 */
	public void switchToWindow(String windowHandle){
		switchTo().window(windowHandle);
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	private TargetLocator switchTo(){
		return driver.switchTo();
	}

	/** 
	 * Wait for fixed period time by milliseconds.
	 * 
	 */
	public void waitForFixedMilliseconds(long milliseconds){
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Wait for several seconds until the js could perceive changes on page.
	 * 
	 */
	public void waitForJsPerceivableSeconds(){
		waitForFixedMilliseconds(600);
	}
	
	/**
	 * Wait for several seconds until human could perceive changes on page.
	 * 
	 */
	public void waitForHumanPerceivableSeconds(){
		waitForFixedMilliseconds(1000);
	}
	
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}
	
	/*  
	 * Get the user-input content to command line.
	 * Just for debugging, will be deprecated later.
	 * 
	 */
	public String valiCode(){
		this.logger.info("Please manually inputting the validation code here: ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		return scan.nextLine();
	}
	
	/*  
	 * Get the user-input content to command line.
	 * Just for debugging, will be deprecated later.
	 * 
	 */
	public String valiCodeByUI(){
		this.logger.info("Please manually inputting the validation code here: ");
		return showInputDialogAndGetValue();
	}
	
	
	
	private String showInputDialogAndGetValue() {
		  String returnString = JOptionPane.showInputDialog("Please enter the validation code here: ");
		  JOptionPane.showMessageDialog(null, "您输入的验证码是："+returnString);
		  return returnString;
		 }
	
	/**
	 * Add cookies. No longer used.
	 * 
	 * 
	public void addCookies(int sleepTime) {
		pause(sleepTime);
		Set<Cookie> cookies = driver.manage().getCookies();
		for (Cookie c : cookies) {
			System.out.println(c.getName() + "->" + c.getValue());
			if (c.getName().equals("logisticSessionid")) {
				Cookie cook = new Cookie(c.getName(), c.getValue());
				driver.manage().addCookie(cook);
				System.out.println(c.getName() + "->" + c.getValue());
				System.out.println("添加成功");
			} else {
				System.out.println("没有找到logisticSessionid");
			}

		}
	}
*/
	
}
