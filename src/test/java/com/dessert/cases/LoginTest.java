package com.dessert.cases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.Test;

import com.dessert.base.TestInitialization;



public class LoginTest extends TestInitialization{
	
	@FindBy(xpath =".//input[@id='username']")
	private WebElement username;
	
	@FindBy(xpath =".//input[@id='pwd']")
	private WebElement password;
	
	@FindBy(xpath =".//input[@id='login-button']")
	private WebElement submit;
	
	@FindBy(xpath =".//div[@id='sms-unsent']/preceding-sibling::div/div/div/div/p[@class='na-num']")
	private WebElement realname;
	
	@FindBy(xpath =".//a[@id='logoutLink']")
	private WebElement logout;
	
	
	@Test(dataProvider = "testData" , description="小米登录")
	public void testLogin(String url,String uname,String pwd,String rname){
		
		PageFactory.initElements(seleniumUtil.driver, this);  //加1行
		
		basePage.open(url);
		//WebElement username = seleniumUtil.findElement(By.xpath(".//input[@id='username']"));
		//WebElement password = seleniumUtil.findElement(By.xpath(".//input[@id='pwd']"));
		//WebElement submit = seleniumUtil.findElement(By.xpath(".//input[@id='login-button']"));
		
		basePage.waitForElementToLoad(username);		
		basePage.typeAfterClear(username,uname);
		basePage.typeAfterClear(password,pwd);
		basePage.click(submit);
		
		basePage.waitForFixedSeconds(10); //等20秒
		
		
		//WebElement realname = seleniumUtil.findElement(By.xpath(".//div[@id='sms-unsent']/preceding-sibling::div/div/div/div/p[@class='na-num']"));
		
		
		assertUtil.assertElementTextEquals(realname, rname);     //验证预期name与实际name是否相同
		basePage.waitForFixedSeconds(10);
		
		
		//WebElement logout = seleniumUtil.findElement(By.xpath(".//a[@id='logoutLink']"));	        
	    basePage.click(logout);  
		basePage.waitForFixedSeconds(10);
		
		
	
		
		
		
	}

	

}
