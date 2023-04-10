package repository.dbRepository;

import model.ConcertArtist;
import model.User;
import org.apache.logging.log4j.Logger;
import repository.Repository;
import utils.JdbcUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class UserDBRepository implements Repository<Integer, User> {

    private JdbcUtils dbUtils;

    private Logger logger;

    public UserDBRepository(Properties props, Logger logger) {
        this.logger = logger;
        this.logger.info("Initializing UserDBRepository with properties: {} ", props);
        this.dbUtils = new JdbcUtils(props);
    }

    @Override
    public User findOne(Integer integer) {
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        return null;
    }

    @Override
    public User save(User entity) {
        return null;
    }

    @Override
    public User delete(Integer integer) {
        return null;
    }

    @Override
    public User update(User entity) {
        return null;
    }

    public User login(User entity, Boolean email, String password){
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        User u = null;
        if( email == true) {
            try (PreparedStatement ps = con.prepareStatement("select * from users where email = ? and password = ?")) {
                ps.setString(1, entity.getEmail());
                ps.setString(2, password);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        String nume = resultSet.getString("name");
                        String emailDb = resultSet.getString("email");
                        Integer id = resultSet.getInt("id");
                        u = new User(emailDb, nume);
                        u.setId(id);
                        logger.traceExit();
                        return u;
                    }
                }

            } catch (SQLException e) {
                logger.error(e);
                System.err.println("DB ERROR " + e);
            }

        }
        else{
            try (PreparedStatement ps = con.prepareStatement("select * from users where name = ? and password = ?")) {
                ps.setString(1, entity.getName());
                ps.setString(2, password);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        String nume = resultSet.getString("name");
                        String emailDb = resultSet.getString("email");
                        Integer id = resultSet.getInt("id");
                        u = new User(emailDb, nume);
                        u.setId(id);
                        logger.traceExit();
                        return u;
                    }
                }

            } catch (SQLException e) {
                logger.error(e);
                System.err.println("DB ERROR " + e);
            }

        }
        logger.traceExit();
        return u;
    }
}
