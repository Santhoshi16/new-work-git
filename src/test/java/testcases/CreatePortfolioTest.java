package testcases;

import org.testng.annotations.Test;

import keywords.ApplicationKeywords;

public class CreatePortfolioTest {

	@Test
	public void createPortfolioTest () {
		 //no webdriver code
		//login
		//create
		//verify
		ApplicationKeywords app = new ApplicationKeywords();//init prop
		
		app.openBrowser("Chrome");
		app.navigate("url");
		app.type("username_css", "majjisanthoshi5@gmail.com");
		app.type("password_xpath", "Durga@16");
		app.validateElementPresent("login_submit_id");
		app.click("login_submit_id");
		app.validateLogin();
		//app.selectDateFromCalendar();
		
			
	}
}
