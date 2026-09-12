package dandelion.storage;

import dandelion.task.Deadline;
import dandelion.task.Event;
import dandelion.task.Task;
import dandelion.task.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores tasks in and loads tasks from a local text file.
 */
public class Storage {
    /** Location of the task data file, relative to the project root. */
    private static final Path FILE_PATH = Path.of("data", "dandelion.txt");

    /**
     * Loads all saved tasks from the data file.
     *
     * @return Loaded tasks, or an empty list when no data file exists.
     * @throws IOException If the data file cannot be read.
     * @throws IllegalArgumentException If a saved task has an invalid format.
     */
    public List<Task> loadTasks() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(FILE_PATH)) {
            return tasks;
        }

        for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
            if (!line.isBlank()) {
                tasks.add(convertLineToTask(line));
            }
        }
        return tasks;
    }

    /**
     * Saves the populated portion of a task array to the data file.
     *
     * @param tasks Task array to save.
     * @param taskCount Number of populated positions in {@code tasks}.
     * @throws IOException If the data directory or file cannot be written.
     */
    public void saveTasks(Task[] tasks, int taskCount) throws IOException {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < taskCount; i++) {
            lines.add(convertTaskToLine(tasks[i]));
        }

        Files.createDirectories(FILE_PATH.getParent());
        Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
    }
}
