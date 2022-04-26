package ru.netology.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.db.DbInteraction;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class DashboardTest { // java -jar .\artifacts\app-deadline.jar -P:jdbc.url=jdbc:mysql://localhost:3306/app -P:jdbc.user=admin -P:jdbc.password=pass -port=7777
    private static final DbInteraction db = new DbInteraction();

    @BeforeEach
    private void setup() {
        Configuration.holdBrowserOpen = true;
        Configuration.browserSize = "1000x800";
    }

    @AfterAll
    static void cleanDB() {
        db.clearData();
    }

    @Test
    public void shouldLogin() {
        var userInfo = DataGenerator.Registration.generateActiveUser();
        db.addUser(userInfo); // заносим пользователя в БД
        var loginPage = open("http://localhost:7777", LoginPage.class);
        var verificationPage = loginPage.validLogin(userInfo);
        var user = db.findUserByLogin(userInfo.getLogin()); // находим пользователя в БД
        var verificationCode = db.getVerificationCode(user); // получаем смс-код из БД
        verificationPage.validVerify(verificationCode);
    }

    @Test
    public void shouldBlockSystem() {
        var authInfo = DataGenerator.Registration.generateActiveUser();
        db.addUser(authInfo);
        var loginPage = open("http://localhost:7777", LoginPage.class);
        loginPage.invalidLoginTriple(authInfo);

    }
}