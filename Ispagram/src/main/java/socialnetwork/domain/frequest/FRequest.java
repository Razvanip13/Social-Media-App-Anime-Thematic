package socialnetwork.domain.frequest;

import socialnetwork.config.Status;
import socialnetwork.domain.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class FRequest extends Entity<Long> {

    Long from;
    Long to;
    //String status;
    Status status;
    LocalDate localDate;

    public FRequest(long from, long to, Status status) {
        this.from = from;
        this.to = to;
        this.status = status;

        localDate= LocalDate.now();
    }

    public FRequest(Long from, Long to, Status status, LocalDate localDate) {
        this.from = from;
        this.to = to;
        this.status = status;
        this.localDate = localDate;
    }

    public Long getFrom() {
        return from;
    }

    public Long getTo() {
        return to;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    @Override
    public String toString() {
        return getId()+ " " +from +" " +to+ " " + status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FRequest)) return false;
        FRequest request = (FRequest) o;

        return this.getFrom().equals(request.getFrom()) && getTo().equals(request.getTo()) && request.getStatus()==Status.pending;
                //|| this.getFrom().equals(request.getTo()) && getTo().equals(request.getFrom());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getStatus());
    }


}
