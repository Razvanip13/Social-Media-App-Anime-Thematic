package socialnetwork.repository.paging.pagingRepo;

import socialnetwork.domain.Entity;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.Paginator;

public interface PagingRepository<ID ,E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findAll(Pageable pageable);   // Pageable e un fel de paginator

    Paginator<E> getPaginator(Pageable pageable);
}