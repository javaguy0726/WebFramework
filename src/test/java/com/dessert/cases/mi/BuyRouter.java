package com.dessert.cases.mi;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;

public class BuyRouter extends TestInitialization {
	@FindBy(xpath =".//div[@id='J_userInfo']/a[@data-stat-id='bf3aa4c80c0ac789']")
	private WebElement login;
	
	@FindBy(xpath =".//input[@id='username']")
	private WebElement username;
	
	@FindBy(xpath =".//input[@id='pwd']")
	private WebElement password;
	
	@FindBy(xpath =".//input[@id='login-button']")
	private WebElement submit;
	
	
	@FindBy(xpath =".//ul[@id='J_categoryList']/li/a[@data-stat-id='44a546bb64cac00a']")
	private WebElement category;
	
	@FindBy(xpath =".//ul[@id='J_categoryList']/li/div/ul/li/a[@data-stat-id='bd9f071f47c03dcf']")
	private WebElement categorychird;
	
	@FindBy(xpath =".//a[@data-gid='2141700014']")
	private WebElement gotobuy;
	
	
	@FindBy(xpath =".//div[@class='step-unit']/ul/li[@class='first']")
	private WebElement selectcolor;
	
	
	@FindBy(xpath =".//a[@class='btn J_btn_next btn-primary']")
	private WebElement nextstep;
	
	@Test(dataProvider = "testData" , description="小米登录")
	public void testBuy(String url,String uname,String pwd){
		
		PageFactory.initElements(seleniumUtil.driver, this); 
		
		basePage.open(url);
		basePage.waitForElementToLoad(login);
		basePage.click(login);
		basePage.waitForElementToLoad(username);
		
		//登录
		basePage.typeAfterClear(username, uname);
		basePage.typeAfterClear(password, pwd);
		basePage.click(submit);
		basePage.waitForElementToLoad(category);
		
		//选择路由器mini点击
		basePage.mouseMoveAndSelectDropdown(category, categorychird);
		basePage.waitForElementToLoad(gotobuy);
		
		//点击立即前往购买
		basePage.click(gotobuy);
		basePage.waitForElementToLoad(selectcolor);
		
		//选择白色
		basePage.click(selectcolor);
		basePage.click(nextstep);
		basePage.waitForFixedSeconds(10);
		
	}
	

}
