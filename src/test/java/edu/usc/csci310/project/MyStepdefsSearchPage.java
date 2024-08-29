package edu.usc.csci310.project;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyStepdefsSearchPage {
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

    @Given("I have a database cleared and users created for search")
    public void iHaveADatabaseClearedAndUsersCreated() {
        setUp();
        Utils.clearDb();
        Utils.createUsers(driver, wait);
    }

    @After
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Given("I am on the search page")
    public void iAmOnTheSearchPage() {
        driver.get(ROOT_URL+"search");
    }

    @And("I put {string} in the search box")
    public void iPutInTheSearchBox(String string) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-box")));
        driver.findElement(By.className("search-box")).sendKeys(string);
    }

    @When("I click the search button")
    public void iClickTheSearchButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-button")));
        driver.findElement(By.className("search-button")).click();
    }

    @And("I select the state radio button")
    public void iSelectTheStateRadioButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("radio-button-state")));
        driver.findElement(By.className("radio-button-state")).click();
    }

    @And("I select the activity radio button")
    public void iSelectTheActivityRadioButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("radio-button-activity")));
        driver.findElement(By.className("radio-button-activity")).click();
    }

    @And("I select the amenities radio button")
    public void iSelectTheAmenitiesRadioButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("radio-button-amenities")));
        driver.findElement(By.className("radio-button-amenities")).click();
    }

    @Then("I should see {string}")
    public void iShouldSee(String string) {
        // check that string is on the webpage
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-list")));

        driver.getPageSource().contains(string);
//        assertTrue(driver.findElement(By.className("park-list")).getText().contains(string));
    }

    @When("I should see {int} parks")
    public void iShouldSeeParks(int num) {
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className("park-card"), num));
        List<WebElement> parkCards = driver.findElements(By.className("park-card"));
        assertEquals(num, parkCards.size());
    }

    @When("I click the Show More Results button")
    public void iClickTheShowMoreResultsButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("show-more-button")));
        driver.findElement(By.className("show-more-button")).click();
    }

    @And("I should see at least one search result")
    public void iShouldSeeAtLeastOneSearchResult() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card")));
        assertFalse(driver.findElements(By.className("park-card")).isEmpty());
    }

    @When("I click on the first park’s name to expand the box")
    public void iClickOnTheFirstParkSNameToExpandTheBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-name")));
        driver.findElements(By.className("park-card-name")).get(0).click();
    }

    @Then("I should see a URL for the park")
    public void iShouldSeeAURLForThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-url")));
        assertTrue(driver.findElement(By.className("park-url")).isDisplayed());
    }

    @When("I click the URL for the park")
    public void iClickTheURLForThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-url")));
        driver.findElement(By.className("park-url")).click();
    }

    @Then("I should go the park’s webpage")
    public void iShouldGoTheParkSWebpage() {
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        driver.switchTo().window(driver.getWindowHandles().toArray()[1].toString());
        assertTrue(driver.getCurrentUrl().contains("https://www.nps.gov/"));
    }

    @Then("I should see an image of the park")
    public void iShouldSeeAnImageOfThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-image")));
        assertTrue(driver.findElement(By.className("park-image")).isDisplayed());
    }

    @Then("I should see an description of the park")
    public void iShouldSeeAnDescriptionOfThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-description")));
        assertTrue(driver.findElement(By.className("park-description")).isDisplayed());
    }

    @Then("I should see the entrance fee of the park")
    public void iShouldSeeTheEntranceFeeOfThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-entrance-fee")));
        assertTrue(driver.findElement(By.className("park-entrance-fee")).isDisplayed());
    }

    @Then("I should see the location of the park")
    public void iShouldSeeTheLocationOfThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-location")));
        assertTrue(driver.findElement(By.className("park-location")).isDisplayed());
    }

    @Then("I should see the amenities at the park")
    public void iShouldSeeTheAmenitiesAtThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-amenities")));
        assertTrue(driver.findElement(By.className("park-amenities")).isDisplayed());
    }

    @Then("I should see the activities at the park")
    public void iShouldSeeTheActivitiesAtThePark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-activities")));
        assertTrue(driver.findElement(By.className("park-activities")).isDisplayed());
    }

    @And("I hover over the first park")
    public void iHoverOverTheFirstPark() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card")));
        WebElement firstParkCard = driver.findElement(By.className("park-card"));
        new Actions(driver).moveToElement(firstParkCard).perform();

    }

    @And("I click the plus button to add to favorites")
    public void iClickThePlusButtonToAddToFavorites() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button_plus")));
        driver.findElement(By.className("button_plus")).click();
    }

    @Then("I should see an alert that it was successful")
    public void iShouldSeeAnAlertThatItWasSuccessful() {
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();
        assertEquals("Park added to favorites successfully.", alertText);
    }

    @And("I click the plus button to add to favorites again")
    public void iClickThePlusButtonToAddToFavoritesAgain() {
        try {
            // Add a delay of 2 seconds (2000 milliseconds) before clicking the plus button again
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e);
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("button_plus")));
        driver.findElement(By.className("button_plus")).click();
    }

    @Then("I should see an alert that the park is already in favorites")
    public void iShouldSeeAnAlertThatTheParkIsAlreadyInFavorites() {
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        alert.accept();
        assertEquals("Park already in favorites.", alertText);
    }

    @And("I set session storage for {string} for search")
    public void iSetTheSessionStorageWithUsername(String username) {
        if (!storageSet) {
            Utils.setSessionStorage(username, driver);
            storageSet = true;
        }
    }

    @And("I dismiss the alert by pressing ok")
    public void iDismissTheAlertByPressingOk() {
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    @Then("I should see an alert that no results were found")
    public void iShouldSeeAnAlertThatNoResultsWereFound() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("no-results-found")));
        assertTrue(driver.findElement(By.className("no-results-found")).isDisplayed());
    }

    @When("I close the alert")
    public void iCloseTheAlert() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("no-results-close-button")));
        driver.findElement(By.className("no-results-close-button")).click();
    }

    @Then("the alert should be closed")
    public void theAlertShouldBeClosed() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("no-results-message")));
        assertTrue(driver.findElement(By.className("no-results-message")).isDisplayed());
    }

    @Then("I should see at least one search result named {string}")
    public void iShouldSeeAtLeastOneSearchResultNamed(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card")));
        List<WebElement> parkCards = driver.findElements(By.className("park-card"));
        boolean found = false;
        for (WebElement parkCard : parkCards) {
            if (parkCard.getText().contains(arg0)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @And("select the {string} radio button")
    public void selectTheRadioButton(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("radio-button-"+arg0)));
        driver.findElement(By.className("radio-button-"+arg0)).click();
    }

    @Then("I should see at least one search result with a stateCode of {string}")
    public void iShouldSeeAtLeastOneSearchResultWithAStateCodeOf(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-location")));
        List<WebElement> parkLocations = driver.findElements(By.className("park-location"));
        boolean foundMatch = false;
        for (WebElement parkLocation : parkLocations) {
            String locationText = parkLocation.getText();
            System.out.println(locationText);
            if (locationText.contains(", " + arg0)) {
                foundMatch = true;
                break;
            }
        }
        assertTrue(foundMatch);
        }

    @Then("I should see at least one search result with an activity of {string}")
    public void iShouldSeeAtLeastOneSearchResultWithAnActivityOf(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-activities")));
        List<WebElement> parkActivities = driver.findElements(By.className("park-activities"));
        boolean foundMatch = false;
        for (WebElement parkActivity : parkActivities) {
            String activityText = parkActivity.getText();
            System.out.println(activityText);
            if (activityText.toLowerCase().contains(arg0.toLowerCase())) {
                foundMatch = true;
                break;
            }
        }
        assertTrue(foundMatch);
    }

    @Then("I should see a show more button")
    public void iShouldSeeAShowMoreButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("show-more-button")));
        assertTrue(driver.findElement(By.className("show-more-button")).isDisplayed());
    }

    @When("I click the show more button")
    public void iClickTheShowMoreButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("show-more-button")));
        driver.findElement(By.className("show-more-button")).click();
    }

    @Then("I should see {int} search results")
    public void iShouldSeeSearchResults(int arg0) {
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className("park-card"), arg0));
        List<WebElement> parkCards = driver.findElements(By.className("park-card"));
        assertEquals(arg0, parkCards.size());
    }

    @Then("I should see a favorite indicator")
    public void iShouldSeeAFavoriteIndicator() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-favorite?")));
        assertTrue(driver.findElement(By.className("park-favorite?")).isDisplayed());
    }

    @Then("I should see a search result with a {string} amenity")
    public void iShouldSeeASearchResultWithAAmenity(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-amenities")));
        List<WebElement> parkAmenities = driver.findElements(By.className("park-amenities"));
        boolean foundMatch = false;
        for (WebElement parkAmenity : parkAmenities) {
            String amenityText = parkAmenity.getText();
            System.out.println(amenityText);
            System.out.println("Search Value = " + arg0);
            if (amenityText.toLowerCase().contains(arg0.toLowerCase())) {
                foundMatch = true;
                break;
            }
        }
        assertTrue(foundMatch);
    }
    @Then("I should see the image and location of park at the top of the box")
    public void iShouldSeeTheImageAndLocationOfParkAtTheTopOfTheBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-image")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-location")));
        assertTrue(driver.findElement(By.className("park-image")).isDisplayed());
        assertTrue(driver.findElement(By.className("park-location")).isDisplayed());
    }

    @And("I click the {string} button in the nav bar")
    public void iClickTheButtonInTheNavBar(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(arg0+"Link")));
        driver.findElement(By.className(arg0+"Link")).click();
    }

    @Then("I should be on the {string} page")
    public void iShouldBeOnThePage(String arg0) {
        assertTrue(driver.getCurrentUrl().contains(arg0.toLowerCase()));
    }

    @Then("I should see {int} more search results")
    public void iShouldSeeMoreSearchResults(int arg0) {
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("park-card"), arg0));
        List<WebElement> parkCards = driver.findElements(By.className("park-card"));
        assertEquals(20, parkCards.size());
    }

    @When("I click on the first park’s name again to collapse the box")
    public void iClickOnTheFirstParkSNameAgainToCollapseTheBox() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-name")));
        driver.findElements(By.className("park-card-name")).get(0).click();
    }

    @Then("the first park's name should no longer be green")
    public void theFirstParkSNameShouldNoLongerBeGreen() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-name")));
        String color = driver.findElements(By.className("park-card-name")).get(0).getCssValue("color");
        assertFalse(color.contains("rgb(0, 128, 0)"));
    }

    @And("I click on the first park’s stateCode which is {string}")
    public void iClickOnTheFirstParkSStateCodeWhichIs(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-state-code")));
        List<WebElement> parkStateCodes = driver.findElements(By.className("park-state-code"));
        for (WebElement parkStateCode : parkStateCodes) {
            if (parkStateCode.getText().equals(arg0)) {
                parkStateCode.click();
                break;
            }
        }
    }

    @Then("I should see at least one search result with the same stateCode as the previous first park which is {string}")
    public void iShouldSeeAtLeastOneSearchResultWithTheSameStateCodeAsThePreviousFirstParkWhichIs(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-name")));
        driver.findElements(By.className("park-card-name")).get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-location")));
        List<WebElement> parkLocations = driver.findElements(By.className("park-location"));
        boolean foundMatch = false;
        for (WebElement parkLocation : parkLocations) {
            String locationText = parkLocation.getText();
            System.out.println(locationText);
            if (locationText.contains(", " + arg0)) {
                foundMatch = true;
                break;
            }
        }
        assertTrue(foundMatch);
    }

    @And("I click on the first activity in the activities list which is {string}")
    public void iClickOnTheFirstActivityInTheActivitiesListWhichIs(String activityName) {
        List<WebElement> activities = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("park-activity")));
        activities.stream() //Put activities into a stream and find the first one...
                .filter(activity -> activity.getText().equals(activityName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Activity not found: " + activityName))
                .click();
    }

    @Then("I should see at least one search result with the same activity as the previous first park which is {string}")
    public void iShouldSeeAtLeastOneSearchResultWithTheSameActivityAsThePreviousFirstParkWhichIs(String activityName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-name")));
        driver.findElements(By.className("park-card-name")).get(1).click();

        List<WebElement> parkCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("park-card")));
        boolean foundActivity = parkCards.stream()
                .flatMap(parkCard -> parkCard.findElements(By.className("park-activity")).stream())
                .anyMatch(activity -> activity.getText().equals(activityName));
        assertTrue(foundActivity);
    }

    @And("I click on the first amenity in the amenities list which is {string}")
    public void iClickOnTheFirstAmenityInTheAmenitiesListWhichIs(String amenityName) {
        List<WebElement> amenities = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("park-amenity")));
        amenities.stream() //Put amenities into a stream and find the first one...
                .filter(amenity -> amenity.getText().equals(amenityName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Amenity not found: " + amenityName))
                .click();
    }

    @Then("I should see at least one search result with the same amenity as the previous first park which is {string}")
    public void iShouldSeeAtLeastOneSearchResultWithTheSameAmenityAsThePreviousFirstParkWhichIs(String amenityName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-card-name")));
        driver.findElements(By.className("park-card-name")).get(0).click();

        List<WebElement> parkCards = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("park-card")));
        boolean foundAmenity = parkCards.stream()
                .flatMap(parkCard -> parkCard.findElements(By.className("park-amenity")).stream())
                .anyMatch(amenity -> amenity.getText().equals(amenityName));
        assertTrue(foundAmenity);
    }

    @Then("i should see the park favorite question set to {string}")
    public void iShouldSeeTheParkFavoriteQuestionSetTo(String arg0) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("park-favorite?")));
        assertTrue(driver.findElement(By.className("park-favorite?")).getText().contains(arg0));
    }

    @And("I am logged in as user {string} for search")
    public void iAmLoggedInAsUser(String name) {
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

    @Given("I click the logout button for search")
    public void iClickTheLogoutButton() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("LogoutButton")));
        driver.findElement(By.className("LogoutButton")).click();
    }

    @Then("I should be on the login page for search")
    public void iShouldBeOnTheLoginPage() {
        assertEquals(ROOT_URL, driver.getCurrentUrl());
    }

    @And("I should not be able to go back to the search page")
    public void iShouldNotBeAbleToGoBackToTheSearchPage() {
        driver.get(ROOT_URL+"search");
        assertEquals(ROOT_URL + "404", driver.getCurrentUrl());
    }

    @Then("I should not have access for search")
    public void iShouldNotHaveAccessForSearch() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("access-denied")));
        assertEquals(driver.getCurrentUrl(), ROOT_URL + "404");
    }

    @Given("I am on the search page using HTTP")
    public void iAmOnTheSearchPageUsingHTTP() {
        driver.get("http://localhost:8080/search");
    }

    @Then("I should see a SSL error for search")
    public void iShouldSeeASSLErrorForSearch() {
        assertTrue(driver.getPageSource().contains("Bad Request"));
    }
}