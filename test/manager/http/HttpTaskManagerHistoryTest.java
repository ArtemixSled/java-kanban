package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskServer;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.InMemoryTaskManager;
import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerHistoryTest {

    private InMemoryTaskManager manager;

    private HttpTaskServer taskServer;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerHistoryTest() {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteAllTasks();
        manager.deleteAllSubTask();
        manager.deleteAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void testGetHistoryTasks() throws IOException, InterruptedException {
        Epic epic = new Epic("epic", "epic", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subTask = new SubTask("Subtask", "Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
        manager.createSubTask(subTask);

        Task task = new Task("Task", "Description", StatusTask.NEW,
                LocalDateTime.of(2005, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();

        URI url = URI.create("http://localhost:8080/tasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response2.body();
        assertTrue(responseBody.contains("Task"), "Задача не найдена в ответе");

        assertEquals(200, response2.statusCode(), "Неверный статус код");
    }
}
