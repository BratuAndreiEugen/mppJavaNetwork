package utils;

import model.ConcertArtist;

public interface IObserver {
    void seatsWereUpdated(Iterable<ConcertArtist> all, Iterable<ConcertArtist> filter, ConcertArtist updated);
}
