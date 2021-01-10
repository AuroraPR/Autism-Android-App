package apr.autismapp.data;



public class UserPass {


    public String user;
    public String password;
    public String name;
    public String emergencyNumber;

    public UserPass(){}

    public UserPass(String user, String password, String name, String emergencyNumber) {
        this.user = user;
        this.password = password;
        this.name = name;
        this.emergencyNumber = emergencyNumber;
    }

    @Override
    public String toString() {
        return "UserPass{" + "user=" + user + ", password=" + password + ", name=" + name + ", emergencyNumber=" + emergencyNumber + '}';
    }


}
