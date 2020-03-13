package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*


@DataJpaTest
class ShowAllOpenTournamentsServiceTest extends Specification{

    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1st Semester"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository


    def student
    def course
    def topic
    def topicDto
    def static topicList
    def static startingDate
    def static conclusionDate
    def formatter
    def tournament1
    def tournament2
    def tournament3
    def tournament4
    def studentId


    def setup(){

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        student = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student) //required to generate an id
        studentId = student.getId()

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topic = new Topic(course, topicDto)
        topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(topic)

        startingDate = LocalDateTime.now().format(formatter)
        conclusionDate = LocalDateTime.now().plusDays(1).format(formatter)

        def tournamentDto1 = new TournamentDto("T1",studentId,topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)
        def tournamentDto2 = new TournamentDto("T2",studentId,topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)
        def tournamentDto3 = new TournamentDto("T3",studentId,topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)
        def tournamentDto4 = new TournamentDto("T4",studentId,topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)

        tournament1 = new Tournament(tournamentDto1)
        tournament2 = new Tournament(tournamentDto2)
        tournament3 = new Tournament(tournamentDto3)
        tournament4 = new Tournament(tournamentDto4)


    }

    def "Both open and close tournaments exist"(){
        given: "several open tournaments"


        tournament1.setStatus("open")
        tournament2.setStatus("closed")
        tournament3.setStatus("open")
        tournament4.setStatus("closed")


        tournamentRepository.save(tournament1)
        tournamentRepository.save(tournament2)
        tournamentRepository.save(tournament3)
        tournamentRepository.save(tournament4)




        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "The returned data is correct"
        result.size() == 2
        result.get(0).getId()==tournament1.getId()
        result.get(1).getId()==tournament3.getId()

    }

    def "No tournaments are open"(){
        given: "Only close tournaments"
        tournament1.setStatus("closed")
        tournament2.setStatus("closed")
        tournament3.setStatus("closed")
        tournament4.setStatus("closed")

        tournamentRepository.save(tournament1);
        tournamentRepository.save(tournament2);
        tournamentRepository.save(tournament3);
        tournamentRepository.save(tournament4);

        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "No tournaments exist"(){
        given: "No tournaments"

        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "An exception is thrown"
        thrown(TutorException)

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }


}