import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * @author Maciej Zabek
 * @version 1.0
 */
@Stateless
public class Solid implements ISolidRemote {
    private ArrayList<Point> points = new ArrayList<>();
    private Point start;
    private Stack<Point> pointsStack = new Stack<Point>();

    /**
     * calculating figure area
     * @param points retrieved from database
     * @return String with calculated value
     */
    public String calculateArea(ArrayList<Point> points){
        this.points = points;
        start = findStartPoint();
        points.remove(start);
        calculateAngle();
        executeGraham();
        List<Point> pointsList = toList();
        double area = getArea(pointsList);
        return String.format("%.5f", area);
    }

    /**
     * looking for a candidate from which Graham scan will begin
     * @return point which will be the start point of the Graham scan
     */
    private Point findStartPoint(){
        Point min = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            min = getMinValue(min, points.get(i));
        }
        return min;
    }

    /**
     * comparing two points
     * @param first point to compare
     * @param second point to compare
     * @return point with the lowest z coordinate(and x in case of equal z coordinates)
     */
    private Point getMinValue(Point first, Point second){
        if (first.z < second.z)
            return first;
        if (first.z > second.z)
            return second;
        if (first.x < second.x)
            return first;
        else
            return second;
    }

    /**
     * calculate the angle of each point and the starting point make with the x-axis.
     */
    private void calculateAngle(){
        for (Point point : points) {
            double d = Math.abs(point.x)+Math.abs(point.z);
            double angle = 0;

            if (point.x >= 0 && point.z >= 0){
                angle = point.z/d;
            } else if (point.x < 0 && point.z >= 0){
                angle = 2 - point.z/d;
            } else if (point.x < 0 && point.z <0){
                angle = 2 + Math.abs(point.z)/d;
            } else if (point.x >=0 && point.z < 0){
                angle = 4 - Math.abs(point.z)/d;
            }
            point.setAngle(angle);
        }
        Collections.sort(points);
    }

    /**
     * performing Graham scan
     */
    private void executeGraham(){
        pointsStack.push(start);
        pointsStack.push(points.get(0));
        pointsStack.push(points.get(1));
        int n = points.size();
        for (int i = 2; i < n; i++){
            while (isRight(pointsStack.get(pointsStack.size() - 2), pointsStack.get(pointsStack.size() - 1), points.get(i))<0){
                pointsStack.pop();
            }
            pointsStack.push(points.get(i));
        }
    }

    /**
     * Checking direction of turn - sarrus method
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return matrix determinant
     */
    private double isRight(Point p1, Point p2, Point p3){
        return (p1.x * p3.z + p2.z * p3.x + p1.z * p2.x) - (p3.x * p1.z + p2.z * p1.x + p3.z * p2.x);
    }

    /**
     * convert stack to list
     * @return list with points which creates a convex hull
     */
    private List<Point> toList(){
        List<Point> pointsList = new ArrayList<Point>();
        int n = pointsStack.size();
        for (int i = 0; i < n; i++){
            pointsList.add(pointsStack.pop());
        }
        return pointsList;
    }

    /**
     * Calculate area using Gauss method
     * @param points list of points
     * @return calculated area
     */
    private double getArea(List<Point> points){
        double result = 0.0;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                result += points.get(i).x * (points.get(n - 1).z - points.get(i + 1).z);
            } else if (i == (n - 1)) {
                result += points.get(i).x * (points.get(i - 1).z - points.get(0).z);
            } else {
                result += points.get(i).x * (points.get(i - 1).z - points.get(i + 1).z);
            }
        }
        return Math.abs(result) / 2.0;
    }

    /**
     * Point representation
     */
    public static class Point implements Comparable<Point> {
        private double x;
        private double y;
        private double z;
        private double angle;

        /**
         * class constructor
         * @param x coordinate
         * @param y coordinate
         * @param z coordinate
         */
        Point(double x, double y, double z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * angle setter
         * @param angle
         */
        public void setAngle(double angle) {
            this.angle = angle;
        }

        /**
         * angle getter
         * @return angle
         */
        public double getAngle() {
            return angle;
        }

        /**
         * compare two angles for sorting purposes
         * @param point comparable point
         * @return
         */
        @Override
        public int compareTo(Point point) {
            if (this.angle < point.getAngle()) {
                return -1;
            } else if (this.angle > point.getAngle()) {
                return 1;
            }
            return 0;
        }
    }
}
