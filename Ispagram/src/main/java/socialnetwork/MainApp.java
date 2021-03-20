package socialnetwork;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.LoginController;
import socialnetwork.domain.*;
import socialnetwork.domain.friendship.Prietenie;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;

import javafx.application.Application;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.PrietenieFileRepository;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;
import java.sql.SQLException;

import static javafx.application.Application.launch;

public class MainApp extends Application {

    String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");

    String friendsFile=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friends");


    final String url=ApplicationContext.getPROPERTIES().getProperty("database.url");
    final String user = ApplicationContext.getPROPERTIES().getProperty("database.user");
    final String password = ApplicationContext.getPROPERTIES().getProperty("database.password");

    //repo
   // Repository<Long, Utilizator> userFileRepository = new UtilizatorFile(fileName, new UtilizatorValidator());

 //   Repository<Tuple<Long,Long>, Prietenie> friendsFileRepository= new PrietenieFileRepository(friendsFile,new PrietenieValidator());

    UtilizatorDbRepository utilizatorDbRepository=new UtilizatorDbRepository(url,user,password);

    FriendshipBDRepository friendshipBDRepository=new FriendshipBDRepository(url,user,password,new PrietenieValidator());

    MessagesBDRepository messagesBDRepository=new MessagesBDRepository(url,user,password,utilizatorDbRepository);

    FriendRequestBDRepository requestBDRepository=new FriendRequestBDRepository(url,user,password);

    PublicEventsDBRepository publicEventsDBRepository=new PublicEventsDBRepository(url,user,password);


    //service

    UtilizatorService utilizatorService=new UtilizatorService(utilizatorDbRepository);

    MessengerService messengerService=new MessengerService(utilizatorDbRepository,messagesBDRepository);

    FriendshipService friendshipService=new FriendshipService(utilizatorDbRepository,friendshipBDRepository, requestBDRepository);

    EventsService eventsService=new EventsService(publicEventsDBRepository);

    public static void main(String[] args) throws SQLException {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        initView(primaryStage);
        primaryStage.setWidth(540);
        primaryStage.setHeight(360);


        primaryStage.show();
    }
    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/views/loginUtilizator.fxml"));
        AnchorPane loginLayout = loginLoader.load();

        Scene scene=new Scene(loginLayout);

        scene.getStylesheets().add("/views/css/styleLogin.css");

        primaryStage.setScene(scene);



        LoginController loginController = loginLoader.getController();
        loginController.setService(utilizatorService,friendshipService,messengerService,eventsService);

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if(event.getCode()==KeyCode.ENTER){

                    loginController.handleCauta();
                }
            }
        });

    }
}


