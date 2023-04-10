package client.controller;

import client.StartObjectClientFX;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.ConcertArtist;
import model.User;
import server.Service;
import utils.IObserver;
import utils.IServices;
import utils.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MainController implements IObserver {

    private IServices service;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        labelUser.setText(user.getName());
    }

    ObservableList<ConcertArtist> modelConcerts = FXCollections.observableArrayList();
    ObservableList<ConcertArtist> modelConcertsDay = FXCollections.observableArrayList();

    @FXML
    private TableView<ConcertArtist> tableViewConcerts;

    @FXML
    private TableColumn<ConcertArtist, Integer> columnId;

    @FXML
    private TableColumn<ConcertArtist, String> columnNume;

    @FXML
    private TableColumn<ConcertArtist, LocalDateTime> columnData;

    @FXML
    private TableColumn<ConcertArtist, String> columnLoc;

    @FXML
    private TableColumn<ConcertArtist, Integer> columnAvb;

    @FXML
    private TableColumn<ConcertArtist, Integer> columnSold;


    @FXML
    private TableView<ConcertArtist> tableViewDay;

    @FXML
    private TableColumn<ConcertArtist, String> columnNumeDay;

    @FXML
    private TableColumn<ConcertArtist, String> columnLocDay;

    @FXML
    private TableColumn<ConcertArtist, Integer> columnAvbDay;

    @FXML
    private TableColumn<ConcertArtist, Integer> columnSoldDay;

    @FXML
    Label labelUser;

    @FXML
    TextField dayFilter;

    @FXML
    Button logoutButton;

    @FXML
    Button buyButton;

    @FXML
    public void initialize()
    {
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    handleLogOut(event);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        };
        logoutButton.setOnAction(event);

        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event1) {
                try {
                    handleBuy(event1);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        };
        buyButton.setOnAction(event1);

        EventHandler<KeyEvent> event2 = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event2) {
                try {
                    handleFilter(event2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        dayFilter.setOnKeyReleased(event2);

        columnId.setCellValueFactory(new PropertyValueFactory<ConcertArtist, Integer>("id"));
        columnNume.setCellValueFactory(new PropertyValueFactory<ConcertArtist, String>("nume"));
        columnData.setCellValueFactory(new PropertyValueFactory<ConcertArtist, LocalDateTime>("data"));
        columnLoc.setCellValueFactory(new PropertyValueFactory<ConcertArtist, String>("location"));
        columnAvb.setCellValueFactory(new PropertyValueFactory<ConcertArtist, Integer>("avbSeats"));
        columnSold.setCellValueFactory(new PropertyValueFactory<ConcertArtist, Integer>("soldSeats"));
        tableViewConcerts.setItems(modelConcerts);

        columnNumeDay.setCellValueFactory(new PropertyValueFactory<ConcertArtist, String>("nume"));
        columnLocDay.setCellValueFactory(new PropertyValueFactory<ConcertArtist, String>("location"));
        columnAvbDay.setCellValueFactory(new PropertyValueFactory<ConcertArtist, Integer>("avbSeats"));
        columnSoldDay.setCellValueFactory(new PropertyValueFactory<ConcertArtist, Integer>("soldSeats"));
        tableViewDay.setItems(modelConcertsDay);


    }

    private void handleLogOut(ActionEvent event) throws IOException{
        ((Node)(event.getSource())).getScene().getWindow().hide();
//        FXMLLoader fxmlLoader=new FXMLLoader(StartObjectClientFX.class.getResource("/view/loginView.fxml"));
//        Scene scene=new Scene(fxmlLoader.load());
//        Stage stage=new Stage();
//        stage.setScene(scene);
//        stage.show();
        try {
            service.logout(getUser(), this);
        }catch (Exception e){
            e.printStackTrace();
        }
//        LoginController lController=fxmlLoader.getController();
//        lController.setService(service);
    }

    private void handleBuy(ActionEvent event) throws IOException{

        ConcertArtist selected = tableViewConcerts.getSelectionModel().getSelectedItem();
        ConcertArtist selectedDay = tableViewDay.getSelectionModel().getSelectedItem();
        if(selected == null && selectedDay == null){
            MessageAlert.showErrorMessage(null, "You haven't selected anything");
            return;
        }
        if(selected == null)
            selected = selectedDay;

        FXMLLoader fxmlLoader=new FXMLLoader(StartObjectClientFX.class.getResource("/view/buyView.fxml"));
        Scene scene=new Scene(fxmlLoader.load());
        Stage stage=new Stage();
        stage.setScene(scene);
        stage.show();
        BuyController ctrl = fxmlLoader.getController();
        ctrl.setConcert(selected);
        ctrl.setService(service);
        //ctrl.update();
        //service.addObserver(ctrl);
    }


    public void handleFilter(KeyEvent event) throws IOException{
        String day = dayFilter.getText();
        if(day.length() < 10)
        {
            return;
        }
        Iterable<ConcertArtist> concerts = null;
        try {
            concerts = service.getAllByDay(LocalDate.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }catch (Exception e){
            return;
        }
        List<ConcertArtist> listD = new ArrayList<>();
        if(concerts != null)
            concerts.forEach(x->listD.add(x));
        modelConcertsDay.setAll(listD);
    }

    public IServices getService() {
        return service;
    }

    public void setService(IServices service) {
        this.service = service;
    }


    public void initModel() throws Exception {
        Iterable<ConcertArtist> concerts = service.getAllConcerts();
        List<ConcertArtist> listC = new ArrayList<>();
        if(concerts != null)
            concerts.forEach(x->listC.add(x));
        modelConcerts.setAll(listC);
    }


    @Override
    public void seatsWereUpdated(Iterable<ConcertArtist> all, Iterable<ConcertArtist> filter, ConcertArtist updated) {
        dayFilter.clear();
        System.out.println("UPDATING SEATS IN UI");
        List<ConcertArtist> allList = new ArrayList<>();
        List<ConcertArtist> filterList = new ArrayList<>();
        if(all!=null){
            all.forEach(x->allList.add(x));
        }
        if(filter != null){
            filter.forEach(x->filterList.add(x));
        }
        modelConcerts.setAll(allList);
        modelConcertsDay.setAll(filterList);

        System.out.println("GATA UI seatsWereUpdated");
    }
}
