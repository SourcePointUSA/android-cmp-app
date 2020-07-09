package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;

public class ConsentViewPage extends Page {

	WebDriver driver;

	public ConsentViewPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@AndroidFindBy(xpath = "//android.view.View[@text='X']")
	public WebElement CloseButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> ConsentMessage;

	@AndroidFindBy(id = "android:id/button1")
	public WebElement ClearCookiesButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> DeleteCookiesMessage;

	@AndroidFindBy(id = "android:id/button1")
	public WebElement YESButton;

	@AndroidFindBy(id = "android:id/button2")
	public WebElement NOButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(className = "android.widget.Button")
	public List<WebElement> ConsentButtons;

	////////////////// TCFv2 application elements

	public WebElement eleButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.widget.Button)")
	public List<WebElement> AllButtons;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='MANAGE PREFERENCES']")
	public WebElement tcfv2_ManagaePreferences;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='ACCEPT ALL']")
	public WebElement tcfv2_AcceptAll;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='REJECT ALL']")
	public WebElement tcfv2_RejectAll;

	@AndroidFindBy(xpath = "//android.view.View[@text='X']")
	public WebElement tcfv2_Dismiss;

	boolean errorFound = false;

	public void scrollAndClick(String text) throws InterruptedException {
	//	waitForElement(tcfv2_AcceptAll, timeOutInSeconds);
		WebElement ele = driver.findElement(By.xpath("//android.widget.Button[@text=\"" + text + "\"]"));
		waitForElement(ele, timeOutInSeconds);
		ele.click();
	}

	ArrayList<String> consentMsg = new ArrayList<String>();

	public ArrayList<String> getConsentMessageDetails() throws InterruptedException {
		waitForElement(tcfv2_ManagaePreferences, timeOutInSeconds);
		for (WebElement msg : ConsentMessage) {
			consentMsg.add(msg.getText());
		}
		return consentMsg;
	}

	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public void clickOnButton(String buttonName) {
		for (WebElement button : ConsentButtons) {
			if (button.getText().equals(buttonName)) {
				button.click();
				break;
			}
		}
	}

	public boolean verifyDeleteCookiesMessage() {
		return DeleteCookiesMessage.get(DeleteCookiesMessage.size() - 1).getText()
				.contains("Cookies for all properties will be");
	}

}
