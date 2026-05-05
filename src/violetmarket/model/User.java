package violetmarket.model;

import java.time.LocalDate;
import java.util.UUID;

public abstract class User {

    protected final String userId;
    protected String name;
    protected final String nyuEmail;
    protected String password;
    protected final LocalDate joinDate;

    public User(String name, String nyuEmail, String password) {
        this.userId   = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.name     = name;
        this.nyuEmail = nyuEmail;
        this.password = password;
        this.joinDate = LocalDate.now();
    }

    // Getters

    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public String getNyuEmail(){
        return nyuEmail;
    }

    public LocalDate getJoinDate(){
        return joinDate;
    }

    public boolean checkPassword(String input){
        return password.equals(input);
    }

    // Abstract

    public abstract String getRole();

    @Override
    public String toString(){
        return String.format("[%s] %s (%s) — joined %s", getRole(), name, nyuEmail, joinDate);
    }
}
