package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepoException;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }


    /**
     *
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the found entity or null if it's not found
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /**
     *
     * @return an iterable with all entities from the repo
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }


    /**
     *
     * @param entity
     *         entity must be not null
     * @return null if it was saved or the entity if it wasn't saved, exception if the argument is illegal
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");

        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {

            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**
     *
     * @param id
     *      id must be not null
     * @return returns the deleted entity or throws exception if it's not found
     */
    @Override
    public E delete(ID id) {


        if(!entities.containsKey(id)) {

            throw new RepoException("Id does not exist");

        }

        return entities.remove(id);
    }

    /**
     *
     * @param entity
     *          entity must not be null
     * @return null if the entity is update or the entity if it's not found
     */
    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

       // entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

    /**
     *
     * @return the number of entities in the repo
     */

    public int size() {
        return entities.size();
    }

}
