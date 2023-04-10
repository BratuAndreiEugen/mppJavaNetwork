package network.objectprotocol;

import model.User;

public class LoggedInResponse extends OkResponse{
    private User u;

    public LoggedInResponse(User u) {
        this.u = u;
    }

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }
}
