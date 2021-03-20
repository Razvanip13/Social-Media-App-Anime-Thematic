package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.config.Status;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.domain.frequest.FRequestDTO;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.util.List;

public class SentRequestsController implements Observer<FriendshipChangeEvent> {

    @FXML
    TableView<FRequestDTO> requestsTable;
    @FXML
    TableColumn<FriendDTO, String> firstNameColumn;
    @FXML
    TableColumn<FriendDTO, String> lastNameColumn;
    @FXML
    TableColumn<FriendDTO, Status> statusColumn;
    @FXML
    TableColumn<FriendDTO, LocalDate> dateColumn;


    @FXML
    Button buttonBack;

    @FXML
    Button buttonNext;

    @FXML
    Label labelCountRequests;


    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private Utilizator user;
    private UserPage userPage;

    ObservableList<FRequestDTO> modelRequests = FXCollections.observableArrayList();


    public void setService(UtilizatorService servU, FriendshipService servP, Utilizator u) {
        this.utilizatorService = servU;
        this.friendshipService = servP;
        this.user = u;
        friendshipService.addObserver(this);
        initModelRequests();
    }

    public void setService(UserPage userPage){

        this.userPage=userPage;
        userPage.subscribe_friends_service(this);
        initModelRequests();

    }

    private void initModelRequests() {


        modelRequests.setAll(userPage.get_user_sent_requests_on_page(0));

        labelCountRequests.setText((userPage.getUser_sent_frined_request_Page()+1)+"/"+userPage.counting_pending_requests_pages());
    }

    private void initializeRequestsTable() {
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<FriendDTO, String>("lastName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<FriendDTO, Status>("status"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<FriendDTO, LocalDate>("friendRequestDate"));
        requestsTable.setItems(modelRequests);
    }

    @FXML
    public void initialize() {
        initializeRequestsTable();
    }


    @FXML
    void handleNext(){


        List<FRequestDTO> friends=userPage.get_users_next_sent_requests_page();

        if(friends.size()!=0){

            modelRequests.setAll(friends);

            labelCountRequests.setText((userPage.getUser_sent_frined_request_Page()+1)+"/"+userPage.counting_pending_requests_pages());

            buttonBack.setDisable(false);

        }
        else{

            buttonNext.setDisable(true);
        }
    }


    @FXML
    void handleBack(){

        List<FRequestDTO> friends=userPage.get_users_previous_sent_requests_page();

        if(friends.size()!=0){

            modelRequests.setAll(friends);

            labelCountRequests.setText((userPage.getUser_sent_frined_request_Page()+1)+"/"+userPage.counting_pending_requests_pages());

            buttonNext.setDisable(false);
        }
        else{

            buttonBack.setDisable(true);
        }

    }

    @FXML
    public void handleWithdraw() {
        try {


            userPage.withdraw_friend_request(requestsTable);

        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }


    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {


        if(friendshipChangeEvent.getType()== ChangeEventType.DELETE){

            modelRequests.setAll(userPage.get_user_sent_requests_on_page(userPage.getUser_sent_frined_request_Page()));

            labelCountRequests.setText((userPage.getUser_sent_frined_request_Page()+1)+"/"+userPage.counting_pending_requests_pages());


            buttonNext.setDisable(false);
            buttonBack.setDisable(false);
        }
    }
}
