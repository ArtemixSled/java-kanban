import java.util.Objects;

public class SubTask extends Task {
    private int idEpic;

    public SubTask(String nameTask, String description, StatusTask statusTask, Epic epicTask) {
        super(nameTask, description, statusTask);
        this.idEpic = epicTask.getId();
    }

    @Override
    public String toString() {
        return super.toString() +
                ", idEpic=" + idEpic +
                '}';
    }

    public int getIdEpic() {
        return idEpic;
    }

    public void setIdEpic(int idEpic) {
        this.idEpic = idEpic;
    }
}
