import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, SubTask> subTaskList;

    public Epic(String nameTask, String description, StatusTask statusTask) {
        super(nameTask, description, statusTask);
        this.subTaskList = new HashMap<>();
    }

    public HashMap<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(HashMap<Integer, SubTask> subTaskList) {
        this.subTaskList = subTaskList;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nsubTaskList=" + subTaskList +
                '}';
    }
}
