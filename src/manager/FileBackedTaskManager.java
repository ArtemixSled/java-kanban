package manager;
import model.Epic;
import model.StatusTask;
import model.SubTask;
import model.Task;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic,startTime,duration");
            writer.newLine();

            Stream.concat(Stream.concat(getAllTasks().stream(), getAllEpics().stream()), getAllSubTask().stream())
                    .forEach(task -> {
                        try {
                            writer.write(toString(task));
                            writer.newLine();
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
                        }
                    });
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        int maxIdTask = 0;

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {

            fileReader.readLine();

            while (fileReader.ready()) {
                Task task = fromString(fileReader.readLine());

                if (task.getId() > maxIdTask) {
                    maxIdTask = task.getId();
                }

                if (task instanceof Epic) {
                    fileBackedTaskManager.epicList.put(task.getId(), (Epic)task);
                } else if (task instanceof SubTask) {
                    fileBackedTaskManager.subTaskList.put(task.getId(), (SubTask) task);
                } else if (task instanceof Task) {
                    fileBackedTaskManager.taskList.put(task.getId(), task);
                }
            }

            fileBackedTaskManager.setIdTask(maxIdTask + 1);

        } catch (IOException e) {
            throw new ManagerSaveException("Не удается прочитать файл", e);
        }

        return fileBackedTaskManager;
    }

    private String toString(Task task) {
        StringBuilder stringTask = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        stringTask.append(task.getId()).append(",");

        Duration duration = task.getDuration();
        String durationString = (duration != null) ? duration.toString() : "PT0M";

        if (task instanceof SubTask) {
            stringTask.append("SUBTASK,");
            SubTask subTask = (SubTask) task;
            stringTask.append(subTask.getNameTask());
            stringTask.append(",");
            stringTask.append(subTask.getStatusTask());
            stringTask.append(",");
            stringTask.append(subTask.getDescription());
            stringTask.append(",");
            stringTask.append(subTask.getIdEpic());
            stringTask.append(",");
            stringTask.append((task.getStartTime() != null) ? task.getStartTime().format(formatter) : "null");
            stringTask.append(",");
            stringTask.append(durationString);
        } else if (task instanceof Epic) {
            stringTask.append("EPIC,");
            Epic epic = (Epic) task;
            stringTask.append(epic.getNameTask());
            stringTask.append(",");
            stringTask.append(epic.getStatusTask());
            stringTask.append(",");
            stringTask.append(epic.getDescription());
            stringTask.append(",");
            stringTask.append("null");
            stringTask.append(",");
            stringTask.append((task.getStartTime() != null) ? task.getStartTime().format(formatter) : "null");
            stringTask.append(",");
            stringTask.append(durationString);
        } else {
            stringTask.append("TASK,");
            stringTask.append(task.getNameTask());
            stringTask.append(",");
            stringTask.append(task.getStatusTask());
            stringTask.append(",");
            stringTask.append(task.getDescription());
            stringTask.append(",");
            stringTask.append("null");
            stringTask.append(",");
            stringTask.append((task.getStartTime() != null) ? task.getStartTime().format(formatter) : "null");
            stringTask.append(",");
            stringTask.append(durationString);
        }
        return stringTask.toString();
    }

    private static Task fromString(String value) {
        String stringTask = value;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String[] split = stringTask.split(",");

        int id = Integer.parseInt(split[0]);
        String nameTask = split[2];
        StatusTask statusTask = StatusTask.valueOf(split[3]);
        String description = split[4];
        Integer idEpic = (!split[5].equals("null")) ? Integer.parseInt(split[5]) : null;
        LocalDateTime startTime = (!split[6].equals("null")) ? LocalDateTime.parse(split[6], formatter) : null;
        Duration duration = Duration.parse(split[7]);

        switch (split[1]) {
            case "SUBTASK":
                SubTask subTask = new SubTask(nameTask, description, statusTask, idEpic, startTime, duration);
                subTask.setId(id);
                return subTask;
            case "EPIC":
                Epic epic = new Epic(nameTask, description, statusTask, startTime, duration);
                epic.setId(id);
                return epic;
            default:
                Task task = new Task(nameTask, description, statusTask, startTime, duration);
                task.setId(id);
                return task;
        }
    }

    @Override
    public Task createTask(Task task) {
        Task taskNew = super.createTask(task);
        save();
        return taskNew;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic epicNew = super.createEpic(epic);
        save();
        return epicNew;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask subTaskNew = super.createSubTask(subTask);
        save();
        return subTaskNew;
    }

    @Override
    public Task returnTaskByID(int id) {
        Task taskNew = super.returnTaskByID(id);
        save();
        return taskNew;
    }

    @Override
    public Epic returnEpicByID(int id) {
        Epic epicNew = super.returnEpicByID(id);
        save();
        return epicNew;
    }

    @Override
    public SubTask returnSubTaskByID(int id) {
        SubTask subTaskNew = super.returnSubTaskByID(id);
        save();
        return subTaskNew;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    public File getFile() {
        return file;
    }
}
