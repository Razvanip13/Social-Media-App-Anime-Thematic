package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;

public class GuiController {


    ObservableList<FriendDTO> modelFriends = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();

    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private MessengerService messengerService;


    private Long logatul=null;

    @FXML
    TextField textFieldSearch;

    @FXML
    TableColumn<FriendDTO, String> tableColumnPrenume;

    @FXML
    TableColumn<FriendDTO, String> tableColumnNume;

    @FXML
    TableColumn<FriendDTO,String> tableColumnFriendsDate;

    @FXML
    TableColumn<FriendDTO, String> tableColumnUserPrenume;

    @FXML
    TableColumn<FriendDTO, String> tableColumnUserNume;


    @FXML
    TableView<FriendDTO> tableViewFriends;

    @FXML
    TableView<Utilizator> tableViewUsers;

    @FXML
    TextField textFieldUserFirstName;

    @FXML
    TextField textFieldUserLastName;

    @FXML
    Button buttonRemoveFriend;

    @FXML
    public void initialize() {

        tableViewFriends.setItems(modelFriends);

        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("firstName"));
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("lastName"));
        tableColumnFriendsDate.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("friendshipDate"));


        tableViewUsers.setItems(modelUsers);

        tableColumnUserPrenume.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("firstName"));
        tableColumnUserNume.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("lastName"));


    }

    private void loadUsersList(){


       modelUsers.setAll(utilizatorService.getAll());

    }

    public void setServices(UtilizatorService utilizatorService, FriendshipService friendshipService, MessengerService messengerService) {

        this.utilizatorService=utilizatorService;
        this.friendshipService=friendshipService;
        this.messengerService=messengerService;
        modelUsers.setAll(utilizatorService.getAll());
    }

    @FXML
    private void searchUser(){

        Long id= Long.parseLong(textFieldSearch.getText());

        logatul=id;

        Utilizator utilizator=utilizatorService.find_user(logatul);

        if(utilizator!=null) {

            textFieldUserFirstName.setText(utilizator.getFirstName());
            textFieldUserLastName.setText(utilizator.getLastName());

            modelFriends.setAll(friendshipService.get_user_friends(id));
        }
        else{

            textFieldUserFirstName.setText("");
            textFieldUserLastName.setText("");

            tableViewFriends.getItems().clear();

            MessageAlert.showErrorMessage(null,"Utilizatorul nu exista");
        }

    }

    @FXML
    void send_friend_request(){

        try {
            Utilizator utilizator = tableViewUsers.getSelectionModel().getSelectedItem();

            friendshipService.send_friend_request(logatul, utilizator.getId());
        }
        catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void remove_friend(){
        try {
            FriendDTO friendDTO = tableViewFriends.getSelectionModel().getSelectedItem();

            friendshipService.remove_friendship(logatul, friendDTO.getId());

            modelFriends.setAll(friendshipService.get_user_friends(logatul));
        }
        catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());

        }
    }

}
