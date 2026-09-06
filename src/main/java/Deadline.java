public class Deadline extends Task{
    private final String by;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D][" + getStatusIcon() + "] "
                + getDescription() + " (by: " + by + ")";
    }
}
