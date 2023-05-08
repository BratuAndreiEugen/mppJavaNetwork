package persistence.repository.dbHibernateRepository;

import model.ConcertArtist;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import persistence.JdbcUtils;
import persistence.repository.ConcertArtistRepository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

public class ConcertArtistDBHRepository implements ConcertArtistRepository {

    private static SessionFactory sessionFactory;

    private Logger logger;

    public static void initialize(){
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Exceptie "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    static void close(){
        if(sessionFactory != null){
            sessionFactory.close();
        }
    }

    public ConcertArtistDBHRepository(Logger logger){
        this.logger = logger;
        this.logger.info("Initializing ConcertArtistDBHibernateRepository with session factory: {} ", sessionFactory);
    }

    @Override
    public Iterable<ConcertArtist> findByArtist(String artist) {
        return null;
    }

    @Override
    public Iterable<ConcertArtist> findByDay(LocalDate day) {
        List<ConcertArtist> concerts = null;
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<ConcertArtist> criteria = builder.createQuery(ConcertArtist.class);
                Root<ConcertArtist> root = criteria.from(ConcertArtist.class);

                // Add a condition to filter the results
                criteria.where(builder.like(root.get("dataStr"), day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "%"));

                concerts = session.createQuery(criteria).getResultList();

                System.out.println("repo Filter" + concerts.size());
                concerts.forEach(System.out::println);
                tx.commit();
            }catch (RuntimeException ex){
                System.err.println(ex);
                if(tx != null)
                    tx.rollback();
            }
        }
        return concerts;
    }

    @Override
    public ConcertArtist findOne(Integer integer) {
        ConcertArtist concert = null;
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                concert = session.get(ConcertArtist.class, integer);
                tx.commit();
            }catch (RuntimeException ex){
                if (tx != null) {
                    logger.error(ex.getMessage());
                    tx.rollback();
                }
                return null;
            }
        }
        return concert;
    }

    @Override
    public Iterable<ConcertArtist> findAll() {
        List<ConcertArtist> concerts = null;
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                concerts = session.createQuery("from ConcertArtist", ConcertArtist.class).stream().toList();
                System.out.println("repo findAll" + concerts.size());
                concerts.forEach(System.out::println);
                tx.commit();
            }catch (RuntimeException ex){
                System.err.println(ex);
                if(tx != null)
                    tx.rollback();
            }
        }
        return concerts;
    }

    @Override
    public ConcertArtist save(ConcertArtist entity) {
        return null;
    }

    @Override
    public ConcertArtist delete(Integer integer) {
        return null;
    }

    @Override
    public ConcertArtist update(ConcertArtist entity) {
        logger.traceEntry("update task {}", entity);
        ConcertArtist c = entity;
        try(Session session = sessionFactory.openSession()){
            Transaction tx = null;
            try{
                tx = session.beginTransaction();
                c = session.load(ConcertArtist.class, entity.getId());
                System.out.println("INAINTE DE UPDATE : " + c);
                c.setAvbSeats(entity.getAvbSeats());
                c.setSoldSeats(entity.getSoldSeats());
                session.update(c);
                c = session.load(ConcertArtist.class, entity.getId());
                System.out.println("DUPA UPDATE : " + c);
                tx.commit();
            }catch (RuntimeException ex){
                System.err.println("Eroare la update "+ex);
                if (tx!=null)
                    tx.rollback();
            }
        }
        return c;
    }
}
