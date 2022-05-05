package ru.netology.test;

import com.codeborne.selenide.Configuration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.db.DbInteraction;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.netology.data.DataGenerator.Registration.setInvalidPassword;

public class DashboardTest {
    private static final DbInteraction db = new DbInteraction();
    private DataGenerator.UserInfo userInfo;
    private LoginPage loginPage;

    @BeforeEach
    private void setup() {
        Configuration.holdBrowserOpen = true;
        Configuration.browserSize = "1000x800";

        userInfo = DataGenerator.Registration.generateActiveUser(); // генерируем нового пользователя
        db.addUser(userInfo); // заносим нового пользователя в БД
        loginPage = open("http://localhost:7777", LoginPage.class);
    }

//    @AfterAll
//    static void cleanDB() {
//        db.clearData();
//    }

    @Test
    @SneakyThrows
    public void shouldLoginAndVerify() {
        var verificationPage = loginPage.validLogin(userInfo); // логинимся через UI
        Thread.sleep(100);
        var verificationCode = db.getVerificationCode(userInfo); // получаем смс-код из БД
        var dashboard = verificationPage.validVerify(verificationCode); // вводим проверочный код через UI

        dashboard.checkIfVisible();
    }

    @Test
    public void shouldNotVerify() {
        var verificationPage = loginPage.validLogin(userInfo); // логинимся через UI
        verificationPage.verify("0000"); // вводим неверный проверочный код через UI

        verificationPage.checkIfErrorAppears();
    }

    @Test
    @SneakyThrows
    public void shouldBlockSystem() {
        var invalidUserInfo = setInvalidPassword(userInfo);

        loginPage.login(invalidUserInfo); // трижды пытаемся войти с неверным паролем через UI
        loginPage.login(invalidUserInfo);
        loginPage.login(invalidUserInfo);
        loginPage.login(userInfo); // логинимся с верным паролем
        Thread.sleep(100);
        var verificationCode = db.getVerificationCode(userInfo); // проверяем появился ли смс-код в БД

        assertNull(verificationCode);
        loginPage.checkIfErrorAppears();
    }
}