package socialnetwork.service;

import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.message.Message;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.pagingRepo.PagingRepoMessages;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.MessageEventChange;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessengerService implements Observable<MessageEventChange> {

    private final Repository<Long, Utilizator> utilizatorRepository;
    private final PagingRepoMessages<Long, Message> messageRepository;
    private final Validator<Message> messageValidator;
    private int page;
    private int size=10;
    private final List<Observer<MessageEventChange>> observers=new ArrayList<>();

    public MessengerService(Repository<Long, Utilizator> utilizatorRepository, PagingRepoMessages<Long, Message> messageRepository) {
        this.utilizatorRepository = utilizatorRepository;
        this.messageRepository = messageRepository;
        this.messageValidator=new MessageValidator(utilizatorRepository);
    }

    public Message send_message(Long id,String message,List<Long> recipient_ids){


        if(id<=0){

            throw new ServiceException("You cannot use a negative or neutral id");
        }

        Utilizator messenger=utilizatorRepository.findOne(id);



        List<Utilizator> to_users=recipient_ids.stream()
                     .map(recipient_id->{

            Utilizator utilizator=utilizatorRepository.findOne(recipient_id);

            if(utilizatorRepository.findOne(recipient_id)==null){

                throw new ServiceException("One or more recipients do not exist");
            }

            return utilizator;
            //to_users.add(utilizator);

        })
                .collect(Collectors.toList());

        Message new_message=new Message(messenger,to_users,message, LocalDateTime.now());

        messageValidator.validate(new_message);

        messageRepository.save(new_message);


        notifyObservers(new MessageEventChange(ChangeEventType.ADD,new_message));

        return new_message;

    }

    public void delete_message(Long id){

        messageRepository.delete(id);

    }

    public void update_message(Long id,String updated_message){


        Message the_message=new Message(null,null,updated_message,null);
        the_message.setId(id);

        messageRepository.update(the_message);

    }

    public List<Message> get_users_unreplied_messages(Long id){

        Utilizator utilizator=utilizatorRepository.findOne(id);

        if(utilizator==null){
            throw new RepoException("The user does not exist");
        }


        return StreamSupport
                .stream(messageRepository.findAll().spliterator(),false)
                .filter(message -> {

                    return message.getTo().contains(utilizator) && message.getReply() == null;
                })
                .sorted(Comparator.comparing(Message::getData))
                .collect(Collectors.toList());


    }

    public void reply_message(Long id, Long user_id,String reply){


        Message message=messageRepository.findOne(id);

        if(message==null){
            throw  new ServiceException("The message does not exist");
        }

        Utilizator utilizator=utilizatorRepository.findOne(user_id);

        if(utilizator==null){
            throw  new ServiceException("The user does not exist in the social network");
        }

        if(!message.getTo().contains(utilizator)){
            throw  new ServiceException("The user cannot reply to this message");
        }

        List<Long> repliedUser=new ArrayList<>();

        repliedUser.add(message.getFrom().getId());

        Message saved_message=send_message(user_id,reply,repliedUser);

        //message.setReply(saved_message);

        saved_message.setReply(message);

        messageRepository.update(saved_message);

    }

    public List<Message> show_two_users_conversation(Long id1,Long id2){

        if(id1.equals(id2)){

            throw new ServiceException("Give me two distinct users");
        }

        Utilizator utilizator1=utilizatorRepository.findOne(id1);

        if(utilizator1==null){
            throw new ServiceException("User 1 does not exist");
        }

        Utilizator utilizator2=utilizatorRepository.findOne(id2);

        if(utilizator2==null){
            throw new ServiceException("User 2 does not exist");
        }





        return StreamSupport
                .stream(messageRepository.findAll().spliterator(),false)
                .filter(message -> {

                    System.out.println(message.getTo().contains(utilizator2));


                    return message.getFrom().getId().equals(id1) && message.getTo().contains(utilizator2)
                            || message.getFrom().getId().equals(id2) && message.getTo().contains(utilizator1);

                })
                .sorted(Comparator.comparing(Message::getData))
                .collect(Collectors.toList());



    }

    public List<Message> getNext_two_users_convo(Long id1,Long id2,int page) {

        page++;


        List<Message> userDataPage=get_two_users_convo_on_page(id1,id2,page);

        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }


    public List<Message> getPrevious_two_users_convo(Long id1,Long id2,int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return get_two_users_convo_on_page(id1,id2,page);
    }


    public List<Message> get_two_users_convo_on_page(Long id1,Long id2, int page){

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        //Paginator<Message> paginator = new Paginator<Message>(pageable, this.show_two_users_conversation(id1,id2));

        //return paginator.paginate().getContent().collect(Collectors.toList());

        return messageRepository.findConversation(pageable,id1,id2).getContent().collect(Collectors.toList());
    }



    public List<Message> get_user_received_messages_in_a_period(Long id, LocalDate begin,LocalDate end){


        if(begin==null || end==null){
            throw new ServiceException("You haven't entered a period");
        }

        if(begin.isAfter(end)){

            throw new ServiceException("Please enter a valid date");
        }



        return messageRepository.get_messages_in_a_period(id,begin,end);


    }


    public List<Message> get_friend_messages_in_a_period(Long id,Long id_friend,LocalDate begin, LocalDate end){

        if(begin==null || end==null){

            throw new ServiceException("You haven't entered the period");

        }

        if(begin.isAfter(end)){

            throw new ServiceException("Give a valid period of time");
        }



        return messageRepository.get_friend_messages_in_a_period(id,id_friend,begin,end);
    }


    @Override
    public void addObserver(Observer<MessageEventChange> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observable<MessageEventChange> e) {

        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageEventChange t) {

        observers.forEach(x->x.update(t));
    }


    public Long get_convo_number_of_pages(Long id1,Long id2){

        Long counter=messageRepository.get_conversation_lines_number(id1,id2);


        if(counter%10>0){

            return counter/10+1;
        }

        return counter/10;
    }
}
