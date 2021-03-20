package socialnetwork.domain.frequest;

import socialnetwork.config.Status;

import java.time.LocalDate;

public class FRequestDTO {

    Long id;
    String firstName;
    String lastName;
    Status status;
    LocalDate friendRequestDate;

    public FRequestDTO(Long id,String firstName, String lastName, Status status, LocalDate friendshipRequestDate) {

        this.id=id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status=status;
        this.friendRequestDate = friendshipRequestDate;
    }

    public LocalDate getFriendRequestDate() {
        return friendRequestDate;
    }

    public Status getStatus() {
        return status;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "FRequestDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", status=" + status +
                ", friendRequestDate=" + friendRequestDate +
                '}';
    }
}
