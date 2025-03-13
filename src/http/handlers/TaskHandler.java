package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.Endpoint;
import model.StatusTask;
import model.Task;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import manager.TaskManager;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                getTasks(exchange);
                break;
            }
            case GET_TASK_BY_ID: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                getTaskById(exchange, id);
                break;
            }
            case POST_UPDATE_TASK: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                for (Task task : taskManager.getAllTasks()) {
                    if (task.getId() == id) {
                        updateTaskById(exchange, id);
                    }
                }
                sendText(exchange, "Not Found", 404);
                break;
            }
            case POST_CREATE_TASK: {
                createTask(exchange);
                break;
            }
            case DELETE_TASK: {
                String[] pathParts = exchange.getRequestURI().getPath().split("/");
                int id = Integer.parseInt(pathParts[2]);
                deleteTask(exchange, id);
                break;
            }
            default:
                sendText(exchange, "Not Found", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("tasks") && requestMethod.equals("GET")) {
            return Endpoint.GET_TASKS;
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && pathParts[2].matches("\\d+")
                && requestMethod.equals("GET")) {
            return Endpoint.GET_TASK_BY_ID;
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && pathParts[2].matches("\\d+")
                && requestMethod.equals("POST")) {
            return Endpoint.POST_UPDATE_TASK;
        } else if (pathParts.length == 2 && pathParts[1].equals("tasks") && requestMethod.equals("POST")) {
            return Endpoint.POST_CREATE_TASK;
        } else if (pathParts.length == 3 && pathParts[1].equals("tasks") && pathParts[2].matches("\\d+")
                && requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_TASK;
        }

        return Endpoint.UNKNOWN;
    }

    public void getTasks(HttpExchange exchange) {
        List<Task> tasks = taskManager.getAllTasks();
        String jsonString = gson.toJson(tasks);
        sendText(exchange, jsonString, 200);
    }

    public void getTaskById(HttpExchange exchange, int id) {
        Task task = taskManager.returnTaskByID(id);

        if (task == null) {
            sendText(exchange, "Not Found", 404);
        }

        String jsonString = gson.toJson(task);
        sendText(exchange, jsonString, 200);
    }

    public void updateTaskById(HttpExchange exchange, int id) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                    String nameTask = jsonObject.get("nameTask").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    StatusTask statusTask = StatusTask.valueOf(jsonObject.get("statusTask").getAsString());
                    long minutes = Long.parseLong(jsonObject.get("duration").getAsString());
                    Duration duration = Duration.ofMinutes(minutes);
                    LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());

                    Task task = new Task(nameTask, description, statusTask, startTime, duration);
                    task.setId(id);
                    if (taskManager.returnTaskByID(id) == null) {
                        sendText(exchange, "Not Found", 404);
                    }
                    if (taskManager.updateTask(task) == null) {
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

    public void createTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try {
            JsonElement jsonElement = JsonParser.parseString(body);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();

                    String nameTask = jsonObject.get("nameTask").getAsString();
                    String description = jsonObject.get("description").getAsString();
                    StatusTask statusTask = StatusTask.valueOf(jsonObject.get("statusTask").getAsString());
                    long minutes = Long.parseLong(jsonObject.get("duration").getAsString());
                    Duration duration = Duration.ofMinutes(minutes);
                    LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());

                    Task task = new Task(nameTask, description, statusTask, startTime, duration);
                    if (taskManager.createTask(task) == null) {
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

    public void deleteTask(HttpExchange exchange, int id) {
        Task task = taskManager.returnTaskByID(id);

        if (task == null) {
            sendText(exchange, "Not Found", 404);
        }

        taskManager.deleteTaskByID(id);
        sendText(exchange, "Success", 200);
    }
}
