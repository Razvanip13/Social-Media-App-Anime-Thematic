package socialnetwork.controller;

import com.sun.xml.bind.XmlAccessorFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.md5.Md5;

public class signUpController {


    @FXML
    ImageView imageViewStylish;

    @FXML
    TextField buttonFirstName;

    @FXML
    TextField buttonLastName;

    @FXML
    TextField buttonUsername;

    @FXML
    TextField buttonPass;

    @FXML
    TextField buttonConfirmPass;


    @FXML
    Button buttonBack;

    UtilizatorService utilizatorService;


    void setService(UtilizatorService utilizatorService){

        this.utilizatorService=utilizatorService;
    }

    @FXML
    void initialize(){

        Image image=new Image("\\views\\icons\\register_photo.jpg");

        imageViewStylish.setImage(image);

    }


    @FXML
    void handleRegisterUser(){

        try {

            String username = buttonUsername.getText();
            String password = Md5.md5(buttonPass.getText());
            String confirmPassword = Md5.md5(buttonConfirmPass.getText());
            String firstName = buttonFirstName.getText();
            String lastName = buttonLastName.getText();



            utilizatorService.addUtilizator(firstName,lastName,username,password,confirmPassword);

            MessageInformation.showInformationMessage(null,"Your account has been registered ^^");

        }
        catch (Exception e){


            MessageAlert.showErrorMessage(null,e.getMessage());
        }



    }


}
