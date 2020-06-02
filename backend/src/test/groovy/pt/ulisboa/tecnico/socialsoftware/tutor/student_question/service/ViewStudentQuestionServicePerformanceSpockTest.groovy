package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ViewStudentQuestionServicePerformanceSpockTest extends Specification {
    public static final String COURSE_NAME = "COURSE_NAME"
    public static final String FIRST_NAME = "FIRST_NAME"
    public static final String USERNAME = "USERNAME"
    public static final String QUESTION_TITLE = "QUESTION_TITLE"
    public static final String QUESTION_CONTENT = "QUESTION_CONTENT"
    public static final String OPTION_CORRECT = "OPTION_CORRECT"
    public static final String OPTION_INCORRECT = "OPTION_INCORRECT"
    public static final String ACRONYM = "C1"
    public static final String ACADEMIC_TERM = "1st Term"


    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def "Performance Test - View 100 (one hundred) StudentQuestions 10000 (ten thousand) times"() {
        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        and: "a user"
        def user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
        and: "100 StudentQuestions"
        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT)
        options.add(OPTION_INCORRECT)
        options.add(OPTION_INCORRECT)
        options.add(OPTION_INCORRECT)
        1.upto(1/*00*/, {
            def studentQuestion = new StudentQuestion(course, user, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
            studentQuestionRepository.save(studentQuestion)
        })

        when:
        0.upto(1/*0000*/, { studentQuestionService.viewOwnStudentQuestions(user.getId()) })

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }
}
