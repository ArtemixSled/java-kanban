package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.Endpoint;
import manager.TaskManager;
import model.Task;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private static TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
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
