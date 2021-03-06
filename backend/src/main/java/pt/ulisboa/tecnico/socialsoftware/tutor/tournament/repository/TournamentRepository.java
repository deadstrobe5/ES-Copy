package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    @Query(value = "select * from tournaments c where c.title = :title", nativeQuery = true)
    Optional<Tournament> findByTitle(String title);

    @Query(value = "select * from tournaments t where t.course_execution_id = :executionId", nativeQuery = true)
    List<Tournament> findAvailable(int executionId);

    @Query(value = "select * from tournaments t where t.course_execution_id = :executionId and t.id in (select tournaments_answered_id from tournaments_answered_list l where l.answered_list_id = :userId)", nativeQuery = true)
    List<Tournament> findParticipated(int userId, int executionId);

}
