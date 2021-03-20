package socialnetwork.repository.paging;

import java.util.stream.Stream;

public interface Page<E> {

    Pageable getPageable();

    Pageable nextPageable();

    void setPageable(Pageable pageable);

    Pageable previousPageable();

    Stream<E> getContent();


}