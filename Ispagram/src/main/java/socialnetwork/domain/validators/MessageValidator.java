package socialnetwork.domain.validators;

import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.message.Message;
import socialnetwork.repository.Repository;
import socialnetwork.service.ServiceException;

public class MessageValidator implements Validator<Message> {

    private final Repository<Long, Utilizator> utilizatorRepository;



    public MessageValidator(Repository<Long, Utilizator> utilizatorRepository) {
        this.utilizatorRepository = utilizatorRepository;
    }



    @Override
    public void validate(Message entity) throws ValidationException {


        String mesaj = "";
        boolean clean = true;



        if (entity.getMessage().equals("")) {

            clean = false;
            mesaj = mesaj + "The message cannot be null";
        }

        if(entity.getTo().contains(utilizatorRepository.findOne(entity.getFrom().getId()))){

            clean =false;
            mesaj=mesaj + "You cannot send a message to your own self";
        }


        if (!clean)
            throw new ServiceException(mesaj);


    }
}
