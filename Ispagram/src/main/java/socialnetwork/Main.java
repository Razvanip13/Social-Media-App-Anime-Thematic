package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.friendship.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.public_events.PublicEvent;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.PrietenieFileRepository;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.UserInterface;

import java.time.LocalDate;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {


        String fileName= ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");

        String friendsFile=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friends");


        final String url=ApplicationContext.getPROPERTIES().getProperty("database.url");
        final String user = ApplicationContext.getPROPERTIES().getProperty("database.user");
        final String password = ApplicationContext.getPROPERTIES().getProperty("database.password");


        //repo
        //Repository<Long,Utilizator> userFileRepository = new UtilizatorFile(fileName, new UtilizatorValidator());

       //Repository<Tuple<Long,Long>, Prietenie> friendsFileRepository= new PrietenieFileRepository(friendsFile,new PrietenieValidator());

        UtilizatorDbRepository utilizatorDbRepository=new UtilizatorDbRepository(url,user,password);

        FriendshipBDRepository friendshipBDRepository=new FriendshipBDRepository(url,user,password,new PrietenieValidator());

        MessagesBDRepository messagesBDRepository=new MessagesBDRepository(url,user,password,utilizatorDbRepository);

        FriendRequestBDRepository requestBDRepository=new FriendRequestBDRepository(url,user,password);

        //service

        UtilizatorService utilizatorService=new UtilizatorService(utilizatorDbRepository);

        MessengerService messengerService=new MessengerService(utilizatorDbRepository,messagesBDRepository);

        FriendshipService friendshipService=new FriendshipService(utilizatorDbRepository,friendshipBDRepository, requestBDRepository);

        //UI
        UserInterface userInterface=new UserInterface(utilizatorService, messengerService, friendshipService);
      // userInterface.run();


        PublicEventsDBRepository publicEventsDBRepository=new PublicEventsDBRepository(url,user,password);



      MainApp.main(args);

    }
}


