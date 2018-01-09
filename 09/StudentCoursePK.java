import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
@Embeddable
public class StudentCoursePK implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "studentId", nullable = false)
    private int studentId;

    @Column(name = "courseId", nullable = false)
    private int courseId;

    /**
     * get student id
     * @return id of the student
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * get course id
     * @return id of the course
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * set student id
     * @param studentId id of the student
     */
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    /**
     * set course id
     * @param courseId id of the course
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
}