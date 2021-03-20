package socialnetwork.repository.database;


import socialnetwork.config.Status;
import socialnetwork.domain.frequest.FRequest;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.pagingRepo.PagingRepoRequests;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestBDRepository implements PagingRepoRequests<Long, FRequest> {

    private final String url;
    private final String username;
    private final String password;

    public FriendRequestBDRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public FRequest findOne(Long aLong) {

        int id= Integer.parseInt(aLong.toString());
        ResultSet resultSet;
        FRequest request=null;


        try(
                Connection connection= DriverManager.getConnection(url,username,password);
                PreparedStatement statement= connection.prepareStatement("SELECT * FROM friend_requests WHERE id=?")

        ){

            statement.setInt(1,id);
            resultSet=statement.executeQuery();

            if(resultSet.next()){

                long the_id=resultSet.getLong("id");
                long from=resultSet.getLong("from");
                long to=resultSet.getLong("to");
                String status=resultSet.getString("status");

                request=new FRequest(from,to,Enum.valueOf(Status.class,status));
                request.setId(the_id);


            }


            resultSet.close();


        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return request;

    }

    @Override
    public Iterable<FRequest> findAll() {

        List<FRequest> requests = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id,\"from\",\"to\",status,\"date\" from friend_requests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                long the_id=resultSet.getLong("id");
                long from=resultSet.getLong(2);
                long to=resultSet.getLong(3);
                String status=resultSet.getString(4);
                LocalDate date = LocalDate.parse(resultSet.getString("date"));

                FRequest request=new FRequest(from,to,Enum.valueOf(Status.class,status),date);
                request.setId(the_id);

                requests.add(request);
            }


            return requests;
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public FRequest save(FRequest entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");




        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO friend_requests(\"from\",\"to\",status,\"date\") VALUES (?, ?, ?, ?)")

        ){
            statement.setInt(1,entity.getFrom().intValue());
            statement.setInt(2,entity.getTo().intValue());
            statement.setString(3,"pending");
            statement.setDate(4,Date.valueOf(entity.getLocalDate().toString()));

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
    public FRequest delete(Long aLong) {

        if (aLong<0)
            throw new IllegalArgumentException("Id must not be a negative or neutral number");


        FRequest request=findOne(aLong);

        if(request==null)
            throw new RepoException("User does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM friend_requests WHERE id=?")

        ){


            statement.setInt(1, aLong.intValue());

            if (statement.executeUpdate() == 1) {

                return request;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return null;

    }

    @Override
    public FRequest update(FRequest entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");




        if(findOne(entity.getId())==null)
            throw new RepoException("User does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE friend_requests SET status=? WHERE id=?;")

        ){
            statement.setString(1,entity.getStatus().toString());
            statement.setInt(2,entity.getId().intValue());

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
    public Page<FRequest> findFriendRequests(Pageable pageable, Long id) {

        List<FRequest> requests = new ArrayList<>();

        ResultSet resultSet=null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend_requests where friend_requests.\"to\"=? order by date desc limit ? offset ?");
             ) {

            statement.setInt(1,id.intValue());
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,pageable.getPageSize()*pageable.getPageNumber());

            resultSet=statement.executeQuery();

            while (resultSet.next()) {

                long the_id=resultSet.getLong("id");
                long from=resultSet.getLong(2);
                long to=resultSet.getLong(3);
                String status=resultSet.getString(4);
                LocalDate date = LocalDate.parse(resultSet.getString("date"));

                FRequest request=new FRequest(from,to,Enum.valueOf(Status.class,status),date);
                request.setId(the_id);

                requests.add(request);
            }


            return new PageImplementation<>(pageable,requests.stream());


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;



    }

    @Override
    public Page<FRequest> findPendingFriendRequests(Pageable pageable, Long id) {

        List<FRequest> requests = new ArrayList<>();

        ResultSet resultSet=null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friend_requests where friend_requests.\"from\"=? and friend_requests.status='pending' order by date desc limit ? offset ?");
        ) {

            statement.setInt(1,id.intValue());
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,pageable.getPageSize()*pageable.getPageNumber());

            resultSet=statement.executeQuery();

            while (resultSet.next()) {

                long the_id=resultSet.getLong("id");
                long from=resultSet.getLong(2);
                long to=resultSet.getLong(3);
                String status=resultSet.getString(4);
                LocalDate date = LocalDate.parse(resultSet.getString("date"));

                FRequest request=new FRequest(from,to,Enum.valueOf(Status.class,status),date);
                request.setId(the_id);

                requests.add(request);
            }


            return new PageImplementation<>(pageable,requests.stream());


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public Long get_friend_requests_number(Long id) {


        Long counter=null;

        ResultSet resultSet=null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) from friend_requests where friend_requests.\"to\"=?");
        ) {


            statement.setInt(1,id.intValue());


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

    @Override
    public Long get_pending_requests_number(Long id) {

        Long counter=null;

        ResultSet resultSet=null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select count(*) from friend_requests where friend_requests.\"from\"=? and friend_requests.status='pending' ");
        ) {


            statement.setInt(1,id.intValue());


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
