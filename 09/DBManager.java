import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class DBManager {
    private EntityManagerFactory factory = null;
    private EntityManager entityManager = null;

    /**
     * constructor
     */
    public DBManager(){
    }

    /**
     * set entity manager
     * @param persistence unit name
     */
    public void setEntityManager(String persistence){
        try {
            factory = Persistence.createEntityManagerFactory(persistence);
            entityManager = factory.createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get id of the course
     * @param courseName
     * @return
     */
    public int getCourseId(String courseName){
        int courseId = 0;
        try {
            List<Courses> result = this.entityManager.createNamedQuery(
                    "Courses.findByName")
                    .setParameter("courseName", courseName)
                    .getResultList();
            courseId = result.get(0).getId();
        } catch (Exception e) {e.printStackTrace();}
        return courseId;
    }

    /**
     * get id of the student
     * @param studentName name
     * @param studentSurname surname
     * @return id
     */
    public int getStudentId(String studentName, String studentSurname) {
        int studentId = 0;
        try {
            List<Students> query = this.entityManager.createNamedQuery(
                    "Students.findByNames"
            )
                    .setParameter("firstName", studentName)
                    .setParameter("lastName", studentSurname)
                    .getResultList();
            studentId = query.get(0).getId();
        } catch (Exception e) {e.printStackTrace();}
        return studentId;
    }

    /**
     * get student's mark for given course
     * @param studentId id of the student
     * @param courseId id of the course
     * @return student's mark
     */
    public float getStudentMark(int studentId, int courseId) {
        float studentMark = 0;
        try {
            List<StudentCourse> query = this.entityManager.createNamedQuery(
                    "StudentCourse.findStudentMark"
            )
                    .setParameter("courseId", courseId)
                    .setParameter("studentId", studentId)
                    .getResultList();
            studentMark = (float) query.get(0).getMark();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return studentMark;
    }

    /**
     * retrieve marks and calculate median
     * @param courseId id of the course
     * @return median
     */
    public double getMedian(int courseId) {
        double median = 0;
        try {
            List<StudentCourse> query = this.entityManager
                    .createNamedQuery("StudentCourse.findAllMarks")
                    .setParameter("courseId", courseId)
                    .getResultList();
            if (query.size() % 2 == 0) {
                median = ((double) query.get(query.size() / 2).getMark()
                        + (double) query.get(query.size() / 2 - 1).getMark()) / 2.0;
            } else {
                median = (double) query.get(query.size() / 2).getMark();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return median;
    }
}
