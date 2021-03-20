package socialnetwork.repository.paging;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Paginator<E> {
    private Pageable pageable;
    private final Iterable<E> elements;

    public Paginator(Pageable pageable, Iterable<E> elements) {

        this.pageable = pageable;
        this.elements = elements;
    }


    //imi da urmatoarea pagina
    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }


    public Page<E> paginate() {

        Stream<E> result = StreamSupport
                .stream(elements.spliterator(), false)
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize());


        return new PageImplementation<>(pageable, result);
    }
}