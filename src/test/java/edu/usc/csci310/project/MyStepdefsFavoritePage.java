
package edu.usc.csci310.project;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyStepdefsFavoritePage {

    private static final String ROOT_URL = "https://localhost:8080/";
    private WebDriver driver;
    private WebDriverWait wait;
    boolean storageSet = false;

    public void setUp(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors", "--ignore-ssl-errors");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(500));
    }

    @Given("I have a database cleared and users created for fav")
    public void iHaveADatabaseClearedAndUsersCreatedForFav() {
        setUp();
        Utils.clearDb();
        Utils.createUsers(driver, wait);
        Utils.populateFavorites();
    }

    @After
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @And("I am on the favorites page")
    public void iAmOnTheFavoritesPage() {
        driver.get(ROOT_URL + "favorites");
    }

    @Then("I should see a minus sign")
    public void iShouldSeeAMinusSign() {
        // assert i see class="button_minus"
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button_minus")));
        assertFalse(driver.findElements(By.className("button_minus")).isEmpty());
    }

    @When("I should see a minus sign and click it")
    public void iShouldSeeAMinusSignAndClickIt() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button_minus")));
        driver.findElements(By.className("button_minus")).get(0).click();
    }

    @Then("I should see a confirmation dialog")
    public void iShouldSeeAConfirmationDialog() {
        // assert i get a popup which asks "Are you sure you want to remove this park from your favorites?"
        wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Are you sure you want to remove this park from favorites?", driver.switchTo().alert().getText());
    }

    @When("I click on the first park box")
    public void iClickOnTheFirstParkBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[1]/div/div/h3")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[1]/div/div/h3")).click();
    }

    @Then("I should see fav {string}")
    public void iShouldSeeInTheListOfFavorites(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        assertTrue(driver.getPageSource().contains(arg0));
    }

    @And("I confirm OK")
    public void iShouldSeeAYesButtonAndClickIt() {
        driver.switchTo().alert().accept();
    }

    @Then("I should see a positive confirmation dialog")
    public void iShouldSeeAnotherConfirmationDialog() {
        // assert i get a popup which asks "Park removed from favorites"
        wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Park removed from favorites successfully.", driver.switchTo().alert().getText());
    }

    @And("I confirm cancel")
    public void iConfirmCancel() {
        driver.switchTo().alert().dismiss();
    }

    @Then("I should still see the park in the list of favorites")
    public void iShouldStillSeeTheParkInTheListOfFavorites() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        assertTrue(driver.getPageSource().contains("Yosemite National Park"));
    }

    @And("I go to the favorites page")
    public void iGoToTheFavoritesPage() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/a[2]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[1]/div[2]/a[2]")).click();
    }

    @And("I click the favorites button")
    public void iClickTheFavoritesButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("NavLinks")));
        driver.findElements(By.className("NavLinks")).get(0).click();
    }

    @And("I am logged in as user {string} for fav")
    public void iAmLoggedInAsUserForFav(String name) {
        driver.get(ROOT_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username-input")));
        driver.findElement(By.id("username-input")).sendKeys(name);
        driver.findElement(By.id("password-input")).sendKeys("Aa1");
        driver.findElement(By.className("login-button")).click();

        // Wait for the login process to complete
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I set session storage for {string} for fav")
    public void iSetSessionStorageForForFav(String username) {
        if (!storageSet) {
            Utils.setSessionStorage(username, driver);
            storageSet = true;
        }
    }

    @When("I press the down arrow on the top favorite park box")
    public void iPressTheDownArrowOnTheTopFavoriteParkBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[1]/div/div/div/button[2]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[1]/div/div/div/button[2]")).click();
    }

    @Then("I should see the order of the two parks switched")
    public void iShouldSeeTheOrderOfTheTwoParksSwitched() {
        // TODO: Have a better waiting mechanism
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Find the element using the XPath
        WebElement element = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[1]/div/div/h3"));
        String elementText = element.getText();
        assertEquals("John Muir National Historic Site", elementText);

        element = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[2]/div/div[1]/h3"));
        elementText = element.getText();
        assertEquals("Yosemite National Park", elementText);
    }

    @When("I press the up arrow on the second favorite park box")
    public void iPressTheUpArrowOnTheSecondFavoriteParkBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[2]/div/div/div/button[1]")));
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[2]/div[2]/div[2]/div/div/div/button[1]")).click();
    }

    @Then("I should see the image, location, and entrance fee of park at the top of the box")
    public void iShouldSeeTheImageLocationAndEntranceFeeOfParkAtTheTopOfTheBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-image")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-location")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-entrance-fee")));
        assertTrue(driver.findElement(By.className("park-image")).isDisplayed());
        assertTrue(driver.findElement(By.className("park-location")).isDisplayed());
        assertTrue(driver.findElement(By.className("park-entrance-fee")).isDisplayed());
    }

    @Then("my favorites list should be set to private by default")
    public void myFavoritesListShouldBeSetToPrivateByDefault() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        WebElement slider = driver.findElement(By.className("slider"));
        String sliderColor = slider.getCssValue("background-color");
        assertEquals(sliderColor, "rgba(0, 255, 0, 1)");
    }

    @When("my favorites list is private")
    public void myFavoritesListIsPrivate() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        WebElement slider = driver.findElement(By.className("slider"));
        String sliderColor = slider.getCssValue("background-color");
        assertEquals(sliderColor, "rgba(0, 255, 0, 1)");
    }

    @And("I uncheck the green private slider")
    public void iUncheckTheGreenPrivateSlider() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        driver.findElement(By.className("switch")).click();
    }

    @Then("my favorites list should then be public")
    public void myFavoritesListShouldThenBePublic() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        WebElement slider = driver.findElement(By.className("slider"));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String sliderColor = slider.getCssValue("background-color");
        assertEquals("rgba(204, 204, 204, 1)",sliderColor);
    }

    @Then("I should see a load more button")
    public void iShouldSeeALoadMoreButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("load-more-button")));
        assertTrue(driver.findElement(By.className("load-more-button")).isDisplayed());
    }

    @When("I click the load more button")
    public void iClickTheLoadMoreButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("load-more-button")));
        driver.findElement(By.className("load-more-button")).click();
    }

    @Then("I should see {int} more favorites")
    public void iShouldSeeMoreFavorites(int arg0) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("park-card"), arg0));
        List<WebElement> parkCards = driver.findElements(By.className("park-card"));
        assertEquals(20, parkCards.size());
    }

    @Given("I click the {string} button in the nav bar for fav")
    public void iClickTheButtonInTheNavBarForFav(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(arg0+"Link")));
        driver.findElement(By.className(arg0+"Link")).click();
    }

    @Then("I should be on the {string} page from fav")
    public void iShouldBeOnThePageFromFav(String arg0) {
        assertTrue(driver.getCurrentUrl().contains(arg0.toLowerCase()));

    }

    @And("the park, {string} should be removed from a's list of favorites")
    public void theParkShouldBeRemovedFromASListOfFavorites(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        assertFalse(driver.getPageSource().contains(arg0));
    }

    @When("I then check the slider again")
    public void iThenCheckTheSliderAgain() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        driver.findElement(By.className("switch")).click();
    }

    @Then("my favorites list should then be private")
    public void myFavoritesListShouldThenBePrivate() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        WebElement slider = driver.findElement(By.className("slider"));
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String sliderColor = slider.getCssValue("background-color");
        assertEquals(sliderColor, "rgba(0, 255, 0, 1)");
    }

    @And("I click on the delete all favorites button")
    public void iClickOnTheDeleteAllFavoritesButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("delete-all-button")));
        driver.findElement(By.className("delete-all-button")).click();
    }

    @Then("I should see a confirmation dialog before deleting all favorites")
    public void iShouldSeeAConfirmationDialogBeforeDeletingAllFavorites() {
        wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Are you sure you want to delete all favorites?", driver.switchTo().alert().getText());
    }

    @Then("I should see a positive confirmation dialog after deleting all favorites")
    public void iShouldSeeAPositiveConfirmationDialogAfterDeletingAllFavorites() {
        wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("All favorites deleted, and privacy status was set back to private.", driver.switchTo().alert().getText());
    }

    @Then("I should still see the parks in the list of favorites")
    public void iShouldStillSeeTheParksInTheListOfFavorites() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-header")));
        assertTrue(driver.getPageSource().contains("Yosemite National Park"));
        assertTrue(driver.getPageSource().contains("John Muir National Historic Site"));
    }

    @And("I should see no favorites")
    public void iShouldSeeNoFavorites() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("no-results-message")));
        assertTrue(driver.findElement(By.className("no-results-message")).isDisplayed());
    }

    @And("I should not be able to go back to the fav page")
    public void iShouldNotBeAbleToGoBackToTheFavPage() {
        driver.get(ROOT_URL + "favorites");
        assertEquals(driver.getCurrentUrl(), ROOT_URL + "404");
    }

    @Given("I click the logout button for fav")
    public void iClickTheLogoutButtonForFav() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("LogoutButton")));
        driver.findElement(By.className("LogoutButton")).click();
    }

    @Then("I should be on the login page for fav")
    public void iShouldBeOnTheLoginPageForFav() {
        assertEquals(ROOT_URL, driver.getCurrentUrl());
    }

    @When("I am on the login page for fav")
    public void iAmOnTheLoginPageForFav() {
        driver.get(ROOT_URL);
    }

    @Then("I should not have access for fav")
    public void iShouldNotHaveAccessForFav() {
        driver.get(ROOT_URL + "favorites");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("app-title")));
        assertEquals(driver.getCurrentUrl(), ROOT_URL + "404");
    }

    @Given("I am on the fav page using HTTP")
    public void iAmOnTheFavPageUsingHTTP() {
        driver.get("http://localhost:8080/favorites");
    }

    @Then("I should see a SSL error for fav")
    public void iShouldSeeASSLErrorForFav() {
        assertTrue(driver.getPageSource().contains("Bad Request"));
    }
}
