package ee490g.epfl.ch.dwarfsleepy.user;

import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String name;
    private String email;
    private String gender;
    private String birthday;

    public User(FirebaseUser firebaseUser, String name, String gender, String birthday) {
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
