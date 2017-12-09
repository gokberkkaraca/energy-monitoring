package ee490g.epfl.ch.dwarfsleepy.user;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    public enum Gender {
        MALE, FEMALE
    }

    private String userId;
    private String name;
    private String email;
    private Gender gender;
    private Date birthday;

    public User(FirebaseUser firebaseUser, String name, Gender gender, Date birthday) {
        this.userId = firebaseUser.getUid();
        this.name = name;
        this.email = firebaseUser.getEmail();
        this.gender = gender;
        this.birthday = birthday;
    }

    public User(){

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
}
