package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.Endpoint;
import manager.TaskManager;
import model.Task;
import java.util.TreeSet;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_PRIORITIZED: {
                getPrioritized(exchange);
                break;
            }
            default:
                sendText(exchange, "Not Found", 404);
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED;
        }

        return Endpoint.UNKNOWN;
    }

    private void getPrioritized(HttpExchange exchange) {
        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String jsonString = gson.toJson(prioritizedTasks);
        sendText(exchange, jsonString, 200);
    }

}
