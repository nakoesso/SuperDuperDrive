package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    // -------------------------
    // Helper methods
    // -------------------------

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private void clickHidden(String elementId) {
        ((JavascriptExecutor) driver).executeScript(
                "document.getElementById('" + elementId + "').click();");
    }

    private void doMockSignUp(String firstName, String lastName, String userName, String password) {
        driver.get(url("/signup"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName"))).sendKeys(firstName);
        driver.findElement(By.id("inputLastName")).sendKeys(lastName);
        driver.findElement(By.id("inputUsername")).sendKeys(userName);
        driver.findElement(By.id("inputPassword")).sendKeys(password);
        driver.findElement(By.id("buttonSignUp")).click();
    }

    private void doLogIn(String userName, String password) {
        driver.get(url("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername"))).sendKeys(userName);
        driver.findElement(By.id("inputPassword")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
        wait.until(ExpectedConditions.titleContains("Home"));
    }

    private void doLogOut() {
        driver.findElement(By.cssSelector("form[action='/logout'] button")).click();
        wait.until(ExpectedConditions.titleContains("Login"));
    }

    private void openNoteModal() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#nav-notes button.btn-info"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));
    }

    private void openCredentialModal() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#nav-credentials button.btn-info"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
    }

    // -------------------------
    // Tests Udacity fournis
    // -------------------------

    @Test
    public void getLoginPage() {
        driver.get(url("/login"));
        Assertions.assertEquals("Login", driver.getTitle());
    }

    @Test
    public void testRedirection() {
        doMockSignUp("Redirection", "Test", "RT" + (System.currentTimeMillis() % 10000), "123");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    public void testBadUrl() {
        String username = "UT" + (System.currentTimeMillis() % 10000);
        doMockSignUp("URL", "Test", username, "123");
        doLogIn(username, "123");
        driver.get(url("/some-random-page"));
        Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }

    // -------------------------
    // 1. Signup, Login, Unauthorized Access
    // -------------------------

    @Test
    public void testUnauthorizedAccessRestriction() {
        driver.get(url("/login"));
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get(url("/signup"));
        Assertions.assertEquals("Sign Up", driver.getTitle());

        driver.get(url("/home"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    @Test
    public void testSignupLoginLogout() {
        String username = "jd" + (System.currentTimeMillis() % 100000);
        doMockSignUp("John", "Doe", username, "pass123");
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));

        doLogIn(username, "pass123");
        Assertions.assertEquals("Home", driver.getTitle());

        doLogOut();
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));

        driver.get(url("/home"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"));
    }

    // -------------------------
    // 2. Notes
    // -------------------------

    @Test
    public void testCreateNoteAndVerifyDisplay() {
        String username = "nu1" + (System.currentTimeMillis() % 100000);
        doMockSignUp("Note", "User", username, "pass");
        doLogIn(username, "pass");

        openNoteModal();
        driver.findElement(By.id("note-title")).sendKeys("Test Title");
        driver.findElement(By.id("note-description")).sendKeys("Test Description");
        clickHidden("noteSubmit");

        wait.until(ExpectedConditions.titleContains("Home"));
        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));

        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Test Title"));
        Assertions.assertTrue(pageSource.contains("Test Description"));
    }

    @Test
    public void testEditNoteAndVerifyChanges() {
        String username = "nu2" + (System.currentTimeMillis() % 100000);
        doMockSignUp("Note", "User", username, "pass");
        doLogIn(username, "pass");

        openNoteModal();
        driver.findElement(By.id("note-title")).sendKeys("Original Title");
        driver.findElement(By.id("note-description")).sendKeys("Original Description");
        clickHidden("noteSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#userTable .btn-success"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));

        WebElement titleField = driver.findElement(By.id("note-title"));
        titleField.clear();
        titleField.sendKeys("Updated Title");

        WebElement descField = driver.findElement(By.id("note-description"));
        descField.clear();
        descField.sendKeys("Updated Description");

        clickHidden("noteSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userTable")));
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Updated Title"));
        Assertions.assertTrue(pageSource.contains("Updated Description"));
        Assertions.assertFalse(pageSource.contains("Original Title"));
    }

    @Test
    public void testDeleteNoteAndVerifyRemoval() {
        String username = "nu3" + (System.currentTimeMillis() % 100000);
        doMockSignUp("Note", "User", username, "pass");
        doLogIn(username, "pass");

        openNoteModal();
        driver.findElement(By.id("note-title")).sendKeys("To Delete");
        driver.findElement(By.id("note-description")).sendKeys("Will be deleted");
        clickHidden("noteSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#userTable .btn-danger"))).click();
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-notes-tab"))).click();
        Assertions.assertFalse(driver.getPageSource().contains("To Delete"));
    }

    // -------------------------
    // 3. Credentials
    // -------------------------

    @Test
    public void testCreateCredentialVerifyEncryptedPassword() {
        String username = "cu1" + (System.currentTimeMillis() % 100000);
        doMockSignUp("Cred", "User", username, "pass");
        doLogIn(username, "pass");

        openCredentialModal();
        driver.findElement(By.id("credential-url")).sendKeys("https://example.com");
        driver.findElement(By.id("credential-username")).sendKeys("myuser");
        driver.findElement(By.id("credential-password")).sendKeys("mypassword");
        clickHidden("credentialSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));

        Assertions.assertTrue(driver.getPageSource().contains("https://example.com"));
        WebElement passwordCell = driver.findElement(
                By.cssSelector("#credentialTable tbody tr td:last-child"));
        Assertions.assertNotEquals("mypassword", passwordCell.getText());
    }

    @Test
    public void testViewCredentialUnencryptedPasswordAndEdit() {
        String username = "cu2" + (System.currentTimeMillis() % 100000);
        doMockSignUp("Cred", "User", username, "pass");
        doLogIn(username, "pass");

        openCredentialModal();
        driver.findElement(By.id("credential-url")).sendKeys("https://test.com");
        driver.findElement(By.id("credential-username")).sendKeys("testuser");
        driver.findElement(By.id("credential-password")).sendKeys("secret123");
        clickHidden("credentialSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#credentialTable .btn-success"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-password")));

        Assertions.assertEquals("secret123",
                driver.findElement(By.id("credential-password")).getAttribute("value"));

        WebElement urlField = driver.findElement(By.id("credential-url"));
        urlField.clear();
        urlField.sendKeys("https://updated.com");

        clickHidden("credentialSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
        Assertions.assertTrue(driver.getPageSource().contains("https://updated.com"));
    }

    @Test
    public void testDeleteCredentialAndVerifyRemoval() {
        String username = "cu3" + (System.currentTimeMillis() % 100000);
        doMockSignUp("Cred", "User", username, "pass");
        doLogIn(username, "pass");

        openCredentialModal();
        driver.findElement(By.id("credential-url")).sendKeys("https://todelete.com");
        driver.findElement(By.id("credential-username")).sendKeys("deluser");
        driver.findElement(By.id("credential-password")).sendKeys("delpass");
        clickHidden("credentialSubmit");
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#credentialTable .btn-danger"))).click();
        wait.until(ExpectedConditions.titleContains("Home"));

        wait.until(ExpectedConditions.elementToBeClickable(By.id("nav-credentials-tab"))).click();
        Assertions.assertFalse(driver.getPageSource().contains("https://todelete.com"));
    }
}
