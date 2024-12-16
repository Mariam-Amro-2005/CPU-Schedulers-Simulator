package logic;

import java.util.*;

public class SRTF {
    static List<Process> processList = new ArrayList<>();
    static List<String> executionOrder = new ArrayList<>(); // To store the execution order
    static int contextSwitchingTime;
    static int totalWaitTime = 0;
    static int totalTurnaroundTime = 0;
    private static Map<String, List<Integer>> executionHistory = new HashMap<>();

    public Map<String, List<Integer>> getExecutionHistory() {
        return executionHistory;
    }

    public static int getTotalWaitTime() {
        return totalWaitTime;
    }

    public static int getTotalTurnaroundTime() {
        return totalTurnaroundTime;
    }

    public static void setContextSwitchingTime(int contextSwitchingTime) {
        SRTF.contextSwitchingTime = contextSwitchingTime;
    }

    public static List<Process> getProcessList() {
        return processList;
    }

    public static void setProcessList(List<Process> processList) {
        SRTF.processList = processList;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = sc.nextInt();

        System.out.print("Enter the context switching time: ");
        contextSwitchingTime = sc.nextInt();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Process Name: ");
            String name = sc.next();

            System.out.print("Process Color: ");
            String color = sc.next();

            System.out.print("Arrival Time: ");
            int arrivalTime = sc.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = sc.nextInt();

            System.out.print("Priority: "); // Not used in SRTF but included for other schedulers
            int priority = sc.nextInt();

            processList.add(new Process(name, color, arrivalTime, burstTime, priority, 0));
        }

        simulateSRTF();

        sc.close();
    }

    public static void simulateSRTF() {

        int currentTime = 0;
        int completed = 0;
        int n = processList.size();
        Process currentProcess = null;

        for (Process p : processList) {
            executionHistory.put(p.getName(), new ArrayList<>());
        }

        while (completed < n) {
            Process nextProcess = getNextProcess(currentTime);

            if (nextProcess != null) {

                if (currentProcess != null && !currentProcess.equals(nextProcess)) {
                    // End time of the previous process >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    executionHistory.get(currentProcess.getName()).add(currentTime);
                    currentTime += contextSwitchingTime; // Add context switch time

                    
                }

                // Start time of the new process >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                if (executionHistory.get(nextProcess.getName()).isEmpty() ||
                        executionHistory.get(nextProcess.getName()).size() % 2 == 0) {
                    executionHistory.get(nextProcess.getName()).add(currentTime);
                }

                if (executionOrder.isEmpty()
                        || !executionOrder.get(executionOrder.size() - 1).equals(nextProcess.getName())) {
                    executionOrder.add(nextProcess.getName()); // Log execution order
                }

                currentProcess = nextProcess;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                currentTime++;

                if (currentProcess.getRemainingTime() <= 0) {
                    // End time of the completed process >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    executionHistory.get(currentProcess.getName()).add(currentTime);

                    // Process completes
                    currentProcess.setCompletionTime(currentTime);
                    currentProcess
                            .setTurnaroundTime(currentProcess.getCompletionTime() - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                    totalWaitTime += currentProcess.getWaitingTime();
                    totalTurnaroundTime += currentProcess.getTurnaroundTime();
                    completed++;
                }
            } else {
                currentTime++; // Idle time
            }
        }

        printResults(totalWaitTime, totalTurnaroundTime);
    }

    static Process getNextProcess(int currentTime) {
        return processList.stream()
                .filter(p -> p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0)
                .min(Comparator.comparingInt(Process::getRemainingTime))
                .orElse(null);
    }

    static void printResults(int totalWaitTime, int totalTurnaroundTime) {
        System.out.println("\nProcess Execution Order:");
        for (String pName : executionOrder) {
            System.out.print(pName + " ");
        }
        System.out.println();
        System.out.println("\nProcess Details:");
        for (Process p : processList) {
            System.out.println("Process " + p.getName() + " - Waiting Time: " + p.getWaitingTime()
                    + ", Turnaround Time: " + p.getTurnaroundTime());
        }

        double avgWaitTime = ((double) totalWaitTime / processList.size());
        double avgTurnaroundTime = ((double) totalTurnaroundTime / processList.size());

        System.out.println("\nAverage Waiting Time: " + Math.ceil(avgWaitTime));
        System.out.println("Average Turnaround Time: " + Math.ceil(avgTurnaroundTime));
    }
}