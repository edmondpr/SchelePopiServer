import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import models.Client;
import models.User;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * Created by edmond on 18/12/2016.
 */
public class Endpoints {


    public static void setClientEndpoints(ConnectionSource connectionSource) {
        try {
            Dao<Client, String> clientDao = DaoManager.createDao(connectionSource, Client.class);
            TableUtils.createTableIfNotExists(connectionSource, Client.class);

            Spark.post("/clients", (request, response) -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Client client = mapper.readValue(request.body(), Client.class);
                    if (!client.isValid()) {
                        response.status(HTTP_BAD_REQUEST);
                        return "";
                    }
                    clientDao.create(client);

                    response.status(200);
                    response.type("application/json");
                    return "success";
                } catch (Exception e) {
                    response.status(HTTP_BAD_REQUEST);
                    return "failure";
                }
            });

            Spark.get("/clients", (request, response) -> {
                List<Client> clients = clientDao.queryForAll();

                response.status(200);
                response.type("application/json");
                return dataToJson(clients);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void setUserEndpoints(ConnectionSource connectionSource) {

        try {
            Dao<User, String> userDao = DaoManager.createDao(connectionSource, User.class);
            TableUtils.createTableIfNotExists(connectionSource, User.class);

            Spark.post("/users", (request, response) -> {
                try {
                    String username = request.queryParams("username");
                    String email = request.queryParams("email");

                    User user = new User();
                    user.setUsername(username);
                    user.setEmail(email);

                    userDao.create(user);

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

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }

}