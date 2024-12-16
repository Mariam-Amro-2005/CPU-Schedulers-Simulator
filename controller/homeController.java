package controller;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class homeController {

    @FXML
    private Button btn_start;

    @FXML
    void goToStart(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/media/dataInput.fxml"));
        Parent root = loader.load();

        dataInputController controller = (dataInputController)loader.getController();
        


        Scene scene = new Scene(root);
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        controller.setStage(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.show();
        // now i went to the next scene
    }

}
