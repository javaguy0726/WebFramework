package com.dessert.cases.oa;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;

public class LoginOATest extends TestInitialization{
	@FindBy(xpath =".//input[@id='oa-username']")
	private WebElement USERNAME;
	
	@FindBy(xpath =".//input[@id='oa-password']")
	private WebElement PASSWORD;
	
	@FindBy(xpath =".//a[@class='loginBtn']")
	private WebElement LOGIN;
	
	@FindBy(xpath =".//p[@class='txt1']")
	private WebElement REAL_NAME;
	
	@FindBy(xpath =".//span[text()='武汉早餐预定']")
	private WebElement BREAKFAST_RESERVE;
	
	@FindBy(css ="#main")
	private WebElement MENU_TEXT;
	
	@FindBy(xpath =".//input[contains(@class,'make-sure')]")
	private WebElement MAKESURE_MENU;
	
	@FindBy(xpath =".//select[@name='user_floor']")
	private WebElement USER_FLOOR;
	
	@FindBy(xpath =".//span[text()='确定']")
	private WebElement MENU_SUBMIT;
	
	@FindBy(xpath =".//div[@class='content guery']/span")
	private WebElement SUCCESS_HINT;
	
	@FindBy(xpath =".//li[@title='退出系统']")
	private WebElement LOGOUT;
	
	@FindBy(xpath =".//div[@class='joa-msg-box']/descendant::span[text()='确定']")
	private WebElement LOGOUT_SURE;
	
	@Test(dataProvider = "testData" , description="OA登录")
	public void testLogin(String url,String uname,String pwd,String floor,String hint){
		PageFactory.initElements(seleniumUtil.driver, this);
		
		//登录OA
		basePage.open(url);
		basePage.waitForElementToLoad(USERNAME);
		basePage.typeAfterClear(USERNAME, uname);
		basePage.typeAfterClear(PASSWORD, pwd);
		basePage.click(LOGIN);
		basePage.waitForElementToLoad(REAL_NAME);
		assertUtil.assertElementTextEquals(REAL_NAME, uname);
		
		//选择早餐
		basePage.click(BREAKFAST_RESERVE);
		basePage.inToFrame("rightMain");
		basePage.waitForElementToLoad(MENU_TEXT);		
		
//		//点第一个菜单
//		for(int i=1;i<6;i++){
//			basePage.findElementBy(MENU_TEXT, "./ul/li["+i+"]/div[2]").click();
//		}
		
		//随机点菜单
		for(int i=1;i<6;i++){
			int size = basePage.sizeOfElementsBy(MENU_TEXT, "./ul/li["+i+"]/div");
//			System.out.println("The sieze of elements is "+size);

			List<WebElement> elements = new ArrayList<WebElement>();
			for(int j=2;j<size;j++){
				elements.add(basePage.findElementBy(MENU_TEXT, "./ul/li["+i+"]/div["+j+"]"));
			}
			basePage.getRandomElementOfList(elements).click();
		}
		
		basePage.waitForElementToLoad(MAKESURE_MENU);
		basePage.click(MAKESURE_MENU);
		//选择楼层
		basePage.waitForElementToLoad(USER_FLOOR);
		basePage.selectOption(USER_FLOOR,floor);
		basePage.waitForElementToLoad(MENU_SUBMIT);
		basePage.click(MENU_SUBMIT);
	//	basePage.waitForFixedSeconds(2000);
		basePage.waitForElementToLoad(SUCCESS_HINT);
		assertUtil.assertElementTextEquals(SUCCESS_HINT, hint);
		
		//退出OA
		basePage.outOfFrame();
		basePage.waitForElementToLoad(LOGOUT);
		basePage.click(LOGOUT);
		basePage.waitForElementToLoad(LOGOUT_SURE);
		basePage.click(LOGOUT_SURE);
		
		basePage.waitForFixedSeconds(20);
	}
}
