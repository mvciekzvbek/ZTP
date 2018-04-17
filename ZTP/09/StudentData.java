import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
public class StudentData {
    private String courseName;
    private String firstName;
    private String lastName;

    /**
     * constructor
     * @param file with input data
     * @throws FileNotFoundException
     */
    public StudentData(String file) throws FileNotFoundException {
        readDataFromFile(file);
    }

    /**
     * read data fom file
     * @param file with input data
     * @throws FileNotFoundException
     */
    public void readDataFromFile(String file) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(file));
        courseName = scanner.nextLine(); //courseName

        String[] fullName = scanner.nextLine().split("\\s+");
        firstName = fullName[0]; // firstName
        lastName = fullName[1]; // lastName
    }

    /**
     *
     * @return name of the course
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     *
     * @return student's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @return student's last name
     */
    public String getLastName() {
        return lastName;
    }
}
