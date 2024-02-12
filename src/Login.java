import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Login {
    protected String emailAddress;
    protected String password;
    protected String name;
    protected int numberLine;

    public Login(String emailAddress, String password,String name){
        this.emailAddress = emailAddress;
        this.password = password;
        this.name = name;
        numberLine = 0;
    }
    public Login(String emailAddress, String password){
        this.emailAddress = emailAddress;
        this.password = password;
        numberLine = 0;
    }
    public boolean isItEmpty() throws FileNotFoundException{
        try{
            File myObj = new File("Accounts.txt");
            Scanner myReader = new Scanner(myObj);
            String[] data = myReader.nextLine().split(";");
            if(data[0].equals("")) return true;
            return false;
        }
        catch(Exception e){
            System.out.println(e);
            return true;
        }
    }

    public String getEmailAddress(){
        return emailAddress;
    }
    public String getPassword(){
        return password;
    }
    public String getName(){
        return name;
    }
    public boolean isItRegisteredEmail() throws FileNotFoundException{
        File myObj = new File("Accounts.txt");
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String[] dataArr = data.split(";");
            if(dataArr[0].equals(getEmailAddress())) return true;
            numberLine++;
        }
        myReader.close();
        return false;
    }
    public boolean isItRightPassword() throws FileNotFoundException{
        File myObj = new File("Accounts.txt");
        Scanner myReader = new Scanner(myObj);
        String data="";
        for(int i=0;i<=numberLine;i++){
            data = myReader.nextLine();
        }
        String[] dataArr = data.split(";");
        for(int i =0;i<dataArr.length;i++)
        if(dataArr[1].equals(getPassword())) return true;
        myReader.close();
        return false;
    }
    public void CreateAccount() throws IOException{
        if(isItEmpty()){
            FileWriter myWriter = new FileWriter("Accounts.txt");
            myWriter.write(getEmailAddress());
            myWriter.write(";");
            myWriter.write(getPassword());
            myWriter.write(";");
            myWriter.write(getName());
            myWriter.close();
        }
        else{
            File myObj = new File("Accounts.txt");
            Scanner myReader = new Scanner(myObj);
            ArrayList<String> data = new ArrayList<String>();
            while (myReader.hasNextLine()) {
                String reading = myReader.nextLine();
                data.add(reading);
                numberLine++;
            }
            myReader.close();
            try {
                FileWriter myWriter = new FileWriter("Accounts.txt");
                for(int i = 0;i<data.size();i++){
                    myWriter.write(data.get(i));
                    myWriter.write("\n");
                } 
                myWriter.write(getEmailAddress());
                myWriter.write(";");
                myWriter.write(getPassword());
                myWriter.write(";");
                myWriter.write(getName());
                myWriter.close();
              } 
              catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
              }
        }
    }
}
