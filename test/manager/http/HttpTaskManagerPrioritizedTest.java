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

public class HttpTaskManagerPrioritizedTest {

    private InMemoryTaskManager manager;

    private HttpTaskServer taskServer;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerPrioritizedTest() {
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
    void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Epic1", "Test1", StatusTask.NEW));

        SubTask subTask1 = manager.createSubTask(new SubTask("SubTask1", "Test1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));
        subTask1.setDuration(Duration.ofHours(20));

        SubTask subTask2 = manager.createSubTask(new SubTask("SubTask2", "Test1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(1999, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));
        subTask2.setDuration(Duration.ofHours(10));

        manager.createTask(new Task("Task1", "Test1", StatusTask.NEW,
                LocalDateTime.of(2006, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        manager.createTask(new Task("Task2", "Test1", StatusTask.NEW,
                LocalDateTime.of(1998, 7, 1, 0, 0, 0, 0), Duration.ofHours(6)));

        manager.createTask(new Task("Чучело", "Test1", StatusTask.NEW, null));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        assertTrue(responseBody.contains("SubTask1"), "Задача не найдена в ответе");
        assertTrue(responseBody.contains("SubTask2"), "Задача не найдена в ответе");
        assertTrue(responseBody.contains("Task1"), "Задача не найдена в ответе");
        assertTrue(responseBody.contains("Task2"), "Задача не найдена в ответе");

        assertEquals(200, response.statusCode(), "Неверный статус код");
    }
}
