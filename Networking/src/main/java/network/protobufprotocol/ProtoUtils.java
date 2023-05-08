package network.protobufprotocol;

import com.google.protobuf.Timestamp;
import model.ConcertArtist;
import model.User;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

public class ProtoUtils {
    public static AppProtobufs.RequestP createLoginRequest(String data, String password){
        AppProtobufs.RequestP request = AppProtobufs.RequestP.newBuilder().setType(AppProtobufs.RequestP.Type.Login).setData(data).setPassword(password).build();
        return request;
    }

    public static AppProtobufs.RequestP createLogoutRequest(User u){
        AppProtobufs.UserP user = AppProtobufs.UserP.newBuilder().setId(u.getId()).setEmail(u.getEmail()).setName(u.getName()).build();
        AppProtobufs.RequestP request = AppProtobufs.RequestP.newBuilder().setType(AppProtobufs.RequestP.Type.Logout).setUser(user).build();
        return request;
    }

    public static AppProtobufs.RequestP createAllRequest(){
        AppProtobufs.RequestP request = AppProtobufs.RequestP.newBuilder().setType(AppProtobufs.RequestP.Type.All).build();
        return request;
    }

    public static AppProtobufs.RequestP createFilterRequest(LocalDate date){

        Timestamp timestamp = Timestamp.newBuilder().setSeconds(date.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond()).build();
        AppProtobufs.RequestP request = AppProtobufs.RequestP.newBuilder().setType(AppProtobufs.RequestP.Type.Filter).setDate(timestamp).build();
        return request;
    }

    public static AppProtobufs.RequestP createBuyRequest(ConcertArtist c, Integer seats){
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(c.getData().toEpochSecond(ZoneOffset.UTC)).build();
        AppProtobufs.ConcertArtistP concert = AppProtobufs.ConcertArtistP.newBuilder().setId(c.getId()).setNume(c.getNume()).setData(timestamp).setLocation(c.getLocation()).setAvb(c.getAvbSeats()).setSold(c.getSoldSeats()).build();
        AppProtobufs.RequestP request = AppProtobufs.RequestP.newBuilder().setType(AppProtobufs.RequestP.Type.Buy).setConcert(concert).setSeats(seats).build();
        return request;
    }

    public static AppProtobufs.ResponseP createLoggedInResponse(User user){
        AppProtobufs.UserP u = AppProtobufs.UserP.newBuilder().setId(user.getId()).setEmail(user.getEmail()).setName(user.getName()).build();
        AppProtobufs.ResponseP response = AppProtobufs.ResponseP.newBuilder().setType(AppProtobufs.ResponseP.Type.LoggedIn).setU(u).build();
        return response;
    }

    public static AppProtobufs.ResponseP createOkResponse(){
        AppProtobufs.ResponseP response = AppProtobufs.ResponseP.newBuilder().setType(AppProtobufs.ResponseP.Type.Ok).build();
        return response;
    }

    public static AppProtobufs.ResponseP createErrorResponse(String mesg){
        AppProtobufs.ResponseP response = AppProtobufs.ResponseP.newBuilder().setType(AppProtobufs.ResponseP.Type.Error).setMesg(mesg).build();
        return response;
    }

    public static AppProtobufs.ResponseP createAllResponse(Iterable<ConcertArtist> concerts){
        AppProtobufs.ResponseP.Builder builder = AppProtobufs.ResponseP.newBuilder().setType(AppProtobufs.ResponseP.Type.All);
        concerts.forEach(c->{
            Timestamp timestamp = Timestamp.newBuilder().setSeconds(c.getData().toEpochSecond(ZoneOffset.UTC)).build();
            AppProtobufs.ConcertArtistP concert = AppProtobufs.ConcertArtistP.newBuilder().setId(c.getId()).setNume(c.getNume()).setData(timestamp).setLocation(c.getLocation()).setAvb(c.getAvbSeats()).setSold(c.getSoldSeats()).build();
            builder.addConcerts(concert);
        });
        return builder.build();
    }

    public static AppProtobufs.ResponseP createUpdateResponse(ConcertArtist upd, Iterable<ConcertArtist> all, Iterable<ConcertArtist> filter){
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(upd.getData().toEpochSecond(ZoneOffset.UTC)).build();
        AppProtobufs.ConcertArtistP concert = AppProtobufs.ConcertArtistP.newBuilder().setId(upd.getId()).setNume(upd.getNume()).setData(timestamp).setLocation(upd.getLocation()).setAvb(upd.getAvbSeats()).setSold(upd.getSoldSeats()).build();
        AppProtobufs.ResponseP.Builder builder = AppProtobufs.ResponseP.newBuilder().setType(AppProtobufs.ResponseP.Type.Update).setUpdated(concert);

        all.forEach(c->{
            Timestamp t = Timestamp.newBuilder().setSeconds(c.getData().toEpochSecond(ZoneOffset.UTC)).build();
            AppProtobufs.ConcertArtistP ct = AppProtobufs.ConcertArtistP.newBuilder().setId(c.getId()).setNume(c.getNume()).setData(t).setLocation(c.getLocation()).setAvb(c.getAvbSeats()).setSold(c.getSoldSeats()).build();
            builder.addAll(ct);
        });
        filter.forEach(c->{
            Timestamp t = Timestamp.newBuilder().setSeconds(c.getData().toEpochSecond(ZoneOffset.UTC)).build();
            AppProtobufs.ConcertArtistP ct = AppProtobufs.ConcertArtistP.newBuilder().setId(c.getId()).setNume(c.getNume()).setData(t).setLocation(c.getLocation()).setAvb(c.getAvbSeats()).setSold(c.getSoldSeats()).build();
            builder.addFilter(ct);
        });

        return builder.build();
    }

    public static User getUserFromResponse(AppProtobufs.ResponseP response){
        User u = new User(response.getU().getEmail(), response.getU().getName());
        u.setId(response.getU().getId());
        return u;
    }

    public static Iterable<ConcertArtist> getAllFromResponse(AppProtobufs.ResponseP response){
        List<ConcertArtist> l = new ArrayList<>();
        response.getAllList().forEach(c->{
            Instant instant = Instant.ofEpochSecond(c.getData().getSeconds(), c.getData().getNanos());
            LocalDateTime t = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            ConcertArtist cn = new ConcertArtist(c.getNume(), t, c.getLocation(), c.getAvb(), c.getSold());
            cn.setId(c.getId());
            l.add(cn);
        });
        return l;
    }

    public static Iterable<ConcertArtist> getFilterFromResponse(AppProtobufs.ResponseP response){
        List<ConcertArtist> l = new ArrayList<>();
        response.getFilterList().forEach(c->{
            Instant instant = Instant.ofEpochSecond(c.getData().getSeconds(), c.getData().getNanos());
            LocalDateTime t = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            ConcertArtist cn = new ConcertArtist(c.getNume(), t, c.getLocation(), c.getAvb(), c.getSold());
            cn.setId(c.getId());
            l.add(cn);
        });
        return l;
    }

    public static Iterable<ConcertArtist> getConcertsFromResponse(AppProtobufs.ResponseP response){
        List<ConcertArtist> l = new ArrayList<>();
        response.getConcertsList().forEach(c->{
            Instant instant = Instant.ofEpochSecond(c.getData().getSeconds(), c.getData().getNanos());
            LocalDateTime t = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            ConcertArtist cn = new ConcertArtist(c.getNume(), t, c.getLocation(), c.getAvb(), c.getSold());
            cn.setId(c.getId());
            l.add(cn);
        });
        return l;
    }

    public static ConcertArtist getConcertFromResponse(AppProtobufs.ResponseP response){
        AppProtobufs.ConcertArtistP c = response.getUpdated();
        Instant instant = Instant.ofEpochSecond(c.getData().getSeconds(), c.getData().getNanos());
        LocalDateTime t = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        ConcertArtist con = new ConcertArtist(c.getNume(), t, c.getLocation(), c.getAvb(), c.getSold());
        con.setId(c.getId());
        return con;
    }
}