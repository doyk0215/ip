public class Event extends Task{
    private final String from;
    private final String to;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description Description of the task.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] "
                + getDescription() + " (from: " + from + " to: " + to + ")";
    }
}
