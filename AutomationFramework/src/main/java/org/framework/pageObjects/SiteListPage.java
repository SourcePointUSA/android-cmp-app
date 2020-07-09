package org.framework.pageObjects;

import static org.framework.logger.LoggingManager.logMessage;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.framework.helpers.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.WithTimeout;

public class SiteListPage extends Page {

	WebDriver driver;

	public SiteListPage(WebDriver driver) throws InterruptedException {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		logMessage("Initializing the " + this.getClass().getSimpleName() + " elements");
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
		Thread.sleep(1000);
	}

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/action_addProperty")
	public WebElement GDPRTCFv2AddButton;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/toolbar_title")
	public WebElement GDPRSiteListPageHeader;

	@AndroidFindBy(id = "com.sourcepointmeta.app:id/websiteListRecycleView")
	public WebElement GDPRSiteListView;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/edit_button")
	public WebElement GDPREditButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/reset_button")
	public WebElement GDPRResetButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/delete_button")
	public WebElement GDPRDeleteButton;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/item_view")
	public List<WebElement> GDPRSiteList;

	@WithTimeout(time = 30, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "com.sourcepointmeta.app:id/propertyNameTextView")
	public WebElement GDPRSiteName;

	@WithTimeout(time = 50, chronoUnit = ChronoUnit.SECONDS)
	@AndroidFindBy(id = "android:id/message")
	public List<WebElement> ErrorMessage;

	@AndroidFindBy(id = "android:id/button1")
	public WebElement YESButton;

	@AndroidFindBy(id = "android:id/button2")
	public WebElement NOButton;

	boolean siteFound = false;

	public boolean isSitePressent_gdpr(String siteName) throws InterruptedException {
		siteFound = false;
		if (driver.findElements(By.id("com.sourcepointmeta.app:id/propertyNameTextView")).size() > 0) {
			if (driver.findElement(By.id("com.sourcepointmeta.app:id/propertyNameTextView")).getText()
					.equals(siteName)) {
				siteFound = true;
			}
		}
		return siteFound;
	}

	public void tapOnSite_gdpr(String siteName, List<WebElement> siteList) throws InterruptedException {
		driver.findElement(By.id("com.sourcepointmeta.app:id/propertyNameTextView")).click();
	}

	public void swipeHorizontaly_gdpr(String siteName) throws InterruptedException {
		WebElement aa = driver.findElement(By.id("com.sourcepointmeta.app:id/propertyNameTextView"));

		Point point = aa.getLocation();
		TouchAction action = new TouchAction((PerformsTouchActions) driver);

		int[] rightTopCoordinates = { aa.getLocation().getX() + aa.getSize().getWidth(), aa.getLocation().getY() };
		int[] leftTopCoordinates = { aa.getLocation().getX(), aa.getLocation().getY() };
		action.press(PointOption.point(rightTopCoordinates[0] - 1, rightTopCoordinates[1] + 1))
				.waitAction(WaitOptions.waitOptions(Duration.ofMillis(3000)))
				.moveTo(PointOption.point(leftTopCoordinates[0] + 1, leftTopCoordinates[1] + 1)).release().perform();
		waitForElement(GDPRDeleteButton, 30);
	}

	public void waitForElement(WebElement ele, int timeOutInSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(ExpectedConditions.visibilityOf(ele));
	}

	public boolean verifyDeleteSiteMessage() {
		return ErrorMessage.get(ErrorMessage.size() - 1).getText().contains("Do you want to delete this property?");
	}

}
