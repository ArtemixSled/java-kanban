package http;

import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import manager.TaskManager;
import java.io.*;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private static TaskManager taskManager;

    HttpServer server;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        try {
            httpTaskServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler(taskManager));
        server.createContext("/epics", new EpicHandler(taskManager));
        server.createContext("/subtasks", new SubtaskHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(1);
    }
}
