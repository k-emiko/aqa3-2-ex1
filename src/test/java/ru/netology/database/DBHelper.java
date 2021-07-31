package ru.netology.database;

import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import ru.netology.data.UserGenerator;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {

    public static void setUp(String dbUrl, UserGenerator.User user) {
        val runner = new QueryRunner();
        val dataSQL = "INSERT INTO users(login, password, id) VALUES (?, ?, ?);";
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
            runner.update(conn, dataSQL, user.getLogin(), user.getPasswordDb(), user.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void cleanUp(String dbUrl) {
        val runner = new QueryRunner();
        val dataSQL = "DElETE FROM users;";
        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass")
        ) {
            runner.execute(conn, "SET FOREIGN_KEY_CHECKS = 0;");
            runner.update(conn, dataSQL);
            runner.execute(conn, "SET FOREIGN_KEY_CHECKS = 1;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getCode(String dbUrl, UserGenerator.User user) {
        String authCode = null;
        val runner = new QueryRunner();
        val idSQL = "SELECT id FROM users WHERE login=?;";
        val dataSQL = "SELECT code FROM auth_codes WHERE user_id=? AND created=(select max(created) from auth_codes)";

        try (
                val conn = DriverManager.getConnection(
                        dbUrl, "app", "pass"
                )
        ) {
            String userId = runner.query(conn, idSQL, new ScalarHandler<>(), user.getLogin());
            authCode = runner.query(conn, dataSQL, new ScalarHandler<>(), userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authCode;
    }
}
