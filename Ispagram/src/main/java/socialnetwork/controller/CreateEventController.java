package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.domain.pages.UserPage;

import java.time.LocalDate;

public class CreateEventController {


    UserPage userPage;

    @FXML
    TextField textFieldName;

    @FXML
    TextField textFieldLocation;

    @FXML
    DatePicker datePickerDate;

    @FXML
    TextArea textAreaDescription;

    @FXML
    public Button buttonExit;

    @FXML
    ImageView imageViewParty;

    public void setService(UserPage userPage){

        this.userPage=userPage;

        Image image=new Image("\\views\\icons\\create_event.jpg");

        imageViewParty.setImage(image);

    }


    @FXML
    public void initialize(){



    }


    @FXML
    public void handleCreate(){

        try {
            String event_name = textFieldName.getText();

            LocalDate event_date = datePickerDate.getValue();

            String description=textAreaDescription.getText();

            String location=textFieldLocation.getText();


            userPage.create_a_new_event(event_name, event_date,description,location);


            MessageInformation.showInformationMessage(null,"Event created ^^");

        }
        catch (Exception e){


            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }



}
