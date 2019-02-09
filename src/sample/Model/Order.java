package sample.Model;

import javafx.application.Platform;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;

public class Order{
    private StringProperty From, Price, Status, Callsign, Time;
    private String OrderNumber, OriginalPrice, City, Phone, Description, To;
    private int statusInt = 0;
    private static Callback<Order, javafx.beans.Observable[]> cb =(Order stock) -> new javafx.beans.Observable[]{
            stock.getStatus(),
    };
    private static ObservableList<Order> Orders = FXCollections.observableArrayList(cb);
    private static ObservableList<Order> Working = FXCollections.observableArrayList();
    public Order(){
        From = new SimpleStringProperty();
        Price = new SimpleStringProperty();
        Status = new SimpleStringProperty("-");
        Callsign = new SimpleStringProperty();
        Time = new SimpleStringProperty();
        Time.set("-");
        Callsign.set("-");
    }

    public void add(){
        Orders.add(this);
    }

    public static ObservableList<Order> getOrders(){
        return Orders;
    }

    public static ObservableList<Order> getWorking(){
        return Working;
    }

    public static Order getOrder(String orderNumber){
        for(Order order: Orders){
            if(order.getOrderNumber().equals(orderNumber)){
                return order;
            }
        }
        return null;
    }

    public static void bindOrder(String orderNumber, String callsign, String price){
        Order order = getOrder(orderNumber);
        order.setCallsign(callsign);
        order.setPrice(price);
        for(Order Order : Orders){
            if(Order.getOrderNumber().equals(orderNumber)){
                Order.setCallsign(callsign);
                Order.setPrice(price);
            }
        }
    }

    public static void removeOrder(String orderNumber){
        Platform.runLater(()->{
            Iterator<Order> iter = Orders.iterator();
            while (iter.hasNext()) {
                Order order = iter.next();
                if (order.getOrderNumber().equals(orderNumber)) {
                    iter.remove();
                    break;
                }
            }
        });
    }

    public int getStatusInt(){
        return statusInt;
    }

    public StringProperty getStatus(){
        return Status;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getFrom() {
        return From.get();
    }

    public StringProperty fromProperty() {
        return From;
    }

    public void setFrom(String from) {
        this.From.set(from);
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getPrice() {
        return Price.get();
    }

    public StringProperty priceProperty() {
        return Price;
    }

    public void setPrice(String price) {
        this.Price.set(price);
        if(OriginalPrice == null){
            OriginalPrice = price;
        }
    }

    public String getOriginalPrice() {
        return OriginalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.OriginalPrice = originalPrice;
    }

    public StringProperty statusProperty() {
        return Status;
    }

    public void setStatus(int status) {
        switch (status){
            case 0:
                setStatus("-");
                break;
            case 1:
                setStatus("ВБР");
                break;
            case 2:
                setStatus("ПРН");
                break;
            case 3:
                setStatus("ТРП");
                break;
            case 4:
                setStatus("ОК");
                break;
            default:

                break;

        }
    }

    public void setStatus(String status) {
        switch (status){
            case "ЦН":
                statusInt = 5;
                break;
            case "ПРН":
                statusInt = 4;
                break;
            case "ОТК":
                statusInt = 3;
                break;
            case "ТРП":
                statusInt = 2;
                break;
            case "ОК":
                statusInt = 1;
                break;
            default:
                statusInt = 0;
                break;

        }
        this.Status.set(status);
    }

    public String getCallsign() {
        return Callsign.get();
    }

    public StringProperty callsignProperty() {
        return Callsign;
    }

    public void setCallsign(String callsign) {
        this.Callsign.set(callsign);
    }

    public String getTime() {
        return Time.get();
    }

    public StringProperty timeProperty() {
        return Time;
    }

    public void setTime(String time) {
        this.Time.set(time);
        if(!Time.get().equals("-")) {
            int timeInt = Integer.parseInt(time);
            if (To.equals("ПОВРЕМЕННАЯ")) {
                if (timeInt > 30) {
                    Platform.runLater(() -> {
                        Price.set(String.valueOf((timeInt - 30) * 24 + Integer.parseInt(Price.get())));
                        double roundPrice = Double.parseDouble(Price.get());
                        roundPrice = roundPrice / 10;
                        if (roundPrice == (int) roundPrice) {
                            roundPrice = (int) roundPrice;
                        } else {
                            roundPrice = (int) roundPrice;
                            roundPrice++;
                        }
                        roundPrice = roundPrice * 10;
                        final String price = String.valueOf((int) roundPrice);
                        new Thread(() -> {
                            NetWork.get().send("[upd~" + getOrderNumber() + "|" + getFrom() + "$" + getTo() + "$" + price + "$" + getPhone() + "$" + getDescription() + "]");
                        }).start();
                    });
                }
            }
        }
    }

    public static void clearOrders(){
        Platform.runLater(()->Orders.clear());
    }

    public static void removeUnwantedOrders(List<String> orderNumbers){
        for (String orderNumber : orderNumbers) {
            boolean a = false;
            for (Order order : Order.getOrders()) {
                if (order.getOrderNumber().equals(orderNumber)) {
                    a = true;
                }
            }
            if (!a) {
                Order.removeOrder(orderNumber);
            }
        }
    }
}
