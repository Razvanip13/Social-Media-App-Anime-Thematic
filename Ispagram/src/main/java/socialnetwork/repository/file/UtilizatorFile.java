package socialnetwork.repository.file;

import socialnetwork.domain.users.Utilizator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UtilizatorFile extends AbstractFileRepository<Long, Utilizator>{


    /**
     * UtilizatorFile repo constructor
     * @param fileName
     * @param validator
     */
    public UtilizatorFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    /**
     *
     * @param attributes List of String
     * @return an object of type Utilizator made from the string attributes
     */
    @Override
    public Utilizator extractEntity(List<String> attributes) {
        //TODO: implement method
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    /**
     *
     * @param entity: Utilizator
     * @return a string with the attributes of entity
     */
    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
