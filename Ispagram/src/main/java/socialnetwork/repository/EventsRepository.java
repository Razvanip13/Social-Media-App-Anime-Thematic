package socialnetwork.repository;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;

public interface EventsRepository<ID,E extends Entity<ID>> extends Repository<ID,E>{


    E subscribe(E entity,ID id_user);

    E unsubscribe(E entity,ID id_user);


}
