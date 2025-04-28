package ru.netology.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

class CardOrderTest {
    private WebDriver driver;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox", "--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void shouldSendValidForm() {
        driver.findElement(By.cssSelector("[data-test-id=name] input"))
                .sendKeys("Иван-Петров Иван");
        driver.findElement(By.cssSelector("[data-test-id=phone] input"))
                .sendKeys("+79270000000");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.cssSelector("button.button")).click();

        String text = driver.findElement(By.cssSelector("[data-test-id=order-success]"))
                .getText().trim();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", text);
    }
}

// ───────── 1. имя на латинице ─────────
@Test
void shouldRejectLatinName() {
    driver.findElement(By.cssSelector("[data-test-id=name] input"))
            .sendKeys("John Snow");
    driver.findElement(By.cssSelector("[data-test-id=phone] input"))
            .sendKeys("+79270000000");
    driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
    driver.findElement(By.cssSelector("button.button")).click();

    String error = driver.findElement(
                    By.cssSelector("[data-test-id=name].input_invalid .input__sub"))
            .getText().trim();

    assertEquals("Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы.", error);
}

// ───────── 2. телефон без «+» ─────────
@Test
void shouldRejectPhoneWithoutPlus() {
    driver.findElement(By.cssSelector("[data-test-id=name] input"))
            .sendKeys("Иван Иванов");
    driver.findElement(By.cssSelector("[data-test-id=phone] input"))
            .sendKeys("89270000000");          // нет «+»
    driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
    driver.findElement(By.cssSelector("button.button")).click();

    String error = driver.findElement(
                    By.cssSelector("[data-test-id=phone].input_invalid .input__sub"))
            .getText().trim();

    assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", error);
}

// ───────── 3. чекбокс не отмечен ─────────
@Test
void shouldRejectUncheckedAgreement() {
    driver.findElement(By.cssSelector("[data-test-id=name] input"))
            .sendKeys("Иван Иванов");
    driver.findElement(By.cssSelector("[data-test-id=phone] input"))
            .sendKeys("+79270000000");
    // чекбокс не трогаем
    driver.findElement(By.cssSelector("button.button")).click();

    boolean highlighted = driver.findElement(
                    By.cssSelector("[data-test-id=agreement].input_invalid"))
            .isDisplayed();

    assertTrue(highlighted);
}