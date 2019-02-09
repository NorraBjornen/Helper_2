package sample.Model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import sample.Controller.MonitorController;

import java.util.Iterator;

public class OfferedPrice {
    private StringProperty DriverId, OfferedPrice, Time, Callsign;
    private String OrderNumber;
    private static ObservableList<OfferedPrice> OfferedPrices = FXCollections.observableArrayList();
    private static ObservableList<OfferedPrice> CurrentData = FXCollections.observableArrayList();
    public OfferedPrice(){
        DriverId = new SimpleStringProperty();
        OfferedPrice = new SimpleStringProperty();
        Time = new SimpleStringProperty();
        Callsign = new SimpleStringProperty();
        OfferedPrices.add(this);
    }

    public static ObservableList<OfferedPrice> getOfferedPrices(){
        return OfferedPrices;
    }

    public static OfferedPrice getOfferedPrice(String orderNumber, String driverId){
        try {
            for (OfferedPrice offeredPrice : OfferedPrices) {
                if (offeredPrice.getOrderNumber().equals(orderNumber) && offeredPrice.getDriverId().equals(driverId)) {
                    return offeredPrice;
                }
            }
            return null;
        } catch (Exception e){
            return null;
        }
    }

    public static void removeOfferedPrice(String orderNumber, String driverId){
        Iterator<OfferedPrice> iter = OfferedPrices.iterator();
        while (iter.hasNext()) {
            OfferedPrice offeredPrice = iter.next();
            if (offeredPrice.getOrderNumber().equals(orderNumber) && offeredPrice.getDriverId().equals(driverId)) {
                iter.remove();
                break;
            }
        }
        Iterator<OfferedPrice> iter2 = CurrentData.iterator();
        while (iter2.hasNext()) {
            OfferedPrice offeredPrice = iter2.next();
            if (offeredPrice.getOrderNumber().equals(orderNumber) && offeredPrice.getDriverId().equals(driverId)) {
                iter2.remove();
                break;
            }
        }
    }

    static void removeAllOfferedPrices(String orderNumber){
        Iterator<OfferedPrice> iter = OfferedPrices.iterator();
        while (iter.hasNext()) {
            OfferedPrice offeredPrice = iter.next();
            if (offeredPrice.getOrderNumber().equals(orderNumber)) {
                iter.remove();
            }
        }
        Iterator<OfferedPrice> iter2 = CurrentData.iterator();
        while (iter2.hasNext()) {
            OfferedPrice offeredPrice = iter2.next();
            if (offeredPrice.getOrderNumber().equals(orderNumber)) {
                iter2.remove();
            }
        }
    }

    static void removeAllDriversOfferedPrices(String driverId){
        Iterator<OfferedPrice> iter = OfferedPrices.iterator();
        while (iter.hasNext()) {
            OfferedPrice offeredPrice = iter.next();
            if (offeredPrice.getDriverId().equals(driverId)) {
                iter.remove();
            }
        }
        Iterator<OfferedPrice> iter2 = CurrentData.iterator();
        while (iter2.hasNext()) {
            OfferedPrice offeredPrice = iter2.next();
            if (offeredPrice.getDriverId().equals(driverId)) {
                iter2.remove();
            }
        }

        for(Order order : Order.getOrders()) {
            boolean has = false;
            for (OfferedPrice offeredPrice : OfferedPrices) {
                if (offeredPrice.getOrderNumber().equals(order.getOrderNumber())) {
                    has = true;
                }
            }

            if (!has) {
                order.setStatus("-");
            }
        }
    }

    public static void setCurrentData(String orderNumber){
        CurrentData.clear();
        for (OfferedPrice offeredPrice : OfferedPrices) {
            if (offeredPrice.getOrderNumber().equals(orderNumber)) {
                CurrentData.add(offeredPrice);
            }
        }
    }

    public static void deleteWhenDriverIsOnOrder(String driverId, String orderNumber){
        Iterator<OfferedPrice> iter = OfferedPrices.iterator();
        while (iter.hasNext()) {
            OfferedPrice offeredPrice = iter.next();
            if (!offeredPrice.getOrderNumber().equals(orderNumber) && offeredPrice.getDriverId().equals(driverId)) {
                iter.remove();
            }
        }

        Iterator<OfferedPrice> iter2 = CurrentData.iterator();
        while (iter2.hasNext()) {
            OfferedPrice offeredPrice = iter2.next();
            if (!offeredPrice.getOrderNumber().equals(orderNumber) && offeredPrice.getDriverId().equals(driverId)) {
                iter2.remove();
            }
        }

        for(Order order : Order.getOrders()) {
            boolean has = false;
            for (OfferedPrice offeredPrice : OfferedPrices) {
                if (offeredPrice.getOrderNumber().equals(order.getOrderNumber())) {
                    has = true;
                }
            }

            if (!has) {
                order.setStatus("-");
            }
        }
    }

    public static ObservableList<OfferedPrice> getCurrentData(){
        return CurrentData;
    }

    public String getDriverId() {
        return DriverId.get();
    }

    public StringProperty driverIdProperty() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        this.DriverId.set(driverId);
        Callsign.set(Driver.getDriver(driverId).getCallSign());
    }

    public String getOfferedPrice() {
        return OfferedPrice.get();
    }

    public StringProperty offeredPriceProperty() {
        return OfferedPrice;
    }

    public void setOfferedPrice(String offeredPrice) {
        this.OfferedPrice.set(offeredPrice);
    }

    public String getTime() {
        return Time.get();
    }

    public StringProperty timeProperty() {
        return Time;
    }

    public void setTime(String time) {
        this.Time.set(time);
        if(OrderNumber.equals(MonitorController.SelectedOrderNumber)) {
            boolean add = true;
            for (OfferedPrice offeredPrice : CurrentData) {
                if (offeredPrice.getDriverId().equals(this.DriverId) && offeredPrice.getOrderNumber().equals(this.OrderNumber)) {
                    add = false;
                }
            }
            if (add) {
                CurrentData.add(this);
            }
        }
        try {
            Thread.sleep(1500);
        }catch (Exception e){}
        Order order = Order.getOrder(OrderNumber);
        if(order != null) {
            if (order.getStatus().get().equals("-") || order.getStatus().get().equals("ОТК")) {
                order.setStatus("ЦН");
            }
        } else {
            System.out.println("order = null");
        }
    }

    public void onlySetTime(String time){
        this.Time.set(time);
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
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

    public static void clearOfferedPrices(){
        OfferedPrices.clear();
        CurrentData.clear();
    }
}
