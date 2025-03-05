package http.handlers;

import http.adapter.DurationAdapter;
import http.adapter.LocalDateAdapter;
import http.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.Endpoint;
import manager.TaskManager;
import model.Task;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private static TaskManager taskManager;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HistoryHandler(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY: {
                getHistory(exchange);
                break;
            }
            default:
                sendText(exchange, "Not Found", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }

        return Endpoint.UNKNOWN;
    }

    private void getHistory(HttpExchange exchange) {
        List<Task> historyTasks = taskManager.getHistory();
        String jsonString = gson.toJson(historyTasks);
        sendText(exchange, jsonString, 200);
    }
}
