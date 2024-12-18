package model;

public class Task {

    private String nameTask;

    private String description;

    private int id;

    private StatusTask statusTask;

    public Task(String nameTask, String description, StatusTask statusTask) {
        this.nameTask = nameTask;
        this.description = description;
        this.statusTask = statusTask;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusTask getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(StatusTask statusTask) {
        this.statusTask = statusTask;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Task task = (Task) object;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        int hash = 17;

        if (id != 0) {
            hash = hash + Integer.hashCode(id);
        }

        hash = hash * 31;

        return hash;
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", statusTask=" + statusTask +
                '}';
    }
}
