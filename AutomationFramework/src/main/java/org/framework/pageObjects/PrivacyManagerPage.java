package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;

public class PrivacyManagerPage extends Page {

	WebDriver driver;

	public PrivacyManagerPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//*[@resource-id='__genieContainer']")
	public WebElement PrivacyManagerView;

	public WebElement eleButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> AllButtons;

	// TCFV2 application elements
	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.widget.Button[@text='Accept All']")
	public WebElement tcfv2_AcceptAll;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Save & Exit']")
	public WebElement tcfv2_SaveAndExitButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Reject All']")
	public WebElement tcfv2_RejectAll;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Cancel']")
	public WebElement tcfv2_Cancel;

	@AndroidFindBy(xpath = "//android.view.View[@text='On']")
	public WebElement tcfv2_On;

	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> ONToggleButtons;

	public void scrollAndClick(String text) throws InterruptedException {
		waitForElement(tcfv2_Cancel, timeOutInSeconds);
		driver.findElement(By.xpath("//android.widget.Button[@text=\""+ text + "\"]")).click();
	}

	boolean privacyManageeFound = false;

	public boolean isPrivacyManagerViewPresent() throws InterruptedException {
		try {
			waitForElement(tcfv2_AcceptAll, timeOutInSeconds);
			if (driver.findElements(By.xpath("//*[@class='android.webkit.WebView']")).size() > 0)
				privacyManageeFound = true;

		} catch (Exception e) {
			privacyManageeFound = false;
			throw e;
		}
		return privacyManageeFound;
	}

	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public WebElement eleButton(String udid, String buttonText) {
		eleButton = (WebElement) driver.findElement(By.id("+buttonText+"));
		return eleButton;

	}

}
