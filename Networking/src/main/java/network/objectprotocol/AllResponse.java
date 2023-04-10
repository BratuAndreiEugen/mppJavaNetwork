package network.objectprotocol;

import model.ConcertArtist;

public class AllResponse extends OkResponse{
    Iterable<ConcertArtist> concerts;

    public AllResponse(Iterable<ConcertArtist> concerts) {
        this.concerts = concerts;
    }

    public Iterable<ConcertArtist> getConcerts() {
        return concerts;
    }

    public void setConcerts(Iterable<ConcertArtist> concerts) {
        this.concerts = concerts;
    }
}
