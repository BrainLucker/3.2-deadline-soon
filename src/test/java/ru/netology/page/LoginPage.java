package ru.netology.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataGenerator;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement loginField = $("[data-test-id=login] input");
    private final SelenideElement passwordField = $("[data-test-id=password] input");
    private final SelenideElement loginButton = $("[data-test-id=action-login]");

    public VerificationPage validLogin(DataGenerator.UserInfo info) {
        loginField.val(info.getLogin());
        passwordField.val(info.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public void invalidLoginTriple(DataGenerator.UserInfo info) {
        loginField.val(info.getLogin());
        passwordField.val("0000");
        loginButton.click();
        loginButton.click();
        loginButton.click();
    }
}