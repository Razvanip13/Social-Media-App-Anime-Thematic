package socialnetwork.ui;

import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.RepoException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.ServiceException;
import socialnetwork.service.UtilizatorService;

import java.io.*;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.time.Month.*;

public class UserInterface {

    private final UtilizatorService utilizatorService;
    private final MessengerService messengerService;
    private final FriendshipService friendshipService;
    private BufferedReader reader=null;

    public UserInterface(UtilizatorService utilizatorService, MessengerService messengerService, FriendshipService friendshipService) {

        this.utilizatorService = utilizatorService;
        this.messengerService = messengerService;
        this.friendshipService = friendshipService;
    }


    private void menu(){

        System.out.println("Bun venit");
        System.out.println("Scrie 'meniu' pentru afisare meniu");
        System.out.println("1. Adauga utilizator");
        System.out.println("2. Arata utilizatorii");
        System.out.println("3. Sterge utilizator");
        System.out.println("4. Modifica un utilizator");
        System.out.println("5. Adauga o prietenie");
        System.out.println("6. Sterge o prietenie");
        System.out.println("7. Calculeaza cate comunitati exista");
        System.out.println("8. Iti gasesc cea mai mare comunitate");
        System.out.println("9. Afiseaza prietenii");
        System.out.println("10. Afiseaza prietenii unui utilizator");
        System.out.println("11. Afiseaza prietenii unui utlizator oficializati intr-o anumita luna");
        System.out.println("12. Trimite un mesaj");
        System.out.println("13. Afiseaza mesajele la care nu a raspuns un utilizator");
        System.out.println("14. Raspunde la un mesaj");
        System.out.println("15. Arata conversatia intre doi utilizatori");
        System.out.println("16. Trimite o cerere");
        System.out.println("17. Accepta o cerere");
        System.out.println("18. Respinge o cerere");
        System.out.println("19. Arata cererile de prietenie ale unui utilizator");
        System.out.println("0. Iesire");
    }

    private void adauga() throws IOException {

        String prenume,nume;

        System.out.print("Prenume:");
        prenume=reader.readLine().trim();


        System.out.print("Nume:");
        nume=reader.readLine().trim();

        if(utilizatorService.addUtilizator(prenume,nume)==null) {


            show_all();

            System.out.println("Utilizator adaugat cu succes ^^");

        }

    }

    public void sterge() throws Exception{


        System.out.print("Id: ");
        Long id=Long.parseLong(reader.readLine().trim());

        if(utilizatorService.deleteUtilizator(id)!=null){

            show_all();
            System.out.println("Utilizator sters cu succes");
        }

    }

    public void modifica() throws Exception{

        long id;
        String prenume,nume;


        System.out.print("Id: ");
        id=Long.parseLong(reader.readLine().trim());

        System.out.print("Prenume: ");
        prenume=reader.readLine().trim();

        System.out.print("Nume: ");
        nume=reader.readLine().trim();

        utilizatorService.updateUtilizator(id,prenume,nume);

        System.out.println("Utilizator modificat cu succes ^^");
        show_all();
    }

    public void add_Prietenie() throws Exception{

        long id1;
        long id2;

        System.out.print("Primul:");
        id1=Long.parseLong(reader.readLine().trim());
        System.out.print("Al doilea: ");
        id2=Long.parseLong(reader.readLine().trim());

        friendshipService.addPrietenie(id1,id2);

        System.out.println("Prietenie oficializata ^^");

    }

    public void show_all() {


        utilizatorService.getAll().forEach(System.out::println);

    }

    public void delete_Prietenie() throws Exception{

        long id1;
        long id2;

        System.out.print("Primul:");
        id1=Long.parseLong(reader.readLine().trim());
        System.out.print("Al doilea: ");
        id2=Long.parseLong(reader.readLine().trim());

        friendshipService.deletePrietenie(id1,id2);
    }

    public void calculate_groups(){


        System.out.println("Numarul de comunitati este "+ friendshipService.getNrComunitati());
    }

    public void show_biggest_group(){

        System.out.print("Cel mai comunicativ grup este ");

        friendshipService.getBiggestCommunity().forEach(x-> System.out.print(x+" "));
        System.out.println();
    }

    public void show_friendships(){

        friendshipService.getAllFriends().forEach(System.out::println);
    }

    public void show_user_friends() throws IOException {


        System.out.print("Id: ");
        Long id=Long.parseLong(reader.readLine().trim());


        friendshipService
                .get_user_friends(id)
                .forEach(friend->{

                    System.out.println(friend.getFirstName()+" | "+ friend.getLastName()+" | "+ friend.getFriendshipDate());

                });


    }


    private Month set_month(String value) throws Exception {


        Month month;
        switch (value) {
            case "1" -> month = JANUARY;
            case "2" -> month = FEBRUARY;
            case "3" -> month = MARCH;
            case "4" -> month = APRIL;
            case "5" -> month = MAY;
            case "6" -> month = JUNE;
            case "7" -> month = JULY;
            case "8" -> month = AUGUST;
            case "9" -> month = SEPTEMBER;
            case "10" -> month = OCTOBER;
            case "11" -> month = NOVEMBER;
            case "12" -> month = DECEMBER;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        }

        return month;
    }

    public void show_user_friends_in_a_specific_month() throws Exception {


        System.out.print("Id: ");
        Long id=Long.parseLong(reader.readLine().trim());

        System.out.print("Luna: ");

        String value=reader.readLine().trim();

        Month month=set_month(value);


        friendshipService
                .get_user_friends_in_a_specific_month(id,month)
                .forEach(friend->{

                    System.out.println(friend.getLeft().getFirstName()+" | "+ friend.getLeft().getLastName()+" | "+ friend.getRight());

                });

    }

    public void send_a_message() throws Exception {

        System.out.print("Id utilizator: ");
        Long id=Long.parseLong(reader.readLine().trim());

        System.out.print("Mesaj: ");
        String mesaj=reader.readLine().trim();

        System.out.print("Destinatari: ");
        String destinatari=reader.readLine().trim();

        List<String> recipient_list = new ArrayList<>(Arrays.asList(destinatari.trim().split(",")));

        recipient_list.forEach(recipient->{

            Pattern pattern=Pattern.compile("^[-+]*[0-9]+$");
            Matcher m=pattern.matcher(recipient);

            if(!m.matches()){
                throw  new ServiceException("The list of recipients is invalid");
            }

        });


        List<Long> recipient_ids= recipient_list
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());


       messengerService.send_message(id,mesaj,recipient_ids);

        System.out.println("Mesaj trimis");

    }


    public void show_unreplied_messages() throws Exception{

        System.out.print("Id utilizator: ");
        Long id=Long.parseLong(reader.readLine().trim());

        messengerService.get_users_unreplied_messages(id).forEach(System.out::println);
    }

    public void answer_to_a_message() throws Exception{

        System.out.print("Id message: ");
        Long id_msg=Long.parseLong(reader.readLine().trim());

        System.out.print("Id user: ");
        Long id_user=Long.parseLong(reader.readLine().trim());

        System.out.print("Reply message: ");
        String reply= reader.readLine();

        messengerService.reply_message(id_msg,id_user,reply);

        System.out.println("Raspuns trimis");
    }

    public void show_conversation() throws Exception{

        System.out.print("Id user1: ");
        Long id1=Long.parseLong(reader.readLine().trim());

        System.out.print("Id user2: ");
        Long id2=Long.parseLong(reader.readLine().trim());

        messengerService.show_two_users_conversation(id1,id2).forEach(System.out::println);

    }

    public void send_a_request() throws Exception{

        System.out.print("Id1: ");
        Long id1=Long.parseLong(reader.readLine().trim());

        System.out.print("Id2: ");
        Long id2=Long.parseLong(reader.readLine().trim());

        friendshipService.send_friend_request(id1,id2);

        System.out.println("Cerere trimisa");
    }

    public void accept_a_request() throws Exception{

        System.out.print("Id cerere: ");
        Long id=Long.parseLong(reader.readLine().trim());

        friendshipService.accept_friend_request(id);

        System.out.println("Cere de prietenie acceptata");

    }


    public void reject_a_request() throws Exception{

        System.out.print("Id cerere: ");
        Long id=Long.parseLong(reader.readLine().trim());


        friendshipService.decline_friend_request(id);

        System.out.println("Cere de prietenie respinsa");

    }

    public void show_a_user_friend_requests() throws Exception{

        System.out.print("Id user: ");

        Long id=Long.parseLong(reader.readLine().trim());

        friendshipService.get_friends_requests(id).forEach(System.out::println);

    }


    public void run() throws Exception {

        menu();

        try {

            reader=new BufferedReader(new InputStreamReader(System.in));
            boolean looping=true;

            while (looping) {

                try {
                    System.out.print("Comanda:");
                    String comanda = reader.readLine();

                    switch (comanda) {
                        case "0" -> looping = false;
                        case "1" -> adauga();
                        case "2" -> show_all();
                        case "3" -> sterge();
                        case "4" -> modifica();
                        case "5" -> add_Prietenie();
                        case "6" -> delete_Prietenie();
                        case "7" -> calculate_groups();
                        case "8" -> show_biggest_group();
                        case "9" -> show_friendships();
                        case "10" -> show_user_friends();
                        case "11" -> show_user_friends_in_a_specific_month();
                        case "12" -> send_a_message();
                        case "13" -> show_unreplied_messages();
                        case "14" -> answer_to_a_message();
                        case "15" -> show_conversation();
                        case "16" -> send_a_request();
                        case "17" -> accept_a_request();
                        case "18" -> reject_a_request();
                        case "19" -> show_a_user_friend_requests();
                        case "meniu" -> menu();
                    }
                }
                catch (ValidationException | IllegalArgumentException | ServiceException | RepoException e){

                    System.out.println(e.getMessage());
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            if(reader!=null)
                reader.close();
        }
    }

}
