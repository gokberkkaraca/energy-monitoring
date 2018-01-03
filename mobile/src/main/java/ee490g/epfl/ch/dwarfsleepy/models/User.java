package ee490g.epfl.ch.dwarfsleepy.models;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class User implements Serializable {

    private String userId;
    private String name;
    private String email;
    private Gender gender;
    private Date birthday;
    private double weight;
    private int height;

    public User(FirebaseUser firebaseUser, String name, Gender gender, Date birthday, double weight, int height) {
        this.userId = firebaseUser.getUid();
        this.name = name;
        this.email = firebaseUser.getEmail();
        this.gender = gender;
        this.birthday = birthday;
        this.weight = weight;
        this.height = height;
    }

    public User() {

    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public double getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public enum Gender {
        MALE, FEMALE
    }

    public int getAge() {
        Calendar calendar = Calendar.getInstance();

        Date currentTime = new Date();
        calendar.setTime(currentTime);
        int currentYear = calendar.get(Calendar.YEAR);

        calendar.setTime(birthday);
        int birthdayYear = calendar.get(Calendar.YEAR);

        return currentYear - birthdayYear;
    }

    public double getBMR(){
        double BMR;
        if (gender.equals(Gender.MALE))
            BMR = 66 + (13.75 * weight) + (5 * height) - (6.8 * getAge());
        else
            BMR = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * getAge());

        return BMR;
    }
}
