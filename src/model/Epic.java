package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskList;

    private LocalDateTime endTime;

    public Epic(String nameTask, String description, StatusTask statusTask) {
        super(nameTask, description, statusTask);
        this.subTaskList = new ArrayList<>();
    }

    public Epic(String nameTask, String description, StatusTask statusTask, LocalDateTime startTime) {
        super(nameTask, description, statusTask, startTime);
        this.subTaskList = new ArrayList<>();
    }

    public Epic(String nameTask, String description, StatusTask statusTask, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, statusTask, startTime, duration);
        this.subTaskList = new ArrayList<>();
    }

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(List<Integer> subTaskList) {
        this.subTaskList = subTaskList;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime entTime) {
        this.endTime = entTime;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nsubTaskList=" + subTaskList +
                '}';
    }
}
