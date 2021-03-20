package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;
//import sun.nio.ch.Util;

import java.io.*;

import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sau cu lambda - curs 4, sem 4 si 5
//        Path path = Paths.get(fileName);
//        try {
//            List<String> lines = Files.readAllLines(path);
//            lines.forEach(linie -> {
//                E entity=extractEntity(Arrays.asList(linie.split(";")));
//                super.save(entity);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    /**
     *
     * @param entity : E
     *         entity must be not null
     * @return the entity if it was saved
     */
    @Override
    public E save(E entity){
        E e=super.save(entity);
        if (e==null)
        {
            writeToFile();
        }
        return e;

    }

    /**
     *
     * @param id : ID
     *      id must be not null
     * @return deleted entity of type or null on the contrary
     */
    @Override
    public E delete(ID id) {

        E sters = super.delete(id);

        writeToFile();

        return sters;
    }

    /**
     *
     * @param entity : E
     *          entity must not be null
     * @return null if the entity was updated or the entity if it's not found
     */
    @Override
    public E update(E entity) {

        E the_entity=super.update(entity);

        writeToFile();

        return the_entity;
    }

    /**
     * Writes the entities to the file
     */
    protected void writeToFile(){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName))) {

            for(E entity : findAll()) {

                bW.write(createEntityAsString(entity));
                bW.newLine();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {


            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    */



}

