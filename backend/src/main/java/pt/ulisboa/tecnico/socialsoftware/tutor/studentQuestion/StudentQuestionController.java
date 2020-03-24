package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
public class StudentQuestionController {


    private StudentQuestionService studentQuestionService;

    @PostMapping(value = "/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto createStudentQuestion(@PathVariable int courseId,
                                                    @Valid @RequestBody StudentQuestionDto studentQuestion) {

        return this.studentQuestionService.createStudentQuestion(courseId, studentQuestion);
    }

    @GetMapping("/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getCourseStudentQuestions(@PathVariable int courseId){
        return this.studentQuestionService.findStudentQuestions(courseId);
    }

}