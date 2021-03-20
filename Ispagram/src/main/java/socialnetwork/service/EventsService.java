package socialnetwork.service;

import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.EventValidator;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.repository.paging.pagingRepo.PagingRepoEvents;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.PublicEventChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.time.temporal.ChronoUnit;

public class EventsService implements Observable<PublicEventChangeEvent> {

    PagingRepoEvents<Long, PublicEvent> eventsRepository;
    EventValidator validator=null;
    private final List<Observer<PublicEventChangeEvent>> observers=new ArrayList<>();
    private int page;

    public EventsService(PagingRepoEvents<Long, PublicEvent> eventsRepository) {
        this.eventsRepository = eventsRepository;

        validator=new EventValidator();
    }


    public void add_event(String name, LocalDate localDate, String description, Utilizator organizer,String location){


        PublicEvent publicEvent=new PublicEvent(name,localDate,location,description,organizer);

        //PublicEvent publicEvent=new PublicEvent(name,localDate,description,organizer);


        validator.validate(publicEvent);

        PublicEvent event_with_id=eventsRepository.save(publicEvent);

        notifyObservers(new PublicEventChangeEvent(ChangeEventType.ADD,event_with_id));

    }

    public List<PublicEvent> notify_user_for_upcoming_events(Long id){




        return eventsRepository.findUserNotifyEvents(id);

    }


    public void subscribe_user(PublicEvent publicEvent,Long id){


        eventsRepository.subscribe(publicEvent,id);


        notifyObservers(new PublicEventChangeEvent(ChangeEventType.SUBSCRIBED,publicEvent));

    }

    public void unsubscribe_user(PublicEvent publicEvent,Long id){


        eventsRepository.unsubscribe(publicEvent,id);

        notifyObservers(new PublicEventChangeEvent(ChangeEventType.UNSUBSCRIBED,publicEvent));

    }

    public List<PublicEvent> get_events(){

        return StreamSupport.stream(eventsRepository.findAll().spliterator(),false)
                .filter(publicEvent -> {return !publicEvent.getDate().isBefore(LocalDate.now());})
                .collect(Collectors.toList());
    }



    //paging
    private int size=7;

    public List<PublicEvent> getNext_events(int page) {

        page++;


        List<PublicEvent> userDataPage=get_events_on_page(page);

        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }


    public List<PublicEvent> getPrevious_events(int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return get_events_on_page(page);
    }

    public List<PublicEvent> get_events_on_page(int page){

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);


        return eventsRepository.findEvents(pageable).getContent().collect(Collectors.toList());
    }


    public List<PublicEvent> get_user_events(Long id){

        return StreamSupport.stream(eventsRepository.findAll().spliterator(),false)
                .filter(publicEvent -> {return !publicEvent.getDate().isBefore(LocalDate.now());})
                .filter(publicEvent -> publicEvent.getSubcribers().contains(id))
                .collect(Collectors.toList());
    }

    //paging

    public List<PublicEvent> getNext_user_events(Long id,int page) {

        page++;


        List<PublicEvent> userDataPage=get_user_events_on_page(id,page);

        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }


    public List<PublicEvent> getPrevious_user_events(Long id,int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return get_user_events_on_page(id,page);
    }

    public List<PublicEvent> get_user_events_on_page(Long id,int page){

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);

        return eventsRepository.findUserEvents(pageable,id).getContent().collect(Collectors.toList());
    }


    public void notify_this_user(PublicEvent event,Long id){


        eventsRepository.notify(event,id);

        notifyObservers(new PublicEventChangeEvent(ChangeEventType.NOTIFY,null));
    }

    public void unnotify_this_user(PublicEvent event,Long id){


        eventsRepository.unnotify(event,id);

        notifyObservers(new PublicEventChangeEvent(ChangeEventType.NOTIFY,null));
    }


    @Override
    public void addObserver(Observer<PublicEventChangeEvent> e) {

        observers.add(e);
    }

    @Override
    public void removeObserver(Observable<PublicEventChangeEvent> e) {


        observers.remove(e);
    }

    @Override
    public void notifyObservers(PublicEventChangeEvent t) {


        observers.forEach(x->x.update(t));
    }


    public Long get_events_number_of_pages(){

        Long counter=eventsRepository.get_events_number();


        if(counter%7>0){

            return counter/7+1;
        }

        return counter/7;
    }

    public Long get_user_events_number_of_pages(Long id){

        Long counter=eventsRepository.get_user_events_number(id);

        if(counter%7>0){

            return counter/7+1;
        }

        return counter/7;
    }
}
