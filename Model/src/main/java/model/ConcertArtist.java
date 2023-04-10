package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConcertArtist extends Entity<Integer>{
    private String nume; // nume artist
    private LocalDateTime data; // data spectacolului
    private String location; // locatie
    private Integer avbSeats; // nr locuri disponibile
    private Integer soldSeats; // nr locuri vandute

    public ConcertArtist(String nume, LocalDateTime data, String location, Integer avbSeats, Integer soldSeats) {
        this.nume = nume;
        this.data = data;
        this.location = location;
        this.avbSeats = avbSeats;
        this.soldSeats = soldSeats;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAvbSeats() {
        return avbSeats;
    }

    public void setAvbSeats(Integer avbSeats) {
        this.avbSeats = avbSeats;
    }

    public Integer getSoldSeats() {
        return soldSeats;
    }

    public void setSoldSeats(Integer soldSeats) {
        this.soldSeats = soldSeats;
    }


    @Override
    public String toString() {
        return "ConcertArtist{" +
                ", nume='" + nume + '\'' +
                ", data=" + data +
                ", location='" + location + '\'' +
                ", avbSeats=" + avbSeats +
                ", soldSeats=" + soldSeats +
                '}';
    }
}
