import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import java.io.Serializable;
import java.util.Collection;


/**
 * @author Maciej Zabek
 * @version 1.0
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Courses.findByName",
            query = "SELECT c FROM Courses c "
                + "WHERE c.courseName = :courseName")
})
@Table(name = "tbl_courses")
public class Courses implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "courseName", nullable = false)
    private String courseName;

    @Column(name = "courseDescr", nullable = true)
    private String courseDescr;

    @Column(name = "courseHours", nullable = true)
    private int courseHours;

    @Column(name = "courseSem", nullable = false)
    private int courseSem;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "Courses")
    private Collection<StudentCourse> studentsCoursesCollection;

    /**
     * get course id
     * @return id of the course
     */
    public int getId() {
        return id;
    }

    /**
     * get course name
     * @return name of the course
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * get course description
     * @return description of the course
     */
    public String getCourseDescr() {
        return courseDescr;
    }

    /**
     * get course hours
     * @return amount of the course hours
     */
    public int getCourseHours() {
        return courseHours;
    }

    /**
     * get course semester
     * @return course semester
     */
    public int getCourseSem() {
        return courseSem;
    }

    /**
     * get course students collection
     * @return collection of the students
     */
    public Collection<StudentCourse> getStudentsCoursesCollection() {
        return studentsCoursesCollection;
    }

    /**
     * set course id
     * @param id of the course
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * set course name
     * @param courseName name of the course
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * set course descr
     * @param courseDescr description of the course
     */
    public void setCourseDescr(String courseDescr) {
        this.courseDescr = courseDescr;
    }

    /**
     * set course hours
     * @param courseHours set hours of the course
     */
    public void setCourseHours(int courseHours) {
        this.courseHours = courseHours;
    }

    /**
     * set course semester
     * @param courseSem semester of the course
     */
    public void setCourseSem(int courseSem) {
        this.courseSem = courseSem;
    }

    /**
     * set students collection for the course
     * @param studentsCoursesCollection students collection for the course
     */
    public void setStudentsCoursesCollection(
            Collection<StudentCourse> studentsCoursesCollection) {
        this.studentsCoursesCollection = studentsCoursesCollection;
    }
}