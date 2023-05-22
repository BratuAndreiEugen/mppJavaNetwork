package rest;

import model.ConcertArtist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import persistence.repository.dbRepository.ConcertArtistDBRepository;

import java.util.List;

//@CrossOrigin
@RestController
@RequestMapping("/app/artists")
public class ConcertArtistController {
    private static final String template = "Hello, %s!";

    @Autowired
    private ConcertArtistDBRepository artRepo;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue = "World") String name){
        return String.format(template, name);
    }

    @RequestMapping( method = RequestMethod.GET)
    public ConcertArtist[] getAll(){
        System.out.println("Get all artists");
        List<ConcertArtist> l = (List<ConcertArtist>) artRepo.findAll();
        return l.toArray(new ConcertArtist[0]);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Integer id){
        System.out.println("Get by id : " + id);
        ConcertArtist c = artRepo.findOne(id);
        if(c == null){
            return new ResponseEntity<String>("Artist not found", HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<ConcertArtist>(c, HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ConcertArtist create(@RequestBody ConcertArtist c){
        System.out.println("Creating user");
        artRepo.save(c);
        c.setId(0);
        return c;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ConcertArtist update(@PathVariable Integer id, @RequestBody ConcertArtist artist){
        System.out.println("Updating user");
        artist.setId(id);
        artRepo.update(artist);
        return artist;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Integer id){
        System.out.println("Deleting user : "+id.toString());
        try{
            artRepo.delete(id);
            return new ResponseEntity<ConcertArtist>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl exception (DELETE )");
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
