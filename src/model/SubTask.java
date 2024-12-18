package model;

public class SubTask extends Task {
    private int idEpic;

    public SubTask(String nameTask, String description, StatusTask statusTask, int idEpic) {
        super(nameTask, description, statusTask);
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
