
import network.servers.AbstractServer;
import network.servers.ObjectConcurrentServer;
import network.servers.ServerException;
import org.apache.logging.log4j.LogManager;
import persistence.repository.dbHibernateRepository.ConcertArtistDBHRepository;
import persistence.repository.dbRepository.ConcertArtistDBRepository;
import persistence.repository.dbRepository.UserDBRepository;
import server.ServerImpl;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartObjectServer {
    private static int defaultPort=55555;

    public static void main(String[] args){
        Properties serverProps=new Properties();
        try {
            serverProps.load(new FileReader("C:\\Proiecte SSD\\Java\\mppJavaNetwork\\Server\\src\\main\\resources\\server.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find chatserver.properties "+e);
            return;
        }
        UserDBRepository userRepo = new UserDBRepository(serverProps, LogManager.getLogger());
        ConcertArtistDBRepository artRepo = new ConcertArtistDBRepository(serverProps, LogManager.getLogger());
        ConcertArtistDBHRepository expRepo = new ConcertArtistDBHRepository(LogManager.getLogger());
        expRepo.initialize();
        ServerImpl implem = new ServerImpl(userRepo, expRepo);
        int chatServerPort = defaultPort;
        try{
            chatServerPort = Integer.parseInt(serverProps.getProperty("server.port"));
        } catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port : " + chatServerPort);
        AbstractServer server = new ObjectConcurrentServer(chatServerPort, implem);
        try{
            server.start();
        }catch (ServerException e){
            System.err.println("Error starting the server " + e.getMessage());
        }


    }
}
