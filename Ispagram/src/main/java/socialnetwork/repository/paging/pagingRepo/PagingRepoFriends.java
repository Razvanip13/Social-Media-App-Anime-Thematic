package socialnetwork.repository.paging.pagingRepo;

import socialnetwork.domain.Entity;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PagingRepoFriends<ID ,E extends Entity<ID>> extends Repository<ID, E> {

    Page<FriendDTO> findFriends(Pageable pageable, Long id);

    List<FriendDTO> find_friends_in_a_period(Long id, LocalDate date1,LocalDate date2);

    Long get_friends_number(Long id);


}
