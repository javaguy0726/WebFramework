package com.dessert.pages.base;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import com.dessert.tools.DateUtil;
import com.dessert.tools.DbUtil;
import com.dessert.tools.ObjectHelper;
import com.dessert.tools.RandomUtil;
import com.dessert.tools.SeleniumUtil;
import com.dessert.tools.SshShellUtil;

/**
 * Convenient utility methods for all page objects. <p>
 * 为所有页面对象提供便利的工具方法。 
 * 
 *@author Renta
 */
public class BasePage {
	
	protected SeleniumUtil selenium = null;
	protected int pageTimeout;
	protected int picTimeout;
	protected int elementTimeout;
	protected int promptWindowTimeOut;
	protected String miUrl;
	protected String dbEnv;

//	protected static HashMap<Object, Object> shareValues = new HashMap<>();
	protected HashMap<Object, Object> shareValues = new HashMap<>();
	
	protected DbUtil dbUtil = null;
	protected SshShellUtil shellUtil = null;
	
	public BasePage(){
		
	}
	
	/**
	 * Set the context by using the parameters from Setup() method. <p>
	 * 设置页面需要使用的各种参数的值。
	 * 
	 * @param basePage
	 */
	public BasePage(BasePage basePage){
		this.selenium = basePage.selenium;
		this.pageTimeout = basePage.pageTimeout;
		this.picTimeout = basePage.picTimeout;
		this.elementTimeout = basePage.elementTimeout;
		this.promptWindowTimeOut = basePage.promptWindowTimeOut;
		this.miUrl = basePage.miUrl;
		this.dbEnv = basePage.dbEnv;
		this.shareValues = basePage.shareValues;
	}
	
	/**
	 * Open an url.<p>
	 * 打开目标地址。
	 * 
	 * @param url  目标地址
	 */
	public void open(String url){
		this.selenium.get(url);
	}
	
	/**
	 * Check if child element found by father element exists or not. <p>
	 * 已知父元素的子元素是否存在。 
	 * 
	 * @param element 父元素
	 * @param locator 子元素的定位
	 * @return true=存在 false=不存在
	 */
	public boolean isElementFoundExist(WebElement element, String locator){
		return this.selenium.isSubElementExist(element, locator);
	}
	
	/**
	 * Check if child element found by father element exists or not. <p>
	 * 已知父元素的子元素是否显示。 
	 * 
	 * @param element 父元素
	 * @param locator 子元素的定位
	 * @return true=存在 false=不存在
	 */
	public boolean isElementFoundDisplayed(WebElement element, String locator){
		return this.selenium.isSubElementDisplayed(element, locator);
	}
	
	/**
	 * Wait for specified number of seconds. <p>
	 * 等待指定的时间
	 * 
	 * @param seconds
	 */
	public void waitForFixedSeconds(int seconds){
		this.selenium.waitForFixedMilliseconds(seconds*1000);
	}

	/**
	 * Use autoIt to complete uploading pictures. Assume the choose file window has been opened. <p>
	 * 使用autoIt上传图片。假设前提条件是：选择文件窗口已经打开。
	 * 
	 * @param autoFileName
	 * @param imgName
	 * @param objTitle
	 */
	public void uploadPicByAutoIt(String autoFileName, String imgName, String objTitle){
		ObjectHelper uploadObject = new ObjectHelper();
		uploadObject.uploadUserPic(autoFileName, imgName, objTitle);
		waitForFixedSeconds(2);
	}
	
	/**
	 * Use autoIt to complete uploading excels. Assume the choose file window has been opened. <p>
	 * 使用autoIt上传excel文件。假设前提条件是：选择文件窗口已经打开。
	 * 
	 * @param autoFileName
	 * @param xlsName
	 * @param objTitle
	 */
	public void uploadXlsByAutoIt(String autoFileName, String xlsName, String objTitle){
		ObjectHelper uploadObject = new ObjectHelper();
		uploadObject.uploadExcel(autoFileName, xlsName, objTitle);
		waitForFixedSeconds(3);
	}
	
	/**
	 * Use autoIt to complete downloading excels. Assume the choose file window has been opened. <p>
	 * 使用autoIt下载excel文件。假设前提条件是：选择文件窗口已经打开。
	 * 
	 * @param autoFileName
	 * @param xlsName
	 * @param objTitle
	 */
	public void downloadXlsByAutoIt(String autoFileName, String xlsName, String objTitle, String alert){
		ObjectHelper uploadObject = new ObjectHelper();
		uploadObject.downloadExcel(autoFileName, xlsName, objTitle, alert);
		waitForFixedSeconds(5);
	}
	
	
	/**
	 * Emulate click operation. <p>
	 * 模拟点击操作
	 * 
	 * @param element
	 */
	public void click(WebElement element){
		this.selenium.click(element, elementTimeout);
	}
	
	/**
	 * Focus on target element. <p>
	 * 焦点放到目标元素上
	 * 
	 * @param element
	 */
	public void focusOnElement(WebElement element){
		this.selenium.PutfocusOn(element);
	}
	
	
	/**
	 * If the target element is not displayed, click on other element to let it displayed. <p>
	 * 如果目标元素未显示，则点击其他元素使其显示出来厚再点击它
	 * 
	 * @param target
	 * @param trigger
	 */
	public void clickAfterTriggered(WebElement target, WebElement trigger){
		boolean displayed = this.selenium.isDisplayed(target);
		if(displayed == false){
			this.selenium.click(trigger, elementTimeout);
			this.selenium.waitForElementToLoad(target, elementTimeout);
		}
		this.selenium.click(target, elementTimeout);
	}
	
	/**
	 * Input characters by keystroke. <p>
	 * 模拟键盘操作，按下一串字符
	 * 
	 * @param content 要用键盘按下的内容
	 * @param hide 是否隐藏字符
	 */
	public void robotKeystrokeInput(String content, boolean hide){
		byte[] bytes = content.getBytes();
		int  ascNum;
		for(byte b : bytes){
			ascNum = (int)b;
			this.selenium.emuKeystroke(ascNum, hide);
		}
	}
	
	/**
	 * Emulate keystroke. <p>
	 * 模拟键盘按下一键
	 * 
	 * @param key
	 * @param hide
	 */
	public void robotKeystroke(int key, boolean hide){
			this.selenium.emuKeystroke(key, hide);
	}
	
	/**
	 * Emulate keystroke. <p>
	 * 模拟键盘按[指定次数]的某键
	 * 
	 * @param key
	 * @param hide
	 * @param round
	 */
	public void robotKeystroke(int key, boolean hide, int round){
		for(int i = 0; i < round; i++){
			robotKeystroke(key, hide);
		}
	}
	
	/**
	 * Emulate mouse single click. <p>
	 * 模拟鼠标点击指定按键
	 * 
	 * @param button 鼠标按键，参考：左键 1024，右键2048，中键4096
	 */
	public void robotMouseSingleClick(int button){
			this.selenium.emuMouseClick(button);
	}
	
	/**
	 * Single click on element using JS. <p>
	 * 使用JS来执行单击某元素的操作
	 * 
	 * @param target 要点击的元素
	 */
	public void jsMouseSingleClick(WebElement target){
		waitForElementToLoad(target);
		this.selenium.executeJS("arguments[0].click();", target);
	}
	
	/**
	 * Input text into field after the field is cleared. <p>
	 * 清空内容后再次输入
	 * 
	 * @param element
	 * @param content
	 */
	public void typeAfterClear(WebElement element, String content){
		this.selenium.typeAfterClear(element, content);
	}
	
	/**
	 * Input text into field directly. <p>
	 * 直接输入内容
	 * 
	 * @param element
	 * @param content
	 */
	public void type(WebElement element, String content){
		this.selenium.typeDirectly(element, content);
	}
	
	/**
	 * Emulate moving mouse on an element. <p>
	 * 模拟鼠标在元素上移动
	 * 
	 * @param element
	 */
	public void mouseMoveToElement(WebElement element){
		this.selenium.mouseMoveToElement(element);
	}
	
	/**
	 * Move mouse to an area and click on link after drop down list is shown. <p>
	 * 移动鼠标到一个区域，等待下拉列表显示后 点击其中一个链接
	 * 
	 * @param element
	 */
	public void mouseMoveAndSelectDropdown(WebElement area, WebElement link){
		this.selenium.mouseMoveToElement(area);
		waitForElementTextToDisplay(link);
		click(link);
	}
	
	public void mouseMoveClickToNewWindow(WebElement area, WebElement link){
		this.selenium.mouseMoveToElement(area);
		waitForElementTextToDisplay(link);
		switchToNewWindow(link);
	}
	
	/**
	 * Get the attribute value of element according to attribute name. <p>
	 * 通过属性名获取元素属性值
	 * 
	 * @param element
	 * @param attributeName
	 * @return
	 */
	public String getAttribute(WebElement element, String attributeName){
		return this.selenium.getAttributeText(element, attributeName);
	}
	
	/**
	 * Get the attribute values of a list of elements. <p>
	 * 从元素列表中获取所有属性值
	 * 
	 * @param elements
	 * @param attributeName
	 * @return
	 */
	public List<String> getMultipleAttributesText(List<WebElement> elements, String attributeName){
		return this.selenium.getMultipleAttributesText(elements, attributeName);
	}
	
	/**
	 * Get the attribute values of a list of elements. <p>
	 * 从元素列表中获取所有文本值
	 * 
	 * @param elements
	 * @return
	 */
	public List<String> getMultipleTexts(List<WebElement> elements){
		return this.selenium.getMultipleTexts(elements);
	}
	
	/**
	 * Get element text. <p>
	 * 获取元素文本值
	 * 
	 * @param element
	 * @return
	 */
	public String getText(WebElement element){
		return this.selenium.getText(element);
	}
	
	/**
	 * Accept the Alert window. <p>
	 * 弹框上点击确定
	 * 
	 */
	public void acceptAlert(){
		 this.selenium.acceptAlert(promptWindowTimeOut);
	}
	
	/**
	 * Accept the Alert window. <p>
	 * 弹框上点击确定,如果连续出现弹出框，则一个个关闭直到关掉所有的
	 * 
	 */
	public void acceptAlertIfExist(){
		this.selenium.acceptAlertIfExist(promptWindowTimeOut);
	}
	
	/**
	 * Switch to specified window.<p>
	 * 切换到指定窗口
	 * 
	 * @param element
	 * @return 返回前一个窗口的句柄
	 */
	public void switchToWindow(String windowHandle){
		this.selenium.switchToWindow(windowHandle);
	}
	
	/**
	 * Click on element and switch to the new window. This will save and return the handle of previous window. <p>
	 * 点击元素并跳转到新打开的窗口
	 * 
	 * @param element
	 * @return 返回前一个窗口的句柄
	 */
	public String switchToNewWindow(WebElement element){
		Set<String> allWindows = this.selenium.getWindowHandles();
		click(element);
		String preWindow = this.selenium.switchToPromptedWindow(allWindows, promptWindowTimeOut);
		return preWindow;
	}
	
	/**
	 * Click on element and switch to the new window.<p>
	 * This will save and return the handle of previous window. <p>
	 * 点击元素,如果有新打开窗口，那么就跳转到新开窗口，如果没有新开窗口，就在本窗口打开新页面
	 * 
	 * @param element
	 * @return 返回前一个or当前窗口的句柄
	 */
	public String switchToNewWindowIfExist(WebElement element){
		Set<String> beforeWindows = this.selenium.getWindowHandles();
		click(element);
		String promptedWindow = this.selenium.getPromptedWindow(beforeWindows, promptWindowTimeOut);
		if(promptedWindow == null){  
			return this.selenium.getWindowHandle();
		}
		else{
			String preWindow = this.selenium.switchToPromptedWindow(beforeWindows, promptWindowTimeOut);
			return preWindow;
		}
		
	}
	
	/**
	 * Click on element and switch to the new window. If the element is not displayed, click other element to let it out first.<br/>
	 * This will save and return the handle of previous window. <p>
	 * 点击元素打开新窗口。如果元素不存在，点击其他元素使该元素先显示出来，然后再点击
	 * 
	 * @param element
	 * @return handle of previous window
	 */
	public String switchToNewWindowAfterTriggered(WebElement element, WebElement trigger){
		Set<String> allWindows = this.selenium.getWindowHandles();
		clickAfterTriggered(element, trigger);
		String preWindow = this.selenium.switchToPromptedWindow(allWindows, promptWindowTimeOut);
		return preWindow;
	}
	
	/**
	 * Click on element, switch to the new window and then switch back.<p>
	 * This will save and return the handle of previous window. <p>
	 * 点击元素打开新窗口后切回到前一窗口
	 * 
	 * @param element
	 * @return handle of next window
	 */
	public String switchBackAndForthWindow(WebElement element){
		String preWindow = switchToNewWindow(element);
		String nextWindow = this.selenium.getWindowHandle();
		this.selenium.switchToWindow(preWindow);
		return nextWindow;
	}
	
	/**
	 * Select options have relationships from multiple Selects. <p> 
	 * The parameter 'options' should be like this: a:b:c:d:e in CSV files. <p>
	 * 在选取级联下拉框的值。CSV文件中的格式是a:b:c
	 * 
	 * @param elements
	 * @param options
	 */
	public void selectCascadingOptions(List<WebElement> elements, String options){
		String[] sepcifiedText = options.split(":");
		WebElement element; 
		for(int i=0; i<elements.size();i++){
			element = elements.get(i);
			this.selenium.waitForSelectToLoadSpecifiedText(element, elementTimeout, sepcifiedText[i]);
			this.selenium.selectByVisibleText(element, sepcifiedText[i]);
		}
	}
	
	/**
	 * Choose single option in Select field. <p>
	 * 下拉框选择指定text的选项
	 * 
	 * @param element
	 * @param optionText
	 */
	public void selectOption(WebElement element, String optionText){
		  this.selenium.waitForSelectToLoadSpecifiedText(element, elementTimeout, optionText);
		  this.selenium.selectByVisibleText(element, optionText);
	}
	
	/**
	 * Choose single option in Select field. <p>
	 * 下拉框选择指定index的选项
	 * 
	 * @param element
	 * @param optionText
	 */
	public void selectOption(WebElement element, int index){
		this.selenium.waitForSelectToLoadIndexOfOption(element, elementTimeout, index);
		this.selenium.selectByIndex(element, index);
	}
	
	/**
	 * Get all the option texts. <p>
	 * 取所有数据内容
	 * 
	 * @param select dropdown element
	 */
	public List<String> getAllOptionTexts(WebElement select){
		List<WebElement> options = this.selenium.getOptions(select);
		List<String> optionTexts = new ArrayList<String>();
		for(WebElement opt : options){
			optionTexts.add(this.selenium.getText(opt));
		}
		return optionTexts;
	}
	
	/**
	 * Get current page url. <p>
	 * 获取当前页面url
	 * 
	 * @return 当前页url
	 */
	public String getCurrentUrl(){
		return this.selenium.getCurrentUrl();
	}
	
	/**
	 * This is used to wait the search results to be shown in table according to text value.<p>
	 * Format is like "./tbody/tr[-][./td[-]/-[contains(text(),'-')]]". <p>
	 * 根据目标元素文本内容来等待显示搜索结果在table中显示
	 * 
	 *  @param table the Table element
	 *  @param timeOut time limit
	 *  @param linePattern the expression in tr[]
	 *  @param colPattern the expression in td[]
	 *  @param textOwner the node that contains the text
	 *  @param text 
	 */
	public void waitForRecordLoadedInTable(WebElement table, String linePattern, String colPattern, String textOwner, String text){
		this.selenium.waitForTableToLoadSpecifiedRecord(table, elementTimeout, linePattern, colPattern, textOwner, text);
	}
	
	/**
	 * This is used to wait the search result (the search results are placed in divs.) to be loaded in table according to link text.<p>
	 * Format is like "div[.//-[contains(text(), '-')]]/extraPattern".<p>
	 * 根据链接文本来等待，直到元素在div下显示出来
	 * 
	 * @param div the div node used for finding.
	 * @param linkText  text link
	 */
	public void waitForRecordLoadedInDivision(WebElement div, String textOwner, String pattern, String linkText){
		this.selenium.waitForDivisionToLoadSpecifiedRecord(div, elementTimeout, textOwner,  pattern, linkText);
	}
	
	/** 
	 * Wait until specified element to be displayed on screen. <p>
	 * 等待直到指定元素显示
	 * 
	 * @param element target element
	 */
	public void waitForElementToLoad(WebElement element){
		this.selenium.waitForElementToLoad(element, elementTimeout);
	}
	
	/** 
	 * Wait until specified element to be found on screen. <p>
	 * 等待一段时间被查找出的子元素被显示出来
	 * 
	 * @param element target element
	 */
	public void waitForElementToLoad(WebElement father, String child){
		this.selenium.waitForElementToLoad(father, child, elementTimeout);
	}
	
	/** 
	 * Click on element several times until specified element to be found on screen. <p>
	 * 点击某个元素数次直到指定元素在页面上显示
	 * 
	 * @param element target element
	 */
	public void clickAndWaitForElementToLoad(WebElement element, WebElement father, String child, int times){
		for(int i = 0; i <= times; i++){
			boolean displayed = true; 
			final long oneRoundWait = 1000;
			final long deadLine = System.currentTimeMillis() + elementTimeout;
			click(element);
		
			while(!isElementFoundDisplayed(father, child)){
				this.selenium.waitForFixedMilliseconds(oneRoundWait);
				if(System.currentTimeMillis() > deadLine){
					displayed = false;
					break;
				}
			}
			
			if(displayed == true){
				break;
			}
		}
	}
	
	/** 
	 * Wait for picture or image to be fully uploaded. <p> 
	 * 等待图片上传并显示
	 * 
	 * */
	public void waitForAttributeChanged(WebElement element, String attributeName, String expectedAttribute){
		this.selenium.waitForAttributeChanged(element, attributeName, expectedAttribute, picTimeout);
	}
	
	/** 
	 * Wait for picture or image to be fully uploaded. <p> 
	 * 等待图片上传并显示出来
	 * 
	 * @param element       
	 * @param expectedAttribute   期望的属性值，通常是图片文件名，处理规则是
	 * 
	 * */
	public void waitForImgFullyUploaded(WebElement element, String expectedAttribute){
		 if(StringUtils.contains(expectedAttribute, "-")){
			 int begin = StringUtils.indexOf(expectedAttribute,"-");
			 int end = StringUtils.indexOf(expectedAttribute, ".");
			 String subAttribute = StringUtils.substring(expectedAttribute, begin, end);
			 expectedAttribute = StringUtils.replace(expectedAttribute, subAttribute,"");                  //将同一个尺寸的图片名字改为网站图片属性值
		 }
		 
		this.selenium.waitForImgAttributeChanged(element, "src", expectedAttribute, picTimeout);
	}
	
	/** 
	 * This is used to wait for attributes to be changed to identical value. <p> 
	 * 等待复数个元素的属性改变
	 * 
	 * */
	public void waitForImgsFullyUploaded(List<WebElement> elements, String expectedAttribute){
		 if(StringUtils.contains(expectedAttribute, "-")){
			 int begin = StringUtils.indexOf(expectedAttribute,"-");
			 int end = StringUtils.indexOf(expectedAttribute, ".");
			 String subAttribute = StringUtils.substring(expectedAttribute, begin, end);
			 expectedAttribute = StringUtils.replace(expectedAttribute, subAttribute,"");                  //将同一个尺寸的图片名字改为网站图片属性值
		 }
		this.selenium.waitForImgsAttributeChanged(elements, "src", expectedAttribute, picTimeout);
	}
	
	/**
	 * Wait until the specified element has text value. <p>
	 * 等待指定元素内容正确显示
	 * 
	 * @param element 
	 */
	public void waitForElementTextToDisplay(WebElement element){
		this.selenium.waitForTextToDisplay(element, elementTimeout);
	}
	
	/**
	 * Wait until the specified element text is correctly displayed in list of elements. <p>
	 * 等待指定元素内容存在于一组元素中
	 * 
	 * @param element node to generate and it should match a list of nodes (important thing).
	 * @param pattern used by findElements method
	 */
	public void waitForElementTextToExist(WebElement element, String pattern, String expectText){
		List<WebElement> elements = this.selenium.findElementsBy(element, pattern);
		this.selenium.waitForTextToBeGot(elements, elementTimeout, expectText);
	}
	
	/** 
	 * Launch a search in Manage site. <p>
	 * 执行一次后台的标准条件搜索
	 * 
	 * */
	public void performSearchInManage(WebElement typeSelect, String searchType, WebElement wordInput,  String searchWord, WebElement searchButton){
		selectOption(typeSelect, searchType);
		typeAfterClear(wordInput, searchWord);
		click(searchButton);
	}
	
	/** 
	 * Get the cell of a table element. <br/>
	 * Format is some like "//table/tbody/tr[1]/td[last()]". <p>
	 * 获取table中某个单元(cell)
	 *  
	 * @param table
	 * @param linePattern
	 * @param colPattern
	 * */
	public WebElement getCellOfTable(WebElement table, String linePattern, String colPattern){
		return this.selenium.getCellOfTable(table, linePattern, colPattern);
	}
	
	/** 
	 * Get the cell of a table element. <br/>
	 * Format is some like "//table/tbody/tr[1]/td[last()]+ ./a". <p>
	 * 获取table中某个单元(cell)，含有额外的匹配模式
	 *  
	 *  @param table
	 *  @param linePattern
	 *  @param colPattern
	 *  @param extraPattern pattern append to td
	 * */
	public WebElement getCellOfTable(WebElement table, String linePattern, String colPattern, String extraPattern){
		WebElement cell = this.selenium.getCellOfTable(table, linePattern, colPattern);
		return this.selenium.findElementBy(cell, extraPattern);
	}
	
	/** 
	 * Get the head of a table element. <br/>
	 * Format is some like "//table/tbody/tr[1]/th[last()]". <p>
	 *  获取表头部
	 *  
	 *  @param table
	 *  @param linePattern
	 *  @param colPattern
	 *  
	 * */
	public WebElement getHeadOfTable(WebElement table, String linePattern, String colPattern){
		return this.selenium.getHeadOfTable(table, linePattern, colPattern);
	}
	
	/** 
	 * Get the head cell of a table element. <br/>
	 * Format is some like "//table/tbody/tr[1]/th[last()]/a". <p>
	 *  获取表头部，含有额外的匹配模式
	 *  
	 *  @param table
	 *  @param linePattern
	 *  @param colPattern
	 *  @param extraPattern pattern append to th
	 *  
	 * */
	public WebElement getHeadOfTable(WebElement table, String linePattern, String colPattern, String extraPattern){
		WebElement head =  this.selenium.getHeadOfTable(table, linePattern, colPattern);
		return this.selenium.findElementBy(head, extraPattern);
	}
	
	/** 
	 * Get the cells of a table element. <br/>
	 * Format is some like "//table/tbody/tr[1]/td[last()]/a". <p>
	 * 获取table某一列的cells
	 *  
	 *  @param table
	 *  @param linePattern
	 *  @param colPattern
	 * */
	public List<WebElement> getCellsOfTable(WebElement table, String linePattern, String colPattern){
		return this.selenium.getCellsOfTable(table, linePattern, colPattern);
	}
	
	/**
	 *  Get the identical cell of all tables. <br/>
	 *  Format is like: "./tbody/tr[-]". <p>
	 *  获取一组tables的某个cell
	 *  
	 *  @param tables
	 *  @param linePattern
	 *  @param colPattern
	 */
	public List<WebElement> getCellOfAllTables(List<WebElement> tables, String linePattern, String colPattern){
		List<WebElement>  eachCells = new ArrayList<WebElement>();
		for(WebElement table : tables){
			eachCells.add(getCellOfTable(table, linePattern, colPattern));
		}
		return eachCells;
	}
	
	/**
	 *  Get the identical cell of all tables. <br/>
	 *  Format is like: "./tbody/tr[-]/a". <p>
	 *  获取一组tables的某个cell，含有其他pattern
	 *  
	 *  @param tables
	 *  @param linePattern
	 *  @param colPattern
	 *  @param extraPattern
	 */
	public List<WebElement> getCellOfAllTables(List<WebElement> tables, String linePattern, String colPattern, String extraPattern){
		List<WebElement>  eachCells = new ArrayList<WebElement>();
		for(WebElement table : tables){
			eachCells.add(getCellOfTable(table, linePattern, colPattern, extraPattern));
		}
		return eachCells;
	}
	
	/**
	 *  Get the identical head of all tables. <br/>
	 *  Format is like: "./tbody/th[-]". <p>
	 *  获取一组table的头
	 *  
	 *  @param tables
	 *  @param linePattern
	 *  @param colPattern
	 */
	public List<WebElement> getHeadOfAllTables(List<WebElement> tables, String linePattern, String colPattern){
		List<WebElement>  eachHeads = new ArrayList<WebElement>();
		for(WebElement table : tables){
			eachHeads.add(getHeadOfTable(table, linePattern, colPattern));
		}
		return eachHeads;
	}
	
	/**
	 *  Get the identical head of all tables. <br/>
	 *  Format is like: "./tbody/th[-]/a". <p>
	 *  获取一组table的头，含有其他pattern
	 *  
	 *  @param tables
	 *  @param linePattern
	 *  @param colPattern
	 *  @param extraPattern
	 */
	public List<WebElement> getHeadOfAllTables(List<WebElement> tables, String linePattern, String colPattern, String extraPattern){
		List<WebElement>  eachHeads = new ArrayList<WebElement>();
		for(WebElement table : tables){
			eachHeads.add(getHeadOfTable(table, linePattern, colPattern, extraPattern));
		}
		return eachHeads;
	}

	/**
	 *  Get single record line of a table. <br/>
	 *  Format is like: "./tbody/tr[-]". <p>
	 *  获取table的某一行记录
	 *  
	 *  @param table
	 *  @param linePattern
	 */
	public WebElement getRecordLineOfTable(WebElement table, String linePattern){
		 return  this.selenium.getSingleRecordOfTable(table, linePattern);
	}
	
	/**
	 *  Get the record lines of a table. <br/>
	 *  Format is like: "./tbody/tr[-]". <p>
	 *  获取table的某些行
	 *  
	 *  @param table
	 *  @param linePattern
	 */
	public List<WebElement> getRecordLinesOfTable(WebElement table, String linePattern){
		return  this.selenium.getRecordLinesOfTable(table, linePattern);
	}
	
	/**
	 *  Get the top record line of a table. <br/>
	 *  Format is like: "./tbody/tr[-]". <p>
	 *  获取table中第一行记录
	 *  
	 *  @param table
	 *  @param linePattern
	 */
	public WebElement getTopRecordLineOfTable(WebElement table, String linePattern){
		return  this.selenium.getRecordLinesOfTable(table, linePattern).get(0);
	}
	
	/**
	 *  Get the record lines of a div. <br/>
	 *  Format is like: ".//div[]". <p>
	 *  获取div下的所有行记录
	 *  
	 *  @param div
	 *  @param linePattern
	 */
	public List<WebElement> getRecordLinesOfDiv(WebElement div, String pattern){
		String by = ".//div[" + pattern + "]";
		return  this.selenium.findElementsBy(div, by);
	}
	
	/**
	 *  Get the single line of all tables. <br/>
	 *  Format is like: "./tbody/tr[-]". <p>
	 *  获取所有table的某一行记录
	 *  
	 *  @param tables
	 *  @param linePattern
	 */
	public List<WebElement> getRecordLineOfAllTables(List<WebElement> tables, String linePattern){
		List<WebElement>  eachLines = new ArrayList<WebElement>();
		for(WebElement table : tables){
			eachLines.add(this.selenium.getSingleRecordOfTable(table, linePattern));
		}
		return eachLines;
	}
	
	/**
	 * Get random element from a list. <p>
	 * 从元素列表中随机抽一个元素
	 * 
	 * @param elements
	 * @return
	 */
	
	public WebElement getRandomElementOfList(List<WebElement> elements){
		return RandomUtil.randomElement(elements);
	}
	
	
	/**
	 *  Get the random record lines of a table. <br/>
	 *  Format is like: "./tbody/tr[-]". <p>
	 *  获取一个table中随机的行数
	 *  
	 *  @param table 
	 *  @param linePattern tr的匹配模式
	 *  @param num 随机记录数
	 */
	public List<WebElement> getRandomRecordLinesOfTable(WebElement table, String linePattern, int num){
		return RandomUtil.randomSubList(this.selenium.getRecordLinesOfTable(table, linePattern), num) ;
	}
	
	/**
	 *  For seller - schedule page: Get the table object of schedule list. <p> 
	 *  根据档期开始时间，找到对应的table
	 * 
	 */
	public WebElement getScheduleBodyTableByStartTime(WebElement table, String startTime){
		return this.selenium.getSpecifiedTable(table, "tr/td/p[text()='"+startTime+"']");
	}
	
	/**
	 *  For seller - schedule page: Get the link of schedule list table. <p> 
	 *  根据档期开始时间，找到对应table的下的某个链接
	 * 
	 */
	public WebElement getScheduleTableLinkByStartTime(WebElement table, String startTime, String pattern){
		return this.selenium.findElementBy(getScheduleBodyTableByStartTime(table, startTime), ".//td//a["+pattern+"]");
	}
	
	/**
	 * Get sub-node elements based on ancestor-node elements. <p>
	 * 根据父列表获取子列表
	 * 
	 * @return sub elements under the list
	 */
	public List<WebElement> getSubElementsByList(List<WebElement> list , String optionBy){
		List<WebElement> subElements = new ArrayList<WebElement>(); 
		WebElement ele = null;
		for(WebElement e : list){
			ele = this.selenium.findElementBy(e, optionBy);
			subElements.add(ele);
		}
		return subElements;
	}
	
	/**
	 * Get sub-node elements' text based on ancestor-node elements. <p>
	 * 根据父列表获取子列表元素的文本列表
	 * 
	 * @param list
	 * @param optionBy
	 * @return
	 */
	public List<String> getSubElementsTextsByList(List<WebElement> list , String optionBy, String excludePattern){
		List<String> subTexts = new ArrayList<String>(); 
		WebElement ele = null;
		String text;
		for(WebElement e : list){
			ele = this.selenium.findElementBy(e, optionBy);
			text = getText(ele);
			text = StringUtils.removePattern(text, excludePattern);
			subTexts.add(text);
		}
		return subTexts;
	}
	
	/**
	 * Validate whether text is in list of elements. <p>
	 * 验证一组元素中是否包含某个元素的文本是指定值
	 * 
	 * @param elements
	 * @param value
	 * @return  exists for true
	 */
	public boolean elementsContainText(List<WebElement> elements , String text){
		boolean rtn = false;
		String eleTxt;
		for(int i = 0; i< elements.size(); i++){
			eleTxt = getText(elements.get(i));
			if(eleTxt.contains(text)){
				rtn = true;
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * Validate whether attribute exists in list of elements. <p>
	 * 验证属性是否存在于一组元素中
	 * 
	 * @param elements
	 * @param value
	 * @return  exists for true
	 */
	public boolean elementsContainAttribute(List<WebElement> elements, String attributeName, String value){
		boolean rtn = false;
		String eleAttr;
		for(int i = 0; i< elements.size(); i++){
			eleAttr = getAttribute(elements.get(i), attributeName);
			if(eleAttr.equals(value)){
				rtn = true;
				break;
			}
		}
		return rtn;
	}
	
	/**
	 * Generate random integer. <p>
	 * 生成随机整数, 并提供参照值
	 * 
	 * @param num 整数最大值
	 * @param refer 参照
	 * @return
	 */
	public int buildIntRandom(int num, int refer){
		if(refer > 0){
			if(num > refer){
				return RandomUtil.buildIntRandomBy(refer)+1;
			}else if(num < refer && num >= 0){
				return RandomUtil.buildIntRandomBy(num)+1;
			}else{
				return RandomUtil.buildIntRandomBy(num)-1;
			}
		}else{
			return RandomUtil.buildIntRandomBy(num)+1;
		}
		 
	}
	
	/**
	 * 	Fills up the time fields with relative date-time. <p>
	 * 输入相对于当前时间的开始时间和结束时间。输入时间格式为"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param startEle 开始时间元素
	 * @param startType 开始时间单位，可选值: 1=年、2=月、3=日、4=时、5=分、6=秒
	 * @param startRela 开始相对时间值,可为正负
	 * @param endEle 结束时间元素
	 * @param endType 结束时间单位，可选值: 1=年、2=月、3=日、4=时、5=分、6=秒
	 * @param endRela 结束相对时间值,可为正负
	 * @return
	 */
	public String inputTimeToControl(WebElement startEle, int startType, int startRela, WebElement endEle, int endType, int endRela){
		Date startDate, endDate;
		switch(startType){
			case 2:
				startDate = DateUtil.getRelativeMonth(startRela);
			break;	
			case 3: 
				startDate = DateUtil.getRelativeDay(startRela);
				break;
			case 5: 
				startDate = DateUtil.getRelativeMinute(startRela);
				break;		
			case 6: 
				startDate = DateUtil.getRelativeSecond(startRela);
				break;			
			default:
				startDate = DateUtil.getRelativeSecond(startRela);
		}
		
		switch(endType){
			case 2:
				endDate = DateUtil.getRelativeMonth(endRela);
			case 3: 
				endDate = DateUtil.getRelativeDay(endRela);
				break;
			case 5: 
				endDate = DateUtil.getRelativeMinute(endRela);
				break;		
			case 6: 
				endDate = DateUtil.getRelativeSecond(endRela);
				break;			
			default:
				endDate = DateUtil.getRelativeSecond(endRela);
		}
		
		String startTime = DateUtil.formatDatetime(startDate);
		String endTime = DateUtil.formatDatetime(endDate);	
		return this.selenium.inputTimeToControl(startTime, startEle, endTime, endEle);
		
	}
	
	/**
	 * Fills up the time fields with relative date-time (lacks second part). <p>
	 * 输入开始时间(绝对时间)和结束时间(相对时间),时间格式为"yyyy-MM-dd HH:mm"
	 * 
	 * @param startTime 开始时间
	 * @param startEle 开始时间元素
	 * @param endEle 结束时间元素
	 * @param endType 结束时间单位，可选值: 1=年、2=月、3=日、4=时、5=分、6=秒
	 * @param endRela 结束相对时间值,可为正负
	 * @return
	 */
	public String inputTimeToControlLackSecond(String startTime, WebElement startEle, WebElement endEle, int endType, int endRela){
		Date endDate;
		switch(endType){
			case 2:
				endDate = DateUtil.getRelativeMonth(endRela);
			case 3: 
				endDate = DateUtil.getRelativeDay(endRela);
				break;
			case 5: 
				endDate = DateUtil.getRelativeMinute(endRela);
				break;		
			case 6: 
				endDate = DateUtil.getRelativeSecond(endRela);
				break;			
			default:
				endDate = DateUtil.getRelativeSecond(endRela);
		}
		String current = DateUtil.formatDatetimeLackSecond(endDate);   
		return this.selenium.inputTimeToControl(startTime, startEle, current, endEle);
	}
	
	/**
	 * 	Fills up the time fields with relative date-time (lacks second part). <p>
	 * 输入相对于当前时间的开始时间和结束时间。输入时间格式为"yyyy-MM-dd HH:mm"
	 * 
	 * @param startEle 开始时间元素
	 * @param startType 开始时间单位，可选值: 1=年、2=月、3=日、4=时、5=分、6=秒
	 * @param startRela 开始相对时间值,可为正负
	 * @param endEle 结束时间元素
	 * @param endType 结束时间单位，可选值: 1=年、2=月、3=日、4=时、5=分、6=秒
	 * @param endRela 结束相对时间值,可为正负
	 * @return
	 */
	public String inputTimeToControlLackSecond(WebElement startEle, int startType, int startRela, WebElement endEle, int endType, int endRela){
		Date startDate, endDate;
		switch(startType){
			case 2:
				startDate = DateUtil.getRelativeMonth(startRela);
				break;				
			case 3: 
				startDate = DateUtil.getRelativeDay(startRela);
				break;
			case 5: 
				startDate = DateUtil.getRelativeMinute(startRela);
				break;		
			case 6: 
				startDate = DateUtil.getRelativeSecond(startRela);
				break;			
			default:
				startDate = DateUtil.getRelativeSecond(startRela);
		}
		
		switch(endType){
			case 2:
				endDate = DateUtil.getRelativeMonth(endRela);
			case 3: 
				endDate = DateUtil.getRelativeDay(endRela);
				break;
			case 5: 
				endDate = DateUtil.getRelativeMinute(endRela);
				break;		
			case 6: 
				endDate = DateUtil.getRelativeSecond(endRela);
				break;			
			default:
				endDate = DateUtil.getRelativeSecond(endRela);
		}
		
		String startTime = DateUtil.formatDatetimeLackSecond(startDate);
		String endTime = DateUtil.formatDatetimeLackSecond(endDate);	
		return this.selenium.inputTimeToControl(startTime, startEle, endTime, endEle);
	}
	
	/**
	 * 	Fills up the time fields with relative date. <p>
	 * 输入开始时间(绝对)和结束(相对)时间。格式为"yyyy-MM-dd"
	 * 
	 * @param startTime 开始时间
	 * @param startEle 开始时间元素
	 * @param endEle 结束时间元素
	 * @param endType 结束时间单位，可选值: 1=年、2=月、3=日、4=时、5=分、6=秒
	 * @param endRela 结束相对时间值,可为正负
	 * @return
	 */
	public String inputDateToControl(String startTime, WebElement startEle, WebElement endEle, int endType, int endRela){
		Date endDate;
		switch(endType){
			case 2:
				endDate = DateUtil.getRelativeMonth(endRela);
			case 3: 
				endDate = DateUtil.getRelativeDay(endRela);
				break;
			case 5: 
				endDate = DateUtil.getRelativeMinute(endRela);
				break;		
			case 6: 
				endDate = DateUtil.getRelativeSecond(endRela);
				break;			
			default:
				endDate = DateUtil.getRelativeSecond(endRela);
		}
		
		String current = DateUtil.formatDate(endDate);   
		return this.selenium.inputTimeToControl(startTime, startEle, current, endEle);
		
	}

	/**
	 * Quit the browser manually. <p>
	 * 关闭浏览器并结束driver
	 * 
	 * @param message
	 */
	public void quitBrowserWithMessage(String message){
		this.selenium.logger.error(message);
		this.selenium.close();
		this.selenium.quit();
	}
	
	/**
	 * Change the style of node.<p>
	 * 改变节点的style值
	 * 
	 * Ex：js.ExecuteScript("arguments[0].style.display='block';", WebElement);  args could be a number, a boolean, a String, WebElement, or a List of any combination of the above. <p>
	 * 
	 * @param styleKey
	 * @param styleValue
	 * @param args
	 */
	public void changeNodesStyle(String styleKey, String styleValue, Object ...args){
		for(int i = 0; i<args.length; i++)	{
			this.selenium.executeJS("arguments["+i+"].style."+styleKey+"='"+styleValue+"'", args[i]);
		}
	}
	
	/**
	 * Change the class value of node. <p>
	 * 改变节点的class值
	 * 
	 * Ex：js.ExecuteScript("arguments[0].className='xxxxx';", WebElement);  args could be a number, a boolean, a String, WebElement, or a List of any combination of the above. <p>
	 * 
	 * @param className
	 * @param args
	 */
	public void changeNodesClass(String className, Object ...args){
		for(int i = 0; i<args.length; i++)	{
			this.selenium.executeJS("var ele = arguments["+i+"]; ele.className='"+className+"';", args[i]);
		}
	}
	
	/**
	 * Element is enabled and displayed. <p>
	 * 判断元素是否可用并显示
	 * 
	 * @param element
	 * @return
	 */
	public boolean elementDisplayedEnabled(WebElement element){
		boolean bln = false;
		if(this.selenium.isDisplayed(element) == true && this.selenium.isEnabled(element) == true){
			bln = true;
		}
		return bln;
	}
	
	/**
	 * Get into frame by id or name. <p>
	 * 进入指定id 或 name的frame中
	 * 
	 * @param nameOrId
	 */
	public void inToFrame(String nameOrId){
		this.selenium.inFrame(nameOrId);
	}
	
	/**
	 * Get out of frame. <p>
	 * 跳到最外层document下
	 * 
	 */
	public void outOfFrame(){
		this.selenium.defaultFrame();
	}
	
	/**
	 * Right click on link and select save link as. <p>
	 * 右击一个链接并选择保存
	 * 
	 */
	public void downloadByRight(WebElement onElement){
		this.selenium.mouseRightClick(onElement);
		this.selenium.pressKey(onElement, "K");
	}
	
	/**
	 * Add random strings to original String. <p>
	 * 在原本的字符串上添加随机的串
	 * 
	 */
	public String buildRandomTestData(String originString, int length){
		return originString += RandomUtil.random(length);
	}

	/**
	 * Find element by an element. <p>
	 * 通过父元素查找子元素
	 * 
	 * @param element
	 * @param by
	 * @return
	 */
	public WebElement findElementBy(WebElement element, String by){
		return this.selenium.findElementBy(element, by);
	}
	
	/**
	 * Find a list of elements by an element. <p>
	 * 通过父元素查找一组子元素
	 * 
	 * @param element
	 * @param by
	 * @return true for success and false for fail
	 */
	public List<WebElement> findElementsBy(WebElement element, String by){
		return this.selenium.findElementsBy(element, by);
	}
	
	/**
	 * Select the correct database. <p>
	 * 选择要连接的数据库
	 * 
	 */
	public DbUtil selectDb(String dbName){
		DbUtil db;
		switch(this.getDbEnv()) {
		case "233_3306":
			db = new DbUtil("//192.168.143.233:3306/"+dbName, "juanpi", "juanpi");
			break;
		case "233_3309":
			db = new DbUtil("//192.168.143.233:3309/"+dbName, "juanpi", "juanpi");
			break;
		case "232_3309":
			db = new DbUtil("//192.168.143.232:3309/"+dbName, "js_xiudang", "d8F02&c19G?5_b4$5c6$");
			break;
		default:
			db = new DbUtil("//192.168.143.232:3309/"+dbName, "js_xiudang", "d8F02&c19G?5_b4$5c6$");
			break;
		}
		return db;
	}
	
	
	/**
	 * Perform multiple update operations and get the updated each result. <p>
	 * 执行一条或多条更新操作，并记录每次的更新起作用的行数
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @param sqls  包含sql语句和参数
	 * @return 每条语句更新的行数
	 */
	public List<Integer> dbUpdate(String dbName, Map<String, Object[]> sqls){
		this.dbUtil = selectDb(dbName);
		this.dbUtil.buildConnection();
		List<Integer> rows = new ArrayList<Integer>();
		for (Entry<String, Object[]> entry : sqls.entrySet()) {
			int row = this.dbUtil.update(entry.getKey(), entry.getValue());
			rows.add(row);
		}
		this.dbUtil.closeConnection();
		return rows;
	}
	
	/**
	 * Perform multiple query operations.  <p>
	 * 执行一条或多条查询语句，并返回结果; 其中每条语句只会取查到的结果的第一行
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @param sqls 包含sql语句和参数,限定为0个或1个
	 * @return
	 */
	public List<Map<String, Object>> dbQueryToMap(String dbName, Map<String, Object[]> sqls){
		this.dbUtil = selectDb(dbName);
		this.dbUtil.buildConnection();
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		for (Entry<String, Object[]> entry:sqls.entrySet()) {
			if(entry.getValue().equals(new String[]{})){                       //如果sql不带参数的情况
				results.add(	this.dbUtil.queryToMap(entry.getKey()));
			}else{
				results.add(this.dbUtil.queryToMap(entry.getKey(), entry.getValue()));
			}
		}
		this.dbUtil.closeConnection();
		return results;
	}
	
	/**
	 * Execute shell scripts.
	 * 执行shell脚本
	 * 
	 * @param ipAddr
	 * @param userName
	 * @param password
	 * @param charset
	 * @param cmds
	 * @return -1 for failed; 0 for passed.
	 */
	public int shellExec(String ipAddr, String userName, String password, String charset, String cmds){
		this.shellUtil = new SshShellUtil(ipAddr, userName, password, charset);
		int res = this.shellUtil.executeCommand(cmds);
		return res;
		
	}
	
	/**
	 * Get the milliseconds for specified days from now.<p>
	 * 获取相对天数的时间戳
	 * 
	 * @param days 相对于当天的天数，可为正负
	 * 
	 * @return 时间戳(保留前10位)
	 */
	public String getTimeForRelativeDay(int days){
		Date date = DateUtil.getRelativeDay(days);
		long timeLong = date.getTime();  
		String timeString = String.valueOf(timeLong);
		timeString = timeString.substring(0,10);
		return timeString;
		
	}
	
	/**
	 * Get the time for specified seconds from now.<p>
	 * 获取相对当前的秒数
	 * 
	 * @param seconds 相对于当前的秒数
	 * 
	 * @return 日期时间  格式为yyyy-MM-dd HH:mm:ss
	 */
	public String getDateTimeForRelativeSeconds(int seconds){
		Date date = DateUtil.getRelativeSecond(seconds);
		String dateTime = DateUtil.formatDatetime(date);
		return dateTime;
	}
	
	
	
	/** 
	 * The Getters and Setters.<p>
	 *  
	 * */
	public void setPageTimeout(int time) {
		this.pageTimeout = time;
	}
	
	public int getPageTimeout() {
		return pageTimeout;
	}
	
	public void setPicTimeout(int time) {
		this.picTimeout = time;
	}
	
	public int getPicTimeout() {
		return picTimeout;
	}
	
	public void setElementTimeout(int time) {
		this.elementTimeout = time;
	}
	
	public int getElementTimeout() {
		return elementTimeout;
	}
	
	public void setPromptWindowTimeout(int time) {
		this.promptWindowTimeOut = time;
	}
	
	public int getPromptWindowTimeout() {
		return promptWindowTimeOut;
	}

	public void setMiUrl(String url){
		this.miUrl = url;
	}
	
	public String getMiUrl(){
		return miUrl;
	}
	
	public Object getShareValue(Object key) {
		return shareValues.getOrDefault(key, "Undefined");
	}
	
	public void setShareValue(Object key, Object value) {
		shareValues.put(key, value);
	}
	
	public SeleniumUtil getSelenium() {
		return this.selenium;
	}
	
	public void setSelenium(SeleniumUtil selenium) {
		this.selenium = selenium;
	}
	
	public String getDbEnv() {
		return dbEnv;
	}

	public void setDbEnv(String dbEnv) {
		this.dbEnv = dbEnv;
	}
}
