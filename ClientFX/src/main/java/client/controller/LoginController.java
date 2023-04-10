package controller;

import app.RunApp;
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
import service.Service;

import java.io.IOException;

public class LoginController {

    private Service service;

    @FXML
    TextField textFieldNume;

    @FXML
    PasswordField passwordField;

    @FXML
    Button loginButton;

    @FXML
    public void initialize()
    {
        EventHandler<javafx.event.ActionEvent> event = new EventHandler<javafx.event.ActionEvent>() {
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
            u = service.login(data, password);
        }catch (ValidationException ve){
            MessageAlert.showErrorMessage(null, "Nume sau parola incorecte");
        }
        if(u != null) {
            System.out.println(u);
            FXMLLoader fxmlLoader = new FXMLLoader(RunApp.class.getResource("/view/mainView.fxml"));
            Scene scene=new Scene(fxmlLoader.load());
            Stage stage=new Stage();
            stage.setScene(scene);
            stage.show();
            MainController mainController = fxmlLoader.getController();
            mainController.setUser(u);
            mainController.setService(service);
            mainController.update();
            service.addObserver(mainController);
            ((Node)(event.getSource())).getScene().getWindow().hide();

        }
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
