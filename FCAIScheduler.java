import java.util.*;

public class FCAIScheduler {
    private List<Process> processes; // All processes
    private List<Process> readyQueue; // Dynamically updated ready queue
    private int contextSwitchingTime;
    private int currentTime = 0;
    private Map<String, List<Integer>> quantumHistory; // Tracks quantum history for each process

    public FCAIScheduler(List<Process> processes, int contextSwitchingTime) {
        this.processes = new ArrayList<>(processes);
        this.readyQueue = new ArrayList<>();
        this.contextSwitchingTime = contextSwitchingTime;
        this.quantumHistory = new HashMap<>();
        for (Process p : processes) {
            quantumHistory.put(p.getName(), new ArrayList<>());
        }
    }

    public void schedule() {
        List<String> executionOrder = new ArrayList<>();
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        while (!allProcessesCompleted()) {
            // Add new processes to the ready queue
            addToReadyQueue();

            if (readyQueue.isEmpty()) {
                currentTime++; // Increment time if no processes are ready
                continue;
            }

            // Recalculate V1 and V2 dynamically
            double v1 = calculateV1();
            double v2 = calculateV2();

            // Update FCAI factors for all processes in the ready queue
            for (Process p : readyQueue) {
                p.updateFcaiFactor(v1, v2);
            }

            // Sort ready queue by FCAI factor
            readyQueue.sort(Comparator.comparingDouble(Process::getFcaiFactor));

            // Select the process with the lowest FCAI factor
            Process selectedProcess = readyQueue.getFirst();

            // Add current quantum to quantum history
            quantumHistory.get(selectedProcess.getName()).add(selectedProcess.getQuantum());

            // Execute process for 40% of its quantum
            int timeSlice = (int) Math.ceil(selectedProcess.getQuantum() * 0.4);
            int executionTime = Math.min(timeSlice, selectedProcess.getRemainingTime());

            System.out.println(selectedProcess.getName() + ": ( "+ currentTime + " - "+ (currentTime+executionTime) + " )");

            currentTime += executionTime;
            System.out.println("Time: after 40% " + currentTime);

            // Update remaining burst time
            selectedProcess.setRemainingTime(selectedProcess.getRemainingTime() - executionTime);

            // Check if preemption is possible after 40% quantum
            if (executionTime >= timeSlice && selectedProcess.getRemainingTime() > 0) {
                // Check for preemption by recalculating FCAI Factors
                addToReadyQueue(); // Update the queue
                v1 = calculateV1();
                v2 = calculateV2();
                for (Process p : readyQueue) {
                    p.updateFcaiFactor(v1, v2);
                }
                readyQueue.sort(Comparator.comparingDouble(Process::getFcaiFactor));

                Process potentialPreemptingProcess = readyQueue.getFirst();
                if (potentialPreemptingProcess != selectedProcess) {
                    // Preempt the current process
                    System.out.println("Switching ...");
                    int unusedQuantum = selectedProcess.getQuantum() - executionTime;
                    selectedProcess.adjustQuantum(unusedQuantum);

                    // Context switch and let the new process run immediately
                    executionOrder.add(selectedProcess.getName()); // Log preempted process
                    currentTime += contextSwitchingTime; // Add context switching time
                    continue; // Restart loop to run the new process
                }
            }

            // If the process finishes or continues, adjust quantum and move on
            if (selectedProcess.getRemainingTime() > 0) {
                // Continue running for rest of quantum
                int remainingQuantum = Math.min((selectedProcess.getQuantum() - executionTime), selectedProcess.getRemainingTime());
                selectedProcess.setRemainingTime(selectedProcess.getRemainingTime() - remainingQuantum);
                currentTime += remainingQuantum;
                System.out.println("Time: after rest of quantum " + currentTime);
                selectedProcess.adjustQuantum(2); // Add 2 to quantum if not finished
            } else {
                readyQueue.remove(selectedProcess); // Remove completed process
                selectedProcess.setCompletionTime(currentTime); // Set completion time
                selectedProcess.setTurnaroundTime(currentTime - selectedProcess.getArrivalTime());
                selectedProcess.setWaitingTime(selectedProcess.getTurnaroundTime() - selectedProcess.getBurstTime());
            }

            executionOrder.add(selectedProcess.getName());
            currentTime += contextSwitchingTime; // Add context switching time
        }

        // Calculate and print results
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();

            System.out.printf("Process %s: Waiting Time = %d, Turnaround Time = %d\n",
                    p.getName(), p.getWaitingTime(), p.getTurnaroundTime());
        }

        double averageWaitingTime = totalWaitingTime / processes.size();
        double averageTurnaroundTime = totalTurnaroundTime / processes.size();

        System.out.println("\nExecution Order: " + String.join(" -> ", executionOrder));
        System.out.printf("Average Waiting Time: %.2f\n", averageWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", averageTurnaroundTime);

        // Print quantum history for each process
        System.out.println("\nQuantum History:");
        for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
            System.out.printf("Process %s: %s\n", entry.getKey(), entry.getValue());
        }
    }

    // Dynamically calculate V1 (last arrival time / 10)
    private double calculateV1() {
        return readyQueue.stream().mapToInt(Process::getArrivalTime).max().orElse(0) / 10.0;
    }

    // Dynamically calculate V2 (max burst time / 10)
    private double calculateV2() {
        return readyQueue.stream().mapToInt(Process::getBurstTime).max().orElse(0) / 10.0;
    }

    // Add processes to the ready queue as they arrive
    private void addToReadyQueue() {
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && !readyQueue.contains(p) && !p.isCompleted()) {
                readyQueue.add(p);
            }
        }
    }

    // Check if all processes are completed
    private boolean allProcessesCompleted() {
        return processes.stream().allMatch(p -> p.getRemainingTime() <= 0);
    }

    public static void main(String[] args) {
        // Example processes
        List<Process> processList = Arrays.asList(
                new Process("P1", "Red", 0, 17, 4, 4),
                new Process("P2", "Blue", 3, 6, 9, 3),
                new Process("P3", "Green", 4, 10, 3, 5),
                new Process("P4", "Green", 29, 4, 8, 2)
        );

        FCAIScheduler scheduler = new FCAIScheduler(processList, 2);
        scheduler.schedule();
    }
}

