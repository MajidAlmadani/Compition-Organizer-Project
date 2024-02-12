import java.util.ArrayList;

public class Participation{
    
    protected String major;
    protected String rank;
    protected String studentID;
    protected String studentName;
    
    public Participation(String major, String rank, String studentID, String studentName) {
        this.major = major;
        this.rank = rank;
        this.studentID = studentID;
        this.studentName = studentName;
    }

    public String getMajor(){
        return major;
    }
    public String getRank(){
        return rank;
    }
    public String getStudentID(){
        return studentID;
    }
    public String getStudentName(){
        return studentName;
    }
    public void setMajor(String major){
        this.major = major;
    }
    public void setRank(String rank){
        this.rank = rank;
    }
    public void setStudentID(String studentID){
        this.studentID = studentID;
    }
    public void setStudentName(String studentName){
        this.studentName = studentName;
    }
    
}
