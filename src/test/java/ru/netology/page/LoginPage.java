package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.data.DataGenerator;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login] input");
    private final SelenideElement passwordField = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification]");


    public VerificationPage validLogin(DataGenerator.UserInfo info) {
        loginField.val(info.getLogin());
        passwordField.val(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public void invalidLogin(DataGenerator.UserInfo info) {
        loginField.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
        loginField.val(info.getLogin());
        passwordField.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
        passwordField.doubleClick().val("0000");
        loginButton.click();
    }

    public SelenideElement validLoginAfterTreeAttempts(DataGenerator.UserInfo info) {
        loginField.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
        loginField.doubleClick().val(info.getLogin());
        passwordField.sendKeys(Keys.CONTROL + "a", Keys.BACK_SPACE);
        passwordField.doubleClick().val(info.getPassword());
        loginButton.click();
        return errorNotification;
    }
}