package rest.client;

import model.ConcertArtist;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import rest.ServiceException;

import java.util.concurrent.Callable;

public class ArtistsClient {
    public static final String URL = "http://localhost:8080/app/artists";

    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public ConcertArtist[] getAll(){
        return execute(() -> restTemplate.getForObject(URL, ConcertArtist[].class));
    }

    public ConcertArtist getById(Integer id){
        return execute(() -> restTemplate.getForObject(URL + "/" + id.toString(), ConcertArtist.class));
    }

    public ConcertArtist create(ConcertArtist c){
        return execute(() -> restTemplate.postForObject(URL, c, ConcertArtist.class));
    }

    public ConcertArtist update(Integer id, ConcertArtist c){
        execute(() -> {
            restTemplate.put(URL + "/" + id.toString(), c);
            return null;
        });
        return c;
    }

    public ConcertArtist delete(Integer id){
        execute(() -> {
            restTemplate.delete(URL + "/" + id.toString());
            return null;
        });
        return null;
    }
}
