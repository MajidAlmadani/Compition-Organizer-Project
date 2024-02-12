import java.io.IOException;
import java.util.ArrayList;

public class CompetitionInformation extends Competitions{
    protected String date;
    protected String link;
    protected String name;
    protected ArrayList<String[]> participations;
    protected boolean teamOrSolo;
    public CompetitionInformation(String name,String link,String date, ArrayList<String[]> arrayList,boolean teamOrSolo){
        this.name = name;
        this.date = date;
        this.link = link;
        this.participations = arrayList;
        this.teamOrSolo = teamOrSolo;
    }
    public void addParticipation(String[] participationInformation) throws IOException{
        readExcel();
        participations.add(participationInformation);
        System.out.println("Added Successfully");
        updateExcelFile();
    }
    
    public void browseCompetitionWebsite(String link){

    }
    public String getDate(){
        return date;
    }
    public String getLink(){
        return link;
    }
    public String getName(){
        return name;
    }
    public ArrayList<String[]> getParticipations(){
        return participations;
    }
    public boolean getTeamOrSolo(){
        return teamOrSolo;
    }
    public void sendEmailMessages(){

    }
    public void setDate(String date){
        this.date = date;
    }
    public void setLink(String link){
        this.link = link;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setTeamOrSolo(boolean teamOrSolo){
        this.teamOrSolo = teamOrSolo;
    }
    public void updateTheWinners(String name,String rank){
        for(int i = 0; i <participations.size();i++){
            String[] temp = participations.get(i);
            if(temp[1].equals(name)){
                temp[3] = rank;
            }
        }
    }
}