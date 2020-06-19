package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.framework.helpers.Page;
import org.openqa.selenium.By;
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

///////////////// TCFv1 application elemnets ///////////////

	@AndroidFindBy(xpath = "//android.view.View[@text='Accept All']")
	public WebElement tcfv1_AcceptAllButton;

	@AndroidFindBy(xpath = "//android.view.View[@text='Reject All']")
	public WebElement tcfv1_RejectAllButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.widget.Button[@text='Cancel']")
	public WebElement tcfv1_CancelButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Save & Exit']")
	public WebElement tcfv1_SaveAndExitButton;

	public void scrollAndClick(String text) throws InterruptedException {

		driver.findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""
						+ text + "\").instance(0))"))
				.click();

	}

	public void loadTime() {
		long startTime = System.currentTimeMillis();
		new WebDriverWait(driver, 60).until(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.Button[@text='Cancel']")));
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("**** Total Privacy Manager Load Time: " + totalTime + " milliseconds");
	}

	boolean privacyManageeFound = false;

	public boolean isPrivacyManagerViewPresent() throws InterruptedException {
		Thread.sleep(10000);

		try {
			if (driver.findElements(By.xpath("//*[@class='android.webkit.WebView']")).size() > 0)
				privacyManageeFound = true;

		} catch (Exception e) {
			privacyManageeFound = false;
			throw e;
		}
		return privacyManageeFound;
	}

	public WebElement eleButton(String udid, String buttonText) {
		eleButton = (WebElement) driver.findElement(By.id("+buttonText+"));
		return eleButton;

	}

}
