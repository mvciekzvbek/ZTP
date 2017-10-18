/**
 * @author Maciej Zabek
 * version 1.0
 */
public class Levenshtein {
    /**
     *
     * @param line first string to compare
     * @param query second string to compare
     * @return Levenshtein distance between two strings
     */
    public static int calculate(String line, String query) {
        int lineLength =  line.length() + 1;
        int queryLength = query.length() + 1;

        int[] cost = new int[lineLength];
        int[] newcost = new int[lineLength];

        for (int i = 0; i < lineLength; i++) cost[i] = i;

        for (int j = 1; j < queryLength; j++) {
            newcost[0] = j;

            for(int i = 1; i < lineLength; i++) {
                int match = (line.charAt(i - 1) == query.charAt(j - 1)) ? 0 : 1;

                int replacement = cost[i - 1] + match;
                int insertion  = cost[i] + 1;
                int removal  = newcost[i - 1] + 1;

                newcost[i] = Math.min(Math.min(insertion, removal), replacement);
            }

            int[] swap = cost; cost = newcost; newcost = swap;
        }

        return cost[lineLength - 1];
    }
}