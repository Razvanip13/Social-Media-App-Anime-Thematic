package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.domain.message.Message;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.MessageEventChange;
import socialnetwork.utils.observer.Observer;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.image.ImageView;

public class ConversationsController implements Observer<MessageEventChange> {

    @FXML
    ListView<String> listViewChat;

    @FXML
    ImageView imageViewChat;

    @FXML
    TextArea textAreaMessage;

    @FXML
    Label labelUserChat;

    @FXML
    Button buttonBack;

    @FXML
    Button buttonNext;

    @FXML
    Label labelCountMessages;


    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private MessengerService messageService;
    private Utilizator user1;
    private Utilizator user2;

    ObservableList<String> modelConversations = FXCollections.observableArrayList();
    private UserPage userPage;
    private Long Id;
    private String firstName;
    private String lastName;

    public void setService(UtilizatorService servU, FriendshipService servP, MessengerService servM, Utilizator u1, Utilizator u2) {
        this.utilizatorService = servU;
        this.friendshipService = servP;
        this.messageService = servM;
        this.user1 = u1;
        this.user2 = u2;

        initModelConversations();
    }

    public void setService(UserPage userPage,Long Id,String firstName,String lastName){

        this.userPage=userPage;
        this.Id=Id;

        this.firstName=firstName;
        this.lastName=lastName;

        labelUserChat.setText(labelUserChat.getText()+" "+this.firstName+" "+this.lastName);

        userPage.subscribe_to_messages(this);

        buttonNext.setDisable(true);


        initModelConversations();

        Image image=new Image("\\views\\icons\\chat_chat.PNG");

        imageViewChat.setImage(image);

        //listViewChat.addEventFilter(KeyEvent., Event::consume);
    }


    private void initModelConversations(){


        modelConversations.setAll(userPage.convo_final_page(Id,0).stream()
                .map(Message::toString)
                .collect(Collectors.toList()));

        labelCountMessages.setText((userPage.getTwo_users_convo_Page()+1)+"/"+ userPage.counting_messages_pages(Id));

    }


    @FXML
    void handleNext(){

        List<String> messages=userPage.get_next_messages_Page().stream()
                .map(Message::toString)
                .collect(Collectors.toList());



        if(messages.size()!=0){

            buttonBack.setDisable(false);

            modelConversations.setAll(messages);

            labelCountMessages.setText((userPage.getTwo_users_convo_Page()+1)+"/"+ userPage.counting_messages_pages(Id));

        }
        else{

            buttonNext.setDisable(true);
        }


    }

    @FXML
    void handleBack(){


        List<String> messages=userPage.get_previous_messages_Page().stream()
                .map(Message::toString)
                .collect(Collectors.toList());




        if(messages.size()!=0){

            modelConversations.setAll(messages);

            buttonNext.setDisable(false);

            labelCountMessages.setText((userPage.getTwo_users_convo_Page()+1)+"/"+ userPage.counting_messages_pages(Id));
        }
        else{

            buttonBack.setDisable(true);
        }

    }



    @FXML
    void handleSendMessage(){


        String message=textAreaMessage.getText();

        userPage.send_message(message,Id);

        textAreaMessage.clear();

    }

    private void initializeConversationsList(){

        listViewChat.setItems(modelConversations);
    }

    @FXML
    public void initialize(){

        initializeConversationsList();
    }

    @Override
    public void update(MessageEventChange messageEventChange) {

        if(messageEventChange.getType()== ChangeEventType.ADD){


            modelConversations.setAll(userPage.convo_final_page(Id,userPage.getTwo_users_convo_Page())
                    .stream()
                    .map(Message::toString)
                    .collect(Collectors.toList()));

            labelCountMessages.setText((userPage.getTwo_users_convo_Page()+1)+"/"+ userPage.counting_messages_pages(Id));


        }

        buttonNext.setDisable(true);
        buttonBack.setDisable(false);

    }
}
