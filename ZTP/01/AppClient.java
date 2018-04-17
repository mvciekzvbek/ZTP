import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Maciej Ząbek
 * version 1.0
 */
public class AppClient {
    private static String query;
    private static int minimumDistance = 99999;
    private static int resultLine = -1;

    /**
     *
     * @param args arguments needed to run a program
     */
    public static void main(String[] args) {
        try {
            query = args[1].toLowerCase().trim();
            resultLine = findMatchingLine(args[0]);
            System.out.println("Linia : " + resultLine);
        }
        catch(Exception e) {
        }
    }

    /**
     *
     * @param filepath path to file
     * @return minimum distance for best match
     */
    private static int findMatchingLine(String filepath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.toLowerCase().trim();
                String[] lineParts = line.split("\\s+");
                String[] queryElements = query.split("\\s");

                if (lineParts.length > queryElements.length) {
                    queryElements = normalizeArray(queryElements, lineParts.length);
                } else if (lineParts.length < queryElements.length) {
                    lineParts = normalizeArray(lineParts, queryElements.length);
                }

                int distance = calculateDistance(queryElements, lineParts);

                if (distance == 0) {
                    resultLine = lineNumber;
                    break;
                } else if (distance < minimumDistance) {
                    minimumDistance = distance;
                    resultLine = lineNumber;
                }
            }
        } catch (IOException e) {
        }
        return resultLine;
    }

    /**
     *
     * @param src array which needs to be normalized
     * @param size size of target array
     * @return normalized array
     */
    private static String[] normalizeArray(String[] src, int size) {
        String[] dest = new String[size];

        switch (src.length) {
            case 1:
                dest[0] = src[0];
                for (int i = 1; i < dest.length; i++) dest[i] = "";
                break;
            case 2:
                for (int i = 0; i < src.length; i++) dest[i] = src[i];
                dest[2] = "";
                break;
            default:
                for (int i = 0; i < dest.length; i++) dest[i] = "";
                break;
        }
        return dest;
    }

    /**
     *
     * @param queryLine array with devided entry(input) string - query
     * @param fileLine array with devided line from file
     * @return Levenshtein distance for current fileLine
     */
    private static int calculateDistance(String[] queryLine, String[] fileLine) {
        int distance = 0;

        for (int i=0; i<fileLine.length; i++) {
            int[] results = new int[fileLine.length];
            for (int j=0; j<queryLine.length; j++) {
                results[j] = Levenshtein.calculate(queryLine[i], fileLine[j]);
            }
            int minDifference = minimum(results);
            distance += minDifference;
        }
        return distance;
    }

    /**
     *
     * @param array with vaules of Levenshtein distance for single comparision
     * @return minimum value from analized array
     */
    private static int minimum(int[] array) {
        int minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }
}