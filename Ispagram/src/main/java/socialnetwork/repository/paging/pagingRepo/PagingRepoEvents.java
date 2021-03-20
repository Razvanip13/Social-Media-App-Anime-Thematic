package socialnetwork.repository.paging.pagingRepo;

import socialnetwork.domain.Entity;
import socialnetwork.repository.EventsRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

import java.util.List;


public interface PagingRepoEvents<ID ,E extends Entity<ID>> extends EventsRepository<ID, E> {

    Page<E> findEvents(Pageable pageable);

    Page<E> findUserEvents(Pageable pageable,Long id);

    E notify(E entity,Long id);

    E unnotify(E entity,Long id);

    List<E> findUserNotifyEvents(Long id);

    Long get_events_number();

    Long get_user_events_number(Long id);


}
