package repository;

import model.ConcertArtist;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ConcertArtistRepository extends Repository<Integer, ConcertArtist> {
    public Iterable<ConcertArtist> findByArtist(String artist);

    public Iterable<ConcertArtist> findByDay(LocalDate day);
}
