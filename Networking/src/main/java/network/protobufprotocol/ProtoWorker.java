package network.protobufprotocol;

import model.ConcertArtist;
import model.User;
import network.objectprotocol.*;
import utils.IObserver;
import utils.IServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ProtoWorker implements Runnable, IObserver {

    private IServices server;

    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ProtoWorker(IServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();

            input = new ObjectInputStream(connection.getInputStream());
            connected = true;

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected){
            try{
                Object request = input.readObject();
                Object response = handleRequest((Request) request);
                if (response!=null){
                    sendResponse((Response) response);
                }
            }catch (IOException e){
                e.printStackTrace();
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }

            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        try{
            input.close();
            output.close();
            connection.close();
        }catch (IOException e){
            System.out.println("Error : " + e);
        }

    }

    private Response handleRequest(Request request){
        Response response = null;
        // verify the instance
        System.out.println("Am primit request");
        if(request instanceof LoginRequest){
            System.out.println("LOGIN REQUEST...");
            LoginRequest logReq = (LoginRequest)request;
            try{
                User u = server.login(logReq.getData(), logReq.getPassword(), this);
                response = new LoggedInResponse(u);
            }catch (Exception e){
                connected=false;
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof LogoutRequest){
            System.out.println("LOGOUT REQUEST...");
            LogoutRequest logReq = (LogoutRequest) request;
            try{
                server.logout(logReq.getUser(), this);
                response = new OkResponse();
                connected = false;
            }catch (Exception e){
                connected=false;
                return  new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof AllRequest){
            System.out.println("GET ALL REQUEST..");
            AllRequest req = (AllRequest) request;
            try{
                response = new AllResponse(server.getAllConcerts());
            }catch (Exception e){
                connected = false;
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof FilterRequest){
            System.out.println("GET FILTERED REQUEST..");
            FilterRequest req = (FilterRequest) request;
            try{
                response = new AllResponse(server.getAllByDay(req.getDate()));
            }catch (Exception e){
                connected = false;
                return new ErrorResponse(e.getMessage());
            }
        }

        if(request instanceof BuyRequest){
            System.out.println("BUY REQUEST..");
            BuyRequest req = (BuyRequest) request;
            try{
                ConcertArtist con = server.updateSeats(req.getConcert(), req.getSeats());
                response = new OkResponse();
            }catch (Exception e){
                connected = false;
                return new ErrorResponse(e.getMessage());
            }
        }


        return response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response : " + response);
        synchronized (output){
            output.writeObject(response);
            output.flush();
        }
    }


    @Override
    public void seatsWereUpdated(Iterable<ConcertArtist> all, Iterable<ConcertArtist> filter, ConcertArtist updated) {
        System.out.println("Seats were updated (WORKER)");
        try{
            sendResponse(new UpdateResponse(updated, all, filter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
