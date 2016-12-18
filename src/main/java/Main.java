import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import helper.Utility;
import models.User;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.sql.SQLException;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        get("/hello", (req, res) -> "Hello Panouri World");

        setEndpoints();
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    private static void setEndpoints() {
        String databaseUrl = Utility.DB_URL;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
            ((JdbcConnectionSource)connectionSource).setUsername(Utility.DB_USER);
            ((JdbcConnectionSource)connectionSource).setPassword(Utility.DB_PASSWORD);

            Endpoints.setUserEndpoints(connectionSource);
            Endpoints.setClientEndpoints(connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
