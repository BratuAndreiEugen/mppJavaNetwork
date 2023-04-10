package model;

public class VanzareBilet extends Entity<Integer>{
    private ConcertArtist concert;
    private String buyer;
    private Integer seats; // nr locuri

    public VanzareBilet(ConcertArtist concert, String buyer, Integer seats) {
        this.concert = concert;
        this.buyer = buyer;
        this.seats = seats;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public ConcertArtist getConcert() {
        return concert;
    }

    public void setConcert(ConcertArtist concert) {
        this.concert = concert;
    }
}
