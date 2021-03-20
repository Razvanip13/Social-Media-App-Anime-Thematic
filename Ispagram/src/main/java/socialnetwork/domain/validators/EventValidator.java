package socialnetwork.domain.validators;

import socialnetwork.domain.public_events.PublicEvent;

import java.time.LocalDate;

public class EventValidator implements Validator<PublicEvent>{
    @Override
    public void validate(PublicEvent entity) throws ValidationException {

        String mesaj="";
        boolean passed=true;


        if(entity.getDate().isBefore(LocalDate.now())){

            passed=false;

            mesaj+="You cannot make an event on a date which passed\n";
        }


        if(entity.getLocation().equals("")){

            passed=false;

            mesaj+="Your event does not have a location.\n";
        }

        if(entity.getName().equals("")){

            passed=false;

            mesaj+="Your event does not have a name.\n";
        }

        if(entity.getDescription().equals("")){

            passed=false;

            mesaj+="Your event does not have a description.\n";
        }


        if(!passed)
            throw new ValidationException(mesaj);


    }
}
