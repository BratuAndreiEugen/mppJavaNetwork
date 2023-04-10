package persistence.repository;

import model.ConcertArtist;

import java.time.LocalDate;

public interface ConcertArtistRepository extends Repository<Integer, ConcertArtist> {
    public Iterable<ConcertArtist> findByArtist(String artist);

    public Iterable<ConcertArtist> findByDay(LocalDate day);
}
