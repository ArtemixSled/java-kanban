package http.handlers;

import http.adapter.DurationAdapter;
import http.adapter.LocalDateAdapter;
import http.adapter.LocalDateTimeAdapter;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.Endpoint;
import manager.TaskManager;
import model.Epic;
import model.StatusTask;
import model.SubTask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                getEpics(exchange);
                break;
            }
            case GET_EPIC_BY_ID: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                getEpicById(exchange, id);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                getAllSubTaskByEpic(exchange, id);
                break;
            }
            case POST_CREATE_EPIC: {
                createEpic(exchange);
                break;
            }
            case DELETE_EPIC: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                deleteEpic(exchange, id);
                break;
            }
            default:
                sendText(exchange, "Not Found", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("epics") && requestMethod.equals("GET")) {
            return Endpoint.GET_EPICS;
        } else if (pathParts.length == 3 && pathParts[1].equals("epics") && pathParts[2].matches("\\d+")
                && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_BY_ID;
        } else if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[2].matches("\\d+")
                && pathParts[3].equals("subtasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        } else if (pathParts.length == 2 && pathParts[1].equals("epics") && requestMethod.equals("POST")) {
            return Endpoint.POST_CREATE_EPIC;
        } else if (pathParts.length == 3 && pathParts[1].equals("epics") && pathParts[2].matches("\\d+")
                && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_EPIC;
        }

        return Endpoint.UNKNOWN;
    }

    private void getEpics(HttpExchange exchange) {
        List<Epic> epics = taskManager.getAllEpics();
        String jsonString = gson.toJson(epics);
        sendText(exchange, jsonString, 200);
    }

    private void getEpicById(HttpExchange exchange, int id) {
        Epic epic = taskManager.returnEpicByID(id);

        if (epic == null) {
            sendText(exchange, "Not Found", 404);
        }

        String jsonString = gson.toJson(epic);
        sendText(exchange, jsonString, 200);
    }

    private void getAllSubTaskByEpic(HttpExchange exchange, int id) {
        Epic epic = taskManager.returnEpicByID(id);

        if (epic == null) {
            sendText(exchange, "Not Found", 404);
        }

        List<SubTask> subTaskList = taskManager.getAllSubTaskByEpic(epic);

        String jsonString = gson.toJson(subTaskList);
        sendText(exchange, jsonString, 200);
    }

    private void createEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);;

        try {
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                    String nameTask = jsonObject.get("nameTask").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    StatusTask statusTask = StatusTask.valueOf(jsonObject.get("statusTask").getAsString());

                    Epic epic = new Epic(nameTask, description, statusTask);
                    if (taskManager.createEpic(epic) == null) {
                        sendText(exchange, "Not Acceptable", 406);
                    }

                    sendText(exchange, "Success", 201);
                } else {
                    sendText(exchange, "{\"Пустой массив\"}", 400);
                }
            } else {
                sendText(exchange, "{\"Ожидался массив JSON\"}", 400);
            }
        } catch (JsonSyntaxException e) {
            sendText(exchange, "{\"Ошибка в синтаксисе JSON\"}", 400);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            sendText(exchange, "{\"Неверное значение поля: " + e.getMessage() + "\"}", 400);
            e.printStackTrace();
        } catch (Exception e) {
            sendText(exchange, "{\"Произошла ошибка: " + e.getMessage() + "\"}", 500);
            e.printStackTrace();
        }
    }

    private void deleteEpic(HttpExchange exchange, int id) {
        Epic epic = taskManager.returnEpicByID(id);

        if (epic == null) {
            sendText(exchange, "Not Found", 404);
        }

        taskManager.deleteEpicByID(id);
        sendText(exchange, "Задача id" + id + " удалена", 200);
    }
}