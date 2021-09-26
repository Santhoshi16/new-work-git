package testcases.rediff;

import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;

import testbase.BaseTest;

public class Session extends BaseTest{
	
	@Test
	public void doLogin() {
		app.log("Logging in");
		app.openBrowser("Chrome");
		app.navigate("url");
		app.type("username_css", "majjisanthoshi5@gmail.com");
		//app.reportFailure("Test is incorrect - non critical failure", false);
		app.type("password_xpath", "Durga@16");
		app.validateElementPresent("login_submit_id");
		app.click("login_submit_id");
		
	}
	
	@Test
	public void doLogout() {
		app.log("Logging out");
	}
	

}
