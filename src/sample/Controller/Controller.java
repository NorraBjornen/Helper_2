package sample.Controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import org.json.JSONObject;
import sample.Main;
import sample.Model.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    private javafx.scene.control.Label Label;
    @FXML
    private TextField City, Phone, Street, House, Apartment, Entrance, Description, To, Price, Ok;
    private List<TextField> TextFieldList = new ArrayList<>();

    private final String USER_AGENT = "Mozilla/5.0";

    private String strTo = "";

    public void initialize() {
        TextFieldList.add(City);
        TextFieldList.add(Phone);
        TextFieldList.add(Street);
        TextFieldList.add(House);
        TextFieldList.add(Apartment);
        TextFieldList.add(Entrance);
        TextFieldList.add(Description);
        TextFieldList.add(To);
        TextFieldList.add(Price);
        TextFieldList.add(Ok);
        for (int i = 0; i < TextFieldList.size(); i++) {
            TextFieldList.get(i).setTextFormatter(new TextFormatter<>((change) -> {
                change.setText(change.getText().toUpperCase());
                return change;
            }));
            int c = i;
            TextFieldList.get(i).setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    if (c != TextFieldList.size() - 1) {
                        TextFieldList.get(c + 1).requestFocus();
                    }
                } else if (e.getCode().equals(KeyCode.LEFT)) {
                    if (c != 0) {
                        TextFieldList.get(c - 1).requestFocus();
                    } else {
                        TextFieldList.get(TextFieldList.size() - 1).requestFocus();
                    }
                } else if (e.getCode() == KeyCode.F1) {
                    Main.stage.setFullScreenExitHint("");
                    Main.stage.setFullScreen(true);
                } else if (e.getCode() == KeyCode.F2) {
                    Main.stage.setFullScreen(false);
                }
            });
        }

        To.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            switch (k) {
                case ENTER:
                    if (To.getText().equals("")) {
                        Price.requestFocus();
                        To.setText(strTo);
                        strTo = "";
                        Label.setText(strTo);
                    } else {

                        String le = "";

                        String route = To.getText();
                        if (route.contains("0") ||
                                route.contains("1") ||
                                route.contains("2") ||
                                route.contains("3") ||
                                route.contains("4") ||
                                route.contains("5") ||
                                route.contains("6") ||
                                route.contains("7") ||
                                route.contains("8") ||
                                route.contains("9")) {

                            String elements[] = route.split(" ");
                            if (elements.length > 1) {
                                String lastElement = elements[elements.length - 1];
                                if (lastElement.contains("0") ||
                                        lastElement.contains("1") ||
                                        lastElement.contains("2") ||
                                        lastElement.contains("3") ||
                                        lastElement.contains("4") ||
                                        lastElement.contains("5") ||
                                        lastElement.contains("6") ||
                                        lastElement.contains("7") ||
                                        lastElement.contains("8") ||
                                        lastElement.contains("9")) {

                                    le = lastElement;
                                    route = "";
                                    for (int i = 0; i < elements.length; i++) {
                                        if (!elements[i].equals(lastElement)) {
                                            route = route + " " + elements[i];
                                        }
                                    }

                                    if (!route.equals("")) {
                                        route = route.substring(1);
                                    }

                                }
                            }
                        }

                        To.setText(new DataBase().getCorrect(route) + " " + le);

                        if (strTo.equals("")) {
                            strTo = To.getText();
                            To.clear();
                        } else {
                            if (strTo.split("\\>").length != 3) {
                                strTo = strTo + " -->" + To.getText();
                                To.clear();
                            } else {
                                To.setText("ПОВРЕМЕННАЯ");
                                strTo = "";
                                Label.setText("");
                                Price.requestFocus();
                            }
                        }
                        Label.setText(strTo);
                    }
                    break;
                case BACK_SPACE:
                    if (To.getText().equals("")) {
                        if (strTo.contains(">")) {
                            String[] routes = strTo.split("\\>");
                            strTo = "";
                            for (int i = 0; i < routes.length - 1; i++) {
                                strTo = strTo + routes[i] + ">";
                            }
                            if (!strTo.equals("")) {
                                strTo = strTo.substring(0, strTo.length() - 4);
                            }
                            Label.setText(strTo);
                        } else {
                            strTo = "";
                            Label.setText("");
                        }
                    }
                    break;
                case LEFT:
                    Description.requestFocus();
                    break;
            }
        });

        House.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    new Thread(() -> {
                        String correct = new DataBase().getCorrect(Street.getText());
                        Platform.runLater(() -> {
                            Street.setText(correct);
                        });
                    }).start();
                } else {

                }
            }
        });

        Description.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    if (Ok.getText().equals("OK")) {
                        new Thread(() -> {

                            String from = City.getText() + "," + Street.getText();
                            if (House.getText().length() != 0) from = from + " " + House.getText();

                        }).start();
                    }
                } else {

                }
            }
        });

        Price.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                if (newPropertyValue) {
                    if (Ok.getText().equals("OK")) {
                        new Thread(() -> {
                            while (To.getText().length() == 0) {
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                }
                            }
                            getPrice();
                        }).start();
                    }
                } else {

                }
            }
        });

        Label.setText("");
        City.setText("КОСТАНАЙ");
        City.setFocusTraversable(false);
        Ok.setText("OK");
        Ok.setEditable(false);
        Phone.requestFocus();

        Ok.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                if (Ok.getText().equals("OK")) {

                    Order order = new Order();

                    if (City.getText().length() == 0) City.setText("КОСТАНАЙ");
                    order.setCity(City.getText());
                    if (Phone.getText().length() == 0) Phone.setText("-");
                    order.setPhone(Phone.getText());
                    if (Description.getText().length() == 0) Description.setText("-");
                    order.setDescription(Description.getText());
                    if (To.getText().length() == 0) To.setText("-");
                    order.setTo(To.getText());
                    if (Price.getText().length() == 0) Price.setText("-");
                    order.setPrice(Price.getText());


                    String from = City.getText() + "," + Street.getText();
                    if (House.getText().length() != 0) from = from + " " + House.getText();
                    if (Apartment.getText().length() != 0) from = from + " кв." + Apartment.getText();
                    if (Entrance.getText().length() != 0) from = from + " п." + Entrance.getText();
                    order.setFrom(from);

                    order.setOrderNumber("0");

                    String message = "[new~" + order.getFrom() + "$" + order.getTo() + "$" + order.getPrice() + "$" + order.getPhone() + "$" + order.getDescription() + "]";

                    NetWork.get().send(message);

                    if (City.getText().equals("КОСТАНАЙ")) {
                        from = Street.getText();
                        if (House.getText().length() != 0) from = from + " " + House.getText();
                        if (Apartment.getText().length() != 0) from = from + " кв." + Apartment.getText();
                        if (Entrance.getText().length() != 0) from = from + " п." + Entrance.getText();
                        order.setFrom(from);
                    }

                    Main.stage.setScene(Main.scene2);
                    //Main.stage.setFullScreen(true);
                    Main.stage.setFullScreenExitHint("");

                    Ok.setText("OK");
                    for (TextField textField : TextFieldList) {
                        if (!textField.equals(City) && !textField.equals(Ok)) {
                            textField.clear();
                        }
                    }

                    order.setStatus("-");

                    Phone.requestFocus();
                } else {
                    Order order = Order.getOrder(MonitorController.SelectedOrderNumber);
                    if (order != null) {

                        if (Phone.getText().length() == 0) Phone.setText("-");
                        String phone = Phone.getText();
                        if (Description.getText().length() == 0) Description.setText("-");
                        String description = Description.getText();
                        if (To.getText().length() == 0) To.setText("-");
                        String to = To.getText();
                        if (Price.getText().length() == 0) Price.setText("-");
                        String price = Price.getText();
                        String from = City.getText() + "," + Street.getText();
                        if (House.getText().length() != 0) from = from + " " + House.getText();
                        if (Apartment.getText().length() != 0) from = from + " кв." + Apartment.getText();
                        if (Entrance.getText().length() != 0) from = from + " п." + Entrance.getText();

                        order.setOriginalPrice(Price.getText());

                        NetWork.get().send("[upd~" + order.getOrderNumber() + "|" + from + "$" + to + "$" + price + "$" + phone + "$" + description + "]");

                        Main.stage.setScene(Main.scene2);
                        //Main.stage.setFullScreen(true);
                        Main.stage.setFullScreenExitHint("");

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
                    }
                }
            } else if (e.getCode().equals(KeyCode.LEFT)) {
                Price.requestFocus();
            }
        });
    }

    private void getPrice() {

        String to = To.getText();
        if (!to.equals("ПОВРЕМЕННАЯ")) {

            String[] waypoints = new String[1];
            waypoints[0] = "kek";

            if (to.contains(">")) {
                waypoints = to.split("\\>");

                for (int i = 0; i < waypoints.length; i++) {
                    if (waypoints[i].contains(" --")) {
                        waypoints[i] = waypoints[i].substring(0, waypoints[i].length() - 3);
                    }
                    waypoints[i] = "КОСТАНАЙ," + waypoints[i];
                    waypoints[i] = waypoints[i].replace(" ", "+");
                    System.out.println(waypoints[i]);
                }

                to = waypoints[waypoints.length - 1];
            }

            if (waypoints.length == 1) {
                to = "КОСТАНАЙ," + to;
            }

            int debarkations = waypoints.length;

            try {
                to = URLEncoder.encode(to, "utf-8");
            } catch (Exception e) {
            }
            System.out.println(to);

            String from = City.getText() + "," + Street.getText();
            if (House.getText().length() != 0) from = from + " " + House.getText();
            try {
                from = URLEncoder.encode(from, "utf-8");
            } catch (Exception e) {
            }

            try {
                String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + from + "&destination=" + to;
                if (!waypoints[0].equals("kek")) {
                    url = url + "&waypoints=";
                    for (int i = 0; i < waypoints.length - 1; i++) {
                        url = url + "via:" + waypoints[i] + "|";
                    }
                    url = url.substring(0, url.length() - 1);
                }
                url = url + "&key=AIzaSyCQvCgUzeezqLNHikWoR4-nq-zDiIZ35VE";
                System.out.println(url);
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", USER_AGENT);
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("cp1251")));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject obj1 = new JSONObject(response.toString());
                obj1 = (JSONObject) obj1.getJSONArray("routes").get(0);
                obj1 = (JSONObject) obj1.getJSONArray("legs").get(0);
                String distance = obj1.getJSONObject("distance").get("text").toString().split(" ")[0];

                double originToDestinationDistance = Double.parseDouble(distance);

                double coefficient = 32;
                double landingPrice = 300;
                double debarkationPrice = 100;

                double price = (coefficient * 0.5) + landingPrice + (coefficient * originToDestinationDistance) + debarkationPrice * (debarkations - 1);
                price = price / 10;

                if (price == (int) price) {
                    price = (int) price;
                } else {
                    price = (int) price;
                    price++;
                }

                double roundPrice = price * 10;

                Platform.runLater(() -> {
                    Price.setText(String.valueOf((int) roundPrice));
                });

                System.out.println("order distance = " + distance);

            } catch (Exception r) {
                System.out.println(r);
                Platform.runLater(() -> {
                    Price.setText("400");
                });
            }
        } else {
            Platform.runLater(() -> {
                Price.setText("700");
            });
        }
    }

}
