package client;

import client.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import network.objectprotocol.ServicesObjectProxy;
import network.protobufprotocol.ProtoProxy;
import utils.IServices;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartProtoClientFX extends Application {

    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("IN CLIENT START");
        Properties clientProps = new Properties();
        try{
            clientProps.load(new FileReader("C:\\Proiecte SSD\\Java\\mppJavaNetwork\\ClientFX\\src\\main\\resources\\appclient.properties"));
            System.out.println("Client props set.");
            clientProps.list(System.out);
        }catch (IOException e){
            System.err.println("CANNOT FIND appclient.properties" + e);
            return;
        }
        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;

        try{
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        }catch (NumberFormatException ex){
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        /**
         * Modified here
         */
        IServices server = new ProtoProxy(serverIP, serverPort);

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginView.fxml"));
        AnchorPane root=loader.load();

        LoginController ctrl = loader.getController();
        ctrl.setService(server);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
}
