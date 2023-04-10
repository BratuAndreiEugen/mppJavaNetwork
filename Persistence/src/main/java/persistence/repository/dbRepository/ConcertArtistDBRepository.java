package repository.dbRepository;

import model.ConcertArtist;
import repository.ConcertArtistRepository;
import utils.JdbcUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConcertArtistDBRepository implements ConcertArtistRepository {

    private JdbcUtils dbUtils;

    private Logger logger;

    public ConcertArtistDBRepository(Properties props , Logger logger){
        this.logger = logger;
        this.logger.info("Initializing ConcertArtistDBRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public ConcertArtist findOne(Integer integer) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        ConcertArtist c = null;
        try(PreparedStatement ps = con.prepareStatement("select * from concertartists where id = ?")){
            ps.setInt(1, integer);
            try(ResultSet resultSet = ps.executeQuery()){
                while (resultSet.next()){
                    String nume = resultSet.getString("name");
                    String dateStr = resultSet.getString("date");
                    String location =  resultSet.getString("location");
                    Integer avb = resultSet.getInt("seats");
                    Integer not = resultSet.getInt("noseats");
                    c = new ConcertArtist(nume, LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), location, avb, not);
                    c.setId(integer);
                    return c;
                }
            }

        }catch(SQLException e){
            logger.error(e);
            System.err.println("DB ERROR " + e);
        }
        logger.traceExit();
        return c;
    }

    @Override
    public Iterable<ConcertArtist> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<ConcertArtist> concerts = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement("select * from concertartists")){
            try(ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()){
                    Integer id = resultSet.getInt("id");
                    String nume =resultSet.getString("name");
                    String dateStr = resultSet.getString("date");
                    String location = resultSet.getString("location");
                    Integer avb = resultSet.getInt("seats");
                    Integer sold = resultSet.getInt("noseats");
                    ConcertArtist c = new ConcertArtist(nume, LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), location, avb, sold);
                    c.setId(id);
                    concerts.add(c);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("DB ERROR " + e);
        }
        logger.traceExit();
        return concerts;
    }

    @Override
    public ConcertArtist save(ConcertArtist entity) {
        logger.traceEntry("saving task {}", entity);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("insert into concertartists(name, date, location, seats, noseats) values (?, ?, ?, ?, ?)")){
            ps.setString(1, entity.getNume());
            ps.setString(2, entity.getData().toString().replace("T", " "));
            ps.setString(3, entity.getLocation());
            ps.setInt(4, entity.getAvbSeats());
            ps.setInt(5, entity.getSoldSeats());
            int result = ps.executeUpdate();
            logger.trace("saved {} instances", result);
            return entity;

        }catch (SQLException e){
            logger.error(e);
            System.err.println("DB ERROR " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public ConcertArtist delete(Integer integer) {
        logger.traceEntry("delete task {}", integer);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("delete from concertartists where id = ?")){
            ps.setInt(1, integer);
            int result = ps.executeUpdate();
            logger.trace("deleted {} instances", result);
            return null;

        }catch (SQLException e){
            logger.error(e);
            System.err.println("DB ERROR " + e);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public ConcertArtist update(ConcertArtist entity) {
        logger.traceEntry("update task {}", entity);
        Connection con = dbUtils.getConnection();
        try(PreparedStatement ps = con.prepareStatement("update concertartists set name = ?, date = ?, location = ?, seats = ?, noseats = ? where id = ?")){
            ps.setString(1, entity.getNume());
            ps.setString(2, entity.getData().toString().replace("T", " "));
            ps.setString(3, entity.getLocation());
            ps.setInt(4, entity.getAvbSeats());
            ps.setInt(5, entity.getSoldSeats());
            ps.setInt(6, entity.getId());
            int result = ps.executeUpdate();
            logger.trace("updated {} instances", result);
            return entity;

        }catch (SQLException e){
            logger.error(e);
            System.err.println("DB ERROR " + e);
        }
        logger.traceExit();
        return entity;
    }

    @Override
    public Iterable<ConcertArtist> findByArtist(String artist) {
        return null;
    }

    @Override
    public Iterable<ConcertArtist> findByDay(LocalDate day) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<ConcertArtist> concerts = new ArrayList<>();
        try(PreparedStatement ps = con.prepareStatement("select * from concertartists where date(date) = ?")){
            ps.setString(1, day.toString());
            try(ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()){
                    Integer id = resultSet.getInt("id");
                    String nume =resultSet.getString("name");
                    String dateStr = resultSet.getString("date");
                    String location = resultSet.getString("location");
                    Integer avb = resultSet.getInt("seats");
                    Integer sold = resultSet.getInt("noseats");
                    ConcertArtist c = new ConcertArtist(nume, LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), location, avb, sold);
                    c.setId(id);
                    concerts.add(c);
                }
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("DB ERROR " + e);
        }
        logger.traceExit();
        return concerts;
    }
}
