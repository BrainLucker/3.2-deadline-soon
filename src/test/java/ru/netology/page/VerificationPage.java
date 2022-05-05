package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    private final SelenideElement codeField = $("[data-test-id=code] input");
    private final SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private final SelenideElement errorNotification = $("[data-test-id=error-notification]");
    private final String errorText = "Неверно указан код! Попробуйте ещё раз.";

    public VerificationPage() {
        codeField.shouldBe(visible);
    }

    public void verify(String verificationCode) {
        codeField.val(verificationCode);
        verifyButton.click();
    }

    public DashboardPage validVerify(String verificationCode) {
        verify(verificationCode);
        return new DashboardPage();
    }

    public void checkIfErrorAppears() {
        errorNotification.shouldBe(visible).shouldHave(text(errorText));
    }
}