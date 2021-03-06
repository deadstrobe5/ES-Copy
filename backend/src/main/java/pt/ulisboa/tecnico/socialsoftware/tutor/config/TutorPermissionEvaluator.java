package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import java.io.Serializable;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private StudentQuestionService studentQuestionService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        int userId = ((User) authentication.getPrincipal()).getId();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(userId).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
                case "DEMO.ACCESS":
                    return courseDto.getName().equals("Demo Course");
                default:
                    return false;
            }
        }

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    CourseDto courseDto = courseService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(userId, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, questionService.findQuestionCourse(id).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(userId, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(userId, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "TOURNAMENT.ACCESS":
                    return userHasThisExecution(userId, tournamentService.findTournamentCourseExecution(id).getCourseExecutionId());
                case "CREATOR.ACCESS":
                    return (userHasThisExecution(userId, tournamentService.findTournamentCourseExecution(id).getCourseExecutionId()) && userIsTheCreator(userId, id));
                case "STUDENT_QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, studentQuestionService.findStudentQuestionCourse(id).getCourseId());
                default: return false;
            }
        }

        return false;
    }

    private boolean userHasAnExecutionOfTheCourse(int userId, int courseId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseId() == courseId);
    }

    private boolean userIsTheCreator(int userId, int tournamentId) {
        TournamentDto tDto = tournamentService.getTournamentById(tournamentId);
        return tDto.getCreatingUserId() == userId;
    }

    private boolean userHasThisExecution(int userId, int courseExecutionId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseExecutionId() == courseExecutionId);
    }

     @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }
}
