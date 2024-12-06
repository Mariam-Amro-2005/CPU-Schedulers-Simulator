import java.util.*;

public class FCAIScheduler {
    private List<Process> processes;
    private int contextSwitchingTime;
    private int currentTime = 0;

    // Constructor
    public FCAIScheduler(List<Process> processes, int contextSwitchingTime) {
        this.processes = new ArrayList<>(processes);
        this.contextSwitchingTime = contextSwitchingTime;
    }

    // Method to schedule processes using the FCAI algorithm
    public void schedule() {
        int lastArrivalTime = getLastArrivalTime();
        int maxBurstTime = getMaxBurstTime();
        List<String> executionOrder = new ArrayList<>();

        while (!allProcessesCompleted()) {
            // Update FCAI factors for all ready processes
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.isCompleted()) {
                    p.updateFcaiFactor(lastArrivalTime, maxBurstTime);
                }
            }

            // Sort processes by FCAI factor (ascending order)
            processes.sort(Comparator.comparingDouble(Process::getFcaiFactor));

            // Select the process with the lowest FCAI factor
            Process selectedProcess = processes.stream()
                    .filter(p -> p.getArrivalTime() <= currentTime && !p.isCompleted())
                    .findFirst().orElse(null);

            if (selectedProcess != null) {
                // Execute process for 40% of its quantum
                int timeSlice = (int) Math.ceil(selectedProcess.getQuantum() * 0.4);
                int executionTime = Math.min(timeSlice, selectedProcess.getRemainingTime());
                currentTime += executionTime;

                // Update remaining burst time
                selectedProcess.setRemainingTime(selectedProcess.getRemainingTime() - executionTime);

                // Check if process needs preemption or adjustment
                if (selectedProcess.getRemainingTime() > 0) {
                    // Preempt process
                    int unusedQuantum = selectedProcess.getQuantum() - executionTime;
                    selectedProcess.adjustQuantum(unusedQuantum);
                } else {
                    // Process completed
                    selectedProcess.adjustQuantum(0);
                }

                executionOrder.add(selectedProcess.getName());
                currentTime += contextSwitchingTime; // Add context switching time
            } else {
                currentTime++; // Increment time if no process is ready
            }
        }

        // Print results
        System.out.println("Execution Order: " + String.join(" -> ", executionOrder));
    }

    // Helper method to get the last arrival time
    private int getLastArrivalTime() {
        return processes.stream().mapToInt(Process::getArrivalTime).max().orElse(0);
    }

    // Helper method to get the maximum burst time
    private int getMaxBurstTime() {
        return processes.stream().mapToInt(Process::getBurstTime).max().orElse(0);
    }

    // Helper method to check if all processes are completed
    private boolean allProcessesCompleted() {
        return processes.stream().allMatch(p -> p.getRemainingTime() <= 0);
    }

    public static void main(String[] args) {
        // Sample process list
        List<Process> processList = Arrays.asList(
                new Process("P1", "Red", 0, 10, 1),
                new Process("P2", "Blue", 2, 8, 2),
                new Process("P3", "Green", 4, 6, 3)
        );

        FCAIScheduler scheduler = new FCAIScheduler(processList, 2);
        scheduler.schedule();
    }
}
