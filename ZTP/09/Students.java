import java.io.Serializable;
import java.util.Collection;
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


/**
 * @author Maciej Zabek
 * @version 1.0
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Students.findByNames",
                query = "SELECT t FROM Students t "
                        + "WHERE t.firstName = :firstName "
                        + "AND t.lastName = :lastName"),
        @NamedQuery(name="Students.findAll",
                query = "SELECT s FROM Students s")
})
@Table(name = "tbl_students")
public class Students implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "firstName", nullable = true)
    private String firstName;

    @Column(name = "lastName", nullable = true)
    private String lastName;

    @Column(name = "semester", nullable = false)
    private int semester;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "Students")
    private Collection<StudentCourse> studentsCoursesCollection;

    /**
     * get student id
     * @return id of the student
     */
    public int getId() {
        return id;
    }

    /**
     * get first name
     * @return first name of the student
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * get last name
     * @return last name of the student
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * get student's current semester
     * @return student's current semester
     */
    public int getSemester() {
        return semester;
    }

    /**
     * get student's courses
     * @return collection of students courses
     */
    public Collection<StudentCourse> getStudentsCoursesCollection() {
        return studentsCoursesCollection;
    }

    /**
     * set student id
     * @param id of the student
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * set student's first name
     * @param firstName of the student
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * set student's last name
     * @param lastName of the student
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * set student's semester
     * @param semester of the student
     */
    public void setSemester(int semester) {
        this.semester = semester;
    }

    /**
     * set stoudent courses collection
     * @param studentsCoursesCollection courses colection of the student
     */
    public void setStudentsCoursesCollection(
            Collection<StudentCourse> studentsCoursesCollection) {
        this.studentsCoursesCollection = studentsCoursesCollection;
    }
}
