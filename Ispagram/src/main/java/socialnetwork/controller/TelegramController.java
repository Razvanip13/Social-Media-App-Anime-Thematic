package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TelegramController {


    @FXML
    TableView<Utilizator> tableViewTelegramUsers;

    @FXML
    TableColumn<Utilizator,String> tableColumnTelegramFirstName;
    @FXML
    TableColumn<Utilizator,String> tableColumnTelegramLastName;

    @FXML
    Button buttonSendTelegram;

    @FXML
    TextArea textAreaMessage;


    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private MessengerService messengerService;
    private Utilizator user;

    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    private UserPage userPage;

    public void setService(UtilizatorService utilizatorService, FriendshipService friendshipService, MessengerService messengerService, Utilizator user) {
        this.utilizatorService = utilizatorService;
        this.friendshipService = friendshipService;
        this.messengerService = messengerService;
        this.user = user;
        initModelUsers();

        tableViewTelegramUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }


    public void setService(UserPage userPage){


        this.userPage=userPage;

        initModelUsers();

        tableViewTelegramUsers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    private void initModelUsers(){

//        Iterable<Utilizator> users = utilizatorService.getAll();
//        List<Utilizator> usersList = StreamSupport.stream(users.spliterator(),false)
//                .collect(Collectors.toList());
//        modelUsers.setAll(usersList);

        modelUsers.setAll(userPage.get_platform_users());
    }


    @FXML
    public void initialize(){

        initializeFriendsTable();
    }

    private void initializeFriendsTable(){

        tableColumnTelegramFirstName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        tableColumnTelegramLastName.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        tableViewTelegramUsers.setItems(modelUsers);
    }


    public void handleSendTelegram(){

//        List<Long> lista= tableViewTelegramUsers.getSelectionModel().getSelectedItems().stream()
//                .map(Utilizator::getId)
//                .collect(Collectors.toList());
//
//        String mesaj=textAreaMessage.getText();
//
//        messengerService.send_message(user.getId(),mesaj,lista);

        userPage.send_message_to_people(tableViewTelegramUsers,textAreaMessage);


        MessageInformation.showInformationMessage(null,"Mesajul a fost trimis");
    }


}
