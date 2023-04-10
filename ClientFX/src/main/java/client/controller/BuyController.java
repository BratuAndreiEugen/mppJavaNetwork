package client.controller;

import exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.ConcertArtist;
import server.Service;
import utils.IObserver;
import utils.IServices;
import utils.Observer;

import java.io.IOException;


public class BuyController implements IObserver {
    private IServices service;

    private ConcertArtist concert;

    public ConcertArtist getConcert() {
        return concert;
    }

    public void setConcert(ConcertArtist concert) {
        this.concert = concert;
        labelConcert.setText(concert.getNume());
        labelSeats.setText(concert.getAvbSeats().toString());

    }

    @FXML
    Label labelConcert;

    @FXML
    Label labelSeats;

    @FXML
    TextField textFieldNume;

    @FXML
    TextField textFieldNumber;

    @FXML
    Button saveButton;

    @FXML
    public void initialize(){
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    handleBuy(event);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        };
        saveButton.setOnAction(event);
    }

    public void handleBuy(ActionEvent event) throws IOException{
        String nume = textFieldNume.getText();
        Integer seats = Integer.valueOf(textFieldNumber.getText().toString());
        textFieldNumber.clear();
        textFieldNume.clear();
        System.out.println(nume);
        System.out.println(seats);
        try {
            service.updateSeats(concert, seats);
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }catch (Exception ve){
            MessageAlert.showErrorMessage(null, ve.getMessage());
        }

    }

    public IServices getService() {
        return service;
    }

    public void setService(IServices service) {
        this.service = service;
    }


    @Override
    public void seatsWereUpdated(Iterable<ConcertArtist> all, Iterable<ConcertArtist> filter, ConcertArtist updated) {

    }
}
