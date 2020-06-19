package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileBy;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;

public class ConsentViewPage extends Page {

	WebDriver driver;

	public ConsentViewPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}
//	protected Report reporter = null;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.view.View[contains(@resource-id,'sp_message_panel_id')]")
	public WebElement ConsentMessageView;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.view.View[@index='0']")
	public WebElement ConsentMessageTitleText;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.view.View[@index='1']")
	public WebElement ConsentMessageBodyText;

	@AndroidFindBy(xpath = "//android.view.View[@text='X']")
	public WebElement CloseButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Continue']")
	public WebElement ContinueButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Accept all cookies']")
	public WebElement AcceptallCookiesButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Accept All cookies']")
	public WebElement AcceptAllCookiesButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Reject all cookies']")
	public WebElement RejectAllCookiesButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Show Purposes']")
	public WebElement ShowPurposesButton;

	@WithTimeout(time = 80, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "(//android.view.View)")
	public List<WebElement> ConsentMessage;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public WebElement ErrorMessageView;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> WrongCampaignErrorText;

	@AndroidFindBy(id = "android:id/button2")
	public WebElement ShowSiteInfoButton;

	@AndroidFindBy(id = "android:id/button1")
	public WebElement ClearCookiesButton;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> DeleteCookiesMessage;

	@AndroidFindBy(id = "android:id/button1")
	public WebElement YESButton;

	@AndroidFindBy(id = "android:id/button2")
	public WebElement NOButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(className = "android.widget.Button")
	public List<WebElement> ConsentButtons;

	public WebElement eleButton;
/////////////// New Script Web Elements ///////////

	@AndroidFindBy(xpath = "//android.widget.Button[@text='I Accept']")
	public WebElement IAcceptButton;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='I Reject']")
	public WebElement IRejectButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(xpath = "//android.widget.Button[@text='Privacy Settings']")
	public WebElement PrivacySettings;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Reject All']")
	public WebElement RejectAll;

	@AndroidFindBy(xpath = "//android.widget.Button[@text='Accept All cookies']")
	public WebElement AcceptAllCookies;

	@AndroidFindBy(xpath = "//android.view.View[@text='X']")
	public WebElement Dismiss;

	boolean errorFound = false;

	public void loadTime() {
		try {

			long startTime = System.currentTimeMillis();
			new WebDriverWait(driver, 120).until(ExpectedConditions.presenceOfElementLocated(
					By.xpath("//android.webkit.WebView[contains(@text,'Notice Message App')]")));
			// new WebDriverWait(driver,
			// 60).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//android.widget.Button[contains(@text,'Privacy
			// Setting')]")));
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("**** Total Message Load Time: " + totalTime + " milliseconds");
		} catch (Exception ex) {
			System.out.println(ex);
			throw ex;
		}

	}

	public void scrollAndClick(String text) throws InterruptedException {
		driver.findElement(MobileBy.AndroidUIAutomator(
				"new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView(new UiSelector().textContains(\""
						+ text + "\").instance(0))"))
				.click();
	}

	public void scrollDown() {
		Dimension dimension = driver.manage().window().getSize();

		Double scrollHeightStart = dimension.getHeight() * 0.5;
		int scrollStart = scrollHeightStart.intValue();

		Double scrollHeightEnd = dimension.getHeight() * 0.8;
		int scrollEnd = scrollHeightEnd.intValue();

		new TouchAction((PerformsTouchActions) driver).press(PointOption.point(0, scrollStart))
				.waitAction(WaitOptions.waitOptions(Duration.ofSeconds(2))).moveTo(PointOption.point(0, scrollEnd))
				.release().perform();
	}

	ArrayList<String> consentMsg = new ArrayList<String>();

	ArrayList<String> expectedList = new ArrayList<String>();

	public void expectedList() {
		expectedList.add("");
		expectedList.add("");
	}

	public ArrayList<String> getConsentMessageDetails() throws InterruptedException {
		Thread.sleep(8000);
		for (WebElement msg : ConsentMessage) {
			consentMsg.add(msg.getText());
			// consentMsg.add(msg.getAttribute("value"));
		}
		return consentMsg;
	}

	public void getLocation() {
		for (WebElement msg : ConsentMessage) {
			Point point = msg.getLocation();
			TouchAction touchAction = new TouchAction((PerformsTouchActions) driver);
			System.out.println("******************");
			System.out.println((point.x) + (msg.getSize().getWidth()));
			System.out.println((point.y) + (msg.getSize().getWidth()));
			System.out.println("******************");
		}
	}

	public String verifyWrongCampaignError() throws InterruptedException {
		Thread.sleep(3000);
		try {
			return WrongCampaignErrorText.get(WrongCampaignErrorText.size() - 1).getText();
		} catch (Exception e) {
			throw e;
		}
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
