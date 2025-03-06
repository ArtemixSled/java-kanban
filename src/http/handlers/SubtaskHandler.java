package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.Endpoint;
import manager.TaskManager;
import model.StatusTask;
import model.SubTask;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                getSubtasks(exchange);
                break;
            }
            case GET_SUBTASK_BY_ID: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                getSubtaskById(exchange, id);
                break;
            }
            case POST_UPDATE_SUBTASK: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                for (SubTask subTask : taskManager.getAllSubTask()) {
                    if (subTask.getId() == id) {
                        updateSubtaskById(exchange, id);
                    }
                }
                sendText(exchange, "Not Found", 404);
                break;
            }
            case POST_CREATE_SUBTASK: {
                createSubTask(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                deleteSubtask(exchange, id);
                break;
            }
            default:
                sendText(exchange, "Not Found", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("subtasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASKS;
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks") && pathParts[2].matches("\\d+")
                && requestMethod.equals("GET")) {
            return Endpoint.GET_SUBTASK_BY_ID;
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks") && pathParts[2].matches("\\d+")
                && requestMethod.equals("POST")) {
            return Endpoint.POST_UPDATE_SUBTASK;
        } else if (pathParts.length == 2 && pathParts[1].equals("subtasks") && requestMethod.equals("POST")) {
            return Endpoint.POST_CREATE_SUBTASK;
        } else if (pathParts.length == 3 && pathParts[1].equals("subtasks") && pathParts[2].matches("\\d+")
                && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_SUBTASK;
        }

        return Endpoint.UNKNOWN;
    }

    private void getSubtasks(HttpExchange exchange) {
        List<SubTask> subTaskList = taskManager.getAllSubTask();
        String jsonString = gson.toJson(subTaskList);
        sendText(exchange, jsonString, 200);
    }

    private void getSubtaskById(HttpExchange exchange, int id) {
        SubTask subTask = taskManager.returnSubTaskByID(id);

        if (subTask == null) {
            sendText(exchange, "Not Found", 404);
        }

        String jsonString = gson.toJson(subTask);
        sendText(exchange, jsonString, 200);
    }

    private void updateSubtaskById(HttpExchange exchange, int id) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                    int idEpic = jsonObject.get("idEpic").getAsInt();
                    String nameTask = jsonObject.get("nameTask").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    StatusTask statusTask = StatusTask.valueOf(jsonObject.get("statusTask").getAsString());
                    long minutes = Long.parseLong(jsonObject.get("duration").getAsString());
                    Duration duration = Duration.ofMinutes(minutes);
                    LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());

                    SubTask subTask = new SubTask(nameTask, description, statusTask, idEpic, startTime, duration);
                    subTask.setId(id);
                    if (taskManager.returnSubTaskByID(id) == null) {
                        sendText(exchange, "Not Found", 404);
                    }
                    if (taskManager.updateSubTask(subTask) == null) {
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

    private void createSubTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                    int idEpic = jsonObject.get("idEpic").getAsInt();
                    String nameTask = jsonObject.get("nameTask").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    StatusTask statusTask = StatusTask.valueOf(jsonObject.get("statusTask").getAsString());
                    long minutes = Long.parseLong(jsonObject.get("duration").getAsString());
                    Duration duration = Duration.ofMinutes(minutes);
                    LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());

                    SubTask subTask = new SubTask(nameTask, description, statusTask, idEpic, startTime, duration);
                    if (taskManager.createSubTask(subTask) == null) {
                        sendText(exchange, "Not Acceptable", 406);
                    }

                    String jsonString = "{\"Задача создана\"}";
                    sendText(exchange, jsonString, 201);
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

    private void deleteSubtask(HttpExchange exchange, int id) {
        SubTask subTask = taskManager.returnSubTaskByID(id);

        if (subTask == null) {
            sendText(exchange, "Not Found", 404);
        }
        taskManager.deleteSubTaskByID(id);
        sendText(exchange, "Success", 200);
    }
}
