package ru.netology.db;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.data.DataGenerator;
import ru.netology.mode.User;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbInteraction {

    @SneakyThrows
    private Connection getConnection() {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "admin", "pass");
    }

    private String passwordEncryption(String password) {
        // TODO Добавить алгоритм генерации пароля
        return "$2a$10$TnNjXMBKBO.I3l1rVG70sOJREv/AgKqDpkr5XuiUvY80tlFmhFzF2";
    }

    @SneakyThrows
    public void clearData() {
        var runner = new QueryRunner();

        try (var conn = getConnection()) {
            runner.update(conn, "DELETE from card_transactions;");
            runner.update(conn, "DELETE from cards;");
            runner.update(conn, "DELETE from auth_codes;");
            runner.update(conn, "DELETE from users;");
        }
    }

    @SneakyThrows
    public void addUser(DataGenerator.UserInfo user) {
        var runner = new QueryRunner();
        var dataSQL = "INSERT INTO users(id, login, password, status) VALUES (?, ?, ?, ?);";
        var password = passwordEncryption(user.getPassword()); // шифруем пароль для записи в БД

        try (var conn = getConnection()) {
            runner.update(conn, dataSQL, user.getId(), user.getLogin(), password, user.getStatus());
        }
    }

    @SneakyThrows
    public User findUserByLogin(String login) {
        var runner = new QueryRunner();
        var userSQL = "SELECT * FROM users WHERE login = ?;";

        try (var conn = getConnection()) {
            return runner.query(conn, userSQL, new BeanHandler<>(User.class), login);
        }
    }

    @SneakyThrows
    public String getVerificationCode(String userId) {
        var runner = new QueryRunner();
        var codeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC;";

        try (var conn = getConnection()) {
            return runner.query(conn, codeSQL, new ScalarHandler<>(), userId);
        }
    }
}