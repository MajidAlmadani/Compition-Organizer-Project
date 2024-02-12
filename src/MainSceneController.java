    
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainSceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML TextField nameTF;
    @FXML TextField passwordTF;
    @FXML TextField emailTF;
    @FXML Label labelOfTheError,remainingRank,competitionNameLabel,rankRangeLabel;
    @FXML ListView<String> listViewCompetitions,listViewParticipantionAdded,listViewParticipation;
    @FXML TextField competitionNameTF,competitionLinkTF,participationNameTf,participationIDTF,participationMajorTF,participationTeamNameTF,rankTF;
    @FXML DatePicker competitionDateTF;
    @FXML TableView<String[]> tableViewParticipantsSolo;
    @FXML private WebEngine engine;
    @FXML private WebView webView;
    ArrayList<String[]> participants = new ArrayList<String[]>();
    static CompetitionInformation compInfo;

    public void switchToLoginScene(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void refreshActionInUpdateWinnerScene(ActionEvent event) throws IOException{
        listViewParticipation.getItems().clear();
        Competitions comp = new Competitions();
        comp.readExcel();
        CompetitionInformation compInfo = comp.selectCompetition(tempName);
        participants = compInfo.getParticipations();
        if(participants.get(0).length == 6){
            for(int i=0; i<participants.size();i++){
                String fomratString = "";
                String[] temp = participants.get(i);
                fomratString += String.format("%s,%s,%s,%s,%s,%s",temp[1],temp[0] ,temp[2],temp[3],temp[4],temp[5]);
                listViewParticipation.getItems().add(fomratString);
            }
        }
        else{
            for(int i=0; i<participants.size();i++){
                String fomratString = "";
                String[] temp = participants.get(i);
                fomratString += String.format("%s,%s,%s,%s",temp[1],temp[0] ,temp[2],temp[3]);
                listViewParticipation.getItems().add(fomratString);
            }
        }
        competitionNameLabel.setText(compInfo.getName());
        rankRangeLabel.setText("The range is from 1 to " + compInfo.participations.size());
    }

    public void updateThisParticipationRank(ActionEvent event) throws IOException{
        String tempStudentName = listViewParticipation.getSelectionModel().getSelectedItem();
        String[] tempInfo = tempStudentName.split(",");
        int count = -1;
        Competitions comp = new Competitions();
        compInfo = comp.selectCompetition(tempName);
        for(int i = 0; i < compInfo.participations.size();i++){
            String[] temp = compInfo.participations.get(i);
            if(compInfo.teamOrSolo){
                if(temp[5].equals("-")){
                    count++;
                }
            }
            else{
                if(temp[3].equals("-")){
                    count++;
                }
            }
        }
        comp.changeRank(tempName,tempInfo[0],rankTF.getText());
        remainingRank.setText("There is " + count + " remaining to assign.");
        if(rankTF.getText() != null){
            compInfo.updateTheWinners(tempInfo[0],rankTF.getText());
        }
        else{
            remainingRank.setText("There is no rank inserted");
        }
    }
    public void finishUpdatingRank(ActionEvent event) throws IOException, ParseException{
        Competitions comp = new Competitions();
        comp.sendingMesseges(tempName);
        switchToMainProgramScene(event);
    }
    public void switchToUpdateWinnerScene(ActionEvent event) throws IOException{
        try{
            root = FXMLLoader.load(getClass().getResource("UpdateWinnerScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            System.out.println();
        }
        
    }
    public void switchToMainProgramScene(ActionEvent event) throws IOException, ParseException{
        Competitions comp = new Competitions();
        comp.readExcel();
        ArrayList<String> result = comp.checkFinishedDate();
        for(int i=0;i<result.size();i++){
            tempName = result.get(i);
            compInfo = comp.selectCompetition(result.get(i));
            switchToUpdateWinnerScene(event);
        }
        try{
            root = FXMLLoader.load(getClass().getResource("ProgramMainScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println();
        }  
    }
    public void switchToMainProgramSceneAfterCreateAccount(ActionEvent event) throws IOException{
        Competitions comp = new Competitions();
        comp.readExcel();
        root = FXMLLoader.load(getClass().getResource("LoginScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToCreateAccountScene(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("CreateAccountScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToCreateCompetitionSelectionScene(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("CreateCompetitionSelectionScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToCreateCopmetitionTeamScene(ActionEvent event) throws IOException{
        participants.clear();
        root = FXMLLoader.load(getClass().getResource("CreateCompetitionTeamScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToCreateCopmetitionSoloScene(ActionEvent event) throws IOException{
        participants.clear();
        root = FXMLLoader.load(getClass().getResource("CreateCompetitionSoloScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToViewCompetitionScene(ActionEvent event) throws IOException, InterruptedException{
        root = FXMLLoader.load(getClass().getResource("ViewCompetitionScene.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void addingListViewCompetitions(ActionEvent event) throws IOException{
        Competitions comp = new Competitions();
        comp.readExcel();
        ArrayList<String> competitionsName = comp.getCompetitionsviewCompetitions();
        listViewCompetitions.getItems().clear();
        for(int i=0; i<competitionsName.size();i++)listViewCompetitions.getItems().add(competitionsName.get(i));
    }
    public void openCompetitionWebsite(ActionEvent event) throws IOException{
        tempName = listViewCompetitions.getSelectionModel().getSelectedItem();
        if(tempName == null){
            labelOfTheError.setText("You didn't pick a competition.");
        }
        else{
            root = FXMLLoader.load(getClass().getResource("BrowseCompetitionWebsiteScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }   
    }
    public void loadPage(ActionEvent event) throws IOException{
        Competitions comp = new Competitions();
        CompetitionInformation compInfo = comp.selectCompetition(tempName);
        String link = compInfo.getLink();
        engine = webView.getEngine();
        engine.load(link);
    }
    static String tempName = "";
    public void switchToViewParticipantsScene(ActionEvent event) throws IOException{
        tempName = listViewCompetitions.getSelectionModel().getSelectedItem();
        if(tempName == null){
            labelOfTheError.setText("You didn't pick a competition.");
        }
        else{
            Competitions comp = new Competitions();
            CompetitionInformation compInfo = comp.selectCompetition(tempName);
            participants = compInfo.getParticipations();
            if(!compInfo.getTeamOrSolo()){
                root = FXMLLoader.load(getClass().getResource("ViewParticipantsSolo.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else{
                root = FXMLLoader.load(getClass().getResource("ViewParticipantTeam.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }
    }
    public void addParticipantsToTableViewSolo(ActionEvent event) throws IOException{
        listViewParticipation.getItems().clear();
        Competitions comp = new Competitions();
        CompetitionInformation compInfo = comp.selectCompetition(tempName);
        participants = compInfo.getParticipations();
        for(int i=0; i<participants.size();i++){
            String fomratString = "";
            String[] temp = participants.get(i);
            fomratString += String.format("%s,%s,%s,%s",temp[1],temp[0] ,temp[2],temp[3]);
            listViewParticipation.getItems().add(fomratString);
        }
    }
    public void addParticipantsToTableViewTeam(ActionEvent event) throws IOException{
        listViewParticipation.getItems().clear();
        Competitions comp = new Competitions();
        CompetitionInformation compInfo = comp.selectCompetition(tempName);
        participants = compInfo.getParticipations();
        for(int i=0; i<participants.size();i++){
            String fomratString = "";
            String[] temp = participants.get(i);
            fomratString += String.format("%s,%s,%s,%s,%s,%s",temp[1],temp[0] ,temp[2],temp[3],temp[4],temp[5]);
            listViewParticipation.getItems().add(fomratString);
        }
    }
    public void switchToAddParticipantScene(ActionEvent event) throws IOException{
        tempName = listViewCompetitions.getSelectionModel().getSelectedItem();
        if(tempName == null){
            labelOfTheError.setText("You didn't pick a competition.");
        }
        else{
            Competitions comp = new Competitions();
            CompetitionInformation compInfo = comp.selectCompetition(tempName);
            participants = compInfo.getParticipations();
            tempName = compInfo.getName();
            if(!compInfo.getTeamOrSolo()){
                root = FXMLLoader.load(getClass().getResource("AddParticipantSoloScene.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else{
                root = FXMLLoader.load(getClass().getResource("AddParticipantTeamScene.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }       
    }
    public void switchToEditParticipantScene(ActionEvent event) throws IOException{
        tempName = listViewCompetitions.getSelectionModel().getSelectedItem();
        if(tempName == null){
            labelOfTheError.setText("You didn't pick a competition.");
        }
        else{
            Competitions comp = new Competitions();
            CompetitionInformation compInfo = comp.selectCompetition(tempName);
            participants = compInfo.getParticipations();
            if(!compInfo.getTeamOrSolo()){
                root = FXMLLoader.load(getClass().getResource("EditParticipantSoloScene.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else{
                root = FXMLLoader.load(getClass().getResource("EditParticipantTeamScene.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }        
    }
    public void editParticipantSolo(ActionEvent event) throws IOException{
        Competitions comp = new Competitions();
        CompetitionInformation compInfo = comp.selectCompetition(tempName);
        participants = compInfo.getParticipations();
        String tempStudentName = listViewParticipation.getSelectionModel().getSelectedItem();
        String[] tempStudentInfo = tempStudentName.split(",");
        if(participationIDTF.getText() != "" & participationNameTf.getText() != "" & participationMajorTF.getText() != ""){
            compInfo.editParticipantSolo(tempName,tempStudentInfo,participationNameTf.getText(),participationIDTF.getText(),participationMajorTF.getText());
            labelOfTheError.setText("Edited");
        }
        else{
            labelOfTheError.setText("There is uncomplete field");
        }
    }

    public void editParticipantTeam(ActionEvent event) throws IOException{
        Competitions comp = new Competitions();
        CompetitionInformation compInfo = comp.selectCompetition(tempName);
        participants = compInfo.getParticipations();
        String tempStudentName = listViewParticipation.getSelectionModel().getSelectedItem();
        String[] tempStudentInfo = tempStudentName.split(",");
        if(participationIDTF.getText() != "" & participationNameTf.getText() != "" & participationMajorTF.getText() != "" & participationTeamNameTF.getText() !=""){
            compInfo.editParticipantTeam(tempName,tempStudentInfo,participationNameTf.getText(),participationIDTF.getText(),participationMajorTF.getText(),participationTeamNameTF.getText());
            labelOfTheError.setText("Edited");
        }
        else{
            labelOfTheError.setText("There is uncomplete field");
        }
    }

    public void createAccountBtnAction(ActionEvent event) throws IOException{
        Login account = new Login(emailTF.getText(),passwordTF.getText(),nameTF.getText());
        account.CreateAccount();
        switchToLoginScene(event);
    }
    public void checkingTheAccount(ActionEvent event) throws IOException, ParseException{
        Login account = new Login(emailTF.getText(),passwordTF.getText());
        try{
            if(!account.isItRegisteredEmail()){
                labelOfTheError.setText("Wrong Email");
            }
            else if(!account.isItRightPassword()){
                labelOfTheError.setText("Wrong Password");
            }
            else{
                switchToMainProgramScene(event);
            }  
        }catch(FileNotFoundException e){
            labelOfTheError.setText("There is no accounts that saved in this computer, Create a new account");
        }
              
    }
    ArrayList<String[]> part = new ArrayList<String[]>();
    public void createCompetition(ActionEvent event) throws IOException{
        Competitions comp = new Competitions();
        if(competitionNameTF.getText() !="" &competitionLinkTF.getText()!=""& String.valueOf(competitionDateTF.getValue())!=""){
            if(part.get(0).length==6) part = comp.changeTeamNumber(part);
            comp.createNewCompetition(competitionNameTF.getText(),competitionLinkTF.getText(),competitionDateTF.getValue(),part);
            root = FXMLLoader.load(getClass().getResource("ProgramMainScene.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        else{
            labelOfTheError.setText("There is uncomplete field in Competition Information, Please Check again.");
        }
    }
    public void addParticipantSoloForCreate(ActionEvent event) throws IOException{
        String[] temp = new String[4];
        temp[0] = participationIDTF.getText();
        temp[1] = participationNameTf.getText();
        temp[2] = participationMajorTF.getText();
        temp[3] = "-";
        if(temp[0] != "" & temp[1] != "" & temp[2] != ""){
            labelOfTheError.setText("Participant Added Successfully.");
            part.add(temp);
            listViewParticipantionAdded.getItems().add(temp[1]);
        }
        else{
            labelOfTheError.setText("There is uncomplete field, Participant is not added.");
        }
    }

    public void addParticipantTeamForCreate(ActionEvent event) throws IOException{
        String[] temp = new String[6];
        temp[0] = participationIDTF.getText();
        temp[1] = participationNameTf.getText();
        temp[2] = participationMajorTF.getText();
        temp[3] = "";
        temp[4] = participationTeamNameTF.getText();
        temp[5] = "-";
        if(temp[0] != "" & temp[1] != "" & temp[2] != "" & temp[4] !=""){
            labelOfTheError.setText("Participant Added Successfully.");
            part.add(temp);
            listViewParticipantionAdded.getItems().add(temp[1]);
        }
        else{
            labelOfTheError.setText("There is uncomplete field, Participant is not added.");
        }
    }

    public void addParticipantSolo(ActionEvent event) throws IOException{
        String[] temp = new String[4];
        temp[0] = participationIDTF.getText();
        temp[1] = participationNameTf.getText();
        temp[2] = participationMajorTF.getText();
        temp[3] = "-";
        if(temp[0] != "" & temp[1] != "" & temp[2] != ""){
            labelOfTheError.setText("Participant Added Successfully.");
            Competitions comp = new Competitions();
            comp.addParticipation(tempName,temp);
            participants.add(temp);
            listViewParticipantionAdded.getItems().add(temp[1]);
        }
        else{
            labelOfTheError.setText("There is uncomplete field, Participant is not added.");
        }
    }

    public void addParticipantTeam(ActionEvent event) throws IOException{
        String[] temp = new String[6];
        temp[0] = participationIDTF.getText();
        temp[1] = participationNameTf.getText();
        temp[2] = participationMajorTF.getText();
        temp[3] = "";
        temp[4] = participationTeamNameTF.getText();
        temp[5] = "-";
        if(temp[0] != "" & temp[1] != "" & temp[2] != "" & temp[4] !=""){
            labelOfTheError.setText("Participant Added Successfully.");
            Competitions comp = new Competitions();
            comp.addParticipation(tempName,temp);
            participants.add(temp);
            listViewParticipantionAdded.getItems().add(temp[1]);
        }
        else{
            labelOfTheError.setText("There is uncomplete field, Participant is not added.");
        }
    }
}