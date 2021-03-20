package socialnetwork.repository.file;

import socialnetwork.config.Constants;
import socialnetwork.domain.friendship.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDate;
import java.util.List;

public class PrietenieFileRepository extends AbstractFileRepository<Tuple<Long,Long>, Prietenie> {


    public PrietenieFileRepository(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    public Prietenie extractEntity(List<String> attributes) {

        Tuple<Long,Long> tuple;

        tuple=new Tuple<>(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1)));

        LocalDate date=LocalDate.parse(attributes.get(2));

        return new Prietenie(tuple,date);

    }

    @Override
    protected String createEntityAsString(Prietenie entity) {

        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";"
                + entity.getDate().format(Constants.DATE_TIME_FORMATTER);

    }
}
