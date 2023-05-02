package network.protobufprotocol;

import model.ConcertArtist;
import model.User;
import network.objectprotocol.*;
import utils.IObserver;
import utils.IServices;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ProtoProxy implements IServices {
    private String host;

    private int port;

    private IObserver client;

    //private ObjectInputStream input;

    //private ObjectOutputStream output;

    private InputStream input;
    private OutputStream output;

    private Socket connection;

    private BlockingQueue<AppProtobufs.ResponseP> qresponses;

    private volatile boolean finished;

    public ProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<AppProtobufs.ResponseP>();
    }

    @Override
    public User login(String data, String password, IObserver client) throws Exception {
        initializeConnection();
        System.out.println("initializeConnection() done");
        sendRequest(ProtoUtils.createLoginRequest(data, password));
        System.out.println("Request sent");
        AppProtobufs.ResponseP response = readResponse();
        System.out.println("Response received");
        if (response.getType() == AppProtobufs.ResponseP.Type.LoggedIn){
            this.client=client;
            return ProtoUtils.getUserFromResponse(response);
        }
        if (response.getType() == AppProtobufs.ResponseP.Type.Error){
            closeConnection();
            throw new Exception(response.getMesg());
        }
        return null;
    }

    @Override
    public void logout(User u, IObserver obs) throws Exception {
        sendRequest(ProtoUtils.createLogoutRequest(u));
        AppProtobufs.ResponseP response = readResponse();
        closeConnection();
        if ( response.getType() == AppProtobufs.ResponseP.Type.Error){
            throw new Exception(response.getMesg());
        }
    }

    @Override
    public Iterable<ConcertArtist> getAllConcerts() throws Exception {
        sendRequest(ProtoUtils.createAllRequest());
        AppProtobufs.ResponseP response = readResponse();
        if(response.getType() == AppProtobufs.ResponseP.Type.All){
            return ProtoUtils.getConcertsFromResponse(response);
        }
        if(response.getType() == AppProtobufs.ResponseP.Type.Error){
            closeConnection();
            throw new Exception(response.getMesg());
        }
        return null;
    }

    @Override
    public Iterable<ConcertArtist> getAllByDay(LocalDate parse) throws Exception {
        sendRequest(ProtoUtils.createFilterRequest(parse));
        AppProtobufs.ResponseP response = readResponse();
        if(response.getType() == AppProtobufs.ResponseP.Type.All){
            return ProtoUtils.getConcertsFromResponse(response);
        }
        if(response.getType() == AppProtobufs.ResponseP.Type.Error){
            closeConnection();
            throw new Exception(response.getMesg());
        }
        return null;
    }

    @Override
    public ConcertArtist updateSeats(ConcertArtist concert, Integer seats) throws Exception {
        sendRequest(ProtoUtils.createBuyRequest(concert, seats));
        AppProtobufs.ResponseP response = readResponse();
        System.out.println("PROXY UPDADTE SEATS HAS RECEIVED A RESPONSE " + response);
        if (response.getType() == AppProtobufs.ResponseP.Type.Ok){
            return null;
        }
        if (response.getType() == AppProtobufs.ResponseP.Type.Error){
            closeConnection();
            throw new Exception(response.getMesg());
        }
        return null;
    }


    private void sendRequest(AppProtobufs.RequestP request)throws Exception {
        try {
            request.writeDelimitedTo(output);
            output.flush();
        } catch (IOException e) {
            throw new Exception("Error sending object "+e);
        }

    }

    private AppProtobufs.ResponseP readResponse() throws Exception {
        AppProtobufs.ResponseP response=null;
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
            //output=new ObjectOutputStream(connection.getOutputStream());
            //output.flush();
            output = connection.getOutputStream();

            //input=new ObjectInputStream(connection.getInputStream());
            input = connection.getInputStream();
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
        Thread tw=new Thread(new ProtoProxy.ReaderThread());
        tw.start();
    }


    private void handleUpdate(AppProtobufs.ResponseP update){
        System.out.println("Handle Update");
        try{
            client.seatsWereUpdated(ProtoUtils.getAllFromResponse(update), ProtoUtils.getFilterFromResponse(update), ProtoUtils.getConcertFromResponse(update));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    AppProtobufs.ResponseP response= AppProtobufs.ResponseP.parseDelimitedFrom(input);
                    System.out.println("response received "+response);
                    if (response.getType() == AppProtobufs.ResponseP.Type.Update){
                        handleUpdate(response);
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
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

}
