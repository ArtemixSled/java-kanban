package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int idEpic;

    public SubTask(String nameTask, String description, StatusTask statusTask, int idEpic, LocalDateTime startTime) {
        super(nameTask, description, statusTask, startTime);
        this.idEpic = idEpic;
    }

    public SubTask(String nameTask, String description, StatusTask statusTask, int idEpic, LocalDateTime startTime, Duration duration) {
        super(nameTask, description, statusTask, startTime, duration);
        this.idEpic = idEpic;
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
