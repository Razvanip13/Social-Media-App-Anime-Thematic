package socialnetwork.repository.database;

import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.paging.*;
import socialnetwork.repository.paging.pagingRepo.PagingRepository;
import socialnetwork.repository.paging.pagingRepo.PagingUsersRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UtilizatorDbRepository implements PagingUsersRepo<Long, Utilizator> {

    private final String url;
    private final String username;
    private final String password;
   // private final Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
       // this.validator = validator;
    }
    @Override
    public Utilizator findOne(Long aLong) {

        int id= Integer.parseInt(aLong.toString());
        ResultSet resultSet=null;

        Utilizator utilizator=null;


        try(
                Connection connection=DriverManager.getConnection(url,username,password);
                PreparedStatement statement= connection.prepareStatement("SELECT * FROM users WHERE id=?");


                ){

            statement.setInt(1,id);
            resultSet=statement.executeQuery();

            if(resultSet.next()){

                Long the_id=resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username= resultSet.getString("username");
                String password= resultSet.getString("password");

                utilizator=new Utilizator(firstName,lastName,username,password);
                utilizator.setId(the_id);







            }


            resultSet.close();


        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return utilizator;
    }

    @Override
    public Iterable<Utilizator> findAll() {

        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username= resultSet.getString("username");
                String password= resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName,username,password);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Utilizator save(Utilizator entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");

      //  validator.validate(entity);

        ResultSet resultSet=null;

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO users(id, first_name, last_name,username,password) VALUES (?, ?, ?,?,?)");

                ){
            statement.setInt(1,entity.getId().intValue());
            statement.setString(2,entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getUsername());
            statement.setString(5, entity.getPassword());

            if(statement.executeUpdate()==1){

                return null;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return entity;
    }

    @Override
    public Utilizator delete(Long aLong) {


        if (aLong<0)
            throw new IllegalArgumentException("Id must not be a negative or neutral number");


        Utilizator user=findOne(aLong);

        if(user==null)
            throw new RepoException("User does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE id=?");

        ){


            statement.setInt(1, aLong.intValue());

            if (statement.executeUpdate() == 1) {

                return user;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return null;

    }

    @Override
    public Utilizator update(Utilizator entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");

        //validator.validate(entity);

        Utilizator utilizator=findOne(entity.getId());

        if(utilizator==null)
            throw new RepoException("User does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE users SET id=?, first_name=?,last_name=?,username=?,password=? WHERE id=?;");

        ){
            statement.setInt(1,entity.getId().intValue());
            statement.setString(2,entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getUsername());
            statement.setString(5, entity.getPassword());
            statement.setInt(6,entity.getId().intValue());


            if(statement.executeUpdate()==1){

                return null;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return entity;
    }


    public int size() {


        try(
                Connection connection=DriverManager.getConnection(url,username,password);
                PreparedStatement statement= connection.prepareStatement("SELECT count(*) as total FROM users");
                ResultSet resultSet=statement.executeQuery();
        ){


            if(resultSet.next()){
                return resultSet.getInt("total");
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }


        return 0;

    }

    @Override
    public Page<Utilizator> findAll(Pageable pageable) {

//        Paginator<Utilizator> paginator = new Paginator<Utilizator>(pageable, this.findAll());
//        return paginator.paginate();

        ResultSet resultSet=null;



        List<Utilizator> users = new ArrayList<>();


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users order by first_name,last_name limit ? offset ?");
        ) {

            statement.setInt(1,pageable.getPageSize());
            statement.setInt(2,pageable.getPageNumber()*pageable.getPageSize());

            resultSet=statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String username= resultSet.getString("username");
                String password= resultSet.getString("password");

                Utilizator utilizator = new Utilizator(firstName, lastName,username,password);
                utilizator.setId(id);
                users.add(utilizator);
            }

            return  new PageImplementation<Utilizator>(pageable,users.stream());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Paginator<Utilizator> getPaginator(Pageable pageable) {




        return new Paginator<>(pageable,this.findAll());
    }

    @Override
    public Utilizator findOne(String the_username) {


        ResultSet resultSet=null;

        Utilizator utilizator=null;


        try(
                Connection connection=DriverManager.getConnection(url,username,password);
                PreparedStatement statement= connection.prepareStatement("SELECT * FROM users WHERE username=?");


        ){

            statement.setString(1,the_username);
            resultSet=statement.executeQuery();

            if(resultSet.next()){



                Long the_id=resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                String password= resultSet.getString("password");

                utilizator=new Utilizator(firstName,lastName,the_username,password);
                utilizator.setId(the_id);




            }


            resultSet.close();


        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return utilizator;
    }

    @Override
    public Long get_users_number() {

        Long counter=null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from users");
             ResultSet resultSet = statement.executeQuery()) {



            while (resultSet.next()) {

                counter=resultSet.getLong(1);
            }
            return counter;


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return counter;

    }


}
