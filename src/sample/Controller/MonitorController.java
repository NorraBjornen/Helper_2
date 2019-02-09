package sample.Controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sample.Main;
import sample.Model.Driver;
import sample.Model.NetWork;
import sample.Model.OfferedPrice;
import sample.Model.Order;

import java.util.List;

public class MonitorController {
    @FXML
    public Label Address, Route, Price, Description, DriverInfo, Phone;
    @FXML
    private TableView<OfferedPrice> Market;
    @FXML
    private TableView<Order> Journal;
    @FXML
    TableColumn<Order, String> AddressC, CallsignC, PriceC, StatusC, TimeC;
    @FXML
    TableColumn<OfferedPrice, String> CallsignD, PriceD, TimeD;

    private ObservableList<Order> OrderData = Order.getOrders();
    private ObservableList<OfferedPrice> MarketData = OfferedPrice.getCurrentData();

    private SortedList<Order> SortedList;

    public static String SelectedOrderNumber = "0";

    public void initialize() {
        Address.setText("");
        Route.setText("");
        Price.setText("");
        Description.setText("");
        DriverInfo.setText("");
        Phone.setText("");

        TimeC.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        AddressC.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        CallsignC.setCellValueFactory(cellData -> cellData.getValue().callsignProperty());
        PriceC.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        StatusC.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        SortedList = new SortedList<>( OrderData,
                (Order order1, Order order2) -> {
                    if( order1.getStatusInt() > order2.getStatusInt() ) {
                        return -1;
                    } else if( order1.getStatusInt() < order2.getStatusInt() ) {
                        return 1;
                    } else {
                        return 0;
                    }
                });

        Journal.setItems(SortedList);
        Journal.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, order) -> {
            if (order != null) {
                Address.setText(order.getFrom());
                Route.setText(order.getTo());
                Price.setText(order.getOriginalPrice());
                Description.setText(order.getDescription());
                try {
                    String driverInfo = Driver.getDriverByCallSign(order.getCallsign()).getDescription() + " " + Driver.getDriverByCallSign(order.getCallsign()).getCarNumber();
                    DriverInfo.setText(driverInfo);
                } catch (Exception e){
                    DriverInfo.setText("");
                }
                Phone.setText(order.getPhone());
                OfferedPrice.setCurrentData(order.getOrderNumber());
                SelectedOrderNumber = order.getOrderNumber();
            } else {
                Address.setText("");
                Route.setText("");
                Price.setText("");
                Description.setText("");
                DriverInfo.setText("");
                Phone.setText("");
            }
        });
        Journal.setOnKeyPressed((KeyEvent keyEvent)-> {
            KeyCode key = keyEvent.getCode();
            switch (key){
                case DOWN:
                    if(OrderData.size() == 1) {
                        Journal.getSelectionModel().select(0);
                        Journal.getFocusModel().focus(0);
                    }
                    break;
                case RIGHT:
                    if(MarketData.size() != 0){
                        Market.requestFocus();
                        Market.getSelectionModel().select(0);
                        Market.getFocusModel().focus(0);
                    }
                    break;
                case DELETE:
                    String orderNumber = Journal.getSelectionModel().getSelectedItem().getOrderNumber();
                    String msg = "[del~"+orderNumber+"]";
                    NetWork.get().send(msg);
                    break;
                case LEFT:
                    if(Journal.getItems().equals(Order.getOrders())) {

                        Journal.setItems(Order.getWorking());
                    } else {
                        Journal.setItems(SortedList);
                    }
                    break;
                case SHIFT:
                    //Order.getOrder(Journal.getSelectionModel().getSelectedItem().getOrderNumber()).setStatus("ОК");
                    NetWork.get().send("[trp~"+Driver.getDriverByCallSign(Journal.getSelectionModel().getSelectedItem().getCallsign()).getDriverId()+"$"+Journal.getSelectionModel().getSelectedItem().getOrderNumber()+"]");
                    break;
                case ALT:
                    if(Journal.getSelectionModel().getSelectedItem() != null) {
                        Order order = Order.getOrder(Journal.getSelectionModel().getSelectedItem().getOrderNumber());
                        if (order != null) {
                            TextField City = (TextField) Main.scene1.lookup("#City");
                            TextField Phone = (TextField) Main.scene1.lookup("#Phone");
                            TextField Street = (TextField) Main.scene1.lookup("#Street");
                            TextField House = (TextField) Main.scene1.lookup("#House");
                            TextField Description = (TextField) Main.scene1.lookup("#Description");
                            TextField To = (TextField) Main.scene1.lookup("#To");
                            TextField Price = (TextField) Main.scene1.lookup("#Price");
                            TextField Ok = (TextField) Main.scene1.lookup("#Ok");

                            String address = order.getFrom();

                            String[] tokens = address.split(" ");
                            int length = tokens.length;

                            if (address.contains("0") ||
                                    address.contains("1") ||
                                    address.contains("2") ||
                                    address.contains("3") ||
                                    address.contains("4") ||
                                    address.contains("5") ||
                                    address.contains("6") ||
                                    address.contains("7") ||
                                    address.contains("8") ||
                                    address.contains("9")) {

                                String street = "";
                                String house;

                                house = tokens[length - 1];
                                for (int a = 0; a < length - 1; a++) {
                                    street = street + " " + tokens[a];
                                }

                                if (street.substring(0, 1).equals(" ")) {
                                    street = street.substring(1);
                                }

                                Street.setText(street);
                                House.setText(house);
                            } else {
                                Street.setText(address);
                            }

                            City.setText("КОСТАНАЙ");
                            Phone.setText(order.getPhone());
                            Description.setText(order.getDescription());
                            To.setText(order.getTo());
                            Price.setText(order.getPrice());
                            Ok.setText("UP");
                            Phone.requestFocus();
                            Main.stage.setScene(Main.scene1);
                            //Main.stage.setFullScreen(true);
                            Main.stage.setFullScreenExitHint("");
                        }
                    }
                    break;
            }
        });

        CallsignD.setCellValueFactory(cellData2 -> cellData2.getValue().callsignProperty());
        PriceD.setCellValueFactory(cellData2 -> cellData2.getValue().offeredPriceProperty());
        TimeD.setCellValueFactory(cellData2 -> cellData2.getValue().timeProperty());
        Market.setItems(MarketData);

        Market.setOnKeyPressed((KeyEvent keyEvent)-> {
            KeyCode key = keyEvent.getCode();
            switch (key){
                case LEFT:
                    if(OrderData.size() != 0){
                        Journal.requestFocus();
                        Journal.getSelectionModel().select(0);
                        Journal.getFocusModel().focus(0);
                    }
                    break;
                case ENTER:
                    OfferedPrice offeredPrice = Market.getSelectionModel().getSelectedItem();
                    String orderNumber = offeredPrice.getOrderNumber();
                    //Order.bindOrder(orderNumber, offeredPrice.getCallsign(), offeredPrice.getOfferedPrice());
                    Order order = Order.getOrder(orderNumber);
                    /*Address.setText(order.getFrom());
                    Route.setText(order.getTo());
                    Price.setText(order.getPrice());
                    Description.setText(order.getDescription());

                    Driver driver = Driver.getDriverByCallSign(order.getCallsign());
                    String driverInfo = driver.getDescription() + " " + driver.getCarNumber();
                    DriverInfo.setText(driverInfo);
                    Phone.setText(order.getPhone());*/
                    Journal.requestFocus();
                    Journal.getSelectionModel().select(0);
                    Journal.getFocusModel().focus(0);

                    NetWork.get().send("[go~" + order.getOrderNumber() + "$" + offeredPrice.getDriverId() + "$1]");
                    break;
            }
        });

    }

    public static void setOrderInfoToLabels(Order order){
        Platform.runLater(()->{
            if (MonitorController.SelectedOrderNumber.equals(order.getOrderNumber())) {
                Label Address = (Label) Main.scene2.lookup("#Address");
                Label Route = (Label) Main.scene2.lookup("#Route");
                Label Price = (Label) Main.scene2.lookup("#Price");
                Label Description = (Label) Main.scene2.lookup("#Description");
                Label DriverInfo = (Label) Main.scene2.lookup("#DriverInfo");
                Label Phone = (Label) Main.scene2.lookup("#Phone");
                Address.setText(order.getFrom());
                Route.setText(order.getTo());
                Price.setText(order.getPrice());
                Description.setText(order.getDescription());
                try {
                    String driverInfo = Driver.getDriverByCallSign(order.getCallsign()).getDescription() + " " + Driver.getDriverByCallSign(order.getCallsign()).getCarNumber();
                    DriverInfo.setText(driverInfo);
                } catch (Exception ee) {
                    DriverInfo.setText("");
                }
                Phone.setText(order.getPhone());
            }
        });
    }
}
