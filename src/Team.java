public class Team extends CompetitionInformation{
    protected String[] majors;
    protected String rank;
    protected String[] studentsID;
    protected String[] studentsName;
    protected String team;
    protected String teamName;

    public String[] getMajors(){
        return majors;
    }
    public String getRank(){
        return rank;
    }
    public String[] getStudentsID(){
        return studentsID;
    }
    public String[] getStudentsName(){
        return studentsName;
    }
    public String getTeam(){
        return team;
    }
    public String getTeamName(){
        return teamName;
    }
    public void setMajors(String[] majors){
        this.majors = majors;
    }
    public void setRank(String rank){
        this.rank = rank;
    }
    public void setStudentsID(String[] studentsID){
        this.studentsID = studentsID;
    }
    public void setStudentsName(String[] studentsName){
        this.studentsName = studentsName;
    }
    public void setTeam(String team){
        this.team = team;
    }
    public void setTeamName(String teamName){
        this.teamName = teamName;
    }


}
