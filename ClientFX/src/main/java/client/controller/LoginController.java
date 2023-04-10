package client.controller;

import client.StartObjectClientFX;
import exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import server.Service;
import utils.IServices;


import java.io.IOException;

public class LoginController {

    private IServices service;

    @FXML
    TextField textFieldNume;

    @FXML
    PasswordField passwordField;

    @FXML
    Button loginButton;

    @FXML
    public void initialize()
    {
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    handleLogin(event);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }

        };
        loginButton.setOnAction(event);
    }

    @FXML
    public void handleLogin(ActionEvent event) throws IOException
    {
        String data = textFieldNume.getText();
        String password = passwordField.getText();
        textFieldNume.clear();
        passwordField.clear();
        System.out.println(data);
        System.out.println(password);
        User u = null;
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(StartObjectClientFX.class.getResource("/view/mainView.fxml"));
            Scene scene=new Scene(fxmlLoader.load());
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.show();
            MainController mainController = fxmlLoader.getController();
            u = service.login(data, password, mainController);
            mainController.setUser(u);
            mainController.setService(service);
            //service.addObserver(mainController);
            mainController.initModel();
            ((Node)(event.getSource())).getScene().getWindow().hide();


        }catch (ValidationException ve){
            MessageAlert.showErrorMessage(null, "Nume sau parola incorecte");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public IServices getService() {
        return service;
    }

    public void setService(IServices service) {
        this.service = service;
    }
}
