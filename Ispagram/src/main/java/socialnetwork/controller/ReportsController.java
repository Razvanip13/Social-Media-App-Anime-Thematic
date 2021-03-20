package socialnetwork.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import socialnetwork.controller.messageboxes.MessageAlert;
import socialnetwork.controller.messageboxes.MessageInformation;
import socialnetwork.controller.reports.FormType;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.domain.pages.UserPage;
import socialnetwork.domain.users.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessengerService;
import socialnetwork.service.UtilizatorService;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportsController {


    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private MessengerService messengerService;
    private Utilizator user;
    private final String FILE = "D:\\Scoala\\RepoLab\\Anul_2\\MAP\\Rapoarte";
    private FormType formType=null;
    private FriendDTO userFriend=null;


    ObservableList<String> reportsModel= FXCollections.observableArrayList();
    ObservableList<FriendDTO> friendsModel =FXCollections.observableArrayList();

    private List<String> reportFriendships=new ArrayList<>();
    private List<String> reportMessages=new ArrayList<>();

    private static final Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,Font.BOLD);
    private static final Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
    private static final Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
    private static final Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


    @FXML
    ListView<String> listViewReport;

    @FXML
    TableView<FriendDTO> tableViewFriends;

    @FXML
    TableColumn<FriendDTO,String> columnFirstName;

    @FXML
    TableColumn<FriendDTO,String> columnLastName;


    @FXML
    DatePicker datePickerBegin;

    @FXML
    DatePicker datePickerEnd;

    @FXML
    Button buttonForm1;

    @FXML
    Button buttonForm2;

    @FXML
    Button buttonSaveForm;

    @FXML
    Button buttonBack;

    @FXML
    Button buttonNext;

    @FXML
    TextField textFieldReportName;
    private UserPage userPage;


    @FXML
    Label labelCountFriends;


    Stage primaryStage;

    public void setService(UtilizatorService utilizatorService, FriendshipService friendshipService, MessengerService messengerService, Utilizator user) {

        this.utilizatorService = utilizatorService;
        this.friendshipService = friendshipService;
        this.messengerService = messengerService;
        this.user = user;
        initModelUsers();
    }


    public void setService(UserPage userPage){

        this.userPage=userPage;

        initModelUsers();
    }

    private void initModelUsers(){

        columnFirstName.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<FriendDTO,String>("lastName"));
       // usersModel.setAll(friendshipService.get_user_friends(user.getId()));

        //friendsModel.setAll(userPage.get_user_friends());

        friendsModel.setAll(userPage.get_report_friends_on_page(0));

        labelCountFriends.setText((userPage.getReports_friends_Page()+1)+"/"+userPage.counting_friends_pages());
    }

    public void initializeLists(){


        listViewReport.setItems(reportsModel);
        tableViewFriends.setItems(friendsModel);

    }


    @FXML
    public void initialize(){

        initializeLists();
    }


    @FXML
    void handleBack(){

        List<FriendDTO> friends=userPage.get_report_friends_on_previous_page();

        if(friends.size()!=0){

            friendsModel.setAll(friends);

            buttonNext.setDisable(false);

            labelCountFriends.setText((userPage.getReports_friends_Page()+1)+"/"+userPage.counting_friends_pages());

        }
        else{

            buttonBack.setDisable(true);
        }
    }

    @FXML
    void handleNext(){

        List<FriendDTO> friends=userPage.get_report_friends_on_next_page();

        if(friends.size()!=0){

            friendsModel.setAll(friends);

            buttonBack.setDisable(false);

            labelCountFriends.setText((userPage.getReports_friends_Page()+1)+"/"+userPage.counting_friends_pages());
        }
        else{

            buttonNext.setDisable(true);
        }

    }


    @FXML
    public void handleForm1(){

        try {


            LocalDate beginValue = datePickerBegin.getValue();

            LocalDate endValue = datePickerEnd.getValue();



            reportsModel.clear();
            reportFriendships.clear();


            reportFriendships=userPage.get_friends_in_a_period(beginValue,endValue);

            reportsModel.add("Friendships in this period: ");

            reportsModel.addAll(reportFriendships);

            reportsModel.add("");
            reportsModel.add("");
            reportsModel.add("Messages in this period: ");



            reportMessages=userPage.get_messages_in_a_period(beginValue,endValue);


            reportsModel.addAll(reportMessages);


            formType = FormType.New_Friends_and_messages;
        }
        catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());
        }

    }


    @FXML
    public void handleForm2(){

        userFriend=null;

        LocalDate beginValue=datePickerBegin.getValue();

        LocalDate endValue=datePickerEnd.getValue();

        userFriend=tableViewFriends.getSelectionModel().getSelectedItem();




        if(userFriend!=null) {


            try {

                reportsModel.clear();

                reportMessages.clear();




                reportMessages.addAll(userPage.get_friend_messages_in_a_period(beginValue,endValue,userFriend.getId()));




                reportsModel.add("Messages sent by " + userFriend.getFirstName() + " " + userFriend.getLastName() + ":");
                reportsModel.add("");


                reportsModel.addAll(reportMessages);


                formType = FormType.Friend_and_Messages;

            }
            catch (Exception e){

                MessageAlert.showErrorMessage(null,e.getMessage());
            }

        }
        else{

            MessageAlert.showErrorMessage(null,"You didn't select any friend!");
        }
    }

    private void add_emptyLine(Document document,int number) throws DocumentException {


        for(int counter = 0; counter<number; counter++){

            document.add(new Paragraph(""));
        }
    }


    private void add_lines(Document document,List<String> lines){

        lines.forEach(line->{

            try {
                document.add(new Paragraph(line,normalFont));

            } catch (DocumentException e) {
                e.printStackTrace();
            }

        });
    }


    private void new_friends_and_messages_form(String pathing){

        try {
            Document document = new Document();

            String save_path = pathing + "\\" + textFieldReportName.getText()+".pdf";

            PdfWriter.getInstance(document, new FileOutputStream(save_path));


            document.open();


            add_emptyLine(document,1);

            document.add(new Paragraph("Friendships and received messages between "
                    + datePickerBegin.getValue()+" and " +datePickerEnd.getValue(),catFont));


            add_emptyLine(document,3);
            document.add(new Paragraph("User: "+ userPage.getFirstName()+" "+userPage.getLastName(),smallBold));

            document.add(new Paragraph("Friendships: ",smallBold));
            add_emptyLine(document,1);

            add_lines(document,reportFriendships);



            add_emptyLine(document,2);

            document.add(new Paragraph("Messages: ",smallBold));
            document.add(new Paragraph(""));


            add_lines(document,reportMessages);


            document.close();

            MessageInformation.showInformationMessage(null,"Formularul a fost generat ^^");

        }catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

    private void friend_and_messages_form(String pathing){

        try {



            Document document = new Document();

            String save_path = pathing + "\\" + textFieldReportName.getText()+".pdf";

            PdfWriter.getInstance(document, new FileOutputStream(save_path));


            document.open();


            add_emptyLine(document,1);

            document.add(new Paragraph("Recieved messages from a friend between "
                    + datePickerBegin.getValue()+" and " +datePickerEnd.getValue(),catFont));


            add_emptyLine(document,3);
            document.add(new Paragraph("User: "+ userPage.getFirstName()+" "+userPage.getLastName(),smallBold));

            document.add(new Paragraph("Friend: " + userFriend.getFirstName()+" "+userFriend.getLastName(),smallBold));


            add_emptyLine(document,2);

            document.add(new Paragraph("Messages: ",smallBold));
            document.add(new Paragraph(""));

            add_lines(document,reportMessages);


            document.close();

            MessageInformation.showInformationMessage(null,"Formularul a fost generat ^^");

        }catch (Exception e){

            MessageAlert.showErrorMessage(null,e.getMessage());
        }


    }


    @FXML
    public void handleSaveForm(){


        if(textFieldReportName.getText().equals("")){

            MessageAlert.showErrorMessage(null,"You haven't inserted a file name");
            return;
        }

        if(formType == null){

            MessageAlert.showErrorMessage(null,"You haven't genereated any form yet");
            return;
        }


        DirectoryChooser directoryChooser=new DirectoryChooser();

        directoryChooser.setInitialDirectory(new File("D:\\"));

        File selectedDirectory=directoryChooser.showDialog(primaryStage);


        switch (formType) {
            case New_Friends_and_messages -> new_friends_and_messages_form(selectedDirectory.getAbsolutePath());
            case Friend_and_Messages -> friend_and_messages_form(selectedDirectory.getAbsolutePath());
        }
    }
}
