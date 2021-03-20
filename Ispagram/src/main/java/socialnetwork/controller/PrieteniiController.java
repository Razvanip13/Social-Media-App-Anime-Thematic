package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.PublicEventChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PrieteniiController implements Observer<FriendshipChangeEvent>{


    @FXML
    TableView<FriendDTO> friendsTable;
    @FXML
    TableColumn<FriendDTO, LocalDate> columnFriendDate;
    @FXML
    TableColumn<FriendDTO,String> columnFriendFirstName;
    @FXML
    TableColumn<FriendDTO,String> columnFriendLastName;


    @FXML
    TableView<Utilizator> usersTable;

    @FXML
    TableColumn<Utilizator,String> columnUserFirstName;
    @FXML
    TableColumn<Utilizator,String> columnUserLastName;

    @FXML
    TableColumn<String,String>  columnUserString;

    @FXML
    Button buttonBack;

    @FXML
    Button buttonNext;

    @FXML
    Button buttonBack1;

    @FXML
    Button buttonNext1;

    @FXML
    ComboBox<String> comboBoxEvents;

    @FXML
    Label labelCountUsers;

    @FXML
    Label labelCountFriends;

    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private MessengerService messengerService;
    private UserPage userPage;
    @FXML
    Label labelLogged;

    @FXML
    ImageView imageViewWelcome;

    private Utilizator user;
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();

    ObservableList<FriendDTO> modelFriends = FXCollections.observableArrayList();

    ObservableList<String> modelNotifications = FXCollections.observableArrayList();

    public void setService(UtilizatorService servU,  FriendshipService servP,MessengerService messengerService, Utilizator u){

        this.utilizatorService = servU;
        this.friendshipService = servP;
        this.messengerService=messengerService;
        this.user = u;
        labelLogged.setText(labelLogged.getText()+" "+user.getFirstName() + " " + user.getLastName());

        friendshipService.addObserver(this);



        initModelFriends();
        initModelUsers();
    }

    public void setService(UserPage userPage){

        this.userPage=userPage;

        userPage.subscribe_friends_service(this);

        labelLogged.setText(labelLogged.getText()+" "+userPage.getFirstName() + " " + userPage.getLastName());

        Image image=new Image("\\views\\icons\\menu.jpg");

        imageViewWelcome.setImage(image);


        initModelFriends();
        initModelUsers();
        load_some_data();
    }

    public void load_some_data(){

        modelNotifications.setAll(userPage.load_notifications()
                .stream()
                .map(PublicEvent::toString)
                .collect(Collectors.toList()));

        comboBoxEvents.setItems(modelNotifications);
    }

    @FXML
    public void handleNotifyEvents(){


    }




    private void initModelUsers(){


        modelUsers.setAll(userPage.get_users_on_page(0));

        labelCountUsers.setText((userPage.getUser_current_Page()+1)+"/"+userPage.counting_users_pages());
    }


    private void initModelFriends(){


        //modelFriends.setAll(userPage.get_user_friends());

        modelFriends.setAll(userPage.get_friends_on_page(0));


        labelCountFriends.setText((userPage.getUser_friends_Page()+1)+"/"+userPage.counting_friends_pages());
    }

    @FXML
    public void initialize(){
        initalizeUsersTable();
        initializeFriendsTable();
    }


    private void initializeFriendsTable(){
        columnFriendDate.setCellValueFactory(new PropertyValueFactory<FriendDTO,LocalDate>("friendshipDate"));
        columnFriendFirstName.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("firstName"));
        columnFriendLastName.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("lastName"));
        friendsTable.setItems(modelFriends);
    }


    private void initalizeUsersTable(){

        columnUserFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        columnUserLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        usersTable.setItems(modelUsers);
    }


    @FXML
    public void handleAddFriends(){
        try {

            userPage.add_friend(usersTable);

            MessageInformation.showInformationMessage(null,"Friend request sent!");
        }
        catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }


    @FXML
    public void handleDeleteFriends(){
        try {

            userPage.delete_friend(friendsTable);


            MessageInformation.showInformationMessage(null, "Friend deleted!");
        }
        catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }


    @FXML
    void handleBack(){


        List<Utilizator> users=userPage.get_users_previous_page();

        if(users.size()!=0){

            modelUsers.setAll(users);

            buttonNext.setDisable(false);

            labelCountUsers.setText((userPage.getUser_current_Page()+1)+"/"+userPage.counting_users_pages());
        }
        else{

            buttonBack.setDisable(true);
        }


    }


    @FXML
    void handleNext(){

        List<Utilizator> users=userPage.get_users_next_page();

        if(users.size()!=0){

            modelUsers.setAll(users);


            buttonBack.setDisable(false);

            labelCountUsers.setText((userPage.getUser_current_Page()+1)+"/"+userPage.counting_users_pages());
        }
        else{
            buttonNext.setDisable(true);
        }

    }

    @FXML
    void handleNextFriends(){

        List<FriendDTO> friends=userPage.get_friends_on_next_page();

        if(friends.size()!=0){

            modelFriends.setAll(friends);

            buttonBack1.setDisable(false);

            labelCountFriends.setText((userPage.getUser_friends_Page()+1)+"/"+userPage.counting_friends_pages());

        }
        else{

            buttonNext1.setDisable(true);
        }
    }

    @FXML
    void handleBackFriends(){


        List<FriendDTO> friends=userPage.get_friends_on_previous_page();

        if(friends.size()!=0){

            modelFriends.setAll(friends);

            buttonNext1.setDisable(false);

            labelCountFriends.setText((userPage.getUser_friends_Page()+1)+"/"+userPage.counting_friends_pages());

        }
        else{

            buttonBack1.setDisable(true);
        }
    }



    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {

        if(friendshipChangeEvent.getType()== ChangeEventType.ADD) {


            modelFriends.setAll(userPage.get_friends_on_page(userPage.getUser_friends_Page()));

            labelCountFriends.setText((userPage.getUser_friends_Page()+1)+"/"+userPage.counting_friends_pages());
        }

        if(friendshipChangeEvent.getType()==ChangeEventType.DELETE){
            modelFriends.setAll(userPage.get_friends_on_page(userPage.getUser_friends_Page()));

            labelCountFriends.setText((userPage.getUser_friends_Page()+1)+"/"+userPage.counting_friends_pages());
        }



        buttonBack1.setDisable(false);
        buttonNext1.setDisable(false);
    }

    @FXML
    public void handleShowRequests() {
        showRequestsDialog();
    }

    @FXML
    public void handleShowSentReq(){
        showSentRequestsDialog();
    }

    private void showSentRequestsDialog(){
            try{
                // create a new loader
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/sentRequestsUtilizator.fxml"));
                AnchorPane root = loader.load();

                // create the next stage and a scene
                Stage nextStage = new Stage();
                nextStage.setTitle("Sent Requests");
                nextStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);

                scene.getStylesheets().add("views/css/the_style.css");
                nextStage.setScene(scene);

                // obtin controller-ul ferestrei si configurez service-urile
                SentRequestsController requestsController = loader.getController(); //creeaza o instanta si aici se face initialize() care configureaza coloanele tabelei
              //  requestsController.setService(utilizatorService, friendshipService,user); // in setService se apeleaza initModel care adauga datele in tabela

                requestsController.setService(userPage);


                // arat fereastra
                nextStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private void showRequestsDialog(){
        try{
            // create a new loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friendRequestsUtilizator.fxml"));
            AnchorPane root = loader.load();

            // create the next stage and a scene
            Stage nextStage = new Stage();
            nextStage.setTitle("Requests");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);

            scene.getStylesheets().add("views/css/the_style.css");
            nextStage.setScene(scene);

            // obtin controller-ul ferestrei si configurez service-urile
            RequestsController requestsController = loader.getController(); //creeaza o instanta si aici se face initialize() care configureaza coloanele tabelei
           // requestsController.setService(utilizatorService, friendshipService,user); // in setService se apeleaza initModel care adauga datele in tabela

            requestsController.setService(userPage);

            // arat fereastra
            nextStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void handleConversation(){
        showConversationsDialog();
    }


    @FXML
    void handleShowTelegram(){

        try {
            // create a new loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/telegramMessage.fxml"));

            AnchorPane root = loader.load();

            // create the next stage and a scene
            Stage nextStage = new Stage();

            nextStage.setTitle("Telegram");

            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
           // scene.getStylesheets().add("/views/css/styleReport.css");
            scene.getStylesheets().add("/views/css/styleTelegram.css");
            nextStage.setScene(scene);


            TelegramController telegramController = loader.getController(); //creeaza o instanta si aici se face initialize() care configureaza coloanele tabelei
            // requestsController.setService(utilizatorService, friendshipService,user); // in setService se apeleaza initModel care adauga datele in tabela

           // telegramController.setService(utilizatorService,friendshipService,messengerService,user);

            telegramController.setService(userPage);

            nextStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showConversationsDialog(){
        try{

            Utilizator u2 = usersTable.getSelectionModel().getSelectedItem();

            if(u2==null){

                throw new Exception("You didn't select an user");
            }

            // create a new loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/chatView.fxml"));
            AnchorPane root = loader.load();


            Stage nextStage = new Stage();
            nextStage.setTitle("Conversation");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/views/css/the_style.css");

            nextStage.setScene(scene);


            ConversationsController conversationsController = loader.getController(); //creeaza o instanta si aici se face initialize() care configureaza coloanele tabelei





           // conversationsController.setService(utilizatorService, friendshipService,messengerService,user,u2);




            conversationsController.setService(userPage,u2.getId(),u2.getFirstName(),u2.getLastName());

            scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {

                    if(event.getCode()== KeyCode.ALT){

                        conversationsController.handleSendMessage();
                    }
                }
            });


            nextStage.show();

        } catch (Exception e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }


    @FXML
    void handleReportDialog(){

        try{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/GuiReports.fxml"));
            AnchorPane root = loader.load();

            Stage nextStage = new Stage();
            nextStage.setTitle("Reports");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
           // scene.getStylesheets().add("/views/css/styleReport.css");
            scene.getStylesheets().add("views/css/the_style.css");
            nextStage.setScene(scene);


            ReportsController reportsController=loader.getController();

           // conversationsController.setService(utilizatorService, friendshipService,messengerService,user);

            reportsController.setService(userPage);

            nextStage.show();

        } catch (Exception e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }

    @FXML
    void handleShowEvents(){

        try{

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/publicEvents.fxml"));
            AnchorPane root = loader.load();

            Stage nextStage = new Stage();
            nextStage.setTitle("Events");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("views/css/the_style.css");
            //scene.getStylesheets().add("/views/css/styleReport.css");
            nextStage.setScene(scene);



            PublicEventsController eventsController = loader.getController();
            // conversationsController.setService(utilizatorService, friendshipService,messengerService,user);



            eventsController.setService(userPage);

            nextStage.show();

        } catch (Exception e) {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }
}
