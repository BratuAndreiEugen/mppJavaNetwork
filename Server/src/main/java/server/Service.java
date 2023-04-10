package server;


import exceptions.ValidationException;
import model.ConcertArtist;
import model.User;
import persistence.repository.dbRepository.ConcertArtistDBRepository;
import persistence.repository.dbRepository.UserDBRepository;
import utils.Observable;
import utils.Observer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Service implements Observable {
    private List<Observer> obs=new ArrayList<>();

    private ConcertArtistDBRepository concertRepo;
    private UserDBRepository userRepo;

    public Service(ConcertArtistDBRepository concertRepo, UserDBRepository userRepo) {
        this.concertRepo = concertRepo;
        this.userRepo = userRepo;
    }

    public User login(String data, String password){
        User u = null;
        if(data.contains("@")) {
            u = userRepo.login(new User(data, ""), true, password);
        }else {
            u = userRepo.login(new User("", data), false, password);
        }
        if(u == null)
            throw new ValidationException("Parola sau nume incorect");
        return u;

    }

    public ConcertArtist updateSeats(ConcertArtist concert, Integer seats){
        ConcertArtist c = null;
        if( concert.getAvbSeats() - seats >= 0) {
            c = new ConcertArtist(concert.getNume(), concert.getData(), concert.getLocation(), concert.getAvbSeats() - seats, concert.getSoldSeats() + seats);
            c.setId(concert.getId());
        }else{
            throw new ValidationException("Nu sunt suficiente locuri");
        }
        ConcertArtist c1 = concertRepo.update(c);
        notifyObservers();
        return c1;
    }

    public ConcertArtist getOne(Integer id){
        return concertRepo.findOne(id);
    }

    public Iterable<ConcertArtist> getAllConcerts(){
        return concertRepo.findAll();
    }

    public Iterable<ConcertArtist> getAllByArtist(String artist){
        return concertRepo.findByArtist(artist);
    }

    public Iterable<ConcertArtist> getAllByDay(LocalDate date){
        return concertRepo.findByDay(date);
    }

    public ConcertArtistDBRepository getConcertRepo() {
        return concertRepo;
    }

    public void setConcertRepo(ConcertArtistDBRepository concertRepo) {
        this.concertRepo = concertRepo;
    }

    public UserDBRepository getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserDBRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void addObserver(Observer e) {
        obs.add(e);
    }

    @Override
    public void removeObserver(Observer e) {
        obs.remove(e);
    }

    @Override
    public void notifyObservers() {
        obs.stream().forEach(x->x.update());
    }
}
