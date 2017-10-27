import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Maciej Zabek
 * @version 1.0
 */
@WebServlet(urlPatterns = {"/Cone"}, name = "servlet124640")
public class Cone extends HttpServlet {

    public static final long serialVersionUID = 1L;
    private double primaryR;
    private double secondaryR;
    private double primaryH;
    private double materialDensity;
    private double defectDensity;
    private ArrayList<Defect> defects = new ArrayList<>();

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equals("GET")) {
            try {
                double x = Double.parseDouble(request.getParameter("x"));
                double y = Double.parseDouble(request.getParameter("y"));
                double z = Double.parseDouble(request.getParameter("z"));
                double r = Double.parseDouble(request.getParameter("r"));

                Defect defect = new Defect(x, y, z, r);
                defects.add(defect);
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try(PrintWriter out = response.getWriter()) {
            primaryR = Double.parseDouble(request.getParameter("ra"));
            secondaryR = Double.parseDouble(request.getParameter("rb"));
            primaryH = Double.parseDouble(request.getParameter("h"));
            materialDensity = Double.parseDouble(request.getParameter("c"));
            defectDensity = Double.parseDouble(request.getParameter("g"));
            out.println(calculateMass());
        } catch (Exception e){
            throw new ServletException(e);
        }
    }

    /**
     * Function responsible for checking if defect is inside a sphere
     * @param randomX x coordinate of randomly generated point
     * @param randomY y coordinate of randomly generated point
     * @param randomZ z coordinate of randomly generated point
     * @param defect analyzed defect
     * @return boolean describing if defect is inside a sphere
     */
    public boolean isInsideSphere(double randomX, double randomY, double randomZ, Defect defect){
        double x = (randomX-defect.x)*(randomX-defect.x);
        double y = (randomY-defect.y)*(randomY-defect.y);
        double z = (randomZ-defect.z)*(randomZ-defect.z);
        double r = defect.r * defect.r;
        return x + y + z <= r;
    }

    /**
     * Function responsible for generating random value
     * @param min minimum value
     * @param max maximum value
     * @return random value from (min,max)
     */
    public double generateRandomDouble(double min, double max){
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    /**
     * Function responsible for mass calculation using Monte Carlo method
     * @return calculated mass
     */
    public double calculateMass(){
        int samples = 1000000;
        int insideDefects = 0;
        int insideCone = 0;

        double xMin = 0 - primaryR;
        double xMax = 0 + primaryR;
        double yMin = 0 - primaryR;
        double yMax = 0 + primaryR;
        double zMin = 0;
        double zMax = 0 + primaryH;
        double randomX, randomY, randomZ;

        for(int i = 0; i < samples; i++){
            randomX = generateRandomDouble(xMin, xMax);
            randomY = generateRandomDouble(yMin, yMax);
            randomZ = generateRandomDouble(zMin, zMax);

            if (isInsideCone(randomX, randomY, randomZ) && isInsideDefect(randomX,randomY,randomZ)){
                insideDefects++;
            } else if (isInsideCone(randomX, randomY, randomZ)){
                insideCone++;
            }
        }
        double cuboidVolume = (2 * primaryR) * (2 * primaryR) *  primaryH;
        double inDefectsV = (double) insideDefects/samples * cuboidVolume;
        double inConeV = (double) insideCone/samples * cuboidVolume;
        return (inDefectsV * defectDensity + inConeV * materialDensity);
    }

    /**
     * Function responsible for checking if randomly generated point is inside defect
     * @param randomX x coordinate of randomly generated point
     * @param randomY y coordinate of randomly generated point
     * @param randomZ z coordinate of randomly generated point
     * @return boolean describing if point is inside defect
     */
    public boolean isInsideDefect(double randomX, double randomY, double randomZ){
        for(Defect elem : defects){
            if (isInsideSphere(randomX, randomY, randomZ, elem)) return true;
        }
        return false;
    }

    /**
     * Function responsible for checking if point is inside a cone
     * @param randomX x coordinate of randomly generated point
     * @param randomY y coordinate of randomly generated point
     * @param randomZ z coordinate of randomly generated point
     * @return boolean describing if point is inside a cone
     */
    public boolean isInsideCone(double randomX, double randomY, double randomZ){
        double maxR = primaryR - (primaryR-secondaryR) * (randomZ/primaryH);
        double pointRadius = Math.sqrt(randomX*randomX + randomY*randomY);

        return (randomZ>=0 && randomZ<=primaryH && pointRadius <= maxR);
    }

    /**
     * class which represents a defect
     */
    class Defect {

        double x, y, z, r;

        /**
         * constructor
         * @param x x coordinate of defect
         * @param y y coordinate of defect
         * @param z z coordinate of defect
         * @param r defect radius
         */
        public Defect(double x, double y, double z, double r){
            this.x = x;
            this.y = y;
            this.z = z;
            this.r = r;
        }
    }
}