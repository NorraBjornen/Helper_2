package sample.Model;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataBase {
    private Connection Connection;
    private Statement Statement;

    public DataBase(){
        String url = "jdbc:mysql://127.0.0.1:3306/disp?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String username = "root";
        String password = "deepundo99";
        try{
            Connection = DriverManager.getConnection(url, username, password);
            Statement = Connection.createStatement();
        } catch (SQLException e) {
            System.out.print(e);
        }
    }

    public void query(String sql){
        try {
            Statement.executeUpdate(sql);
        } catch (Exception e){}
    }

    public void go(){
        List<String> listWrong = new ArrayList<>();
        List<String> listCorrect = new ArrayList<>();
        try {
            String sql = "SELECT * FROM correct_streets";
            ResultSet resultSet = Statement.executeQuery(sql);
            while (resultSet.next()){

                boolean add = true;

                for(String string : listWrong){
                    if(string.trim().equals(resultSet.getString("Wrong").trim()) || resultSet.getString("Wrong").contains(">")){
                        add = false;
                    }
                }

                if(add){
                    listWrong.add(resultSet.getString("Wrong").trim());
                    listCorrect.add(resultSet.getString("Correct").trim());
                }
            }

            for(int i = 0; i < listWrong.size(); i++){
                sql = "INSERT INTO streets (`Wrong`, `Correct`) VALUES ('" + listWrong.get(i) + "', '" + listCorrect.get(i) + "')";
                query(sql);
            }

            System.out.println("OK");

        } catch (Exception e){
            System.out.println(e);
        }
    }

    public String getStreets(){
        String line = "";
        try {
            String sql = "SELECT Correct FROM streets";
            ResultSet resultSet = Statement.executeQuery(sql);
            while (resultSet.next()){
                if(!resultSet.getString("Correct").equals("-")) {
                    line = line + "|"  + resultSet.getString("Correct");
                }
            }

            if(line.equals("")){
                line = "NULL";
            } else {
                line = line.substring(1);
            }

        } catch (Exception e){
            System.out.println(e);
        }
        return line;
    }

    public String getCorrect(String wrong){
        try {
            String sql = "SELECT Correct FROM streets WHERE Wrong='" + wrong + "'";
            ResultSet resultSet = Statement.executeQuery(sql);
            if(resultSet.next()){
                if(!resultSet.getString("Correct").equals("-")) {
                    return resultSet.getString("Correct");
                } else {
                    return wrong;
                }
            } else {
                if(!wrong.equals("")) {
                    sql = "INSERT INTO streets (`Wrong`) VALUES ('" + wrong + "')";
                    query(sql);
                }
                return wrong;
            }
        } catch (Exception e){
            System.out.println(e);
            return wrong;
        }
    }
}

