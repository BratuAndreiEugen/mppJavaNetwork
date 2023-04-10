package server;

import exceptions.ValidationException;
import model.ConcertArtist;
import model.User;
import persistence.repository.dbRepository.ConcertArtistDBRepository;
import persistence.repository.dbRepository.UserDBRepository;
import utils.IObserver;
import utils.IServices;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImpl implements IServices {
    private UserDBRepository userRepo;

    private ConcertArtistDBRepository concertRepo;

    private List<IObserver> loggedClients;

    public ServerImpl(UserDBRepository userRepo, ConcertArtistDBRepository concertRepo) {
        this.userRepo = userRepo;
        this.concertRepo = concertRepo;
        this.loggedClients = new CopyOnWriteArrayList<>();
    }

    @Override
    public synchronized User login(String data, String password, IObserver client){
        User u = null;
        if(data.contains("@")) {
            u = userRepo.login(new User(data, ""), true, password);
        }else {
            u = userRepo.login(new User("", data), false, password);
        }
        if(u == null)
            throw new ValidationException("Parola sau nume incorect");
        loggedClients.add(client);
        return u;
    }

    public synchronized void logout(User u, IObserver client){
        loggedClients.remove(client);
    }

    public synchronized Iterable<ConcertArtist> getAllConcerts(){
        return concertRepo.findAll();
    }

    public synchronized Iterable<ConcertArtist> getAllByDay(LocalDate date){
        return concertRepo.findByDay(date);
    }

    public synchronized ConcertArtist updateSeats(ConcertArtist concert, Integer seats){
        ConcertArtist c = null;
        if( concert.getAvbSeats() - seats >= 0) {
            c = new ConcertArtist(concert.getNume(), concert.getData(), concert.getLocation(), concert.getAvbSeats() - seats, concert.getSoldSeats() + seats);
            c.setId(concert.getId());
        }else{
            throw new ValidationException("Nu sunt suficiente locuri");
        }
        ConcertArtist c1 = concertRepo.update(c);
        notifySeatsUpdate(c1);
        return c1;
    }

    private final int defaultThreadsNo=5;

    private void notifySeatsUpdate(ConcertArtist c1){
        ExecutorService exec = Executors.newFixedThreadPool(defaultThreadsNo);
        Iterable<ConcertArtist> all = this.getAllConcerts();
        Iterable<ConcertArtist> fil = this.getAllByDay(c1.getData().toLocalDate());
        for(IObserver o : loggedClients){
            exec.execute(() -> {
                try{
                    System.out.println("Notifying : " + o.toString());
                    o.seatsWereUpdated(all, fil, c1);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                }
            });
        }
        exec.shutdown();
    }
}
