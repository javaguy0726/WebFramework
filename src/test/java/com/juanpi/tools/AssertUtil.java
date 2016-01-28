package com.juanpi.tools;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

public class AssertUtil {

	private SeleniumUtil seleniumUtil;
	private Logger logger;
	
	public AssertUtil(SeleniumUtil sele){
		this.logger = Logger.getLogger(AssertUtil.class.getName());
		this.seleniumUtil = sele;
	}
	
	/**
	 * Assert element contains specified text.<br/>
	 * @param element
	 * @param text
	 */
	public void assertElementTextContain(WebElement element, String text) {
		String str = seleniumUtil.getText(element);
		this.logger.info("Verifying if the element ["+ seleniumUtil.getElementLocator(element, ">") +"] contains text ["+text+"]...");
		Assert.assertTrue(str.contains(text), "Element ["+seleniumUtil.getElementLocator(element, ">") + "] does not contain "+text+".");
	}
	
	/**
	 * Assert element text contains specified text.<p>
	 * @param element
	 * @param text
	 */
	public void assertTextContain(String elementText, String text) {
		this.logger.info("Verifying if the element text ["+ elementText +"] contains text ["+text+"]...");
		Assert.assertTrue(elementText.contains(text), "Element Text ["+elementText + "] does not contain "+text+".");
	}
	
	/**
	 * Assert element is equal to specified text. <p>
	 * 验证元素的文本值是否相等
	 * 
	 * @param element
	 * @param text
	 */
	public void assertElementTextEquals(WebElement element, String text) {
		String str = seleniumUtil.getText(element);
		this.logger.info("Verifying if the element ["+ seleniumUtil.getElementLocator(element, ">") +"] text is equal to ["+text+"]...");
		Assert.assertTrue(str.equals(text), "Element ["+seleniumUtil.getElementLocator(element, ">") + "] text is not equal to"+text+".");
	}
	
	/**
	 * Assert element is equal to specified text. <p>
	 * 验证元素的文本值是否相等,分隔符是空格
	 * 
	 * @param element
	 * @param text
	 */
	public void assertElementPartialTextEquals(WebElement element, String text, int index) {
		assertElementPartialTextEquals(element, text, index, " ");
	}
	
	/**
	 * Assert element is equal to partial specified text. <p>
	 * 验证元素的指定部分文本值是否相等
	 * 
	 * @param element
	 * @param text
	 * @param index
	 * @param seperator
	 */
	public void assertElementPartialTextEquals(WebElement element, String text, int index, String seperator) {
		String str = seleniumUtil.getText(element);
		String[] splitted = StringUtils.split(str, seperator);
		this.logger.info("Verifying if the element's ["+ seleniumUtil.getElementLocator(element, ">") +"] partial text is equal to ["+text+"]...");
		Assert.assertTrue(splitted[index].equals(text), "Element ["+seleniumUtil.getElementLocator(element, ">") + "] text is not equal to"+text+".");
	}
	
	/**
	 * Assert element text is equal to specified text.<br/>
	 * @param element
	 * @param text
	 */
	public void assertTextEquals(String elementText, String text) {
		this.logger.info("Verifying if the element text ["+ elementText +"] text is equal to ["+text+"]...");
		Assert.assertTrue(elementText.equals(text), "Element text ["+ elementText + "] text is not equal to ["+text+"].");
	}
	
	/**
	 * Assert elements texts are equal to identical text.<p>
	 * @param element
	 * @param text 
	 */
	public void assertElementsTextEquals(List<WebElement> elements, String text) {
		String str;
		this.logger.info("Verifying if the list of elements' ["+ elements +"] text are equal to ["+text+"]...");
		for(WebElement element : elements){
			str = seleniumUtil.getText(element);
			Assert.assertEquals(str, text, "Elements ["+seleniumUtil.getElementLocator(element, ">") + "] text are not equal to"+text+".");
		}
	}
	
	/**
	 * Assert random elements texts are equal to identical text. <p>
	 * 
	 * @param elements
	 * @param num size of sub list
	 */
	public void assertRandomElementsTextEquals(List<WebElement> elements, int num, String text) {
		String str;
		this.logger.info("Verifying if random sub list of elements' ["+ elements +"] text are equal to ["+text+"]...");
		List<WebElement> random =  RandomUtil.randomSubList(elements, num);
		for(WebElement element : random){
			str = seleniumUtil.getText(element);
			Assert.assertEquals(str, text, "Elements ["+ seleniumUtil.getElementLocator(element, ">") + "] text are not equal to"+text+".");
		}
	}
	
	/**
	 * Assert random elements texts contain identical text. <p>
	 * 验证元素列表中的元素都含有某个文本
	 * 
	 * @param elements
	 * @param num size of sub list
	 */
	public void assertRandomElementsTextContains(List<WebElement> elements, int num, String text) {
		String str;
		this.logger.info("Verifying if random sub list of elements' ["+ elements +"] text contain ["+text+"]...");
		List<WebElement> random =  RandomUtil.randomSubList(elements, num);
		for(WebElement element : random){
			str = seleniumUtil.getText(element);
			Assert.assertTrue(str.contains(text), "Elements ["+seleniumUtil.getElementLocator(element, ">") + "] text do not contain ["+text+"].");
		}
	}
	
	/**
	 * Assert date is in expected range. <p>
	 * 验证时间区间
	 * 
	 * @param begin
	 * @param end
	 * @param target
	 */
	public void assertDateBetween(String begin, String end, String target) {
		
		 Date beginDate = DateUtil.parseStringToDate(begin);
		 Date endDate = DateUtil.parseStringToDate(end);
		 Date targetDate = DateUtil.parseStringToDate(target);
		
		Assert.assertTrue(DateUtil.between(beginDate, endDate, targetDate));
	}
	
	/**
	 * Assert list of dates are in expected range. <p>
	 * 验证一组时间都在区间内
	 * 
	 * @param begin
	 * @param end
	 * @param target
	 */
	public void assertDateBetween(String begin, String end, List<String> targets) {
		for(String target : targets){
			assertDateBetween(begin, end, target);
		}
		
	}
	
	/**
	 * Assert only one record is found. <p>
	 * 验证元素列表中只有一个元素
	 * @param element
	 */
	public void assertSingleResultIsFound(List<WebElement> elements) {
		this.logger.info("Verifying elements ["+elements+"] has only one record...");
		Assert.assertTrue(elements.size()==1,"More than one result is found !");
	}
	
	/**
	 * Assert element is displayed. <p>
	 * 验证元素是否被显示
	 * @param element
	 */
	public void assertElementDisplayed(WebElement element) {
		this.logger.info("Verifying element ["+seleniumUtil.getElementLocator(element, ">")+"] has been displayed...");
		Assert.assertTrue(seleniumUtil.isDisplayed(element),"More than one result is found !");
	}
	
	/**
	 * Fail the assertion directly. <p>
	 * 使断言失败
	 * 
	 * @param message
	 */
	public void assertTrue(boolean condition){
		Assert.assertTrue(condition);
	}
	
	/**
	 * Fail the assert directly. <p>
	 * 
	 * @param message
	 */
	public void fail(String message){
		Assert.fail(message);
	}
	
	
	
	
	
}
