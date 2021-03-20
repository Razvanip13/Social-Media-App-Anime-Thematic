package socialnetwork.repository.database;

import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.message.Message;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.PageImplementation;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.pagingRepo.PagingRepoMessages;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


public class MessagesBDRepository implements PagingRepoMessages<Long, Message> {

    private final String url;
    private final String username;
    private final String password;
    UtilizatorDbRepository user_repo;

    public MessagesBDRepository(String url, String username, String password,UtilizatorDbRepository utilizatorDbRepository) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.user_repo=utilizatorDbRepository;
    }

    @Override
    public Message findOne(Long aLong) {

        int id= Integer.parseInt(aLong.toString());
        ResultSet resultSet1;
        ResultSet resultSet2;
        ResultSet resultSet3;
        Message the_message=null;

        String get_message="select id_user,data,message,id_reply from messages where id=?";

        String get_to="select u.id,u.first_name,u.last_name,u.username,u.password from messages\n" +
                "inner join to_users tu on messages.id = tu.id_message\n" +
                "inner join users u on u.id = tu.id_user\n" +
                "where messages.id=?";


        try(
                Connection connection= DriverManager.getConnection(url,username,password);
                PreparedStatement statement1=connection.prepareStatement(get_message);
                PreparedStatement statement2= connection.prepareStatement(get_to)

        ){

            statement1.setInt(1,id);
            resultSet1=statement1.executeQuery();

            if(resultSet1.next()){

                Long msg_id=resultSet1.getLong("id_user");
                LocalDateTime msg_datetime=resultSet1.getObject("data",LocalDateTime.class);
                String msg_message=resultSet1.getString("message");
                long id_reply=resultSet1.getLong("id_reply");

                List<Utilizator> to_users=new ArrayList<>();

                statement2.setInt(1,id);

                resultSet2=statement2.executeQuery();

                while (resultSet2.next()){

                    Long user_id=resultSet2.getLong(1);
                    String first_name=resultSet2.getString(2);
                    String last_name=resultSet2.getString(3);
                    String username=resultSet2.getString(4);
                    String password=resultSet2.getString(5);

                    Utilizator user=new Utilizator(first_name,last_name,username,password);
                    user.setId(user_id);

                    to_users.add(user);

                }

                resultSet2.close();

                //connection.close();

                Utilizator this_one=user_repo.findOne(msg_id);

                the_message=new Message(this_one,to_users,msg_message,msg_datetime);
                the_message.setId(aLong);


                Message reply_message=null;

                if(id_reply!=0){



                //  resultSet1.close();

                  statement1.setInt(1, (int) id_reply);

                  resultSet3=statement1.executeQuery();

                  if(resultSet3.next()) {
                      Long msg_reply_id = resultSet3.getLong("id_user");
                      LocalDateTime msg_reply_datetime = resultSet3.getObject("data", LocalDateTime.class);
                      String msg_reply_message = resultSet3.getString("message");

                      reply_message = new Message(null, null, msg_reply_message, msg_reply_datetime);

                      reply_message.setId(id_reply);


                  }

                }

                the_message.setReply(reply_message);

            }

            resultSet1.close();



        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return the_message;
    }

    @Override
    public Iterable<Message> findAll() {


        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");

                Message new_message=findOne(id);
                messages.add(new_message);
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return null;
    }

    private Long get_suitable_message_id(){

        List<Long> message_ids = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");

                message_ids.add(id);
            }

            Long best_id=0L;

            for(Long id : message_ids){

                if(id>best_id){

                    best_id=id;
                }
            }

            best_id++;

            return best_id;

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return 0L;
    }

    private int get_suitable_id(){

        List<Long> message_ids = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT id from to_users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");

                message_ids.add(id);
            }

            Long best_id=0L;

            for(Long id : message_ids){

                if(id>best_id){

                    best_id=id;
                }
            }

            best_id++;

            return best_id.intValue();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return 0;
    }

    @Override
    public Message save(Message entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");


        Long id=get_suitable_message_id();

        entity.setId(id);

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("INSERT INTO messages(id,id_user,data,message) VALUES (?,?,?,?)");
                PreparedStatement statement1=connection.prepareStatement("INSERT INTO to_users(id,id_message,id_user) VALUES(?,?,?)")
        ){


            statement.setInt(1,entity.getId().intValue());
            statement.setInt(2,entity.getFrom().getId().intValue());
            statement.setObject(3,entity.getData());
            statement.setString(4,entity.getMessage());


            statement.executeUpdate();

            List<Utilizator> utilizatorList=entity.getTo();

            int best_id=get_suitable_id();

            for( Utilizator utilizator: utilizatorList){

                statement1.setInt(1,best_id);
                statement1.setInt(2,entity.getId().intValue());
                statement1.setInt(3,utilizator.getId().intValue());

                statement1.executeUpdate();

                best_id++;
            }

            return null;

        }
        catch (SQLException e){

            e.printStackTrace();
        }


        return entity;
    }

    @Override
    public Message delete(Long aLong) {


        if (aLong<0)
            throw new IllegalArgumentException("Id must not be a negative or neutral number");


        Message message=findOne(aLong);

        if(message==null)
            throw new RepoException("User does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("DELETE FROM messages WHERE id=?")

        ){


            statement.setInt(1, aLong.intValue());

            if (statement.executeUpdate() == 1) {

                return message;
            }


        }
        catch (SQLException e){

            e.printStackTrace();
        }


        return null;
    }

    @Override
    public Message update(Message entity) {

        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");


        Message message=findOne(entity.getId());

        if(message==null)
            throw new RepoException("User does not exist");

        try(
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("UPDATE messages SET id_reply=? WHERE id=?;")

        ){
            //statement.setString(1,entity.getMessage());
            statement.setInt(1,entity.getReply().getId().intValue());
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
    public Page<Message> findConversation(Pageable pageable, Long id1, Long id2) {


        String query="select m.id,m.message,m.data,t.id_user from messages m inner join to_users t on m.id = t.id_message and m.id_user=? and t.id_user=? or m.id=t.id_message and m.id_user=? and t.id_user=? order by data limit ? offset ?";


        ResultSet resultSet=null;
        List<Message> list=new ArrayList<>();

        try(

                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select m.id,m.message,m.data,t.id_user,u.first_name,u.last_name from messages m inner join to_users t on\n" +
                        "    m.id = t.id_message and m.id_user=? and t.id_user=? or m.id=t.id_message and m.id_user=? and t.id_user=?\n" +
                        "        inner join users u on m.id_user = u.id\n" +
                        "           order by data\n" +
                        "               limit ? offset ?")

                ){

                statement.setInt(1,id1.intValue());
                statement.setInt(2,id2.intValue());
                statement.setInt(3,id2.intValue());
                statement.setInt(4,id1.intValue());
                statement.setInt(5,pageable.getPageSize());
                statement.setInt(6,pageable.getPageSize()*pageable.getPageNumber());


                resultSet=statement.executeQuery();

                while(resultSet.next()){

                    Long id=resultSet.getLong("id");
                    String message=resultSet.getString("message");
                    LocalDateTime msg_datetime=resultSet.getObject("data",LocalDateTime.class);
                    String first_name=resultSet.getString("first_name");
                    String last_name=resultSet.getString("last_name");

                    Utilizator utilizator=new Utilizator(first_name,last_name);


                    Message message1=new Message(utilizator,message,msg_datetime);

                    list.add(message1);
                }


                Stream<Message> messageStream=list.stream();

                return new PageImplementation<>(pageable,messageStream);

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }


        return null;
    }

    @Override
    public List<Message> get_messages_in_a_period(Long id, LocalDate date1, LocalDate date2) {


        ResultSet resultSet=null;
        List<Message> list=new ArrayList<>();

        try(

                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select m.id,m.message,m.data,t.id_user,u.first_name,u.last_name from messages m\n" +
                        "    inner join to_users t on m.id = t.id_message  and t.id_user=?\n" +
                        "            inner join users u on m.id_user = u.id\n" +
                        "                    where data>=? and data<=? \n" +
                        "                order by data")

        ){

            statement.setInt(1,id.intValue());
            statement.setObject(2, date1.atStartOfDay());
            statement.setObject(3,date2.atStartOfDay());



            resultSet=statement.executeQuery();

            while(resultSet.next()){

                Long the_id=resultSet.getLong("id");
                String message=resultSet.getString("message");
                LocalDateTime msg_datetime=resultSet.getObject("data",LocalDateTime.class);
                String first_name=resultSet.getString("first_name");
                String last_name=resultSet.getString("last_name");

                Utilizator utilizator=new Utilizator(first_name,last_name);


                Message message1=new Message(utilizator,message,msg_datetime);

                list.add(message1);
            }


            return list;

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }


        return null;

    }

    @Override
    public List<Message> get_friend_messages_in_a_period(Long id, Long id_friend, LocalDate date1, LocalDate date2) {

        ResultSet resultSet=null;
        List<Message> list=new ArrayList<>();

        try(

                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("select m.id,m.message,m.data,t.id_user,u.first_name,u.last_name from messages m\n" +
                        "    inner join to_users t on m.id = t.id_message and m.id_user=?  and t.id_user=?\n" +
                        "            inner join users u on m.id_user = u.id\n" +
                        "                    where data>? and data<?\n" +
                        "                order by data")

        ){


            statement.setInt(1,id_friend.intValue());
            statement.setInt(2,id.intValue());
            statement.setObject(3, date1.atStartOfDay());
            statement.setObject(4,date2.atStartOfDay());



            resultSet=statement.executeQuery();

            while(resultSet.next()){

                Long the_id=resultSet.getLong("id");
                String message=resultSet.getString("message");
                LocalDateTime msg_datetime=resultSet.getObject("data",LocalDateTime.class);
                String first_name=resultSet.getString("first_name");
                String last_name=resultSet.getString("last_name");

                Utilizator utilizator=new Utilizator(first_name,last_name);


                Message message1=new Message(utilizator,message,msg_datetime);

                list.add(message1);
            }


            return list;

        } catch (Exception throwables) {
            throwables.printStackTrace();
        }


        return null;

    }

    @Override
    public Long get_conversation_lines_number(Long id1, Long id2) {
        Long counter=null;

        ResultSet resultSet=null;


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT count(*) from messages inner join to_users tu on messages.id = tu.id_message " +
                     "where messages.id_user=? and tu.id_user=? or messages.id_user=? and tu.id_user=? ");
        ) {


            statement.setInt(1,id1.intValue());
            statement.setInt(2,id2.intValue());
            statement.setInt(3,id2.intValue());
            statement.setInt(4,id1.intValue());

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
