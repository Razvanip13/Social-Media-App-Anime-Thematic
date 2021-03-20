package socialnetwork.domain.pages;

import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import socialnetwork.config.Status;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.domain.frequest.FRequestDTO;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.message.Message;
import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.Paginator;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.MessageEventChange;
import socialnetwork.utils.events.PublicEventChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserPage {


    private final Utilizator user;
    private final UtilizatorService utilizatorService;
    private final FriendshipService friendshipService;
    private final MessengerService messengerService;
    private final String firstName;
    private final String lastName;
    private final EventsService eventsService;

    private int user_current_Page=0;
    private int user_current_frined_request_Page=0;
    private int user_sent_frined_request_Page=0;
    private int two_users_convo_Page=0;
    private int user_events_Page=0;
    private int events_Page=0;
    private int user_friends_Page=0;
    private int reports_friends_Page=0;
    private Long friend_id;




    public UserPage(Utilizator user, UtilizatorService utilizatorService, FriendshipService friendshipService, MessengerService messengerService, EventsService eventsService) {
        this.user = user;
        this.utilizatorService = utilizatorService;
        this.friendshipService = friendshipService;
        this.messengerService = messengerService;
        this.eventsService = eventsService;
        firstName=user.getFirstName();
        lastName=user.getLastName();



    }

    public Utilizator getUser() {
        return user;
    }

    public int getUser_current_Page() {
        return user_current_Page;
    }

    public int getUser_current_frined_request_Page() {
        return user_current_frined_request_Page;
    }

    public int getUser_sent_frined_request_Page() {
        return user_sent_frined_request_Page;
    }

    public int getTwo_users_convo_Page() {
        return two_users_convo_Page;
    }

    public int getUser_events_Page() {
        return user_events_Page;
    }

    public int getEvents_Page() {
        return events_Page;
    }

    public int getUser_friends_Page() {
        return user_friends_Page;
    }

    public int getReports_friends_Page() {
        return reports_friends_Page;
    }

    public Long getFriend_id() {
        return friend_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public List<Utilizator> get_platform_users(){


        Iterable<Utilizator> users = utilizatorService.getAll();

        return StreamSupport.stream(users.spliterator(),false)
                .filter(user1 ->!user1.equals(this.user))
                .collect(Collectors.toList());

    }

    // page-uri


    //friends

    public List<FriendDTO> get_friends_on_page(int page){


        user_friends_Page=page;

        return friendshipService.get_user_friends_on_page(user.getId(),user_friends_Page);

    }

    public List<FriendDTO> get_friends_on_previous_page(){

        List<FriendDTO> friendDTOS= friendshipService.getPreviousFriends(user.getId(),user_friends_Page);

        if(friendDTOS.size()!=0){

            user_friends_Page--;
        }


        return friendDTOS;

    }

    public List<FriendDTO> get_friends_on_next_page(){


        List<FriendDTO> friendDTOS= friendshipService.getNextFriends(user.getId(),user_friends_Page);


        if(friendDTOS.size()!=0){

            user_friends_Page++;

        }


        return friendDTOS;
    }


    //reports

    public List<FriendDTO> get_report_friends_on_page(int page){


        reports_friends_Page=page;

        return friendshipService.get_user_friends_on_page(user.getId(),reports_friends_Page);

    }

    public List<FriendDTO> get_report_friends_on_previous_page(){

        List<FriendDTO> friendDTOS= friendshipService.getPreviousFriends(user.getId(),reports_friends_Page);

        if(friendDTOS.size()!=0){

            reports_friends_Page--;
        }


        return friendDTOS;

    }

    public List<FriendDTO> get_report_friends_on_next_page(){


        List<FriendDTO> friendDTOS= friendshipService.getNextFriends(user.getId(),reports_friends_Page);


        if(friendDTOS.size()!=0){

            reports_friends_Page++;
        }


        return friendDTOS;
    }


    //users

    public List<Utilizator> get_users_on_page(int page){


        user_current_Page=page;

        return utilizatorService.getUsersOnPage(page);

    }

    public List<Utilizator> get_users_previous_page(){


        List<Utilizator> utilizators=utilizatorService.getPreviousUsers(user_current_Page);

        if(utilizators.size()!=0){

            user_current_Page--;
        }


        return utilizators;

    }

    public List<Utilizator> get_users_next_page(){


        List<Utilizator> utilizators=utilizatorService.getNextUsers(user_current_Page);

        if(utilizators.size()!=0){

            user_current_Page++;
        }


        return utilizators;

    }



    //friend requests

    public List<FRequestDTO> get_friend_requests_on_page(int page){


        user_current_frined_request_Page=page;

        return friendshipService.get_user_friends_request_on_page(user.getId(),user_current_frined_request_Page);

    }


    public List<FRequestDTO> get_users_previous_requests_page(){


        List<FRequestDTO> fRequestDTOS=friendshipService.getPrevious_friend_requests(user.getId(),user_current_frined_request_Page);

        if(fRequestDTOS.size()!=0){

            user_current_frined_request_Page--;
        }


        return fRequestDTOS;

    }

    public List<FRequestDTO> get_users_next_requests_page(){


        List<FRequestDTO> fRequestDTOS=friendshipService.getNext_friend_requests(user.getId(),user_current_frined_request_Page);

        if(fRequestDTOS.size()!=0){

            user_current_frined_request_Page++;
        }


        return fRequestDTOS;

    }

    //pending sent requests

    public List<FRequestDTO> get_user_sent_requests_on_page(int page){


        user_sent_frined_request_Page=page;

        return friendshipService.get_user_friends_sent_request_on_page(user.getId(),user_sent_frined_request_Page);

    }

    public List<FRequestDTO> get_users_previous_sent_requests_page(){


        List<FRequestDTO> fRequestDTOS=friendshipService.getPrevious_friend_sent_requests(user.getId(),user_sent_frined_request_Page);

        if(fRequestDTOS.size()!=0){

            user_sent_frined_request_Page--;
        }


        return fRequestDTOS;

    }


    public List<FRequestDTO> get_users_next_sent_requests_page(){


        List<FRequestDTO> fRequestDTOS=friendshipService.getNext_friend_sent_requests(user.getId(),user_sent_frined_request_Page);

        if(fRequestDTOS.size()!=0){

            user_sent_frined_request_Page++;
        }


        return fRequestDTOS;

    }


    //public events

    public List<PublicEvent> get_public_events_on_page(int page){


        events_Page=page;

        return eventsService.get_events_on_page(events_Page);

    }

    public List<PublicEvent> get_previous_events_Page(){


        List<PublicEvent> events=eventsService.getPrevious_events(events_Page);

        if(events.size()!=0){

            events_Page--;
        }



        return events;

    }


    public List<PublicEvent> get_next_events_Page(){


        List<PublicEvent> events=eventsService.getNext_events(events_Page);



        if(events.size()!=0){

            events_Page++;
        }



        return events;

    }


    //user subscribed events

    public List<PublicEvent> get_user_events_on_page(int page){


        user_events_Page=page;

        return eventsService.get_user_events_on_page(user.getId(), user_events_Page);

    }

    public List<PublicEvent> get_previous_user_events_Page(){


        List<PublicEvent> events=eventsService.getPrevious_user_events(user.getId(),user_events_Page);;

        if(events.size()!=0){

            user_events_Page--;
        }


        return events;

    }


    public List<PublicEvent> get_next_user_events_Page(){


        List<PublicEvent> events=eventsService.getNext_user_events(user.getId(),user_events_Page);

        if(events.size()!=0){

            user_events_Page++;
        }


        return events;

    }


    //users conversation

    public List<Message> get_two_user_convo_on_page(Long id_prieten,int page){


        two_users_convo_Page=page;
        friend_id=id_prieten;

        return messengerService.get_two_users_convo_on_page(user.getId(),friend_id,page);

    }

    public List<Message> get_previous_messages_Page(){



        List<Message> events=messengerService.getPrevious_two_users_convo(user.getId(),friend_id,two_users_convo_Page);

        if(events.size()!=0){

            two_users_convo_Page--;
        }


        return events;

    }


    public List<Message> get_next_messages_Page(){


        List<Message> events=messengerService.getNext_two_users_convo(user.getId(),friend_id,two_users_convo_Page);

        if(events.size()!=0){

            two_users_convo_Page++;
        }


        return events;

    }


    public List<Message> convo_final_page(Long friend_id,int page){


        this.friend_id=friend_id;

        List<Message> messages=get_two_user_convo_on_page(friend_id,page);

        List<Message> auxiliar=messages;

        while(auxiliar.size()>0){


            auxiliar=get_next_messages_Page();

            if(auxiliar.size()>0) {
                messages = auxiliar;
            }

        }

        return messages;
    }




    public List<FriendDTO> get_user_friends(){

        return friendshipService.get_user_friends(user.getId());
    }


    //f1
    public void add_friend(TableView<Utilizator> usersTable) throws Exception {

        Utilizator u = usersTable.getSelectionModel().getSelectedItem();

        if(u==null){

            throw new Exception("You didn't select an user");
        }
        else

            friendshipService.send_friend_request(user.getId(), u.getId());

    }


    //f1
    public void delete_friend(TableView<FriendDTO> friendsTable) throws Exception {

        FriendDTO u = friendsTable.getSelectionModel().getSelectedItem();

        if(u==null){


            throw new Exception("You didn't select a friend");


        }
        else
            friendshipService.remove_friendship(user.getId(), u.getId());
    }



    public void subscribe_friends_service(Observer<FriendshipChangeEvent> e){

        friendshipService.addObserver(e);

    }

    //f2
    public List<FRequestDTO> get_user_requests(){


        return friendshipService.get_friends_requests(user.getId());
    }



    public void handle_friend_request(TableView<FRequestDTO> requestsTable,Status status){


        FRequestDTO reqDTO = requestsTable.getSelectionModel().getSelectedItem();

        switch (status) {
            case approved -> friendshipService.accept_friend_request(reqDTO.getId());
            case rejected -> friendshipService.decline_friend_request(reqDTO.getId());
        }

    }

    //f3
    public void withdraw_friend_request(TableView<FRequestDTO> requestsTable){


        FRequestDTO reqDTO = requestsTable.getSelectionModel().getSelectedItem();
        friendshipService.remove_pending_friend_request(reqDTO.getId());

    }

    public List<FRequestDTO> get_pending_requests(){

        return friendshipService.get_user_pending_requests(user.getId());
    }


    public void send_message_to_people(TableView<Utilizator> tableViewTelegramUsers, TextArea textAreaMessage){


        List<Long> lista= tableViewTelegramUsers.getSelectionModel().getSelectedItems().stream()
                .map(Utilizator::getId)
                .collect(Collectors.toList());

        String mesaj=textAreaMessage.getText();

        messengerService.send_message(user.getId(),mesaj,lista);
    }


    public List<String> show_conversation_with_a_user(Long id){

        return messengerService.show_two_users_conversation(user.getId(),id)
                .stream()
                .map(Message::toString)
                .collect(Collectors.toList());

    }


    public List<String> get_friends_in_a_period(LocalDate beginValue,LocalDate endValue){

        return friendshipService
                .get_user_friends_in_a_period(user.getId(), beginValue, endValue)
                .stream()
                .map(x -> "      " + x.toString() + " " + x.getFriendshipDate())
                .collect(Collectors.toList());
    }


    public List<String> get_messages_in_a_period(LocalDate beginValue,LocalDate endValue){


        return messengerService
                .get_user_received_messages_in_a_period(user.getId(), beginValue, endValue)
                .stream()
                .map(x -> "       " + x.toString())
                .collect(Collectors.toList());
    }

    public List<String> get_friend_messages_in_a_period(LocalDate beginValue,LocalDate endValue,Long Id) {


        return messengerService
                .get_friend_messages_in_a_period(user.getId(), Id, beginValue, endValue)
                .stream()
                .map(Message::toString)
                .collect(Collectors.toList());
    }


    public List<PublicEvent> get_public_events(){

        return eventsService.get_events();
    }


    public List<PublicEvent> get_user_subscribed_events(){


        List<PublicEvent> list=eventsService.get_user_events(user.getId());

        list.forEach(System.out::println);


        return list;
    }


    public void subscribe_events_service(Observer<PublicEventChangeEvent> e){

        eventsService.addObserver(e);
    }

    public void create_a_new_event(String event_name,LocalDate date,String description,String location){

        eventsService.add_event(event_name,date,description,user,location);
    }


    public void subscribe_to_a_public_event(TableView<PublicEvent> tableView){


        PublicEvent publicEvent = tableView.getSelectionModel().getSelectedItem();


        if(publicEvent!=null) {

            eventsService.subscribe_user(publicEvent, user.getId());

        }

    }

    public void unsubscribe_to_a_public_event(TableView<PublicEvent> tableView){

        PublicEvent publicEvent = tableView.getSelectionModel().getSelectedItem();

        if(publicEvent!=null) {

            eventsService.unsubscribe_user(publicEvent, user.getId());

        }

    }

    public void get_notifications(TableView<PublicEvent> tableView){

        PublicEvent publicEvent = tableView.getSelectionModel().getSelectedItem();

        if(publicEvent!=null) {

            eventsService.notify_this_user(publicEvent, user.getId());

        }

    }

    public void stop_notifications(TableView<PublicEvent> tableView){

        PublicEvent publicEvent = tableView.getSelectionModel().getSelectedItem();

        if(publicEvent!=null) {

            eventsService.unnotify_this_user(publicEvent, user.getId());

        }
    }


    public List<PublicEvent> load_notifications(){


        return eventsService.notify_user_for_upcoming_events(user.getId());
    }


    public void send_message(String message,Long id){


        List<Long> recipients=new ArrayList<>();

        recipients.add(id);


        messengerService.send_message(user.getId(),message,recipients);
    }


    public void subscribe_to_messages(Observer<MessageEventChange> e){

        messengerService.addObserver(e);

    }


    public Long counting_users_pages(){


        Long counter= utilizatorService.get_users_number_of_pages();

        if(counter==0){

            return 1L;
        }

        return counter;
    }

    public Long counting_friends_pages(){

        Long counter=friendshipService.get_friends_number_of_pages(user.getId());

        if(counter==0){

            return 1L;
        }

        return counter;
    }


    public Long counting_messages_pages(Long id){

        Long counter=messengerService.get_convo_number_of_pages(user.getId(),id);

        if(counter==0){

            return 1L;
        }

        return counter;

    }


    public Long counting_friend_requests_pages(){

        Long counter=friendshipService.get_friend_requests_number_of_pages(user.getId());

        if(counter==0){

            return 1L;
        }

        return counter;
    }


    public Long counting_pending_requests_pages(){

        Long counter=friendshipService.get_pending_requests_number_of_pages(user.getId());

        if(counter==0){

            return 1L;
        }

        return counter;
    }

    public Long counting_events_pages(){

        Long counter=eventsService.get_events_number_of_pages();

        if(counter==0){

            return 1L;
        }

        return counter;
    }


    public Long counting_user_events_pages(){

        Long counter=eventsService.get_user_events_number_of_pages(user.getId());

        if(counter==0){

            return 1L;
        }

        return counter;
    }


}
