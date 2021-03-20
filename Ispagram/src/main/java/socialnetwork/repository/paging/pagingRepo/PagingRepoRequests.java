package socialnetwork.repository.paging.pagingRepo;

import socialnetwork.domain.Entity;
import socialnetwork.domain.frequest.FRequest;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

public interface PagingRepoRequests<ID ,E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findFriendRequests(Pageable pageable, Long id);

    Page<E> findPendingFriendRequests(Pageable pageable, Long id);

    Long get_friend_requests_number(Long id);

    Long get_pending_requests_number(Long id);
}
