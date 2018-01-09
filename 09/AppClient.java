import java.io.FileNotFoundException;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
public class AppClient {
    /**
     * read data from file and execute median and result calculation
     * @param args entry application arguments
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        StudentData student = new StudentData(args[0]);
        String courseName = student.getCourseName();
        String firstName = student.getFirstName();
        String lastName = student.getLastName();

        DBManager dbManager = new DBManager();
        dbManager.setEntityManager("myPersistence");
        int courseId = dbManager.getCourseId(courseName);
        int studentId = dbManager.getStudentId(firstName, lastName);
        float studentMark = dbManager.getStudentMark(studentId, courseId);
        double median = dbManager.getMedian(courseId);
        float result = (float)
                (Math.round((studentMark / median) * 100) - 100);
        System.out.println("Wynik : " + String.format("%.0f", result) + "%");
    }
}