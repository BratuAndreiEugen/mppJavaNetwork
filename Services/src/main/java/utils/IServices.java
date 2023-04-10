package utils;

import model.ConcertArtist;
import model.User;

import java.time.LocalDate;

public interface IServices {
    User login(String data, String password ,IObserver client) throws Exception;

    void logout(User u, IObserver obs) throws Exception;

    Iterable<ConcertArtist> getAllConcerts() throws Exception;

    Iterable<ConcertArtist> getAllByDay(LocalDate parse) throws Exception;

    ConcertArtist updateSeats(ConcertArtist concert, Integer seats) throws Exception;
}
