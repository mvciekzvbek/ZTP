import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "StudentCourse.findStudentMark",
                query = "SELECT sm FROM StudentCourse sm "
                        + "WHERE sm.studentCoursePK.courseId = :courseId "
                        + "AND sm.studentCoursePK.studentId = :studentId"),
        @NamedQuery(name = "StudentCourse.findAllMarks",
                query = "SELECT sc FROM StudentCourse sc " +
                        "WHERE sc.Courses.id = :courseId " +
                        "AND sc.mark >= 50 " +
                        "AND sc.Courses.courseSem = sc.Students.semester")
})
@Table(name = "tbl_studentcourse")
public class StudentCourse implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected StudentCoursePK studentCoursePK;

    @Column(name = "mark", nullable = false)
    private int mark;

    @JoinColumn(name = "studentId",
            referencedColumnName = "Id",
            insertable = false,
            updatable = false)
    @ManyToOne(optional = false)
    private Students Students;

    @JoinColumn(name = "courseId",
            referencedColumnName = "Id",
            insertable = false,
            updatable = false)
    @ManyToOne(optional = false)
    private Courses Courses;

    /**
     * get student course primary key
     * @return studentcourse primary key class instance
     */
    public StudentCoursePK getStudentCoursePK() {
        return studentCoursePK;
    }

    /**
     * get mark
     * @return mark
     */
    public int getMark() {
        return mark;
    }

    /**
     * get students
     * @return students
     */
    public Students getStudents() {
        return Students;
    }

    /**
     * get courses
     * @return courses
     */
    public Courses getCourses() {
        return Courses;
    }

    /**
     * set studentcourse primary key
     * @param studentCoursePK studentCourse primary key
     */
    public void setStudentCoursePK(StudentCoursePK studentCoursePK) {
        this.studentCoursePK = studentCoursePK;
    }

    /**
     * set mark
     * @param mark mark
     */
    public void setMark(int mark) {
        this.mark = mark;
    }

    /**
     * set students
     * @param students students
     */
    public void setStudents(Students students) {
        Students = students;
    }

    /**
     * set courses
     * @param courses
     */
    public void setCourses(Courses courses) {
        Courses = courses;
    }
}
