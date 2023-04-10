package network.objectprotocol;

import model.ConcertArtist;

public class UpdateResponse implements Response{

    private ConcertArtist updatedConcert;
    private Iterable<ConcertArtist> allConcerts;
    private Iterable<ConcertArtist> filteredConcerts;


    public UpdateResponse(ConcertArtist updatedConcert, Iterable<ConcertArtist> allConcerts, Iterable<ConcertArtist> filteredConcerts) {
        this.updatedConcert = updatedConcert;
        this.allConcerts = allConcerts;
        this.filteredConcerts = filteredConcerts;
    }

    public Iterable<ConcertArtist> getAllConcerts() {
        return allConcerts;
    }

    public void setAllConcerts(Iterable<ConcertArtist> allConcerts) {
        this.allConcerts = allConcerts;
    }

    public Iterable<ConcertArtist> getFilteredConcerts() {
        return filteredConcerts;
    }

    public void setFilteredConcerts(Iterable<ConcertArtist> filteredConcerts) {
        this.filteredConcerts = filteredConcerts;
    }

    public ConcertArtist getUpdatedConcert() {
        return updatedConcert;
    }

    public void setUpdatedConcert(ConcertArtist updatedConcert) {
        this.updatedConcert = updatedConcert;
    }
}
