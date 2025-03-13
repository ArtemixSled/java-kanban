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

public class HttpTaskManagerEpicTest {

    private InMemoryTaskManager manager;

    private HttpTaskServer taskServer;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    public HttpTaskManagerEpicTest() {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);

        JsonArray taskArray = new JsonArray();
        taskArray.add(gson.toJsonTree(epic));


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskArray.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Неверный статус код");

        List<Epic> tasksFromManager = manager.getAllEpics();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Task", tasksFromManager.get(0).getNameTask(), "Некорректное имя задачи");
    }


    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task"), "Задача не найдена в ответе");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Task"), "Задача не найдена в ответе");
    }

    @Test
    public void testGetSubtaskEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        SubTask subTask1 = new SubTask("Task 1", "Description 1", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2005, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));
        SubTask subTask2 = new SubTask("Task 2", "Description 2", StatusTask.NEW, epic.getId(),
                LocalDateTime.of(2001, 1, 1, 0, 0, 0, 0), Duration.ofHours(6));


        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SubTask> subtasks = manager.getAllSubTaskByEpic(epic);

        assertEquals(200, response.statusCode(), "Неверный статус код");
        assertEquals(2, subtasks.size(), "Некорректное количество задач");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Task", "Description", StatusTask.NEW);
        manager.createEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус код");

        Epic deletedTask = manager.returnEpicByID(1);
        assertNull(deletedTask, "Задача не удалена");
    }
}
