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
    private final Map<String, RunningTask> tasks;

    /**
     * Constructs a new TaskManager with a consumer which closes a task.
     * @param taskCloser The consumer which will close tasks.
     */
    public AbstractTaskManager(Consumer<T> taskCloser) {
        this.taskCloser = taskCloser;
        this.serverTasks = new ArrayList<>();
        this.tasks = new HashMap<>();
    }

    protected abstract T runTaskImpl(Runnable runnable);

    public T runTask(Runnable runnable) {
        return addTask(runTaskImpl(runnable));
    }

    /**
     * Associates a synchronous task with a key which can be cancelled later by that key.
     * @param key The key of the task.
     * @param abstractTask The AbstractTask.
     * @return The implementation-specific scheduled task.
     */
    public T runTask(String key, AbstractTask abstractTask) {
        T task = runTask(abstractTask);
        tasks.put(key, new RunningTask(task, abstractTask));
        return task;
    }

    protected abstract T runTaskAsynchronouslyImpl(Runnable runnable);

    public T runTaskAsynchronously(Runnable runnable) {
        return addTask(runTaskAsynchronouslyImpl(runnable));
    }

    /**
     * Associates an asynchronous task with a key which can be cancelled later by that key.
     * @param key The key of the task.
     * @param abstractTask The AbstractTask.
     * @return The implementation-specific scheduled task.
     */
    public T runTaskAsynchronously(String key, AbstractTask abstractTask) {
        T task = runTaskAsynchronously(abstractTask);
        tasks.put(key, new RunningTask(task, abstractTask));
        return task;
    }

    private T addTask(T task) {
        serverTasks.add(task);
        return task;
    }

    public abstract void cancelTask(T task);

    /**
     * Cancels a single task by key.
     * @param key The key of the task.
     * @return Whether or not the task existed.
     */
    public boolean cancelTask(String key) {
        RunningTask task = tasks.remove(key);
        if (task == null) return false;
        task.cancel();
        return true;
    }

    /**
     * Cancels all tasks.
     */
    public void cancelAllTasks() {
        for (RunningTask task : tasks.values()) {
            task.cancel();
        }
        tasks.clear();

        for (T task : serverTasks) {
            taskCloser.accept(task);
        }
        serverTasks.clear();
    }

    private final class RunningTask {
        private final T task;
        private final AbstractTask abstractTask;

        private RunningTask(T task, AbstractTask abstractTask) {
            this.task = task;
            this.abstractTask = abstractTask;
        }

        public void cancel() {
            cancelTask(task);
            abstractTask.cancel();
        }
    }
}
