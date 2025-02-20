package manager;
import model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node<T> {
        T task;

        Node<T> prev;

        Node<T> next;

        Node(T task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> taskMap = new HashMap<>();

    private int size = 0;

    private Node head;

    private Node tail;

    @Override
    public void add(Task newTask) {
        if (newTask == null) {
            return;
        }

        Task task  = new Task(newTask.getNameTask(), newTask.getDescription(), newTask.getStatusTask(), newTask.getStartTime(), newTask.getDuration());
        task.setId(newTask.getId());

        Node<Task> newNode = new Node<>(task);

        linkLast(newNode);
        taskMap.put(newNode.task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {
        if (node == null) {
            return;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        taskMap.remove(node.task.getId());
        size--;
    }

    private void linkLast(Node<Task> node) {
        if (tail == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
        size++;
    }

    @Override
    public void remove(int id) {
        if (taskMap.containsKey(id)) {
            removeNode(taskMap.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            historyList.add(current.task);
            current = current.next;
        }
        return historyList;
    }
}
