package socialnetwork.domain.validators;

import socialnetwork.domain.friendship.Prietenie;

public class PrietenieValidator implements Validator<Prietenie>{

    @Override
    public void validate(Prietenie entity) throws ValidationException {

        if(entity.getId().getRight().equals(entity.getId().getLeft())) {

            throw new ValidationException("Poti face o prietenie doar intre doua persoane diferite.");

        }
    }
}
