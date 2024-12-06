import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.*;


public class SRTF{
    static List<Process> processList = new ArrayList<>();
    static List<String> executionOrder = new ArrayList<>(); // To store the execution order
    static int contextSwitchingTime;

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

            System.out.print("Initial Quantum: ");
            int quantum = sc.nextInt();

            processList.add(new Process(name, arrivalTime, burstTime, color, quantum));
        }

        // Simulate SRTF with context switching and dynamic quantum updates
        simulateSRTF();

        sc.close();
    }

    static void simulateSRTF() {
        int currentTime = 0;
        int completed = 0;
        int n = processList.size();
        Process currentProcess = null;
        int totalWaitTime = 0, totalTurnaroundTime = 0;

        while (completed < n) {
            // Get the process with the shortest remaining time that has arrived
            Process nextProcess = getNextProcess(currentTime);

            if (nextProcess != null) {
                if (currentProcess != null ) { //&& !currentProcess.equals(nextProcess)
                    // Context switch
                    currentTime += contextSwitchingTime;
                    // Update quantum for preempted process
                    updateQuantumForPreemption(currentProcess, currentProcess.quantum);
                }

                if (currentProcess == null || !currentProcess.equals(nextProcess)) {
                    executionOrder.add(nextProcess.name);  // Add process to execution order only when it starts or resumes
                }

                currentProcess = nextProcess;
                int executionTime = Math.min(currentProcess.quantum, currentProcess.remainingTime);
                currentProcess.remainingTime -= executionTime;
                currentTime += executionTime;

                // If process still has remaining work after completing its quantum
                if (currentProcess.remainingTime > 0) {
                    // Update quantum by +2 if process has work left after completing its quantum
                    currentProcess.quantum += 2;
                } else {
                    // Process completes
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    totalWaitTime += currentProcess.waitingTime;
                    totalTurnaroundTime += currentProcess.turnaroundTime;
                    completed++;
                }
            } else {
                currentTime++;
            }
        }

        // Output Results
        printResults(totalWaitTime, totalTurnaroundTime);
    }

    static Process getNextProcess(int currentTime) {
        Process selected = null;
        for (Process p : processList) {
            if (p.arrivalTime <= currentTime && p.remainingTime > 0) {
                if (selected == null || p.remainingTime < selected.remainingTime) {
                    selected = p;
                }
            }
        }
        return selected;
    }

    static void updateQuantumForPreemption(Process process, int unusedQuantum) {
        if (process.remainingTime > 0) {
            process.quantum += unusedQuantum;  // Add unused quantum back
        }
    }

    static void printResults(int totalWaitTime, int totalTurnaroundTime) {
        System.out.println("\nProcess Execution Order:");
        for (String pName : executionOrder) {
            System.out.print(pName + " ");
        }
        System.out.println();

        System.out.println("\nProcess Details:");
        for (Process p : processList) {
            System.out.println("Process " + p.name + " - Waiting Time: " + p.waitingTime
                    + ", Turnaround Time: " + p.turnaroundTime);
        }

        double avgWaitTime = (double) totalWaitTime / processList.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processList.size();

        System.out.println("\nAverage Waiting Time: " + avgWaitTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }
}