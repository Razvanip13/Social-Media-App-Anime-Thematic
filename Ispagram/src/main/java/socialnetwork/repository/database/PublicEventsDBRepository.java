package socialnetwork.repository.database;

import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.pagingRepo.PagingRepoEvents;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PublicEventsDBRepository implements PagingRepoEvents<Long, PublicEvent> {


    private final String url;
    private final String username;
    private final String password;


    public PublicEventsDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }




    @Override
    public PublicEvent findOne(Long aLong) {


        int id= Integer.parseInt(aLong.toString());
        ResultSet resultSet;
        ResultSet resultSet2=null;
        PublicEvent publicEvent=null;


        try(
                Connection connection= DriverManager.getConnection(url,username,password);
                PreparedStatement statement= connection.prepareStatement("SELECT * FROM public_events WHERE id=?");
                PreparedStatement statement2= connection.prepareStatement("SELECT id_user FROM events_subscriptions WHERE id_event=?")

        ){

            statement.setInt(1,id);
            resultSet=statement.executeQuery();

            if(resultSet.next()){

                long the_id=resultSet.getLong("id");
                String name=resultSet.getString("name");
                LocalDate date = LocalDate.parse(resultSet.getString("date"));




                List<Long> subscribers=new ArrayList<>();


                statement2.setInt(1,id);

                resultSet2=statement2.executeQuery();


                while(resultSet2.next()){


                    Long id_subcriber=resultSet2.getLong("id_user");


                    subscribers.add(id_subcriber);

                }



                publicEvent=new PublicEvent(name,date,subscribers);



                publicEvent.setId(the_id);


            }


            resultSet.close();


        }
        catch (SQLException e){
            e.printStackTrace();
        }



        return publicEvent;


    }

    @Override
    public Iterable<PublicEvent> findAll() {

        List<PublicEvent> events = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id from public_events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");

                PublicEvent publicEvent=findOne(id);
                events.add(publicEvent);
            }

            return events;
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return null;


    }

    @Override
    public PublicEvent save(PublicEvent entity) {


        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");


        ResultSet resultSet=null;

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO public_events(name,date,id_organizer,description,location) VALUES (?, ?,?,?,?)");
                PreparedStatement statement1=connection.prepareStatement("SELECT id From public_events WHERE name=?")
        ){

            statement.setString(1,entity.getName());
            statement.setDate(2, Date.valueOf(entity.getDate().toString()));
            statement.setInt(3,entity.getOrganizer().getId().intValue());
            statement.setString(4,entity.getDescription());
            statement.setString(5,entity.getLocation());

            if(statement.executeUpdate()==1){


                statement1.setString(1,entity.getName());

                resultSet=statement1.executeQuery();

                if(resultSet.next()){

                    Long id=resultSet.getLong("id");

                    entity.setId(id);

                    return entity;

                }


                return null;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return entity;

    }

    @Override
    public PublicEvent delete(Long aLong) {


        if (aLong<0)
            throw new IllegalArgumentException("Id must not be a negative or neutral number");


        PublicEvent event=findOne(aLong);

        if(event==null)
            throw new RepoException("Event does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM public_events WHERE id=?");

        ){


            statement.setInt(1, aLong.intValue());

            if (statement.executeUpdate() == 1) {

                return event;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PublicEvent update(PublicEvent entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");



        PublicEvent publicEvent=findOne(entity.getId());

        if(publicEvent==null)
            throw new RepoException("Event does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE public_events SET id=?, name=?,date=?,id_organizer=?,description=?,location=? WHERE id=?;");

        ){

            statement.setInt(1,entity.getId().intValue());
            statement.setString(2,entity.getName());
            statement.setString(3, entity.getDate().toString());
            statement.setInt(4,entity.getOrganizer().getId().intValue());
            statement.setString(5,entity.getDescription());
            statement.setString(6,entity.getLocation());
            statement.setInt(7,entity.getId().intValue());



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
    public PublicEvent subscribe(PublicEvent entity, Long id_user) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");


        ResultSet resultSet=null;

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO events_subscriptions(id_event,id_user,notify) VALUES ( ?, ?, 1)");
                PreparedStatement statement2 = connection.prepareStatement("SELECT * from events_subscriptions where id_event=? and id_user=?")
        ){


            statement2.setInt(1,entity.getId().intValue());
            statement2.setInt(2,id_user.intValue());

            resultSet=statement2.executeQuery();

            if(resultSet.next()){
                throw new RepoException("You are already subscribed");
            }


            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,id_user.intValue());




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
    public PublicEvent unsubscribe(PublicEvent entity, Long id_user) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");


        ResultSet resultSet=null;

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM events_subscriptions WHERE id_event=? and id_user=?");

        ){
            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,id_user.intValue());


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
    public Page<PublicEvent> findEvents(Pageable pageable) {


        LocalDate now=LocalDate.now();

        List<PublicEvent> events = new ArrayList<>();

        ResultSet resultSet=null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select pe.id,pe.name,pe.date,pe.description,u.id,u.first_name,u.last_name,pe.location from public_events pe inner join users u on pe.id_organizer=u.id where pe.date>=? order by date limit ? offset ?");
             ) {


            statement.setDate(1,Date.valueOf(now));
            statement.setInt(2,pageable.getPageSize());
            statement.setInt(3,pageable.getPageSize()*pageable.getPageNumber());

            resultSet=statement.executeQuery();

            while (resultSet.next()) {
                Long id = resultSet.getLong(1);

                String name=resultSet.getString(2);

                LocalDate date = LocalDate.parse(resultSet.getString(3));

                String description=resultSet.getString(4);
                Long id_organizer=resultSet.getLong(5);
                String first_name=resultSet.getString(6);
                String last_name=resultSet.getString(7);
                String location=resultSet.getString(8);


               // PublicEvent publicEvent=findOne(id);

                Utilizator organizer=new Utilizator(first_name,last_name);
                organizer.setId(id_organizer);

                PublicEvent publicEvent=new PublicEvent(name,date,location,description,organizer);

                publicEvent.setId(id);

                events.add(publicEvent);
            }

            return new PageImplementation<>(pageable,events.stream());


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public Page<PublicEvent> findUserEvents(Pageable pageable, Long id_user) {

        LocalDate now=LocalDate.now();

        List<PublicEvent> events = new ArrayList<>();

        ResultSet resultSet=null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select public_events.id,public_events.name,public_events.date,es.id_user,es.notify from public_events inner join events_subscriptions es on\n" +
                     "    public_events.id = es.id_event\n" +
                     "    where date>=? and es.id_user=? order by date limit ? offset ?");
        ) {


            statement.setDate(1,Date.valueOf(now));
            statement.setInt(2,id_user.intValue());
            statement.setInt(3,pageable.getPageSize());
            statement.setInt(4,pageable.getPageSize()*pageable.getPageNumber());

            resultSet=statement.executeQuery();

            while (resultSet.next()) {

                Long id= resultSet.getLong("id");

                String name=resultSet.getString("name");

                LocalDate date = LocalDate.parse(resultSet.getString("date"));

             //   List<Long> subscribers=new ArrayList<>();

                Integer notify= resultSet.getInt("notify");

                PublicEvent publicEvent=new PublicEvent(name,date,notify);

               // System.out.println(publicEvent.getDate());

                publicEvent.setId(id);

                events.add(publicEvent);
            }

            return new PageImplementation<>(pageable,events.stream());


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;


    }

    @Override
    public PublicEvent notify(PublicEvent entity, Long id) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");



        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE events_subscriptions SET notify=1 WHERE id_event=? and id_user=?");

        ){

            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,id.intValue());




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
    public PublicEvent unnotify(PublicEvent entity, Long id) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");



        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE events_subscriptions SET notify=0 WHERE id_event=? and id_user=?");

        ){

            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,id.intValue());




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
    public List<PublicEvent> findUserNotifyEvents(Long id_user) {

        LocalDate now=LocalDate.now();

        List<PublicEvent> events = new ArrayList<>();

        ResultSet resultSet=null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select public_events.id,public_events.name,public_events.date,es.id_user,es.notify from public_events inner join events_subscriptions es on\n" +
                     "    public_events.id = es.id_event\n" +
                     "    where date>=? and date-? <=20 and es.id_user=? and es.notify=1 order by date");
        ) {


            statement.setDate(1,Date.valueOf(now));
            statement.setDate(2,Date.valueOf(now));
            statement.setInt(3,id_user.intValue());


            resultSet=statement.executeQuery();

            while (resultSet.next()) {

                Long id= resultSet.getLong("id");

                String name=resultSet.getString("name");

                LocalDate date = LocalDate.parse(resultSet.getString("date"));


                Integer notify= resultSet.getInt("notify");

                PublicEvent publicEvent=new PublicEvent(name,date,notify);


                publicEvent.setId(id);

                events.add(publicEvent);
            }

            return events;


        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;


    }

    @Override
    public Long get_events_number() {

        Long counter=null;

        ResultSet resultSet=null;

        LocalDate now=LocalDate.now();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from public_events where date>=? ");
        ) {


            statement.setDate(1,Date.valueOf(now));


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
    public Long get_user_events_number(Long id) {


        Long counter=null;

        ResultSet resultSet=null;

        LocalDate now=LocalDate.now();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from public_events inner join events_subscriptions es on public_events.id = es.id_event where date>=? and es.id_user=?");
        ) {


            statement.setDate(1,Date.valueOf(now));
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
