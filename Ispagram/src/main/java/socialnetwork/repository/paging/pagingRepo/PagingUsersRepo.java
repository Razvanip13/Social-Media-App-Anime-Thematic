package socialnetwork.repository.paging.pagingRepo;

import socialnetwork.domain.Entity;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;

public interface PagingUsersRepo<ID ,E extends Entity<ID>> extends Repository<ID, E> {


    Page<E> findAll(Pageable pageable);

    Paginator<E> getPaginator(Pageable pageable);

    E findOne(String username);

    Long get_users_number();
}
