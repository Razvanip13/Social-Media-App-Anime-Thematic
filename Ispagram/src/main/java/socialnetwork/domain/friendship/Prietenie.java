package socialnetwork.domain.friendship;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Tuple;

import java.time.LocalDate;
import java.util.Objects;


public class Prietenie extends Entity<Tuple<Long,Long>> implements Comparable<Prietenie> {

    LocalDate date;
    Long id1;
    Long id2;

    public Prietenie(Tuple<Long,Long> tuple) {

        this.id1=tuple.getLeft();
        this.id2=tuple.getRight();
        this.setId(tuple);

        date=LocalDate.now();
    }

    public Prietenie(Tuple<Long,Long> tuple, LocalDate data){

        this.id1=tuple.getLeft();
        this.id2=tuple.getRight();
        this.setId(tuple);

        this.date=data;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDate getDate() {

        return date;
    }


    /**
     *
     * @return information about the respective friend in string format
     */
    @Override
    public String toString() {
        return "Prietenie: " + id1 + " " + id2 + " " + date;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prietenie)) return false;
        Prietenie prietenie = (Prietenie) o;

        return getId().getLeft().equals(prietenie.getId().getLeft()) && getId().getRight().equals(prietenie.getId().getRight())
                || getId().getRight().equals(prietenie.getId().getLeft()) && getId().getLeft().equals(prietenie.getId().getRight());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate());
    }

    @Override
    public int compareTo(Prietenie o) {

        if(this.getId().getLeft()<o.getId().getLeft())
            return -1;

        if(this.getId().getLeft()>o.getId().getLeft())
            return 1;

        return this.getId().getRight().compareTo(o.getId().getRight());

    }
}
