package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login] input");
    private final SelenideElement passwordField = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification]");

    public void login(DataGenerator.UserInfo userInfo) {
        loginField.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
        loginField.doubleClick().val(userInfo.getLogin());
        passwordField.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
        passwordField.doubleClick().val(userInfo.getPassword());
        loginButton.click();
    }

    public VerificationPage validLogin(DataGenerator.UserInfo userInfo) {
        login(userInfo);
        return new VerificationPage();
    }

    public void checkIfErrorAppears() {
        errorNotification.shouldBe(visible);
    }
}