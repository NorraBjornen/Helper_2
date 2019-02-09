package sample.Model;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.omg.PortableServer.POA;
import sample.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

public class Driver {
    private static ObservableList<Driver> Drivers = FXCollections.observableArrayList();
    private String DriverId, CallSign, CarNumber, Description, Position, Status, Radius;
    private boolean firstGeo = true;

    public static void clearDrivers(){
        for(Driver driver : Drivers){
            driver.erase();
        }
        Drivers.clear();
    }

    public static ObservableList<Driver> getDrivers() {
        return Drivers;
    }

    public Driver(){
        Drivers.add(this);
    }

    public static Driver getDriver(String driverId){
        for (Driver driver : Drivers) {
            if (driver.getDriverId().equals(driverId)) {
                return driver;
            }
        }
        return null;
    }

    public static Driver getDriverByCallSign(String callsign){
        for (Driver driver : Drivers) {
            if (driver.getCallSign().equals(callsign)) {
                return driver;
            }
        }
        return null;
    }

    public String getRadius() {
        return Radius;
    }

    public void setRadius(String radius) {
        Radius = radius;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
        String color;
        if(Status.equals("1")){
            color = "Green";
        } else {
            color = "Red";
        }
        Platform.runLater(()->{
            try {
                Main.WebEngine.executeScript("marker" + DriverId + ".setIcon(image"+color+")");
            } catch (Exception e){
                new Thread(()->{
                    try{Thread.sleep(1000);} catch (Exception ea){}
                    setStatus(Status);
                }).start();
            }
        });
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getCallSign() {
        return CallSign;
    }

    public void setCallSign(String callSign) {
        CallSign = callSign;
    }

    public String getCarNumber() {
        return CarNumber;
    }

    public void setCarNumber(String carNumber) {
        CarNumber = carNumber;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
        //draw();
        new Thread(()->draw()).start();
    }

    private void draw(){
        try{Thread.sleep(500);} catch (Exception e){}
        String color;
        if(Status.equals("1")){
            color = "Green";
        } else {
            color = "Red";
        }
        Platform.runLater(()->{
            try {
                if (firstGeo) {
                    Main.WebEngine.executeScript("var marker" + DriverId + " = new google.maps.Marker({\n" +
                            "position: new google.maps.LatLng(" + Position + "),\n" +
                            "label: {" +
                            "  text: '" + Driver.getDriver(DriverId).getCallSign() + "'," +
                            "  color: 'red',"+
                            "  fontSize: '20px',"+
                            "  fontWeight: 'bold'"+
                            "}," +
                            "title: 'Hello World!'" +
                            "});" +
                            "marker" + DriverId + ".setMap(document.map);" +
                            "marker" + DriverId + ".setIcon(image"+color+")");
                    firstGeo = false;
                } else {
                    Main.WebEngine.executeScript("marker" + DriverId + ".setPosition(new google.maps.LatLng(" + Position + "))\n");
                }
            } catch (Exception e){
                new Thread(()->draw()).start();
            }
        });
    }

    private void erase(){
        try {
            Platform.runLater(() -> {
                try {
                    Main.WebEngine.executeScript("marker" + DriverId + ".setMap(null);\n" +
                            "marker" + DriverId + " = null;");
                } catch (Exception e){}
            });
        } catch (Exception e){}
    }

    public void exit(){
        OfferedPrice.removeAllDriversOfferedPrices(DriverId);
        erase();
        Drivers.remove(this);
    }
}
