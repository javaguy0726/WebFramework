package com.dessert.cases.jd;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;


public class LoginTest extends TestInitialization{
	@FindBy(xpath =".//*[@id='loginname']")
	private WebElement username;
	
	@FindBy(xpath =".//*[@id='nloginpwd']")
	private WebElement password;
	
	@FindBy(xpath =".//*[@id='loginsubmit']")
	private WebElement submit;
	
	@FindBy(xpath =".//*[@id='ttbar-login']/a[2]")
	private WebElement logout;
	
	@Test(dataProvider ="testData", description ="京东登录")//引用csv
	public void testLogin(String url,String uname,String pwd){
		PageFactory.initElements(seleniumUtil.driver, this);
		
		basePage.open(url);
		basePage.waitForElementToLoad(username);
		basePage.typeAfterClear(username,uname);//清空之后输入
		basePage.typeAfterClear(password,pwd);
		basePage.click(submit);
		
		basePage.waitForFixedSeconds(10);//等待10s
		
		basePage.click(logout);
		basePage.waitForFixedSeconds(10);
		
	}
	
	
}