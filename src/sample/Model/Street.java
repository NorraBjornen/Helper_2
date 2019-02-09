package sample.Model;

import java.util.ArrayList;
import java.util.List;

public class Street {
    private String Wrong, Correct;
    private static List<Street> Streets = new ArrayList<>();

    public Street(){
        Streets.add(this);
    }

    public static List<Street> getStreets() {
        return Streets;
    }

    public String getWrong() {
        return Wrong;
    }

    public void setWrong(String wrong) {
        Wrong = wrong;
    }

    public String getCorrect() {
        return Correct;
    }

    public void setCorrect(String correct) {
        Correct = correct;
    }
}
