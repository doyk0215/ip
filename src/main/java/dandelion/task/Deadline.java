package dandelion.task;

/**
 * Represents a task that has a deadline.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Description of the task.
     * @param by Deadline for completing the task.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns this task's deadline.
     *
     * @return Deadline for completing this task.
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] "
                + getDescription() + " (by: " + by + ")";
    }
}
