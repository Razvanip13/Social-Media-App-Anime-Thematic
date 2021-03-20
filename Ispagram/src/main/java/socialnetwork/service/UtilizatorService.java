package socialnetwork.service;

import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.paging.*;
import socialnetwork.repository.paging.pagingRepo.PagingRepository;
import socialnetwork.repository.paging.pagingRepo.PagingUsersRepo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService  {
    private final PagingUsersRepo<Long, Utilizator> repo;

    private int page = 0;
    private int size = 10;
    UtilizatorValidator utilizatorValidator=null;

    public UtilizatorService(PagingUsersRepo<Long, Utilizator> repo) {

        this.repo = repo;
        utilizatorValidator=new UtilizatorValidator(repo);
    }

    /**
     * Adauga un nou utilizator
     * @param prenume : String
     * @param nume : String
     * @return object of type Utilizator or exception if the data is invalid
     */
    public Utilizator addUtilizator(String prenume,String nume) {


        Utilizator messageTask=new Utilizator(prenume,nume);




        List<Utilizator> lista=StreamSupport
                .stream(repo.findAll().spliterator(),false)
                .collect(Collectors.toList());


        Long size=0L;

        for(Utilizator utilizator: lista){

            if(size<utilizator.getId()){

                size=utilizator.getId();
            }
        }

        size++;


        messageTask.setId(size);






        return repo.save(messageTask);
    }

    public Utilizator addUtilizator(String prenume,String nume,String username,String password,String confirmPassword) {




        Utilizator utilizator=new Utilizator(prenume,nume,username,password,confirmPassword);

        utilizatorValidator.validate(utilizator);

        List<Utilizator> lista=StreamSupport
                .stream(repo.findAll().spliterator(),false)
                .collect(Collectors.toList());


        Long size=0L;

        for(Utilizator user: lista){

            if(size<user.getId()){

                size=user.getId();
            }
        }

        size++;


        utilizator.setId(size);




        return repo.save(utilizator);
    }


    /**
     * Deletes the user with the given id and all the friendships with the other users
     * @param id : Long
     * @return the user with the respective id or exception if the id was not found
     */
    public Utilizator deleteUtilizator(Long id){

        return repo.delete(id);
    }

    /**
     * Updates the data of the user who has the given id
     * @param id : Long
     * @param prenume : String
     * @param nume : String
     * @return null if the user was modified or an object with the given data on the contrary
     */
    public void updateUtilizator(Long id,String prenume,String nume){

        Utilizator nou=new Utilizator(prenume,nume);

        nou.setId(id);

        Utilizator out=repo.update(nou);

        if(out!=null)
            throw new ServiceException("Utilizatorul nu a fost gasit");



    }

    /**
     *
     * @return an iterable with all the user
     */
    public List<Utilizator> getAll(){


        return StreamSupport.
                stream(repo.findAll().spliterator(), false).
                sorted((x, y) -> (int) (x.getId() - y.getId())).
                collect(Collectors.toList());
    }

    /**
     * Finds a user with the respective id
     * @param id : Long
     * @return the user or null in case it's not found
     */
    public Utilizator find_user(Long id){

        return repo.findOne(id);
    }


    public Optional<Utilizator> match_login(String username, String password){



        List<Utilizator> users=StreamSupport
                .stream(repo.findAll().spliterator(),false)
                .collect(Collectors.toList());

        return users.stream().filter(x-> x.getUsername().equals(username) && x.getPassword().equals(password)).findFirst();

    }


    public List<Utilizator> getNextUsers(int page) {

        page++;


        List<Utilizator> userDataPage=getUsersOnPage(page);

        if(userDataPage.size()==0){
            page--;
        }

        return userDataPage;
    }


    public List<Utilizator> getPreviousUsers(int page) {

        if(page==0){

            return new ArrayList<>();
        }

        page--;

        return getUsersOnPage(page);
    }


    public List<Utilizator> getUsersOnPage(int page) {

        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Utilizator> studentPage = repo.findAll(pageable);
        return studentPage.getContent().collect(Collectors.toList());
    }

    public Paginator<Utilizator> getUsersPaginator(Pageable pageable){

        return repo.getPaginator(pageable);
    }


    public Long get_users_number_of_pages(){

        Long counter=repo.get_users_number();


        if(counter%10>0){

            return counter/10+1;
        }

        return counter/10;
    }



}
