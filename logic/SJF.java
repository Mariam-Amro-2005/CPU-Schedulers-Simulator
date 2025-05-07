package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SJF {
    static List<Process> processList = new ArrayList<>();
    static List<String> executionOrder = new ArrayList<>(); // To store the execution order
    static int totalWaitTime = 0;
    static int totalTurnaroundTime = 0;

    public static int getTotalWaitTime() {
        return totalWaitTime;
    }

    public static int getTotalTurnaroundTime() {
        return totalTurnaroundTime;
    }

    public static List<Process> getProcessList() {
        return processList;
    }

    public static void setProcessList(List<Process> list) {
        processList = list;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int numProcesses = sc.nextInt();

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Process Name: ");
            String name = sc.next();

            // System.out.print("Process Color: ");
            String color = "*";

            System.out.print("Arrival Time: ");
            int arrivalTime = sc.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = sc.nextInt();

            System.out.print("Priority: "); // Not used in SRTF but included for other schedulers
            int priority = sc.nextInt();

            processList.add(new Process(name, color, arrivalTime, burstTime, priority, 0));
        }

        simulateSJF();

        sc.close();
    }

    public static void simulateSJF() {
        int currentTime = 0;
        int completed = 0;
        int n = processList.size();

        while (completed < n) {
            int CT = currentTime;
            // filter processes that have arrived and aren't completed
            List<Process> readyProcesses = processList.stream()
                    .filter(p -> p.getArrivalTime() <= CT && !p.isCompleted())
                    .collect(Collectors.toList());

            if (readyProcesses.isEmpty()) {
                // if no process is ready increment the current time
                currentTime++;
                continue;
            }
            // select the process with the shortest burst time
            Process currentProcess = readyProcesses.stream()
                    .min((p1, p2) -> Integer.compare(p1.getBurstTime(), p2.getBurstTime()))
                    .orElse(null);

            if (currentProcess != null) {
                // execute the selected process
                executionOrder.add(currentProcess.getName());
                currentTime += currentProcess.getBurstTime();
                currentProcess.setCompletionTime(currentTime);

                int turnaroundTime = currentProcess.getCompletionTime() - currentProcess.getArrivalTime();
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();

                currentProcess.setTurnaroundTime(turnaroundTime);
                currentProcess.setWaitingTime(waitingTime);

                totalTurnaroundTime += turnaroundTime;
                totalWaitTime += waitingTime;

                currentProcess.setRemainingTime(0); // remaining time becomes 0 as its non premitive
                completed++;
            }
        }

        printResults();
    }

    static void printResults() {
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