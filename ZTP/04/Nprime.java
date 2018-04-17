import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;

/**
 *
 * @author Maciej Zabek
 * @version 1.0
 */
public class Nprime extends HttpServlet {
    @EJB
    private NprimeRemote bean;
    public static final long serialVersionUID = 1L;

    /**
     *
     * @param request from servlet http methods
     * @param response from servlet http methods
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
            throws ServletException, IOException {
        try {
            PrintWriter out = response.getWriter();
            int n = Integer.parseInt(request.getParameter("n"));
            String method = request.getMethod();
            int result = bean.prime(n, method);
            out.println(result);
        } catch (Exception e){}
    }

    /**
     * Handles the HTTP POST method.
     * @param request
     * @param response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP GET method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }
}
