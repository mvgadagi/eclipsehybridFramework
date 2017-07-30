package com.hybridFramework.homepage;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.hybridFramework.testBase.TestBase;

import junit.framework.Assert;

public class TestDataDriverScript extends TestBase {

	@DataProvider(name = "testData")
	public Object[][] dataSource() {
		return getData("TestData.xlsx", "LoginTestData");
	}

	@Test(dataProvider = "testData")
	public void testLogin(String userName, String password, String runMode) {
		System.out.println("username : " + userName);
		System.out.println("password : " + password);
		System.out.println("runMode : " + runMode);
	}

	@Test
	public void testFailure() {
		Assert.fail();
	}

	@Test
	public void skipMethod() {
		System.out.println("Skipping Test!!");
	}
}
