package network.objectprotocol;

import model.ConcertArtist;
import model.User;
import utils.IObserver;
import utils.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServicesObjectProxy implements IServices {
    private String host;

    private int port;

    private IObserver client;

    private ObjectInputStream input;

    private ObjectOutputStream output;

    private Socket connection;

    private BlockingQueue<Response> qresponses;

    private volatile boolean finished;

    public ServicesObjectProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<Response>();
    }

    @Override
    public User login(String data, String password, IObserver client) throws Exception {
        initializeConnection();
        System.out.println("initializeConnection() done");
        sendRequest(new LoginRequest(data, password));
        System.out.println("Request sent");
        Response response = readResponse();
        System.out.println("Response received");
        if (response instanceof LoggedInResponse){
            this.client=client;
            return ((LoggedInResponse) response).getU();
        }
        if (response instanceof ErrorResponse){
            ErrorResponse err=(ErrorResponse)response;
            closeConnection();
            throw new Exception(err.getMessage());
        }
        return null;
    }

    @Override
    public void logout(User u, IObserver obs) throws Exception {
        sendRequest(new LogoutRequest(u));
        Response response = readResponse();
        closeConnection();
        if ( response instanceof ErrorResponse){
            ErrorResponse err = (ErrorResponse) response;
            throw new Exception(err.getMessage());
        }
    }

    @Override
    public Iterable<ConcertArtist> getAllConcerts() throws Exception {
        sendRequest(new AllRequest());
        Response response = readResponse();
        if(response instanceof AllResponse){
            return ((AllResponse) response).getConcerts();
        }
        if(response instanceof ErrorResponse){
            ErrorResponse err=(ErrorResponse)response;
            closeConnection();
            throw new Exception(err.getMessage());
        }
        return null;
    }

    @Override
    public Iterable<ConcertArtist> getAllByDay(LocalDate parse) throws Exception {
        sendRequest(new FilterRequest(parse));
        Response response = readResponse();
        if(response instanceof AllResponse){
            return ((AllResponse) response).getConcerts();
        }
        if(response instanceof ErrorResponse){
            ErrorResponse err=(ErrorResponse)response;
            closeConnection();
            throw new Exception(err.getMessage());
        }
        return null;
    }

    @Override
    public ConcertArtist updateSeats(ConcertArtist concert, Integer seats) throws Exception {
        sendRequest(new BuyRequest(concert, seats));
        Response response = readResponse();
        System.out.println("PROXY UPDADTE SEATS HAS RECEIVED A RESPONSE " + response);
        if (response instanceof OkResponse){
            return null;
        }
        if (response instanceof ErrorResponse){
            ErrorResponse err=(ErrorResponse)response;
            closeConnection();
            throw new Exception(err.getMessage());
        }
        return null;
    }


    private void sendRequest(Request request)throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private Response readResponse() throws Exception {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() {
        try {
            connection = new Socket(host, port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(UpdateResponse update){
        System.out.println("Handle Update");
        try{
            client.seatsWereUpdated(update.getAllConcerts(), update.getFilteredConcerts(), update.getUpdatedConcert());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (response instanceof UpdateResponse){
                        handleUpdate((UpdateResponse)response);
                    }else{
                        /*responses.add((Response)response);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        synchronized (responses){
                            responses.notify();
                        }*/
                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

}
