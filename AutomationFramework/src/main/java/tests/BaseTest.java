package tests;

import org.framework.appium.AppiumServer;
import org.framework.drivers.AndroidDriverBuilder;
import org.framework.helpers.Page;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import static org.framework.logger.LoggingManager.logMessage;

import java.io.IOException;

public class BaseTest extends Page {
	public WebDriver driver = null;

	@BeforeTest
	public void startAppiumServer() throws IOException {
		if (AppiumServer.appium == null || !AppiumServer.appium.isRunning()) {
			AppiumServer.start();
		}
	}

	@AfterTest
	public void stopAppiumServer() throws IOException {
		if (AppiumServer.appium != null || AppiumServer.appium.isRunning()) {
			AppiumServer.stop();
		}
	}

	@Parameters({ "model" })
	@BeforeMethod
	public void setupDriver(@Optional String model) throws IOException {
		try {
			setupMobileDriver(model);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Parameters({ "platformName", "model" })
	public void setupMobileDriver(String model) throws IOException {
		try {
			driver = new AndroidDriverBuilder().setupDriver(model);
			logMessage(model + " driver has been created for execution");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterMethod
	public void teardownDriver() {
		try {
			driver.quit();
			logMessage("Driver has been quit from execution");
		} catch (Exception e) {
			System.out.println("Exception occured - " + e.getMessage());
			throw e;
		}
	}

}