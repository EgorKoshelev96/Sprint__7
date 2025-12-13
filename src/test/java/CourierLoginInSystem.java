public class CourierLoginInSystem {
    private String login;
    private String password;

    public CourierLoginInSystem(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public CourierLoginInSystem() {

    }
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
