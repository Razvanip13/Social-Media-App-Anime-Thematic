package socialnetwork.controller.messageboxes;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageInformation {
    static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);
        message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }

    public static void showInformationMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.INFORMATION);
        message.initOwner(owner);
        message.setTitle("Mesaj confirmare");
        message.setContentText(text);
        message.showAndWait();
    }
}
