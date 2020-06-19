package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.framework.helpers.Page;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.HidesKeyboard;
//import io.appium.java_client.WebElement;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;
import io.appium.java_client.touch.offset.PointOption;

public class NewSitePage extends Page {
	WebDriver driver;

	public NewSitePage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/toolbar_title")
	public WebElement GDPRNewSitePageHeader;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Account ID")
	public WebElement AccountIDLabel;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/action_saveProperty")
	public WebElement GDPRSaveButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etAccountID")
	public WebElement GDPRAccountID;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etPropertyName")
	public WebElement GDPRSiteName;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/toggleStaging")
	public WebElement GDPRToggleButton;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etAuthID")
	public WebElement GDPRAuthID;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etKey")
	public WebElement GDPRParameterKey;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etValue")
	public WebElement GDPRParameterValue;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/btn_addParams")
	public WebElement GDPRParameterAddButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> ErrorMessage;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/button1")
	public WebElement OKButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etPropertyId")
	public WebElement GDPRSiteId;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/etPMId")
	public WebElement GDPRPMId;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/toggleShowPM")
	public WebElement GDPRShowPrivacyManager;

	boolean paramFound = false;

	public void selectCampaign(WebElement ele, String staggingValue) throws InterruptedException {

		if (staggingValue.equals("ON")) {
			Point point = ele.getLocation();
			TouchAction touchAction = new TouchAction((PerformsTouchActions) driver);

			touchAction.tap(PointOption.point(point.x + 20, point.y + 20)).perform();
		}
		Thread.sleep(3000);
	}

	public void addTargetingParameter(WebElement paramKey, WebElement paramValue, String key, String value)
			throws InterruptedException {

		((HidesKeyboard) driver).hideKeyboard();
		paramKey.sendKeys(key);

		((HidesKeyboard) driver).hideKeyboard();
		paramValue.sendKeys(value);

		((HidesKeyboard) driver).hideKeyboard();
	}

	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}
	
}
