package socialnetwork.domain.message;

import socialnetwork.domain.Entity;
import socialnetwork.domain.users.Utilizator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {


    Utilizator from;
    List<Utilizator> to;
    String message;
    LocalDateTime data;
    Message reply;


    public void setReply(Message reply){

        this.reply=reply;
    }


    /**
     *
     * @return returns the message to which this message replies
     */
    public Message getReply() {
        return reply;
    }

    public Message(Utilizator from, List<Utilizator> to, String message, LocalDateTime data) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.data = data;
        this.reply=null;
    }

    public Message(Utilizator from,String message,LocalDateTime data){

        this.from=from;
        this.message=message;
        this.data=data;
    }


    public Utilizator getFrom() {
        return from;
    }

    public List<Utilizator> getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getData() {
        return data;
    }

    @Override
    public String toString() {

        if(reply==null) {

            return  from.getFirstName() + " " + from.getLastName() + ": " + message + " [" + getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                    "] ";
        }

        return  "Reply for: " + reply.getMessage() + " [" + reply.getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +" ]\n       "
                + from.getFirstName() + " " + from.getLastName() + ": " + message + " [" + getData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +
                "] ";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return Objects.equals(getFrom(), message1.getFrom()) &&
                Objects.equals(getTo(), message1.getTo()) &&
                Objects.equals(getMessage(), message1.getMessage()) &&
                Objects.equals(getData(), message1.getData()) &&
                Objects.equals(getReply(), message1.getReply());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getMessage(), getData(), getReply());
    }
}
