package start;

import model.ConcertArtist;
import rest.ServiceException;
import rest.client.ArtistsClient;

public class StartRestClient {
    private final static ArtistsClient client = new ArtistsClient();
    public static void main(String[] args){
        //get one
        show(() -> System.out.println(client.getById(1)));

        // get all
        show(() -> {
            ConcertArtist[] c = client.getAll();
            for(ConcertArtist con : c){
                System.out.println(con);
            }
        });

        ConcertArtist newConcert = new ConcertArtist("TEST_NAME", "2023-12-12 12:00", "TEST_LOC", 200, 0);
        // create
        show(() -> {
            System.out.println(client.create(newConcert));
        });
        ConcertArtist[] c = client.getAll();
        Integer i = 0;
        for(ConcertArtist con : c){
            if(con.getNume().equals("TEST_NAME")){
                i = con.getId();
            }
        }
        Integer finalI = i;
        show(() -> System.out.println(client.getById(finalI)));

        // update
        newConcert.setAvbSeats(newConcert.getAvbSeats() - 10);
        newConcert.setSoldSeats(newConcert.getSoldSeats() + 10);
        show(() ->{
            System.out.println(client.update(finalI, newConcert));
        });

        // delete
        show(() -> {
            System.out.println(client.delete(finalI));
        });
        show(() -> System.out.println(client.getById(finalI)));
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            //  LOG.error("Service exception", e);
            System.out.println("Service exception"+ e);
        }
    }
}
