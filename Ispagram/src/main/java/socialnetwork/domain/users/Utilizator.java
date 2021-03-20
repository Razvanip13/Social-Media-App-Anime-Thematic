package socialnetwork.domain.users;

import socialnetwork.domain.Entity;

import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String username;
    private String password;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    private String confirmPassword;


    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Utilizator(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }


    public Utilizator(String firstName, String lastName, String username, String password,String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.confirmPassword=confirmPassword;
    }



    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    @Override
    public String toString() {
        return getId()+ " "+ firstName + " " + lastName;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return  getUsername().equals(that.getUsername()) && getPassword().equals(that.getPassword());


    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }
}