public class Process {
    // Attributes
    private String name;         // Process name (e.g., P1, P2, etc.)
    private String color;        // Color for graphical representation
    private int arrivalTime;     // Time the process arrives in the ready queue
    private int burstTime;       // Total burst time required by the process
    private int priority;        // Priority of the process (lower value = higher priority)
    private int remainingTime;   // Remaining burst time during execution
    private double fcaiFactor;   // FCAI factor for custom scheduling
    private int quantum;         // Time quantum allocated for the process

    // Constructor
    public Process(String name, String color, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.color = color;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime; // Initially, remaining time equals burst time
        this.fcaiFactor = 0.0;          // Initial FCAI factor is set to 0
        this.quantum = 0;              // Initial quantum is set to 0
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public double getFcaiFactor() {
        return fcaiFactor;
    }

    public void setFcaiFactor(double fcaiFactor) {
        this.fcaiFactor = fcaiFactor;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    // Methods

    /**
     * Updates the FCAI factor for the process based on the formula:
     * FCAI Factor = (10 - Priority) + (Arrival Time / V1) + (Remaining Time / V2)
     *
     * @param v1 Derived from last arrival time / 10
     * @param v2 Derived from max burst time / 10
     */
    public void updateFcaiFactor(double v1, double v2) {
        this.fcaiFactor = (10 - this.priority) + (this.arrivalTime / v1) + (this.remainingTime / v2);
    }

    /**
     * Resets the quantum based on FCAI scheduling rules.
     * For example: Increment by 2 or unused quantum.
     *
     * @param increment Value to increment the quantum by
     */
    public void adjustQuantum(int increment) {
        this.quantum += increment;
    }

    @Override
    public String toString() {
        return String.format("Process{name='%s', arrivalTime=%d, burstTime=%d, priority=%d, remainingTime=%d, fcaiFactor=%.2f, quantum=%d}",
                name, arrivalTime, burstTime, priority, remainingTime, fcaiFactor, quantum);
    }

    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
