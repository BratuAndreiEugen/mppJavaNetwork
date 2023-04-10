package network.objectprotocol;

import model.ConcertArtist;

public class BuyRequest implements Request{
    private ConcertArtist concert;
    private Integer seats;

    public BuyRequest(ConcertArtist concert, Integer seats) {
        this.concert = concert;
        this.seats = seats;
    }

    public ConcertArtist getConcert() {
        return concert;
    }

    public void setConcert(ConcertArtist concert) {
        this.concert = concert;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }
}
