package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subTaskList;

    public Epic(String nameTask, String description, StatusTask statusTask) {
        super(nameTask, description, statusTask);
        this.subTaskList = new ArrayList<>();
    }

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(List<Integer> subTaskList) {
        this.subTaskList = subTaskList;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nsubTaskList=" + subTaskList +
                '}';
    }


}
