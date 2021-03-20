package socialnetwork.service;

import socialnetwork.Networkanalyzer.NetworkAnalyzer;
import socialnetwork.config.Status;
import socialnetwork.domain.*;
import socialnetwork.domain.frequest.FRequest;
import socialnetwork.domain.frequest.FRequestDTO;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.friendship.Prietenie;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.*;
import socialnetwork.repository.paging.pagingRepo.PagingRepoFriends;
import socialnetwork.repository.paging.pagingRepo.PagingRepoRequests;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class
FriendshipService implements Observable<FriendshipChangeEvent> {


    private final Repository<Long, Utilizator> repo;
    private final PagingRepoFriends<Tuple<Long,Long>, Prietenie> repoPrietenie;
    private final PagingRepoRequests<Long, FRequest> requestRepository;
    private final List<Observer<FriendshipChangeEvent>> observers=new ArrayList<>();

    public FriendshipService(Repository<Long, Utilizator> repo, PagingRepoFriends<Tuple<Long, Long>, Prietenie> repoPrietenie, PagingRepoRequests<Long, FRequest> requestRepository) {
        this.repo = repo;
        this.repoPrietenie = repoPrietenie;
        this.requestRepository = requestRepository;
    }

    /**
     * Adds a new friendship or throws exception if it isn't valid
     * @param id1: Long
     * @param id2: Long
     */
    public void addPrietenie(Long id1,Long id2){

        if(repo.findOne(id1)!=null && repo.findOne(id2)!=null){

            Tuple<Long,Long> tuple=new Tuple<>(id1,id2);


            List<Prietenie> lista= StreamSupport.stream(repoPrietenie.findAll().spliterator(),false)
                    .collect(Collectors.toList());


            lista.forEach(x->{

                if(tuple.equals(x.getId()))
                    throw new ServiceException("These two users are already friends");
            });

            repoPrietenie.save(new Prietenie(new Tuple<>(id1,id2)));

            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD,null));
        }
        else
            throw new ServiceException("The two ids are invalid");

    }

    /**
     *
     * @return an iterable for the friendships in the file
     */
    public Iterable<Prietenie> getAllFriends() {


        return StreamSupport
                .stream(repoPrietenie.findAll().spliterator(),false)
                .sorted(Prietenie::compareTo)
                .collect(Collectors.toList());




    }

    /**
     * Deletes the friendship between the two users with the respective ids
     * @param id1 : Long
     * @param id2 : Long
     */
    public void deletePrietenie(Long id1,Long id2){

        Prietenie prietenie=repoPrietenie.delete(new Tuple<>(id1,id2));


        if(prietenie==null){

            throw new ServiceException("The friendship was not found");
        }
        else{

            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE,null));
        }


    }

    /**
     *
     * @return a Tuple with all friendships from the file
     */
    private List<Tuple<Long, Long>> getFriendshipTuples() {


        List<Prietenie> list = StreamSupport
                .stream(repoPrietenie.findAll().spliterator(), false)
                .collect(Collectors.toList());


        return list.stream()
                .map(x -> new Tuple<>(x.getId().getLeft(), x.getId().getRight()))
                .collect(Collectors.toList());
    }

    /**
     *
     * @return a list with the ids of the users
     */
    private List<Long> getUsersNode() {

        List<Utilizator> list = StreamSupport
                .stream(repo.findAll().spliterator(), false)
                .collect(Collectors.toList());


        return list.stream()
                .map(Entity::getId)
                .collect(Collectors.toList());
    }

    /**
     *
     * @return the number of communities
     */
    public Long getNrComunitati() {
        return new NetworkAnalyzer(getFriendshipTuples(), getUsersNode()).getNumberOfComponents();
    }


    /**
     *
     * @return a list with the ids of the user who take part from the biggest community
     */
    public List<Long> getBiggestCommunity(){


        return new NetworkAnalyzer(getFriendshipTuples(), getUsersNode()).biggestCommunity();
    }


    /**
     *
     * @return a list with all friendships from the social network
     */
    public List<Prietenie> getFriendships(){

        return StreamSupport
                .stream(repoPrietenie.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * Provides a list with FriendDTO's for the given id user
     * @param id : Long
     * @return a list with all friend users for the given id
     */
    public List<FriendDTO> get_user_friends(Long id){


        List<Prietenie> list = StreamSupport
                .stream(repoPrietenie.findAll().spliterator(),false)
                .filter(x-> x.getId().getLeft().equals(id) || x.getId().getRight().equals(id))
                .collect(Collectors.toList());



        return list
                .stream()
                .map(x->{

                    if(!x.getId().getLeft().equals(id)){


                        Utilizator utilizator=repo.findOne(x.getId().getLeft());

                        return new FriendDTO(utilizator.getId(),utilizator.getFirstName(),utilizator.getLastName(),x.getDate());
                    }
                    else{

                        Utilizator utilizator=repo.findOne(x.getId().getRight());

                        return new FriendDTO(utilizator.getId(),utilizator.getFirstName(),utilizator.getLastName(),x.getDate());

                    }


                })
                .collect(Collectors.toList());



    }

    private int page;
    private int size=10;

    public List<FriendDTO> getNextFriends(Long id,int page) {

        page++;


        List<FriendDTO> userDataPage=get_user_friends_on_page(id,page);

        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }



    public List<FriendDTO> getPreviousFriends(Long id,int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return get_user_friends_on_page(id,page);
    }


    public List<FriendDTO> get_user_friends_on_page(Long id,int page){

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
//        Paginator<FriendDTO> paginator = new Paginator<FriendDTO>(pageable, this.get_user_friends(id));
//
//        return paginator.paginate().getContent().collect(Collectors.toList());


        return repoPrietenie.findFriends(pageable,id).getContent().collect(Collectors.toList());
    }

    /**
     * Provides a list with FriendDTO's for the given id user in a specific month
     * @param id : Long
     * @param month : Month
     * @return a list with the friends of a user made in a specific month
     */
    public List<Tuple<Utilizator,LocalDate>> get_user_friends_in_a_specific_month(Long id, Month month){


        List<Prietenie> list = StreamSupport
                .stream(repoPrietenie.findAll().spliterator(),false)
                .filter(x-> (x.getId().getLeft().equals(id) || x.getId().getRight().equals(id)) && x.getDate().getMonth().equals(month))
                .collect(Collectors.toList());


        return list
                .stream()
                .map(x->{

                    if(!x.getId().getLeft().equals(id)){

                        return new Tuple<>(repo.findOne(x.getId().getLeft()),x.getDate());
                    }

                    return new Tuple<>(repo.findOne(x.getId().getRight()),x.getDate());

                })
                .collect(Collectors.toList());

    }

    /**
     * Sends a friend request from a user to another one
      * @param id1 : Long
     * @param id2 : Long
     */
   public void send_friend_request(Long id1,Long id2){


        if(repo.findOne(id1)!=null && repo.findOne(id2)!=null) {


            if(id1.equals(id2)){

                throw new ServiceException("You cannot send a friend request to your own self");
            }


            if(repoPrietenie.findOne(new Tuple<>(id1,id2))!=null){

                throw new ServiceException("They are already friends!");
            }


            List<FRequest> fRequestList = StreamSupport
                    .stream(requestRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());


            FRequest request = new FRequest(id1, id2, Status.pending);

            fRequestList.forEach(fRequest ->{

                if(fRequest.getTo().equals(request.getFrom()) && fRequest.getFrom().equals(request.getTo()) && fRequest.getStatus()==Status.pending){

                    throw new ServiceException("The respective user already sent you a friend request which is pending");
                }



            });




            if (fRequestList.contains(request)) {

                throw new ServiceException("This request already exists");
            }

            requestRepository.save(request);

            notifyObservers(new FriendshipChangeEvent(ChangeEventType.SENT,null));
        }
        else
            throw new ServiceException("One or both users do not exist in database");
   }

    /**
     * You get the list of friend request for the respective id
     * @param id : Long
     * @return Returns the friends requests of a user for a given id
     */
    public List<FRequestDTO> get_friends_requests(Long id){


        if(repo.findOne(id)==null){

            throw new ServiceException("The id is not valid");
        }

        List<FRequest> requests= StreamSupport
                .stream(requestRepository.findAll().spliterator(),false)
                .filter(fRequest -> {

                    return fRequest.getTo().equals(id);  //&& fRequest.getStatus().equals(Status.pending);

                })
                .collect(Collectors.toList());

        return requests.stream()
                .map(fRequest->{

                    Utilizator utilizator=repo.findOne(fRequest.getFrom());

                    return new FRequestDTO(fRequest.getId(),utilizator.getFirstName(),utilizator.getLastName(),fRequest.getStatus(),fRequest.getLocalDate());

                })
                .collect(Collectors.toList());


    }


    public List<FRequestDTO> getNext_friend_requests(Long id,int page) {

        page++;


        List<FRequestDTO> userDataPage=get_user_friends_request_on_page(id,page);

        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }


    public List<FRequestDTO> getPrevious_friend_requests(Long id,int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return get_user_friends_request_on_page(id,page);
    }



    public List<FRequestDTO> get_user_friends_request_on_page(Long id,int page){

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);

        return requestRepository.findFriendRequests(pageable,id).getContent()
                .map(fRequest->{

                    Utilizator utilizator=repo.findOne(fRequest.getFrom());

                    return new FRequestDTO(fRequest.getId(),utilizator.getFirstName(),utilizator.getLastName(),fRequest.getStatus(),fRequest.getLocalDate());

                })
                .collect(Collectors.toList());
    }


    /**
     * Gets all the requests which a user sent and which are pending
     * @param id : Long
     * @return a list with all friend_request of the user with the given id which are pending
     */
    public List<FRequestDTO> get_user_pending_requests(Long id){


        if(repo.findOne(id)==null){

            throw new ServiceException("The id is not valid");
        }

        List<FRequest> requests= StreamSupport
                .stream(requestRepository.findAll().spliterator(),false)
                .filter(fRequest -> {

                    return fRequest.getFrom().equals(id) && fRequest.getStatus().equals(Status.pending);

                })
                .collect(Collectors.toList());

        return requests.stream()
                .map(fRequest->{

                    Utilizator utilizator=repo.findOne(fRequest.getTo());

                    return new FRequestDTO(fRequest.getId(),utilizator.getFirstName(),utilizator.getLastName(),fRequest.getStatus(),fRequest.getLocalDate());

                })
                .collect(Collectors.toList());


    }

    public List<FRequestDTO> getNext_friend_sent_requests(Long id,int page) {

        page++;


        List<FRequestDTO> userDataPage=get_user_friends_sent_request_on_page(id,page);



        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }


    public List<FRequestDTO> getPrevious_friend_sent_requests(Long id,int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return get_user_friends_sent_request_on_page(id,page);
    }


    public List<FRequestDTO> get_user_friends_sent_request_on_page(Long id,int page){

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);


         return requestRepository.findPendingFriendRequests(pageable,id)
                 .getContent()
                 .map(fRequest->{

                     Utilizator utilizator=repo.findOne(fRequest.getTo());

                     return new FRequestDTO(fRequest.getId(),utilizator.getFirstName(),utilizator.getLastName(),fRequest.getStatus(),fRequest.getLocalDate());

                 })
                 .collect(Collectors.toList());
    }



    /**
     * Removes the pending request of the user with the given id
     * @param id : Long
     * @return the deleted request or null if no request was deleted
     */
    public FRequestDTO remove_pending_friend_request(Long id){

        FRequest deleted_request= requestRepository.delete(id);

        if(deleted_request!=null){

            Utilizator utilizator=repo.findOne(deleted_request.getTo());

            FRequestDTO deleted_dto= new FRequestDTO(deleted_request.getId(),utilizator.getFirstName(),utilizator.getLastName(),deleted_request.getStatus(),deleted_request.getLocalDate());

            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE,null));

            return deleted_dto;
        }

        return null;
    }



    /**
     * Accepts a friend request with the respective id
     * @param id : Long
     */
    public void accept_friend_request(Long id){


        FRequest request=requestRepository.findOne(id);

        if(request==null){

            throw new ServiceException("The respective request does not exist");
        }


        if(!request.getStatus().equals(Status.pending)){

            throw new ServiceException("This request is finished");
        }


        request.setStatus(Status.approved);

        requestRepository.update(request);

        addPrietenie(request.getFrom(),request.getTo());

        notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE,null));

    }

    /**
     * Declines a friend request with the respective id
     * @param id: Long
     */
    public void decline_friend_request(Long id){

        FRequest request=requestRepository.findOne(id);

        if(request==null){

            throw new ServiceException("The respective request does not exist");
        }



        if(!request.getStatus().equals(Status.pending)){

            throw new ServiceException("This request is finished");
        }


        request.setStatus(Status.rejected);

        requestRepository.update(request);

        notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE,null));

    }

    /**
     * Remove the friendship between the two users with the respective ids
     * @param id1 : Long
     * @param id2 : Long
     */
    public void remove_friendship(Long id1,Long id2){


        Prietenie prietenie=repoPrietenie.delete(new Tuple<>(id1,id2));

        if(prietenie==null){

            throw new ServiceException("The friendship was not found");
        }
        else{

            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE,null));

        }


    }


    public List<FriendDTO> get_user_friends_in_a_period(Long id,LocalDate begin,LocalDate end){

        if(begin==null || end==null){

            throw new ServiceException("You haven't inserted a period of time");
        }


        if(begin.isAfter(end)){

            throw new ServiceException("Please, insert a valid period of time");
        }



        return repoPrietenie.find_friends_in_a_period(id,begin,end);

    }




    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {

        observers.add(e);

    }

    @Override
    public void removeObserver(Observable<FriendshipChangeEvent> e) {

        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {

        observers.forEach(x->x.update(t));

    }


    public Long get_friends_number_of_pages(Long id){

        Long counter=repoPrietenie.get_friends_number(id);

        if(counter%10>0){

            return counter/10+1;
        }

        return counter/10;
    }


    public Long get_friend_requests_number_of_pages(Long id){

        Long counter=requestRepository.get_friend_requests_number(id);

        if(counter%10>0){

            return counter/10+1;
        }

        return counter/10;
    }

    public Long get_pending_requests_number_of_pages(Long id){

        Long counter=requestRepository.get_pending_requests_number(id);

        if(counter%10>0){

            return counter/10+1;
        }

        return counter/10;
    }


}
