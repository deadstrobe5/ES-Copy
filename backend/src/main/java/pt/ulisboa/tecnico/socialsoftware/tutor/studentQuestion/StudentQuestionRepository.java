package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface StudentQuestionRepository extends JpaRepository<StudentQuestion, Integer> {
    @Query(value = "SELECT * FROM student_questions sq WHERE sq.course_id = :courseId", nativeQuery = true)
    List<StudentQuestion> findStudentQuestions(int courseId);

    @Query(value = "SELECT * FROM student_questions sq WHERE sq.key = :key", nativeQuery = true)
    Optional<StudentQuestion> findByKey(Integer key);

    @Query(value = "SELECT MAX(key) FROM student_questions", nativeQuery = true)
    Integer getMaxStudentQuestionNumber();

    @Query(value = "SELECT * FROM student_questions sq WHERE sq.user_id = :userId", nativeQuery = true)
    List<StudentQuestion> findStudentQuestionsFromStudent(int userId);

    @Query(value = "SELECT * FROM student_questions sq WHERE sq.id = :id: AND sq.user_id = :userId:", nativeQuery = true)
    StudentQuestion findSpecificStudentQuestion(int userId, int id);
    
}
