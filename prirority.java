import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class prirority {
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

            // System.out.print("Process Color: ");
            //intialized only bec we don't need color
            String color = "yellow";

            System.out.print("Arrival Time: ");
            int arrivalTime = sc.nextInt();

            System.out.print("Burst Time: ");
            int burstTime = sc.nextInt();

            System.out.print("Priority: "); // Not used in SRTF but included for other schedulers
            int priority = sc.nextInt();

            processList.add(new Process(name, color, arrivalTime, burstTime, priority, 0));
        }

        simulatePirority();

        sc.close();
    }
    static void simulatePirority(){
        int currentTime = 0;
        int completed = 0;
        int totalWaitTime=0;
        int totalTurnaroundTime=0;
        while(completed<processList.size()){
            Process highestprio=null;
            for(Process p:processList){
                if(p.getArrivalTime()<=currentTime && p.getCompletionTime()==0){
                    if(highestprio==null || highestprio.getPriority()>p.getPriority()||(p.getPriority() == highestprio.getPriority() && p.getArrivalTime() < highestprio.getArrivalTime())){
                        highestprio=p;
                    }
                }
            }
            if(highestprio!=null){ 
                if(!executionOrder.isEmpty()){
                    currentTime+=contextSwitchingTime;
                }
                currentTime+=highestprio.getBurstTime();
                highestprio.setCompletionTime(currentTime);
                highestprio.setTurnaroundTime(highestprio.getCompletionTime()-highestprio.getArrivalTime());
                highestprio.setWaitingTime(highestprio.getTurnaroundTime()-highestprio.getBurstTime());
                executionOrder.add(highestprio.getName());
                totalTurnaroundTime+=highestprio.getTurnaroundTime();
                totalWaitTime+=highestprio.getWaitingTime();
                ++completed;
            }
            else{
                currentTime = processList.stream()
                         .filter(p -> p.getCompletionTime() == 0)
                         .mapToInt(Process::getArrivalTime)
                         .min()
                         .orElse(currentTime + 1);
            }
        }
        printResults(totalWaitTime, totalTurnaroundTime);
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

        double avgWaitTime = (double) totalWaitTime / processList.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processList.size();

        System.out.println("\nAverage Waiting Time: " + Math.ceil(avgWaitTime));
        System.out.println("Average Turnaround Time: " + Math.ceil(avgTurnaroundTime));
    }

}
