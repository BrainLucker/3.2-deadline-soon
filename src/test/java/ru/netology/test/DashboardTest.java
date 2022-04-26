package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.db.DbInteraction;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DashboardTest { // java -jar .\artifacts\app-deadline.jar -P:jdbc.url=jdbc:mysql://localhost:3306/app -P:jdbc.user=admin -P:jdbc.password=pass -port=7777
    private static final DbInteraction db = new DbInteraction();
    private DataGenerator.UserInfo userInfo;

    @BeforeEach
    private void setup() {
        Configuration.holdBrowserOpen = true;
        Configuration.browserSize = "1000x800";
        userInfo = DataGenerator.Registration.generateActiveUser();
    }

    @AfterAll
    static void cleanDB() {
        db.clearData();
    }

    @Test
    public void shouldLoginAndVerify() {
        var userId = userInfo.getId();

        db.addUser(userInfo); // заносим нового пользователя в БД
        var loginPage = open("http://localhost:7777", LoginPage.class);
        var verificationPage = loginPage.validLogin(userInfo); // логинимся через UI
        var verificationCode = db.getVerificationCode(userId); // получаем смс-код из БД
        var dashboard = verificationPage.validVerify(verificationCode); // вводим проверочный код через UI

        dashboard.getHeading().shouldBe(visible);
    }

    @Test
    public void shouldNotVerify() {
        db.addUser(userInfo); // заносим нового пользователя в БД
        var loginPage = open("http://localhost:7777", LoginPage.class);
        var verificationPage = loginPage.validLogin(userInfo); // логинимся через UI
        var errorNotification = verificationPage.invalidVerify("0000"); // вводим неверный проверочный код через UI

        errorNotification.shouldBe(visible).shouldHave(text("Неверно указан код! Попробуйте ещё раз."));
    }

    @Test
    public void shouldBlockSystem() {
        var userId = userInfo.getId();

        db.addUser(userInfo); // заносим нового пользователя в БД
        var loginPage = open("http://localhost:7777", LoginPage.class);
        loginPage.invalidLogin(userInfo); // трижды пытаемся войти с неверным паролем через UI
        loginPage.invalidLogin(userInfo);
        loginPage.invalidLogin(userInfo);
        var errorNotification = loginPage.validLoginAfterTreeAttempts(userInfo); // логинимся с верным паролем
        var verificationCode = db.getVerificationCode(userId); // проверяем появился ли смс-код в БД

        assertNull(verificationCode);
        errorNotification.shouldBe(visible);
    }
}