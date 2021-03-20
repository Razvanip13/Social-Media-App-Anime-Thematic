package socialnetwork.repository.paging.pagingRepo;

import socialnetwork.domain.Entity;
import socialnetwork.domain.friendship.FriendDTO;
import socialnetwork.repository.EventsRepository;
import socialnetwork.repository.Repository;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface PagingRepoMessages<ID ,E extends Entity<ID>> extends Repository<ID, E> {

    Page<E> findConversation(Pageable pageable, Long id1, Long id2);


    List<E> get_messages_in_a_period(Long id, LocalDate date1, LocalDate date2);

    List<E> get_friend_messages_in_a_period(Long id,Long id_friend, LocalDate date1, LocalDate date2);


    Long get_conversation_lines_number(Long id1,Long id2);

}
