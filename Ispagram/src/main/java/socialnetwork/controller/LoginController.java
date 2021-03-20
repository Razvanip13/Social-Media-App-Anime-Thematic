package socialnetwork.controller;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;


import java.awt.*;
import java.security.Signature;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.utils.md5.Md5;

/*
Prima fereastra, cea de logare, care o va porni pe a doua specifica pentru un utilizator cand se da "Search!"
 */
public class LoginController {
    // are un singur textfield unde se va introduce user id.
    @FXML
    private TextField textFieldUsername;



    @FXML
    private PasswordField passwordFieldLogin;

    @FXML
    private Button buttonCauta;

    //cele doua service-uri vor fi transmise catre a doua fereastra. Aici le folosim doar ca sa gasim utilizatorul
    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;

    private MessengerService messengerService;
    private EventsService eventsService;

    @FXML
    private ImageView imageViewWelcome;

    public void setService(UtilizatorService servU, FriendshipService servP, MessengerService messengerService, EventsService eventsService){
        this.utilizatorService = servU;
        this.friendshipService = servP;
        this.messengerService = messengerService;
        this.eventsService=eventsService;


       Image image=new Image("\\views\\icons\\shiro_login.jpg");

       // Image image=new Image("file:shiro_login.jpg");

        imageViewWelcome.setImage(image);


    }

    // Caut in repository utilizatorul. Daca l-am gasit, pornesc fereastra a doua.
    @FXML
    public void handleCauta(){
        try {


                String crypted_pass= Md5.md5(passwordFieldLogin.getText());


                Optional<Utilizator> u = utilizatorService.match_login(textFieldUsername.getText(),crypted_pass);



                if (u.isPresent()) {
                    showFriendsDialog(u.get());
                }
                else {
                    MessageAlert.showErrorMessage(null, "Username or password is incorrect");
                }

        }
        catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }


    // metoda care porneste a doua fereastra, specifica pentru un user
    private void showFriendsDialog(Utilizator u) {
        try{


            // create a new loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friendsUtilizator.fxml"));
            AnchorPane root = loader.load();

            // create the next stage and a scene
            Stage nextStage = new Stage();
            nextStage.setTitle("Friendships");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
           // scene.getStylesheets().add("/views/css/mainstyle.css");
            scene.getStylesheets().add("/views/css/the_style.css");
            nextStage.setScene(scene);

            // obtin controller-ul ferestrei si configurez service-urile
            PrieteniiController prieteniiMeniuController = loader.getController();
          //  prieteniiMeniuController.setService(utilizatorService, friendshipService,messengerService,u);

            UserPage userPage=new UserPage(u,utilizatorService,friendshipService,messengerService,eventsService);

            prieteniiMeniuController.setService(userPage);

            // arat fereastra
            nextStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void handleRegister(){

        try{


            // create a new loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/signUp.fxml"));
            AnchorPane root = loader.load();

            // create the next stage and a scene
            Stage nextStage = new Stage();
            nextStage.setTitle("Register");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/views/css/styleLogin.css");
            nextStage.setScene(scene);


            // obtin controller-ul ferestrei si configurez service-urile
            signUpController signUpController=loader.getController();
            //  prieteniiMeniuController.setService(utilizatorService, friendshipService,messengerService,u);

            signUpController.buttonBack.setOnAction(e-> nextStage.close());

            signUpController.setService(utilizatorService);


            // arat fereastra
            nextStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}
