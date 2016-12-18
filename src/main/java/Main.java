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

        Spark.get("/users", new Route() {
            public Object handle(Request request, Response response) {
                return  "User: username=test, email=test@test.net";
            }
        });

        connectToDB();
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }

    private static void connectToDB() {
        String databaseUrl = Utility.DB_URL;

        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
            ((JdbcConnectionSource)connectionSource).setUsername(Utility.DB_USER);
            ((JdbcConnectionSource)connectionSource).setPassword(Utility.DB_PASSWORD);

            Dao<User,String> userDao = DaoManager.createDao(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);

            post("/users", (request, response) -> {
                try {
                    String username = request.queryParams("username");
                    String email = request.queryParams("email");

                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);

                    try {
                        userDao.create(user);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    response.status(201);
                    response.type("application/json");
                    return user;
                } catch (Exception e) {
                    return "";
                }
            });

            Spark.get("/users/:id", new Route() {
                @Override
                public Object handle(Request request, Response response) {
                    try {
                        User user = userDao.queryForId(request.params(":id"));
                        if (user != null) {
                            return "Username: " + user.getUsername(); // or JSON? :-)
                        } else {
                            response.status(404); // 404 Not found
                            return "User not found";
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return response;
                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
