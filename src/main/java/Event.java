/**
 * Represents a task that takes place during a specified period.
 */
public class Event extends Task {
    private final String from;
    private final String to;

    /**
     * Creates an incomplete event task.
     *
     * @param description Description of the task.
     * @param from Starting time of the event.
     * @param to Ending time of the event.
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
