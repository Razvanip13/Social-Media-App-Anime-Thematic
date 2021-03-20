package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.GuiController;
import socialnetwork.domain.friendship.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendRequestBDRepository;
import socialnetwork.repository.database.FriendshipBDRepository;
import socialnetwork.repository.database.MessagesBDRepository;
import socialnetwork.repository.database.UtilizatorDbRepository;
import socialnetwork.repository.file.PrietenieFileRepository;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;

public class MainFx extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");

        String friendsFile=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friends");


        final String url=ApplicationContext.getPROPERTIES().getProperty("database.url");
        final String user = ApplicationContext.getPROPERTIES().getProperty("database.user");
        final String password = ApplicationContext.getPROPERTIES().getProperty("database.password");

        //repo
       // Repository<Long, Utilizator> userFileRepository = new UtilizatorFile(fileName, new UtilizatorValidator());

      //  Repository<Tuple<Long,Long>, Prietenie> friendsFileRepository= new PrietenieFileRepository(friendsFile,new PrietenieValidator());

        UtilizatorDbRepository utilizatorDbRepository=new UtilizatorDbRepository(url,user,password);

        FriendshipBDRepository friendshipBDRepository=new FriendshipBDRepository(url,user,password,new PrietenieValidator());

        MessagesBDRepository messagesBDRepository=new MessagesBDRepository(url,user,password,utilizatorDbRepository);

        FriendRequestBDRepository requestBDRepository=new FriendRequestBDRepository(url,user,password);

        //service

        UtilizatorService utilizatorService=new UtilizatorService(utilizatorDbRepository);

        MessengerService messengerService=new MessengerService(utilizatorDbRepository,messagesBDRepository);

        FriendshipService friendshipService=new FriendshipService(utilizatorDbRepository,friendshipBDRepository, requestBDRepository);


        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/GuiMain.fxml"));
        AnchorPane root=loader.load();



        GuiController ctrl=loader.getController();
        ctrl.setServices(utilizatorService,friendshipService,messengerService);

        primaryStage.setScene(new Scene(root, 1000, 500));
        primaryStage.setTitle("Social Network");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
