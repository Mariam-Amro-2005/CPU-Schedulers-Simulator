package controller;

import logic.SJF;
import javafx.scene.Scene;
import logic.Process;
import javafx.stage.Stage;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class dataInputController {
    private Stage stage;
    private List<Process> processlist = new ArrayList<>();
    private ObservableList<Process> data = FXCollections.observableArrayList();;
    private int contextSwitchingTime;
    private int roundRobinTime;


    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private TextField AT_TF;

    @FXML
    private TableColumn<Process, Integer> AT_col;

    @FXML
    private TableColumn<Process, Integer> PQ_col;

    @FXML
    private TextField BT_TF;

    @FXML
    private TextField PQ_tf;

    @FXML
    private TableColumn<Process, Integer> BT_col;

    @FXML
    private TextField CS_TF;

    @FXML
    private RadioButton FCAI_R;

    @FXML
    private TableView<Process> H_table;

    @FXML
    private TextField PN_TF;

    @FXML
    private TextField PPN_TF;

    @FXML
    private TableColumn<Process, Integer> PN_col;

    @FXML
    private RadioButton PS_R;

    @FXML
    private RadioButton SJF_R;

    @FXML
    private RadioButton SRTF_R;

    @FXML
    private Button addP_btn;

    @FXML
    private TableColumn<Process, String> name_col;

    @FXML
    private ToggleGroup secdular;

    @FXML
    private Button simulate_btn;

    @FXML
    void Simulate(ActionEvent event) throws IOException {
        
        boolean choosen = false;
        int n = 0;
        if (PS_R.isSelected()) {
            System.out.println("PS_R");
            n = 4;
        } else if (FCAI_R.isSelected()) {
            System.out.println("FCAI_R");
            n = 3;
        } else if (SRTF_R.isSelected()) {
            System.out.println("SRTF_R");
            n = 2;
        } else if (SJF_R.isSelected()) {
            n = 1;
            System.out.println("SJF_R");
        } else {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("You must specify the schedular you want!");
            errorAlert.showAndWait();
            choosen = true;
        }
        if (!choosen) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/media/simulation.fxml"));
            Parent root = loader.load();

            simulateController controller = (simulateController) loader.getController();

            Scene scene = new Scene(root);
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            simulateController.setStage(primaryStage);
            System.out.println(n);
            // calling the function to display tabel and chart values
            controller.displayN(processlist, n,contextSwitchingTime,roundRobinTime);

            primaryStage.setScene(scene);
            primaryStage.show();
        }


    }

    @FXML
    public void initialize() {

        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));

        AT_col.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        BT_col.setCellValueFactory(new PropertyValueFactory<>("burstTime"));
        PN_col.setCellValueFactory(new PropertyValueFactory<>("priority"));
        PQ_col.setCellValueFactory(new PropertyValueFactory<>("quantum"));
    }

    @FXML
    void add_process(ActionEvent event) {

        try {

            String name = (String) PN_TF.getText().trim(); // Process name (e.g., P1, P2, etc.)
            int arrivalTime = Integer.parseInt(AT_TF.getText().trim()); // Time the process arrives in the ready queue
            int burstTime = Integer.parseInt(BT_TF.getText().trim()); // Total burst time required by the process
            int priority = Integer.parseInt(PPN_TF.getText().trim()); // Priority of the process (lower value = higher
            int quantum = Integer.parseInt(PQ_tf.getText().trim()); // Time quantum allocated for the process
            // int contextSwitchingTime = Integer.parseInt(CS_TF.getText().trim());
           
            contextSwitchingTime = Integer.parseInt(CS_TF.getText().trim());
            System.out
                    .println(name  + "," + arrivalTime + "," + burstTime + "," + priority + "," + quantum);

            processlist.add(new Process(name, "color", arrivalTime, burstTime, priority, quantum));

            updateTable(name, arrivalTime, burstTime, priority, quantum);
            PN_TF.setText("");
            AT_TF.setText("");
            BT_TF.setText("");
            PPN_TF.setText("");
            PQ_tf.setText("");

        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("Input not valid");
            errorAlert.setContentText("You must all the information about your process!");
            errorAlert.showAndWait();
        }

    }

    void updateTable(String n, int a, int b, int p, int q) {

        data.add(new Process(n,"c", a, b, p, q));
        H_table.setItems(data);
    }

}