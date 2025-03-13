package manager.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import http.HttpTaskServer;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateAdapter;
import http.adapter.LocalDateTimeAdapter;
import manager.InMemoryTaskManager;
import model.Epic;
import model.StatusTask;
import model.SubTask;
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
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtaskTest {

    private InMemoryTaskManager manager;

    private HttpTaskServer taskServer;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerSubtaskTest() {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Test Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(10));

        JsonArray taskArray = new JsonArray();
        taskArray.add(gson.toJsonTree(subtask));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskArray.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код");

        List<SubTask> tasksFromManager = manager.getAllSubTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask", tasksFromManager.get(0).getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Test Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(10));
        manager.createSubTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        List<SubTask> tasksFromManager = manager.getAllSubTask();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Subtask", tasksFromManager.get(0).getNameTask(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Test Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(10));
        manager.createSubTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Subtask"), "Задача не найдена в ответе");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Test Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(10));
        manager.createSubTask(subtask);

        subtask.setNameTask("Updated Task");
        subtask.setDescription("Updated Description");

        JsonArray taskArray = new JsonArray();
        taskArray.add(gson.toJsonTree(subtask));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskArray.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код");

        SubTask updatedTask = manager.returnSubTaskByID(2);
        assertNotNull(updatedTask, "Задача не обновилась");
        assertEquals("Updated Task", updatedTask.getNameTask(), "Имя задачи не обновилось");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание задачи не обновилось");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subtask = new SubTask("Subtask", "Test Description", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0), Duration.ofMinutes(10));
        manager.createSubTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        SubTask deletedTask = manager.returnSubTaskByID(2);
        assertNull(deletedTask, "Задача не удалена");
    }
}
