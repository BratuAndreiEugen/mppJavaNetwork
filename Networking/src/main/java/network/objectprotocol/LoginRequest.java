package network.objectprotocol;

import model.User;

public class LoginRequest implements Request{
    private String data;

    private String password;

    public LoginRequest(String data, String password) {
        this.data = data;
        this.password = password;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
