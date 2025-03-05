package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import http.HttpTaskServer;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.InMemoryTaskManager;
import model.StatusTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Duration;

public class HttpTaskManagerTasksTest {

    private InMemoryTaskManager manager;

    private HttpTaskServer taskServer;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerTasksTest() {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description", StatusTask.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(10));

        JsonArray taskArray = new JsonArray();
        taskArray.add(gson.toJsonTree(task));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskArray.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test Task", tasksFromManager.get(0).getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusTask.NEW,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofHours(1));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task 1"), "Задача не найдена в ответе");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusTask.NEW, LocalDateTime.now(), Duration.ofHours(1));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task 1"), "Задача не найдена в ответе");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusTask.NEW, LocalDateTime.now(), Duration.ofHours(1));
        manager.createTask(task);

        task.setNameTask("Updated Task");
        task.setDescription("Updated Description");

        JsonArray taskArray = new JsonArray();
        taskArray.add(gson.toJsonTree(task));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskArray.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код");

        Task updatedTask = manager.returnTaskByID(1);
        assertNotNull(updatedTask, "Задача не обновилась");
        assertEquals("Updated Task", updatedTask.getNameTask(), "Имя задачи не обновилось");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание задачи не обновилось");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description 1", StatusTask.NEW, LocalDateTime.now(), Duration.ofHours(1));
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        Task deletedTask = manager.returnTaskByID(1);
        assertNull(deletedTask, "Задача не удалена");
    }
}
