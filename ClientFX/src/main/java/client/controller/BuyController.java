package controller;

import exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.ConcertArtist;
import service.Service;
import utils.Observable;
import utils.Observer;


import java.io.IOException;


public class BuyController implements Observer {
    private Service service;

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
        EventHandler<ActionEvent> event = new EventHandler<javafx.event.ActionEvent>() {
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
            this.setConcert(service.updateSeats(concert, seats));
        }catch (ValidationException ve){
            MessageAlert.showErrorMessage(null, ve.getMessage());
        }

    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public void update() {
        this.setConcert(service.getOne(concert.getId()));
    }
}
