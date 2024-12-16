package controller;

import logic.FCAIScheduler;
import logic.Process;
import logic.SJF;
import logic.SRTF;
import logic.prirority;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class simulateController {

    static Stage primaryStage;
    private ObservableList<Process> data = FXCollections.observableArrayList();

    public static Stage getStage() {
        return primaryStage;
    }

    public static void setStage(Stage stage) {
        simulateController.primaryStage = stage;
    }

    @FXML
    private BorderPane PP;

    @FXML
    private TableColumn<Process, Process> AT_col;

    @FXML
    private TableColumn<Process, Process> BT_col;

    @FXML
    private TableView<Process> H_table;

    @FXML
    private TableColumn<Process, Process> PN_col;

    @FXML
    private TableColumn<Process, Process> PQ_col;

    @FXML
    private TableColumn<Process, Process> TAT_col;

    @FXML
    private TableColumn<Process, Process> WT_col;

    @FXML
    private TextField avg_tt_tf;

    @FXML
    private TextField avg_wt_tf;

    @FXML
    private TableColumn<Process, Process> name_col;

    double WaitTime, TurnaroundTime;

    @FXML
    public void initialize() {

        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        AT_col.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        BT_col.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        PN_col.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        PQ_col.setCellValueFactory(new PropertyValueFactory<>("quantum"));
        TAT_col.setCellValueFactory(new PropertyValueFactory<>("turnaroundTime"));
        WT_col.setCellValueFactory(new PropertyValueFactory<>("waitingTime"));
    }

    public void displayN(List<Process> processList, int sechdularNum, int context, int roundRT) {
        if (processList.isEmpty()) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("You must add at least one process!");
            errorAlert.showAndWait();
        } else {
            if (sechdularNum == 1) { // for sjf

                SJF sjf = new SJF();
                sjf.setProcessList(processList);
                sjf.simulateSJF();

                processList = sjf.getProcessList();
                // now we have the simulated process list with it's details
                createProcessBlocks(processList);

                WaitTime = SJF.getTotalWaitTime();
                TurnaroundTime = SJF.getTotalTurnaroundTime();
                updateTablett(processList);

            } else if (sechdularNum == 2) { // for SRTF

                SRTF srtf = new SRTF();
                srtf.setProcessList(processList);
                srtf.setContextSwitchingTime(context);

                srtf.simulateSRTF();
                // // System.out.println(pr.getExecutionOrder());

                processList = srtf.getProcessList();

                System.out.println(processList);
                System.out.println(srtf.getExecutionHistory());

                createMultiBlocks(processList,srtf.getExecutionHistory());

                WaitTime = srtf.getTotalWaitTime();
                TurnaroundTime = srtf.getTotalTurnaroundTime();

                updateTablett(processList);
            } else if (sechdularNum == 3) {// for fcai

                FCAIScheduler fcai = new FCAIScheduler(processList, context);
                fcai.schedule();

                processList = fcai.getProcesses();
                System.out.println(processList);
                System.out.println(fcai.getExecutionHistory());

                createMultiBlocks(processList, fcai.getExecutionHistory());

                WaitTime = fcai.getTotalWaitTime();
                TurnaroundTime = fcai.getTotalTurnaroundTime();

                updateTablett(processList);
            } else if (sechdularNum == 4) { // for priority

                prirority pr = new prirority();
                pr.setProcessList(processList);
                pr.setContextSwitchingTime(context);

                pr.simulatePirority();
                // System.out.println(pr.getExecutionOrder());

                processList = pr.getProcessList();

                // System.out.println(processList);

                createProcessBlocks(processList);

                WaitTime = pr.getTotalWaitTime();
                TurnaroundTime = pr.getTotalTurnaroundTime();

                updateTablett(processList);
            }
        }

    }

    public void createProcessBlocks(List<Process> list) {
        List<ProcessBlock> processBlocks = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Process pro = list.get(i);
            processBlocks.add(new ProcessBlock(pro.getName(), pro.getArrivalTime() + pro.getWaitingTime(),
                    pro.getBurstTime(), i));
        }

        Pane chartPane = new Pane(); // Main pane for the chart
        double blockHeight = 40; // Height of process blocks
        double timeScale = 50; // Pixels per time unit
        double rowSpacing = 60; // Space between process rows

        int maxTime = processBlocks.stream()
                .mapToInt(block -> block.getStartTime() + block.getDuration())
                .max()
                .orElse(0);

        // Add gridlines dynamically
        for (int i = 0; i <= maxTime; i++) {
            double x = i * timeScale;
            javafx.scene.shape.Line gridline = new javafx.scene.shape.Line(x, 0, x, processBlocks.size() * rowSpacing);
            gridline.setStroke(Color.LIGHTGRAY);
            gridline.getStrokeDashArray().addAll(5.0, 5.0);
            chartPane.getChildren().add(gridline);

            // Add time markers
            Text timeMarker = new Text(x, processBlocks.size() * rowSpacing + 10, String.valueOf(i));
            timeMarker.setFont(Font.font("Arial", 12));
            chartPane.getChildren().add(timeMarker);
        }

        // Add blocks dynamically
        for (ProcessBlock block : processBlocks) {
            double x = block.getStartTime() * timeScale;
            double y = block.getProcessID() * rowSpacing;

            Rectangle rect = new Rectangle(x, y, block.getDuration() * timeScale, blockHeight);
            rect.setFill(getColorForProcess(block.getProcessID()));
            rect.setStroke(Color.BLACK);
            rect.setArcWidth(10);
            rect.setArcHeight(10);
            rect.setEffect(new DropShadow(2, 2, 2, Color.GRAY));

            Text label = new Text(x + 5, y + blockHeight / 2 + 5, block.getProcessName());
            label.setFont(Font.font("Arial", 14));
            label.setFill(Color.WHITE);

            chartPane.getChildren().addAll(rect, label);
        }

        // Create ScrollPane
        ScrollPane scrollPane = new ScrollPane(chartPane);
        scrollPane.setPrefWidth(600); // Fixed width for ScrollPane
        scrollPane.setFitToHeight(true); // Fit content height
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show horizontal scrollbar
        

        // Set the scroll pane to the left region of the BorderPane
        PP.setLeft(null); // Clear existing content
        PP.setLeft(scrollPane); // Add the scroll pane
    }

    public void createMultiBlocks(List<Process> list, Map<String, List<Integer>> executionHistory) {

        Pane chartPane = new Pane(); // Main pane for the chart
        double blockHeight = 40; // Height of process blocks
        double timeScale = 50; // Pixels per time unit
        double rowSpacing = 60; // Space between process rows

        // Calculate max time for the gridlines
        int maxTime = list.stream()
                .mapToInt(block -> executionHistory.get(block.getName())
                        .stream().max(Integer::compareTo).orElse(0)) // Max time from all executions
                .max()
                .orElse(0);

        // Add gridlines dynamically
        for (int i = 0; i <= maxTime; i++) {
            double x = i * timeScale;
            javafx.scene.shape.Line gridline = new javafx.scene.shape.Line(x, 0, x, list.size() * rowSpacing);
            gridline.setStroke(Color.LIGHTGRAY);
            gridline.getStrokeDashArray().addAll(5.0, 5.0);
            chartPane.getChildren().add(gridline);

            // Add time markers
            Text timeMarker = new Text(x, list.size() * rowSpacing + 10, String.valueOf(i));
            timeMarker.setFont(Font.font("Arial", 12));
            chartPane.getChildren().add(timeMarker);
        }

        // Add blocks dynamically for each process
        for (Process block : list) {
            List<Integer> executionTimes = executionHistory.get(block.getName());
            int processIndex = list.indexOf(block); // Get index of the process in the list

            for (int i = 0; i < executionTimes.size()-1; i += 2) {
                int startTime = executionTimes.get(i);
                int endTime = executionTimes.get(i + 1);

                // Calculate x position and width for the block
                double x = startTime * timeScale;
                double width = (endTime - startTime) * timeScale;
                double y = processIndex * rowSpacing;

                // Create a rectangle for the execution block
                Rectangle rect = new Rectangle(x, y, width, blockHeight);
                rect.setFill(getColorForProcess(processIndex)); // Use process index for color
                rect.setStroke(Color.BLACK);
                rect.setArcWidth(10);
                rect.setArcHeight(10);
                rect.setEffect(new DropShadow(2, 2, 2, Color.GRAY));

                // Add a label with the process name
                Text label = new Text(x + 5, y + blockHeight / 2 + 5, block.getName());
                label.setFont(Font.font("Arial", 14));
                label.setFill(Color.WHITE);

                chartPane.getChildren().addAll(rect, label);
            }
        }


        // Create ScrollPane
        ScrollPane scrollPane = new ScrollPane(chartPane);
        scrollPane.setPrefWidth(600); // Fixed width for ScrollPane
        scrollPane.setFitToHeight(true); // Fit content height
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show horizontal scrollbar
        

        // Set the scroll pane to the left region of the BorderPane
        PP.setLeft(null); // Clear existing content
        PP.setLeft(scrollPane); // Add the scroll pane
    }

    public static class ProcessBlock {
        private String processName;
        private int startTime;
        private int duration;
        private int processID;

        public ProcessBlock(String processName, int startTime, int duration, int processID) {
            this.processName = processName;
            this.startTime = startTime;
            this.duration = duration;
            this.processID = processID;

        }

        public String getProcessName() {
            return processName;
        }

        public int getStartTime() {
            return startTime;
        }

        public int getDuration() {
            return duration;
        }

        public int getProcessID() {
            return processID;
        }
    }

    // Helper method to get a color for each process
    private Color getColorForProcess(int processID) {
        Color[] colors = { Color.CORNFLOWERBLUE, Color.TOMATO, Color.GOLD,
                Color.LIGHTGREEN, Color.MEDIUMPURPLE };
        return colors[processID % colors.length];
    }

    void updateTablett(List<Process> list) {
        for (int i = 0; i < list.size(); i++) {
            Process pro = list.get(i);
            data.add(pro);
            // System.out.println(pro);
            H_table.setItems(data);
        }
        double avgWaitTime = Math.ceil((double) WaitTime / list.size());
        double avgTurnaroundTime = Math.ceil((double) TurnaroundTime / list.size());

        avg_wt_tf.setText(String.valueOf(avgWaitTime));
        avg_tt_tf.setText(String.valueOf(avgTurnaroundTime));
    }
}
