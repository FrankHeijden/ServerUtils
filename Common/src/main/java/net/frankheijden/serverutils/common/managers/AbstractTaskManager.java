package net.frankheijden.serverutils.common.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.frankheijden.serverutils.common.entities.AbstractTask;

public abstract class AbstractTaskManager<T> {

    private final List<T> serverTasks;
    private final Consumer<T> taskCloser;
    private final Map<String, AbstractTask> tasks;

    /**
     * Constructs a new TaskManager with a consumer which closes a task.
     * @param taskCloser The consumer which will close tasks.
     */
    public AbstractTaskManager(Consumer<T> taskCloser) {
        this.taskCloser = taskCloser;
        this.serverTasks = new ArrayList<>();
        this.tasks = new HashMap<>();
    }

    public abstract void runTask(Runnable runnable);

    public void runTask(String key, AbstractTask task) {
        tasks.put(key, task);
        runTask(task);
    }

    public abstract void runTaskAsynchronously(Runnable runnable);

    public void runTaskAsynchronously(String key, AbstractTask task) {
        tasks.put(key, task);
        runTaskAsynchronously(task);
    }

    public void addTask(T task) {
        serverTasks.add(task);
    }

    /**
     * Cancels a single task by key.
     * @param key The key of the task.
     * @return Whether or not the task existed.
     */
    public boolean cancelTask(String key) {
        AbstractTask task = tasks.remove(key);
        if (task == null) return false;
        task.cancel();
        return true;
    }

    /**
     * Cancels all tasks.
     */
    public void cancelAllTasks() {
        for (AbstractTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();

        for (T task : serverTasks) {
            taskCloser.accept(task);
        }
        serverTasks.clear();
    }
}
