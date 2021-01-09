package apr.autismapp.data;



public class UserPass {


    public String user;
    public String password;

    public UserPass(){}

    public UserPass(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserPass{" + "user=" + user + ", password=" + password + '}';
    }


}
