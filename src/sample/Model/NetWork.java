package sample.Model;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.TextFields;
import sample.Controller.MonitorController;
import sample.Main;
import sample.View.Browser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static sample.Main.scene2;
import static sample.Main.scene4;

public class NetWork extends Constants{
    private static NetWork NetWork;
    private static final String IP_HOST = "83.166.242.161";
    private static final String IP_LOCAL = "192.168.1.153";
    private static final String IPL = "127.0.0.1";
    private static final String IP = IP_HOST;
    private static int Port = 1489;
    private java.net.Socket Socket;
    private boolean already_reconnecting;
    private List<String> messagesToSend;
    private Thread pinging;

    public static NetWork get(){
        if(NetWork == null){
            NetWork = new NetWork();
        }
        return NetWork;
    }

    private NetWork(){
        messagesToSend = new ArrayList<>();
        already_reconnecting = false;
        try {
            Socket = new Socket(IP, Port);
            sendWithoutSave("[ping~ok]");
            new Thread(() -> listen()).start();
        } catch (Exception e){
            System.out.println(e.toString() + " while construct");
            reconnect();
        }
    }

    private void reconnect(){
        if(!already_reconnecting) {
            already_reconnecting = true;
            new Thread(() -> {
                System.out.println("reconnection started");
                if(pinging != null) {
                    pinging.interrupt();
                }
                while (!connect()) {
                    try {Thread.sleep(1000);} catch (Exception e) {}
                }
                try{Thread.sleep(2000);}catch (Exception e){}
                new Thread(() -> listen()).start();
                String msg = "";
                for (String message : messagesToSend) {
                    if (!message.equals("[i~dsp]")) {
                        msg = msg + message;
                    }
                }
                if (!msg.equals("")) {
                    sendWithoutSave(msg);
                } else {
                    sendWithoutSave("[ping~ok]");
                }
            }).start();
        }
    }

    public void listen(){
        try(InputStream in = Socket.getInputStream()){
            pinging = new Thread(()->{
                while (!Socket.isClosed()){
                    try{
                        Thread.sleep(10000);
                        try{Socket.close();} catch (Exception e){}
                        reconnect();
                    } catch (Exception e){

                    }
                }
                try{Socket.close();} catch (Exception e){}
            });
            pinging.start();

            byte[] data = new byte[128 * 1024];

            try{Thread.sleep(2500);} catch (Exception e){}

            while (!Socket.isClosed()){
                int readBytes = in.read(data);
                String message = new String(data, 0, readBytes);
                adapt(message);
            }
        } catch (Exception e){
            System.out.println(e.toString() + " while listen");
            reconnect();
        }
    }

    private void adapt(String message){
        if(message.equals("[ping~ok]")){
            message = "ping - " + new SimpleDateFormat("yyMMddHHmmss").format(new Date());
            System.out.println(message);
        } else {
            System.out.println(message);
            String[] messages = message.split("\\]");
            for (String str : messages) {
                str = str.substring(1);
                identify(str);
            }

        }
        pinging.interrupt();
    }

    private void identify(String message) {
        try {
            String command = message.split(COMMAND_DELIMITER)[0];
            String command_text = message.split(COMMAND_DELIMITER)[1];
            switch (command) {
                case COMMAND_GET:
                    if (!command_text.equals("NULL")) {
                        List<String> orderNumbers = new ArrayList<>();
                        String[] ordersTexts = command_text.split(TOKEN_DELIMITER);
                        for (String string : ordersTexts) {
                            String elements[] = string.split(ELEMENT_DELIMITER);
                            String orderNumber = elements[0];
                            orderNumbers.add(orderNumber);
                            Order order = Order.getOrder(orderNumber);
                            if(order == null) {
                                order = new Order();
                                order.setCity(elements[1].split(",")[0]);
                                if(elements[1].split(",")[0].equals("КОСТАНАЙ")) {
                                    order.setFrom(elements[1].split(",")[1]);
                                } else {
                                    order.setFrom(elements[1]);
                                }
                                order.setOrderNumber(orderNumber);
                                order.setTo(elements[2]);
                                order.setPrice(elements[3]);
                                order.setPhone(elements[4]);
                                order.setDescription(elements[5]);
                                order.setCallsign(elements[7]);
                                order.setStatus(Integer.parseInt(elements[8]));
                                order.setTime(elements[9]);
                                order.add();
                            } else {
                                order.setCity(elements[1].split(",")[0]);
                                if(elements[1].split(",")[0].equals("КОСТАНАЙ")) {
                                    order.setFrom(elements[1].split(",")[1]);
                                } else {
                                    order.setFrom(elements[1]);
                                }
                                order.setOrderNumber(orderNumber);
                                order.setTo(elements[2]);
                                order.setPrice(elements[3]);
                                order.setPhone(elements[4]);
                                order.setDescription(elements[5]);
                                order.setCallsign(elements[7]);
                                order.setStatus(Integer.parseInt(elements[8]));
                                order.setTime(elements[9]);
                                MonitorController.setOrderInfoToLabels(order);
                            }
                        }
                        Order.removeUnwantedOrders(orderNumbers);
                    } else {
                        Order.clearOrders();
                    }
                    break;
                case COMMAND_NEW:
                    String elements[] = command_text.split(ELEMENT_DELIMITER);
                    String orderNumber = elements[0];
                    if(Order.getOrder(orderNumber) == null) {
                        Order order = new Order();
                        order.setCity(elements[1].split(",")[0]);
                        if(elements[1].split(",")[0].equals("КОСТАНАЙ")) {
                            order.setFrom(elements[1].split(",")[1]);
                        } else {
                            order.setFrom(elements[1]);
                        }
                        order.setOrderNumber(orderNumber);
                        order.setTo(elements[2]);
                        order.setPrice(elements[3]);
                        order.setPhone(elements[4]);
                        order.setDescription(elements[5]);
                        order.setCallsign(elements[7]);
                        order.setStatus(Integer.parseInt(elements[8]));
                        order.setTime(elements[9]);
                        order.add();
                    }
                    break;
                case COMMAND_UPDATE:
                    elements = command_text.split(ELEMENT_DELIMITER);

                    Order order = Order.getOrder(elements[0]);
                    if(order != null) {
                        order.setCity(elements[1].split(",")[0]);
                        if (elements[1].split(",")[0].equals("КОСТАНАЙ")) {
                            order.setFrom(elements[1].split(",")[1]);
                        } else {
                            order.setFrom(elements[1]);
                        }
                        order.setTo(elements[2]);
                        order.setPrice(elements[3]);
                        order.setPhone(elements[4]);
                        order.setDescription(elements[5]);
                        order.setCallsign(elements[7]);
                        order.setStatus(Integer.parseInt(elements[8]));
                        order.setTime(elements[9]);

                        MonitorController.setOrderInfoToLabels(order);
                    }
                    break;
                case COMMAND_GET_DRIVERS:
                    Driver.clearDrivers();
                    if (!command_text.equals("NULL")) {
                        String str[] = command_text.split(TOKEN_DELIMITER);
                        for (String string : str) {
                            elements = string.split(ELEMENT_DELIMITER);
                            Driver driver = new Driver();
                            driver.setDriverId(elements[0]);
                            driver.setCallSign(elements[1]);
                            driver.setCarNumber(elements[2]);
                            driver.setDescription(elements[3]);
                            driver.setStatus(elements[4]);
                            driver.setPosition(elements[5]);
                            driver.setRadius(elements[6]);
                        }
                    }
                    break;
                case COMMAND_GET_OFFERED_PRICES:
                    OfferedPrice.clearOfferedPrices();
                    if (!command_text.equals("NULL")) {
                        String[] offeredPricesTexts;
                        if(command_text.contains("|")) {
                            offeredPricesTexts = command_text.split(TOKEN_DELIMITER);
                        } else {
                            offeredPricesTexts = new String[]{command_text};
                        }
                        for (String string : offeredPricesTexts) {
                            elements = string.split(ELEMENT_DELIMITER);
                            OfferedPrice offeredPrice = new OfferedPrice();
                            offeredPrice.setDriverId(elements[0]);
                            offeredPrice.setOrderNumber(elements[1]);
                            offeredPrice.setOfferedPrice(elements[2]);
                            offeredPrice.setTime(elements[3]);

                            for(Order order1 : Order.getOrders()){
                                if(order1.getOrderNumber().equals(offeredPrice.getOrderNumber()) && (order1.getStatusInt() == 4 || order1.getStatusInt() == 2 || order1.getStatusInt() == 1)){
                                    order1.setPrice(offeredPrice.getOfferedPrice());
                                }
                            }

                        }
                    }
                    break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                case COMMAND_ONLINE:
                    elements = command_text.split(ELEMENT_DELIMITER);
                    Driver driver = new Driver();
                    driver.setDriverId(elements[0]);
                    driver.setCallSign(elements[1]);
                    driver.setCarNumber(elements[2]);
                    driver.setDescription(elements[3]);
                    driver.setStatus(elements[4]);
                    driver.setPosition(elements[5]);
                    driver.setRadius(elements[6]);
                    System.out.println(command_text);
                    break;
                case COMMAND_OFFER:
                    elements = command_text.split(ELEMENT_DELIMITER);
                    OfferedPrice offeredPrice = new OfferedPrice();
                    offeredPrice.setDriverId(elements[0]);
                    offeredPrice.setOrderNumber(elements[1]);
                    offeredPrice.setOfferedPrice(elements[2]);
                    offeredPrice.setTime(elements[3]);
                    break;
                case COMMAND_UPDATE_PRICE:
                    elements = command_text.split(ELEMENT_DELIMITER);
                    offeredPrice = OfferedPrice.getOfferedPrice(elements[1], elements[0]);
                    offeredPrice.setOfferedPrice(elements[2]);
                    offeredPrice.onlySetTime(elements[3]);
                    break;
                case COMMAND_GEO:
                    String driverId = command_text.split(TOKEN_DELIMITER)[0];
                    String position = command_text.split(TOKEN_DELIMITER)[1];
                    Driver.getDriver(driverId).setPosition(position);
                    System.out.println(position);
                    break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                case COMMAND_ACCEPT:
                    driverId = command_text.split(ELEMENT_DELIMITER)[0];
                    orderNumber = command_text.split(ELEMENT_DELIMITER)[1];
                    order = Order.getOrder(orderNumber);
                    if(order != null) {
                        order.setStatus("ПРН");
                        driver = Driver.getDriver(driverId);
                        if(driver != null) {
                            Order.bindOrder(orderNumber, driver.getCallSign(), OfferedPrice.getOfferedPrice(orderNumber, driverId).getOfferedPrice());
                            Label DriverInfo = (Label) scene2.lookup("#DriverInfo");

                            order.setCallsign(driver.getCallSign());
                            String driverInfo = driver.getDescription() + " " + driver.getCarNumber();
                            Platform.runLater(() -> {
                                if (MonitorController.SelectedOrderNumber.equals(orderNumber)) {
                                    DriverInfo.setText(driverInfo);
                                }
                            });
                            OfferedPrice.deleteWhenDriverIsOnOrder(driverId, orderNumber);
                        }
                    }
                    break;
                case COMMAND_REFUSE:
                    driverId = command_text.split(ELEMENT_DELIMITER)[0];
                    orderNumber = command_text.split(ELEMENT_DELIMITER)[1];
                    OfferedPrice.removeOfferedPrice(orderNumber, driverId);
                    order = Order.getOrder(orderNumber);
                    if(order != null) {
                        if (!order.getCallsign().equals("-")) {
                            order.setStatus("ОТК");
                            order.setCallsign("ОТК");
                            order.setPrice(order.getOriginalPrice());
                            order.setTime("-");
                            if (MonitorController.SelectedOrderNumber.equals(orderNumber)) {
                                Label DriverInfo = (Label) scene2.lookup("#DriverInfo");
                                Platform.runLater(() -> {
                                    DriverInfo.setText("");
                                });
                            }
                        }
                        boolean clear = true;
                        for (OfferedPrice offeredPrice1 : OfferedPrice.getOfferedPrices()) {
                            if (offeredPrice1.getOrderNumber().equals(order.getOrderNumber())) {
                                clear = false;
                                break;
                            }
                        }
                        if (clear) {
                            order.setStatus("-");
                            order.setCallsign("-");
                        }
                    }
                    break;
                case COMMAND_COMPLETED:
                    driverId = command_text.split(ELEMENT_DELIMITER)[0];
                    orderNumber = command_text.split(ELEMENT_DELIMITER)[1];
                    Order.removeOrder(orderNumber);
                    OfferedPrice.removeAllOfferedPrices(orderNumber);
                    break;
                case COMMAND_HURRY:
                    driverId = command_text.split(ELEMENT_DELIMITER)[0];
                    orderNumber = command_text.split(ELEMENT_DELIMITER)[1];
                    order = Order.getOrder(orderNumber);
                    if(order != null){
                        order.setStatus("ТРП");
                    }
                    break;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                case COMMAND_EXIT:
                    driverId = command_text;
                    driver = Driver.getDriver(driverId);
                    if(driver != null){
                        driver.exit();
                    }
                    break;
                case "clr":
                    messagesToSend.clear();
                    System.out.println("messages cleared");
                    break;
                case "time":
                    order = Order.getOrder(command_text.split(ELEMENT_DELIMITER)[0]);
                    if(order != null) {
                        order.setTime(command_text.split(ELEMENT_DELIMITER)[1]);
                    }
                    break;
                case "sts":
                    driverId = command_text.split(ELEMENT_DELIMITER)[0];
                    String status = command_text.split(ELEMENT_DELIMITER)[1];
                    driver = Driver.getDriver(driverId);
                    if(driver != null){
                        driver.setStatus(status);
                    }
                    break;
                case "del":
                    Order.removeOrder(command_text);
                    OfferedPrice.removeAllOfferedPrices(command_text);
                    break;
                default:
                    //System.out.println(message);
                    break;
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void sendWithoutSave(String message){
        try{OutputStream output = Socket.getOutputStream();
            output.write(message.getBytes(StandardCharsets.UTF_8));
            output.flush();
        } catch (Exception e){
            System.out.println(e.toString() + "while send without save");
            reconnect();
        }
    }

    public void send(String message){
        messagesToSend.add(message);
        try{OutputStream output = Socket.getOutputStream();
            output.write(message.getBytes(StandardCharsets.UTF_8));
            output.flush();
        } catch (Exception e){
            System.out.println(e.toString() + "while send");
            reconnect();
        }
    }

    public boolean connect(){
        try {
            if (Socket.isClosed()) {
                try {
                    Socket = new Socket(IP, Port);
                    already_reconnecting = false;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e){
            try {
                Socket = new Socket(IP, Port);
                already_reconnecting = false;
                return true;
            } catch (Exception a) {
                return false;
            }
        }
    }

}
