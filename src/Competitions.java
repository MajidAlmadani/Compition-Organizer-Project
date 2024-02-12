import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Competitions {
    protected ArrayList<String> competitionsName;
    protected ArrayList<String> competitionsDates;
    protected ArrayList<String> competitionsLinks;
    protected ArrayList<ArrayList<String[]>> participations;
    protected int numberofCompetitions;

    public Competitions() {
        competitionsName = new ArrayList<String>();
        competitionsDates = new ArrayList<String>();
        competitionsLinks = new ArrayList<String>();
        participations = new ArrayList<ArrayList<String[]>>();
        numberofCompetitions = 0;
    }

    public void addParticipation(String name,String[] participationInformation) throws IOException{
        readExcel();
        int count =0;
        for(int i=0;i<competitionsName.size();i++){
            if(competitionsName.get(i).equals(name)){
                participations.get(i).add(participationInformation);
                count = i;
                break;
            }
        }
        File file = new File("Competitions Participations.xlsx");
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(count);
        sheet.createRow(5+participations.get(count).size()).createCell(0).setCellValue(participations.get(count).size()+1);
        for(int i = 0; i<participationInformation.length;i++){
            sheet.createRow(5+participations.get(count).size()).createCell(i+1).setCellValue(participationInformation[i]);
        }
        fis.close();
        wb.close();

        System.out.println("Added Successfully");
        updateExcelFile();
    }

    public void editParticipantSolo(String competitionName,String[] studentInfo,String name,String id,String major) throws IOException{
        readExcel();
        for(int k=0; k<competitionsName.size();k++){
            if(competitionsName.get(k).equals(competitionName)){
                for(int i=0;i<participations.size();i++){
                    String[] temp = participations.get(k).get(i);
                    if(temp[1].equals(studentInfo[0])){
                        temp[0] = id;
                        temp[1] = name;
                        temp[2] = major;
                        break;
                    }
                }
            }
        }
        updateExcelFile();
    }
    public void editParticipantTeam(String competitionName,String[] studentInfo,String name,String id,String major,String teamName) throws IOException{
        readExcel();
        for(int k=0; k<competitionsName.size();k++){
            if(competitionsName.get(k).equals(competitionName)){
                for(int i=0;i<participations.size();i++){
                    String[] temp = participations.get(k).get(i);
                    if(temp[1].equals(studentInfo[0])){
                        temp[0] = id;
                        temp[1] = name;
                        temp[2] = major;
                        temp[4] = teamName;
                        break;
                    }
                }
            }
        }
        updateExcelFile();
    }
    
    public ArrayList<String> checkFinishedDate() throws ParseException{
        CompetitionInformation compInfo;
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> dates = new ArrayList<String>();
        for(int i=0;i<participations.size();i++){
            for(int j=0;j<participations.get(i).size();j++){
                String[] temp = participations.get(i).get(j);
                if(temp.length == 6){
                    if(temp[5].equals("-")){
                        result.add(competitionsName.get(i));
                        dates.add(competitionsDates.get(i));
                        break;
                    }
                }
                else{
                    if(temp[3].equals("-")){
                        result.add(competitionsName.get(i));
                        dates.add(competitionsDates.get(i));
                        break;
                    }
                }
            }
        }
        for(int i = 0; i < result.size(); i++){
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String nowDate = dateFormat.format(new Date());
            if(!(nowDate.compareTo(dates.get(i))>=0)){
                result.remove(result.get(i));
                dates.remove(dates.get(i));
            }
        }
        return result;
    }

    public void changeRank(String competitionName, String studentName, String updatedRank) throws IOException{
        readExcel();
        for(int i=0;i<competitionsName.size();i++){
            if(competitionsName.get(i).equals(competitionName)){
                for(int j=0;j<participations.get(i).size();j++){
                    if(participations.get(i).get(j).length == 6){
                        if(participations.get(i).get(j)[1].equals(studentName)) participations.get(i).get(j)[5] = updatedRank;
                    }
                    else{
                        if(participations.get(i).get(j)[1].equals(studentName)) participations.get(i).get(j)[3] = updatedRank;
                    }
                }
            }
        }
        
        updateExcelFile();
    }

    public void createNewCompetition(String competitionName, String competitionLink, LocalDate localDate, ArrayList<String[]> participants) throws IOException{
        readExcel();
        competitionsName.add(competitionName);
        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        competitionsDates.add(formattedDate);
        competitionsLinks.add(competitionLink);
        participations.add(participants);
        numberofCompetitions++;
        updateExcelFileForCreate();
        CompetitionInformation compInfo = selectCompetition(competitionName);
        if(participants.get(0).length == 6){
            compInfo.teamOrSolo = true;
        } 
    }

    public ArrayList<String[]> changeTeamNumber(ArrayList<String[]> participants){
        String[] temp = participants.get(0);
        String countingString = "";
        countingString = temp[4];
        int count = 0;
        ArrayList<String> teamNames = new ArrayList<String>();
        for(int i=0; i<participants.size(); i++){
            String[] tmp = participants.get(i);
            if(tmp[4].equals(countingString)) count++;
            if(!(teamNames.contains(tmp[4]))) teamNames.add(tmp[4]);
        }
        ArrayList<String[]> result = new ArrayList<String[]>();
        for(int i=0;i<teamNames.size();i++){
            String teamName = teamNames.get(i);
            for(int j=0; j<participants.size(); j++){
                String[] tmp = participants.get(j);
                if(tmp[4].equals(teamName)){
                    tmp[3] = String.valueOf(i+1);
                    result.add(tmp);
                } 
            }
        }
        return result;
    }

    public void sendingMesseges(String competitionName) throws IOException {
        readExcel();
        CompetitionInformation compInfo = selectCompetition(competitionName);

        ArrayList<String> studensmail = new ArrayList<String>();
        ArrayList<String> studentname = new ArrayList<String>();
        ArrayList<String> rankStudent = new ArrayList<String>();
        int count=0;
        for(int i=0; i<competitionsName.size(); i++){
            if(competitionsName.get(i).equals(competitionName)){
                count = i;
                break;
            }
        }
        for(int j =0;j<participations.get(count).size();j++){
            String[] names = participations.get(count).get(j);
            studensmail.add("S"+names[0]+"@kfupm.edu.sa");
            if(names.length==6) {
        	    studentname.add(names[1]+"/"+names[4]);
                rankStudent.add(names[5]);
            }
            else {
                studentname.add(names[1]);
                rankStudent.add(names[3]);
            }
        }

        String stringContainer = "";
        String subject = "Congratulation on achieving [their ranking] place in [the competition name]";
	    try (BufferedReader br = new BufferedReader(new FileReader("Email Body template.txt"))){
	        String sCurrentLine;
	        while ((sCurrentLine = br.readLine()) != null) {
                stringContainer += sCurrentLine + "\n";
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } 
	    for (int k = 0; k < studentname.size(); k++) {
            String templateMessage = stringContainer;
            String templateSubject = subject;
            templateMessage = templateMessage.replace("[Student name/Team name]", studentname.get(k)).replace("[Competition name]",competitionName);
            templateSubject = templateSubject.replace("[their ranking]",rankStudent.get(k));
            templateSubject = templateSubject.replace("[the competition name]",competitionName);

            final String username = "";
            final String password = "";
            String fromEmail = "";
            String toEmail = studensmail.get(k);
            
            
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            
            Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username,password);
                }
            });
            MimeMessage msg = new MimeMessage(session);
            try {
                msg.setFrom(new InternetAddress(fromEmail));
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
                msg.setSubject(templateSubject);
                
                Multipart emailContent = new MimeMultipart();
                
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText(templateMessage);
                emailContent.addBodyPart(textBodyPart);
                
                msg.setContent(emailContent);
                
                Transport.send(msg);
                System.out.println("Sent message");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
		}
    }






    public void readExcel() throws IOException{
        File file = new File("Competitions Participations.xlsx");
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        numberofCompetitions = 0;
        int numberofsheet=wb.getNumberOfSheets();
        competitionsName.clear();
        competitionsDates.clear();
        competitionsLinks.clear();
        participations.clear();
        for(int number=0; number<numberofsheet; number++){
            ArrayList car = new ArrayList();
            numberofCompetitions++;
            XSSFSheet sheet = wb.getSheetAt(number);
            Iterator iterator= sheet.iterator();
            while(iterator.hasNext()){
                XSSFRow row =(XSSFRow) iterator.next();
                
                Iterator cellIterator = row.cellIterator();
                while(cellIterator.hasNext()){
                XSSFCell cell = (XSSFCell) cellIterator.next();
                
                
                switch(cell.getCellType()){
                case STRING:
                car.add(cell.getStringCellValue()); break;
                case NUMERIC:
                car.add(cell.getRawValue());break;
                case BOOLEAN: 
                car.add(cell.getBooleanCellValue());break;
                }
                }
                }
                wb.close();
                fis.close();
                
                String competitionName = "";
                String competitionLink = "";
                String competitionDate = "";
                ArrayList<String[]> participants = new ArrayList<String[]>();
                participants.clear();
                int count = 1;
                boolean teamOrSolo = false;
                for (int i = 0; i < car.size(); i++) {
                    String temp = (String) car.get(i);
                    if(temp.equals("Competition Name")){
                        competitionName += car.get(i+1);
                        i++;
                    }
                    else if(temp.equals("Competition Link")){
                        competitionLink += car.get(i+1);
                        i++;
                    }
                    else if(temp.equals("competition date")){
                        String tmp= "";
                        if( sheet.getRow(2).getCell(1).getCellType() == CellType.STRING){
                            tmp = sheet.getRow(2).getCell(1).getStringCellValue();
                        }
                        else{
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
                            tmp = dateFormat.format(sheet.getRow(2).getCell(1).getDateCellValue());
                        }
                        competitionDate += tmp;
                        i++;
                    }
                    else if(temp.equals("team")){
                        teamOrSolo = true;
                    }
                    else if(Character.isDigit(temp.charAt(0))){
                        boolean flag = true;
                        for(int j=1;j<temp.length();j++){
                            if(!Character.isDigit(temp.charAt(j)) & temp.charAt(j) != '.') flag = false;
                        }
                        if(flag && Double.parseDouble(temp) == count){
                            String[] participantsInfo;
                            if(teamOrSolo){
                                participantsInfo = new String[6];
                            }
                            else{
                                participantsInfo = new String[4];
                            }
                            
                            for(int k =0; k<participantsInfo.length;k++){
                                participantsInfo[k] = (String) car.get(i+1);
                                i++;
                            }
                            count++;
                            participants.add(participantsInfo);
                        }
                    }
                    
                    
                }
                competitionsName.add(competitionName);
                competitionsDates.add(competitionDate);
                competitionsLinks.add(competitionLink);
                participations.add(participants);
                car.clear();
        }
    }

    public void printAllInformations() throws ParseException{
        for(int i=0; i<competitionsName.size(); i++){
            System.out.println("The name of the competition is " + competitionsName.get(i));
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            System.out.println("The Link of the competition is " + competitionsLinks.get(i));
            System.out.println("The Date of the competition is " + competitionsDates.get(i));
            for(int j =0;j<participations.get(i).size();j++){
                String[] names = participations.get(i).get(j);
                for(int k =0;k<names.length;k++){
                    System.out.print(names[k] + " ");
                }
                System.out.println();
            }
        }
    }
    public CompetitionInformation selectCompetition(String competitionNameSelected) throws IOException{
        int count = 0;
        readExcel();
        for(int i=0;i<competitionsName.size();i++){
            if(competitionsName.get(i).equals(competitionNameSelected)){
                count = i;
                break;
            } 
        }
        boolean teamOrSolo = false;
        if(participations.get(count).get(0).length == 6) teamOrSolo = true;

        return new CompetitionInformation(competitionNameSelected,competitionsLinks.get(count),competitionsDates.get(count),participations.get(count),teamOrSolo);
    }
    public ArrayList<String> getCompetitionsviewCompetitions(){
        return competitionsName;
    }
    
    public void updateExcelFile() throws IOException{
        File file = new File("Competitions Participations.xlsx");
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        while(competitionsName.size()>wb.getNumberOfSheets()) {
            wb.createSheet((competitionsName.get(competitionsName.size()-1)));
            Cell cell2 = wb.getSheetAt(wb.getNumberOfSheets()-1).createRow(0).createCell(1);
            cell2.setCellValue(competitionsName.get(wb.getNumberOfSheets()-1));
            XSSFSheet sheet = wb.getSheetAt(wb.getNumberOfSheets()-1);
            wb.getSheetAt(wb.getNumberOfSheets()-1).createRow(0).createCell(0).setCellValue("Competition Name");
            sheet.createRow(1).createCell(0).setCellValue("Competition Link");
            sheet.createRow(2).createCell(0).setCellValue("competition date");
            sheet.createRow(0).createCell(1).setCellValue(competitionsName.get(competitionsName.size()-1));
            sheet.createRow(1).createCell(1).setCellValue(competitionsLinks.get(competitionsName.size()-1));
            sheet.createRow(2).createCell(1).setCellValue(competitionsDates.get(competitionsName.size()-1));
            for(int j =0;j<participations.get(competitionsName.size()-1).size();j++){
                String[] names = participations.get(competitionsName.size()-1).get(j);
                wb.getSheetAt(wb.getNumberOfSheets()-1).createRow(5+j).createCell(0).setCellValue(j+1);
                String[] menuSolo = {"Student ID","Student Name","Major","Rank"};
                String[] menuTeam = {"Student ID","Student Name","Major","team","Team Name","Rank"};
                for(int k =0;k<names.length;k++){
                    if(names.length == 6){
                        sheet.createRow(4).createCell(1+k).setCellValue(menuTeam[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                    else{
                        sheet.createRow(4).createCell(1+k).setCellValue(menuSolo[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                }
            }
            sheet.autoSizeColumn(participations.get(competitionsName.size()-1).size());
        }
        int count =0;
        while(count<wb.getNumberOfSheets()) {
            Cell cell2 = wb.getSheetAt(count).createRow(0).createCell(1);
            cell2.setCellValue(competitionsName.get(count));
            XSSFSheet sheet = wb.getSheetAt(count);
            sheet.createRow(0).createCell(0).setCellValue("Competition Name");
            sheet.createRow(1).createCell(0).setCellValue("Competition Link");
            sheet.createRow(2).createCell(0).setCellValue("competition date");
            sheet.getRow(0).createCell(1).setCellValue(competitionsName.get(count));
            sheet.getRow(1).createCell(1).setCellValue(competitionsLinks.get(count));
            sheet.getRow(2).createCell(1).setCellValue(competitionsDates.get(count));
            for(int j =0;j<participations.get(count).size();j++){
                String[] names = participations.get(count).get(j);
                wb.getSheetAt(count).createRow(5+j).createCell(0).setCellValue(j+1);
                String[] menuSolo = {"Student ID","Student Name","Major","Rank"};
                String[] menuTeam = {"Student ID","Student Name","Major","team","Team Name","Rank"};
                for(int k =0;k<names.length;k++){
                    if(names.length == 6){
                        sheet.createRow(4).createCell(1+k).setCellValue(menuTeam[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                    else{
                        sheet.createRow(4).createCell(1+k).setCellValue(menuSolo[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                }
            }
            sheet.autoSizeColumn(participations.get(competitionsName.size()-1).size());
            count++;
        }       
        
        for(int number=0; number<competitionsName.size(); number++){
            XSSFSheet sheet = wb.getSheetAt(number);
            Cell cell2Update0 = sheet.getRow(0).createCell(0);
            Cell cell3Update0 = sheet.getRow(1).createCell(0);
            Cell cell4Update0 = sheet.getRow(2).createCell(0);
            Cell cell2Update = sheet.getRow(0).createCell(1);
            Cell cell3Update = sheet.getRow(2).createCell(1);
            Cell cell4Update = sheet.getRow(1).createCell(1);
            cell2Update0.setCellValue("Competition Name");
            cell3Update0.setCellValue("Competition Link");
            cell4Update0.setCellValue("competition date");
            Competitions comp = new Competitions();
            CompetitionInformation compInfo = comp.selectCompetition(competitionsName.get(number));
            cell2Update.setCellValue(competitionsName.get(number));
            cell3Update.setCellValue(competitionsDates.get(number));
            cell4Update.setCellValue(competitionsLinks.get(number));
            String[] menuSolo = {"Student ID","Student Name","Major","Rank"};
            String[] menuTeam = {"Student ID","Student Name","Major","team","Team Name","Rank"};
            for(int j =0;j<participations.get(number).size();j++){
                String[] names = participations.get(number).get(j);
                if(compInfo.teamOrSolo){
                    Cell cell5Update0 = wb.getSheetAt(number).getRow(5+j).createCell(0);
                    cell5Update0.setCellValue(j+1);
                    for(int k =0;k<names.length-1;k++){
                        Cell cell5UpdateName = wb.getSheetAt(number).getRow(4).createCell(1+k);
                        cell5UpdateName.setCellValue(menuTeam[k]);
                        Cell cell5Update = wb.getSheetAt(number).getRow(5+j).createCell(1+k);
                        cell5Update.setCellValue(names[k]);
                    }
                }
                else{
                    Cell cell5Update0 = wb.getSheetAt(number).getRow(5+j).createCell(0);
                    cell5Update0.setCellValue(j+1);
                    for(int k =0;k<names.length-1;k++){
                        Cell cell5UpdateName = wb.getSheetAt(number).getRow(4).createCell(1+k);
                        cell5UpdateName.setCellValue(menuSolo[k]);
                        Cell cell5Update = wb.getSheetAt(number).getRow(5+j).createCell(1+k);
                        cell5Update.setCellValue(names[k]);
                }
                }
            }
        }
        fis.close();
        FileOutputStream fileOut2 = new FileOutputStream("Competitions Participations.xlsx");
        wb.write(fileOut2);
        wb.close();
    }

    public void updateExcelFileForCreate() throws IOException{
        File file = new File("Competitions Participations.xlsx");
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        while(competitionsName.size()>wb.getNumberOfSheets()) {
            wb.createSheet((competitionsName.get(competitionsName.size()-1)));
            Cell cell2 = wb.getSheetAt(wb.getNumberOfSheets()-1).createRow(0).createCell(1);
            cell2.setCellValue(competitionsName.get(wb.getNumberOfSheets()-1));
            XSSFSheet sheet = wb.getSheetAt(wb.getNumberOfSheets()-1);
            wb.getSheetAt(wb.getNumberOfSheets()-1).createRow(0).createCell(0).setCellValue("Competition Name");
            sheet.createRow(1).createCell(0).setCellValue("Competition Link");
            sheet.createRow(2).createCell(0).setCellValue("competition date");
            sheet.createRow(0).createCell(1).setCellValue(competitionsName.get(competitionsName.size()-1));
            sheet.createRow(1).createCell(1).setCellValue(competitionsLinks.get(competitionsName.size()-1));
            sheet.createRow(2).createCell(1).setCellValue(competitionsDates.get(competitionsName.size()-1));
            for(int j =0;j<participations.get(competitionsName.size()-1).size();j++){
                String[] names = participations.get(competitionsName.size()-1).get(j);
                wb.getSheetAt(wb.getNumberOfSheets()-1).createRow(5+j).createCell(0).setCellValue(j+1);
                String[] menuSolo = {"Student ID","Student Name","Major","Rank"};
                String[] menuTeam = {"Student ID","Student Name","Major","team","Team Name","Rank"};
                for(int k =0;k<names.length;k++){
                    if(names.length == 6){
                        sheet.createRow(4).createCell(1+k).setCellValue(menuTeam[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                    else{
                        sheet.createRow(4).createCell(1+k).setCellValue(menuSolo[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                }
            }
            sheet.autoSizeColumn(participations.get(competitionsName.size()-1).size());
        }
        int count =0;
        while(count<wb.getNumberOfSheets()) {
            Cell cell2 = wb.getSheetAt(count).createRow(0).createCell(1);
            cell2.setCellValue(competitionsName.get(count));
            XSSFSheet sheet = wb.getSheetAt(count);
            sheet.createRow(0).createCell(0).setCellValue("Competition Name");
            sheet.createRow(1).createCell(0).setCellValue("Competition Link");
            sheet.createRow(2).createCell(0).setCellValue("competition date");
            sheet.getRow(0).createCell(1).setCellValue(competitionsName.get(count));
            sheet.getRow(1).createCell(1).setCellValue(competitionsLinks.get(count));
            sheet.getRow(2).createCell(1).setCellValue(competitionsDates.get(count));
            for(int j =0;j<participations.get(count).size();j++){
                String[] names = participations.get(count).get(j);
                wb.getSheetAt(count).getRow(5+j).createCell(0).setCellValue(j+1);
                String[] menuSolo = {"Student ID","Student Name","Major","Rank"};
                String[] menuTeam = {"Student ID","Student Name","Major","team","Team Name","Rank"};
                for(int k =0;k<names.length;k++){
                    if(names.length == 6){
                        sheet.createRow(4).createCell(1+k).setCellValue(menuTeam[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                    else{
                        sheet.createRow(4).createCell(1+k).setCellValue(menuSolo[k]);
                        sheet.createRow(5+j).createCell(1+k).setCellValue(names[k]);
                    }
                }
            }
            sheet.autoSizeColumn(participations.get(competitionsName.size()-1).size());
            count++;
        }       
        
        for(int number=0; number<competitionsName.size(); number++){
            XSSFSheet sheet = wb.getSheetAt(number);
            Cell cell2Update0 = sheet.getRow(0).createCell(0);
            Cell cell3Update0 = sheet.getRow(1).createCell(0);
            Cell cell4Update0 = sheet.getRow(2).createCell(0);
            Cell cell2Update = sheet.getRow(0).createCell(1);
            Cell cell3Update = sheet.getRow(2).createCell(1);
            Cell cell4Update = sheet.getRow(1).createCell(1);
            cell2Update0.setCellValue("Competition Name");
            cell3Update0.setCellValue("Competition Link");
            cell4Update0.setCellValue("competition date");
            Competitions comp = new Competitions();
            CompetitionInformation compInfo = comp.selectCompetition(competitionsName.get(number));
            cell2Update.setCellValue(competitionsName.get(number));
            cell3Update.setCellValue(competitionsDates.get(number));
            cell4Update.setCellValue(competitionsLinks.get(number));
            String[] menuSolo = {"Student ID","Student Name","Major","Rank"};
            String[] menuTeam = {"Student ID","Student Name","Major","team","Team Name","Rank"};
            for(int j =0;j<participations.get(number).size();j++){
                String[] names = participations.get(number).get(j);
                if(names.length == 6){
                    Cell cell5Update0 = wb.getSheetAt(number).getRow(5+j).createCell(0);
                    cell5Update0.setCellValue(j+1);
                    for(int k =0;k<names.length-1;k++){
                        Cell cell5UpdateName = wb.getSheetAt(number).getRow(4).createCell(1+k);
                        cell5UpdateName.setCellValue(menuTeam[k]);
                        Cell cell5Update = wb.getSheetAt(number).getRow(5+j).createCell(1+k);
                        cell5Update.setCellValue(names[k]);
                    }
                }
                else{
                    Cell cell5Update0 = wb.getSheetAt(number).getRow(5+j).createCell(0);
                    cell5Update0.setCellValue(j+1);
                    for(int k =0;k<names.length-1;k++){
                        Cell cell5UpdateName = wb.getSheetAt(number).getRow(4).createCell(1+k);
                        cell5UpdateName.setCellValue(menuSolo[k]);
                        Cell cell5Update = wb.getSheetAt(number).getRow(5+j).createCell(1+k);
                        cell5Update.setCellValue(names[k]);
                }
                }
            }
        }
        fis.close();
        FileOutputStream fileOut2 = new FileOutputStream("Competitions Participations.xlsx");
        wb.write(fileOut2);
        wb.close();
    }
}