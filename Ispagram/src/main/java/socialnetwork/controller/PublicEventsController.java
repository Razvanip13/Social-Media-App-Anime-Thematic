package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.domain.frequest.FRequestDTO;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.PublicEventChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PublicEventsController implements Observer<PublicEventChangeEvent> {


    ObservableList<PublicEvent> modelEvents = FXCollections.observableArrayList();

    ObservableList<PublicEvent> modelUserEvents = FXCollections.observableArrayList();

    @FXML
    TableColumn<PublicEvent, String> columnPublicEvent;

    @FXML
    TableColumn<PublicEvent, LocalDate> columnPublicDate;

    @FXML
    TableColumn<PublicEvent,ImageView> columnNotify;


    @FXML
    TableColumn<PublicEvent, String> columnUserEventName;

    @FXML
    TableColumn<PublicEvent, LocalDate> columnUserEventDate;

    @FXML
    TableView<PublicEvent> tableViewEvents;

    @FXML
    TableView<PublicEvent> tableViewUserEvents;

    @FXML
    TextField textFieldDate;

    @FXML
    TextField textFieldEventName;

    @FXML
    TextField textFieldOrganizer;

    @FXML
    TextArea textAreaDescription;

    @FXML
    TextField textFieldLocation;

    @FXML
    ImageView imageViewEvents;

    @FXML
    Button buttonBackEvent;

    @FXML
    Button buttonNextEvents;

    @FXML
    Button buttonBackPublicEvent;

    @FXML
    Button buttonNextPublicEvent;

    @FXML
    Label labelCountEvents;

    @FXML
    Label labelCountUserEvents;

    UserPage userPage;


    public void setService(UserPage userPage){


        this.userPage=userPage;


        this.userPage.subscribe_events_service(this);

        Image image=new Image("\\views\\icons\\events.png");

        imageViewEvents.setImage(image);

        initModelEvents();
        initUserModelEvents();
    }


    public void initModelEvents(){


        modelEvents.setAll(userPage.get_public_events_on_page(0));

        labelCountEvents.setText((userPage.getEvents_Page()+1)+"/"+userPage.counting_events_pages());

    }


    public void initUserModelEvents(){


        modelUserEvents.setAll(userPage.get_user_events_on_page(0));

        labelCountUserEvents.setText((userPage.getUser_events_Page()+1)+"/"+userPage.counting_user_events_pages());
    }


    @FXML
    public void initialize(){

        initializeEventsTable();
        initializeUserEventsTable();

    }

    public void initializeEventsTable(){


        columnPublicEvent.setCellValueFactory(new PropertyValueFactory<PublicEvent,String>("name"));
        columnPublicDate.setCellValueFactory(new PropertyValueFactory<PublicEvent,LocalDate>("date"));
        tableViewEvents.setItems(modelEvents);
    }

    public void initializeUserEventsTable(){

        columnUserEventName.setCellValueFactory(new PropertyValueFactory<PublicEvent,String>("name"));
        columnUserEventDate.setCellValueFactory(new PropertyValueFactory<PublicEvent,LocalDate>("date"));
        columnNotify.setCellValueFactory(new PropertyValueFactory<>("bell"));
        tableViewUserEvents.setItems(modelUserEvents);

    }


    @FXML
    void handleUnsubscribe(){

        userPage.unsubscribe_to_a_public_event(tableViewUserEvents);


    }

    @FXML
    void handleSubscribe(){

        try {

            userPage.subscribe_to_a_public_event(tableViewEvents);
        }
        catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());
        }


    }


    @FXML
    void handleShowDetails(){

        try{

            PublicEvent event= tableViewEvents.getSelectionModel().getSelectedItem();

            if(event!=null) {

                textFieldEventName.setText(event.getName());

                textFieldDate.setText(event.getDate().toString());

                textFieldLocation.setText(event.getLocation());

                textFieldOrganizer.setText(event.getFull_name());

                textAreaDescription.setText(event.getDescription());

            }


        }catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }



    @FXML
    void handleAddEvent(){



        try{
            // create a new loader
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/createEvent.fxml"));
            AnchorPane root = loader.load();

            // create the next stage and a scene
            Stage nextStage = new Stage();
            nextStage.setTitle("Create events");
            nextStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);

            scene.getStylesheets().add("views/css/the_style.css");
            nextStage.setScene(scene);

            // obtin controller-ul ferestrei si configurez service-urile
            CreateEventController createEventController = loader.getController(); //creeaza o instanta si aici se face initialize() care configureaza coloanele tabelei
            //  requestsController.setService(utilizatorService, friendshipService,user); // in setService se apeleaza initModel care adauga datele in tabela

            createEventController.buttonExit.setOnAction(e-> nextStage.close());

            createEventController.setService(userPage);


            // arat fereastra
            nextStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void handleNextPublic(){

        List<PublicEvent> events=userPage.get_next_events_Page();

        if(events.size()!=0){

            modelEvents.setAll(events);

            buttonBackPublicEvent.setDisable(false);

            labelCountEvents.setText((userPage.getEvents_Page()+1)+"/"+userPage.counting_events_pages());
        }
        else
        {
            buttonNextPublicEvent.setDisable(true);
        }

    }


    @FXML
    void handleBackPublic(){

        List<PublicEvent> events=userPage.get_previous_events_Page();

        if(events.size()!=0){

            modelEvents.setAll(events);

            buttonNextPublicEvent.setDisable(false);

            labelCountEvents.setText((userPage.getEvents_Page()+1)+"/"+userPage.counting_events_pages());
        }
        else{
            buttonBackPublicEvent.setDisable(true);
        }

    }


    @FXML
    void handleNextU(){

        List<PublicEvent> events=userPage.get_next_user_events_Page();

        if(events.size()!=0){

            modelUserEvents.setAll(events);
            buttonBackEvent.setDisable(false);

            labelCountUserEvents.setText((userPage.getUser_events_Page()+1)+"/"+userPage.counting_user_events_pages());
        }
        else{

            buttonNextEvents.setDisable(true);
        }

    }

    @FXML
    void handleBackU(){

        List<PublicEvent> events=userPage.get_previous_user_events_Page();

        if(events.size()!=0){

            modelUserEvents.setAll(events);

            buttonNextEvents.setDisable(false);

            labelCountUserEvents.setText((userPage.getUser_events_Page()+1)+"/"+userPage.counting_user_events_pages());

        }
        else{

            buttonBackEvent.setDisable(true);
        }

    }


    @FXML
    void handleNotify(){


        userPage.get_notifications(tableViewUserEvents);

    }

    @FXML
    void handleUnnotify(){



        userPage.stop_notifications(tableViewUserEvents);

    }


    @Override
    public void update(PublicEventChangeEvent publicEventChangeEvent) {


        if(publicEventChangeEvent.getType()== ChangeEventType.ADD){



            modelEvents.setAll(userPage.get_public_events_on_page(userPage.getEvents_Page()));


            buttonNextPublicEvent.setDisable(false);
            buttonBackPublicEvent.setDisable(false);

            labelCountEvents.setText((userPage.getEvents_Page()+1)+"/"+userPage.counting_events_pages());
        }

        if(publicEventChangeEvent.getType()==ChangeEventType.SUBSCRIBED){

            modelUserEvents.setAll(userPage.get_user_events_on_page(userPage.getUser_events_Page()));

            buttonNextEvents.setDisable(false);
            buttonBackEvent.setDisable(false);

            labelCountUserEvents.setText((userPage.getUser_events_Page()+1)+"/"+userPage.counting_user_events_pages());

        }

        if(publicEventChangeEvent.getType()==ChangeEventType.UNSUBSCRIBED){

            modelUserEvents.setAll(userPage.get_user_events_on_page(userPage.getUser_events_Page()));

            buttonNextEvents.setDisable(false);
            buttonBackEvent.setDisable(false);

            labelCountUserEvents.setText((userPage.getUser_events_Page()+1)+"/"+userPage.counting_user_events_pages());
        }

        if(publicEventChangeEvent.getType()==ChangeEventType.NOTIFY){

            modelUserEvents.setAll(userPage.get_user_events_on_page(userPage.getUser_events_Page()));

            buttonNextEvents.setDisable(false);
            buttonBackEvent.setDisable(false);

            labelCountUserEvents.setText((userPage.getUser_events_Page()+1)+"/"+userPage.counting_user_events_pages());
        }

    }
}
