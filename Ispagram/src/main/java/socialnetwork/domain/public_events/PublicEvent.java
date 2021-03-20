package socialnetwork.domain.public_events;

import socialnetwork.domain.Entity;
import socialnetwork.domain.users.Utilizator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PublicEvent extends Entity<Long> {

    String name;
    LocalDate date;
    List<Long> subcribers;
    String location;

    String description;
    Utilizator organizer;

    String full_name;

    Integer notify;

    ImageView bell;

    Image image;

    public ImageView getBell() {
        return bell;
    }

    public Integer getNotify() {
        return notify;
    }

    public PublicEvent(String name, LocalDate date, List<Long> subcribers) {

        this.name = name;
        this.subcribers = subcribers;
        this.date=date;
    }


    public PublicEvent(String name, LocalDate date, String desciption, Utilizator organizer) {
        this.name = name;
        this.date = date;
        this.description = desciption;
        this.organizer = organizer;

        full_name=organizer.getFirstName()+" "+organizer.getLastName();
    }


    public PublicEvent(String name, LocalDate date, String location, String description, Utilizator organizer) {
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
        this.organizer = organizer;
        this.full_name = organizer.getFirstName()+" "+organizer.getLastName();
    }

    public PublicEvent(String name, LocalDate date,Integer notify) {
        this.name = name;
        this.date = date;
        this.notify=notify;



        if(notify==1) {

           image=new Image("\\views\\icons\\fill_bell2.png");

        }
        else {

            image=new Image("\\views\\icons\\empty_bell2.png");
        }

        bell=new ImageView();

        bell.setImage(image);

    }

    public String getLocation() {
        return location;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getDescription() {
        return description;
    }

    public Utilizator getOrganizer() {
        return organizer;
    }



    public String getName() {
        return name;
    }

    public List<Long> getSubcribers() {
        return subcribers;
    }


    public LocalDate getDate() {
        return date;
    }


    @Override
    public String toString() {


        long time= ChronoUnit.DAYS.between(LocalDate.now(), this.getDate());


        if(time==0){

            return '"'+ name + '"' +" today!";
        }

        if(time==1){

            return '"'+ name + '"'+ " tomorrow";

        }


        return '"'+ name + '"'+" in "+ time  + " days";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicEvent)) return false;
        PublicEvent that = (PublicEvent) o;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDate(), getSubcribers());
    }
}
