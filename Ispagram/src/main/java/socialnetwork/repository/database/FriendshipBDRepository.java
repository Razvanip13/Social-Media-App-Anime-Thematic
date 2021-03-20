package socialnetwork.repository.database;

import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.friendship.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.pagingRepo.PagingRepoFriends;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipBDRepository implements PagingRepoFriends<Tuple<Long,Long>, Prietenie> {

    private final String url;
    private final String username;
    private final String password;
    private final Validator<Prietenie> validator;

    public FriendshipBDRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Prietenie findOne(Tuple<Long, Long> friends) {

        int id1=friends.getLeft().intValue();
        int id2=friends.getRight().intValue();
        ResultSet resultSet;
        Prietenie prietenie=null;


        try(
                Connection connection=DriverManager.getConnection(url,username,password);
                PreparedStatement statement= connection.prepareStatement("SELECT * FROM friendships WHERE id1=? and id2=? or id1=? and id2=?")


        ){

            statement.setInt(1,id1);
            statement.setInt(2,id2);
            statement.setInt(3,id2);
            statement.setInt(4,id1);
            resultSet=statement.executeQuery();

            if(resultSet.next()){

                Long the_id1 = resultSet.getLong("id1");
                Long the_id2 = resultSet.getLong("id2");
                LocalDate date = LocalDate.parse(resultSet.getString("date"));


                prietenie=new Prietenie(new Tuple<>(the_id1,the_id2),date);

            }


            resultSet.close();

        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return prietenie;
    }

    @Override
    public Iterable<Prietenie> findAll() {

        Set<Prietenie> friends = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                LocalDate date = LocalDate.parse(resultSet.getString("date"));


                Prietenie prietenie=new Prietenie(new Tuple<>(id1,id2),date);

                friends.add(prietenie);
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    @Override
    public Prietenie save(Prietenie entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");

        validator.validate(entity);



        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO friendships(id1, id2, \"date\") VALUES (?, ?, ?)")

        ){
            statement.setInt(1,entity.getId().getLeft().intValue());
            statement.setInt(2,entity.getId().getRight().intValue());
            statement.setDate(3, Date.valueOf(entity.getDate().toString()));

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
    public Prietenie delete(Tuple<Long, Long> friends) {


        if (friends==null)
            throw new IllegalArgumentException("Tuple must not be null");


        Prietenie prietenie=findOne(friends);

        if(prietenie==null)
            throw new RepoException("Friendship does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM friendships WHERE id1=? and id2=? or id1=? and id2=?")

        ){




            statement.setInt(1,friends.getLeft().intValue());
            statement.setInt(2,friends.getRight().intValue());
            statement.setInt(3,friends.getRight().intValue());
            statement.setInt(4,friends.getLeft().intValue());

            if (statement.executeUpdate() == 1) {

                return prietenie;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return null;

    }

    @Override
    public Prietenie update(Prietenie entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");

        validator.validate(entity);

        Prietenie prietenie=findOne(entity.getId());

        if(prietenie==null)
            throw new RepoException("Friendships does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE friendships SET \"date\"=? WHERE id1=? and id2=?")

        ){
            statement.setDate(1,Date.valueOf(entity.getDate().toString()));
            statement.setInt(2,entity.getId().getLeft().intValue());
            statement.setInt(3,entity.getId().getRight().intValue());

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
                PreparedStatement statement= connection.prepareStatement("SELECT count(*) as \"total\" FROM friendships");
                ResultSet resultSet=statement.executeQuery()
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
    public Page<FriendDTO> findFriends(Pageable pageable, Long id) {


        List<FriendDTO> friends = new ArrayList<>();

        ResultSet resultSet = null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select u.id,u.first_name,u.last_name,fr.date from friendships fr,users u\n" +
                     "where fr.id1=? and fr.id2=u.id or fr.id2=? and fr.id1=u.id \n" +
                     "order by first_name,last_name limit ? offset ?" );
             ) {


            statement.setInt(1,id.intValue());
            statement.setInt(2,id.intValue());
            statement.setInt(3,pageable.getPageSize());
            statement.setInt(4,pageable.getPageSize()*pageable.getPageNumber());


            resultSet=statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                String first_name=resultSet.getString(2);
                String last_name=resultSet.getString(3);
                LocalDate date = LocalDate.parse(resultSet.getString(4));


                FriendDTO friendDTO=new FriendDTO(id1,first_name,last_name,date);

                friends.add(friendDTO);
            }

            return new PageImplementation<>(pageable,friends.stream());

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public List<FriendDTO> find_friends_in_a_period(Long id, LocalDate date1, LocalDate date2) {

        List<FriendDTO> friends = new ArrayList<>();

        ResultSet resultSet = null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select u.id,u.first_name,u.last_name,fr.date from friendships fr,users u\n" +
                     "    where (fr.id1=? and fr.id2=u.id or fr.id2=? and fr.id1=u.id) and date>=? and date<=?\n" +
                     "         order by first_name,last_name,date" );
        ) {


            statement.setInt(1,id.intValue());
            statement.setInt(2,id.intValue());
            statement.setDate(3,Date.valueOf(date1.toString()));
            statement.setDate(4,Date.valueOf(date2.toString()));


            resultSet=statement.executeQuery();

            while (resultSet.next()) {
                Long id1 = resultSet.getLong(1);
                String first_name=resultSet.getString(2);
                String last_name=resultSet.getString(3);
                LocalDate date = LocalDate.parse(resultSet.getString(4));


                FriendDTO friendDTO=new FriendDTO(id1,first_name,last_name,date);

                friends.add(friendDTO);
            }

            return friends;

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;


    }

    @Override
    public Long get_friends_number(Long id) {

        Long counter=null;

        ResultSet resultSet=null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from friendships where id1=? or id2=? ");
            ) {


            statement.setInt(1,id.intValue());
            statement.setInt(2,id.intValue());

            resultSet = statement.executeQuery();

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
