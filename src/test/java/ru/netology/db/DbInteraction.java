package ru.netology.db;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
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

        try (var conn = getConnection();) {
            runner.update(conn, "DELETE from card_transactions;");
            runner.execute(conn, "DELETE from cards;");
            runner.execute(conn, "DELETE from auth_codes;");
            runner.execute(conn, "DELETE from users;");
        }
    }

    @SneakyThrows
    public void addUser(DataGenerator.UserInfo user) {
        var runner = new QueryRunner();
        var dataSQL = "INSERT INTO users(id, login, password, status) VALUES (?, ?, ?, ?);";
        var password = passwordEncryption(user.getPassword()); // шифруем пароль для записи в БД

        try (var conn = getConnection();) {
            runner.update(conn, dataSQL, user.getId(), user.getLogin(), password, user.getStatus());
        }
    }

    @SneakyThrows
    public User findUserByLogin(String login) {
        var userSQL = "SELECT * FROM users WHERE login = ?;";
        var runner = new QueryRunner();

        try (var conn = getConnection();) {
            var first = runner.query(conn, userSQL, login, new BeanHandler<>(User.class));
            return first;
        }
    }

    @SneakyThrows
    public String getVerificationCode(User user) {
        var codeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC;";

        try (
                var conn = getConnection();
                var codeStmt = conn.prepareStatement(codeSQL);
        ) {
            codeStmt.setString(1, user.getId());
            try (var rs = codeStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("code");
                }
            }
            return null;
        }
    }

    @SneakyThrows
    public String GetUserStatus(User user) {
        var statusSQL = "SELECT status FROM users WHERE user_id = ?;";

        try (
                var conn = getConnection();
                var statusStmt = conn.prepareStatement(statusSQL);
        ) {
            statusStmt.setString(1, user.getId());
            try (var rs = statusStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
            return null;
        }
    }
}