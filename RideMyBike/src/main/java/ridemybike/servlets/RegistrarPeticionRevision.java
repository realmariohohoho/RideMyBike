package ridemybike.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import ridemybike.dominio.Bicicleta;
import ridemybike.dominio.EstadoBicicleta;
import ridemybike.dominio.Freno;
import ridemybike.dominio.PeticionRevision;
import ridemybike.dominio.db.BicicletaDB;
import ridemybike.dominio.db.PeticionRevisionDB;

/**
 * Insertar una bicicleta en la BD pendiente de aprovacion
 *
 * @author Alberto
 */
@WebServlet(name = "RegistrarPeticionRevision", urlPatterns = {"/RegistrarPeticionRevision"})
@MultipartConfig
public class RegistrarPeticionRevision extends HttpServlet {
    private final String TAMANO_CUADRO_INCORRECTO = "Este campo debe ser una cifra de 3 números (cm)";
    private final String TAMANO_DESCRIPCION_NOVALIDO = "Este campo es demasiado pequeño o contiene caracteres ilegales(\\,\",\',\\x00,\\x1,-,_), recuerda que tu descripción de la bici debe ser la mejor posible para que los usuarios quieran alquilarla";
  
    /** 
     * @param cadena
     * @return TRUE SI CONTIENE UN CARACTER ILEGAL FALSE SI TODO BIEN
     */
    public boolean compruebaCaracteresEspeciales(String cadena){
        if(cadena.contains("\\") || cadena.contains("\"") || cadena.contains("\'") || cadena.contains("\\x00") || cadena.contains("\\x1") || cadena.contains("-") || cadena.contains("_") || cadena.contains("&") || cadena.contains(" or ") || cadena.contains(" and ") ){
            return true;
        }
        return false;
    }
    
    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        String url = "";
        String Modelo = "";
        String marca = "";
        String TamanoCuadro = "";
        String Descripcion = "";
        String TipoFreno = "";
        String Ciudad = "";
        String fecha1 = "";
        String hora1 = "";
        String nombreUsuario = "";
        try {
            Modelo = request.getParameter("modelo");
            marca = request.getParameter("marca");
            TamanoCuadro = request.getParameter("tamanoCuadro");
            Descripcion = request.getParameter("descripcion");
            TipoFreno = request.getParameter("tipoFreno");
            Ciudad = request.getParameter("ciudad");
            fecha1 = request.getParameter("fecha1");
            hora1 = request.getParameter("hora1");
            HttpSession session= request.getSession();
            nombreUsuario = session.getAttribute("usuario").toString();
            Part foto = request.getPart("foto");
            
            if(compruebaCaracteresEspeciales(Descripcion)){
                url = "/registrar_bicicletaFallo.jsp";
                request.setAttribute("errorDescripcion", TAMANO_DESCRIPCION_NOVALIDO);
                try{
                    Double variable = Double.parseDouble(TamanoCuadro);
                }catch(Exception e){
                    request.setAttribute("errorTamano", TAMANO_CUADRO_INCORRECTO);
                }
            }else{
                boolean var = false;
                try{
                    Double variable = Double.parseDouble(TamanoCuadro);
                }catch(Exception e){
                    var = true;
                    url = "/registrar_bicicletaFallo.jsp";
                    request.setAttribute("errorTamano", TAMANO_CUADRO_INCORRECTO);

                }
                if(!var){
                    Bicicleta bici = new Bicicleta();
                    bici.setDescripcion(Descripcion);
                    bici.setMarca(marca);
                    bici.setModelo(Modelo);
                    bici.setTamCuadro(Double.parseDouble(TamanoCuadro));
                    Freno freno = Freno.valueOf(TipoFreno);
                    bici.setFreno(freno);
                    EstadoBicicleta estado = EstadoBicicleta.Pendiente;
                    bici.setEstado(estado);
                    bici.setUsuarioPropietario(nombreUsuario);
                    UUID idUno = UUID.randomUUID();
                    bici.setCodigoActivacion(idUno.toString());
                    bici.setImagen(foto);
                    int codigoBici = BicicletaDB.insertarBicicleta(bici);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Date parsedDate = dateFormat.parse(fecha1+" "+hora1+":00.000");
                    Timestamp fechaPeticion = new java.sql.Timestamp(parsedDate.getTime());


                    PeticionRevision peticion = new PeticionRevision();
                    peticion.setCiudad(Ciudad);
                    peticion.setCodigoUsuario(nombreUsuario);

                    peticion.setFecha(fechaPeticion);

                    peticion.setCodigoBicicleta(codigoBici);
                    PeticionRevisionDB.insertarPeticionRevision(peticion);

                    url = "/direccionRegistroCorrecto.jsp";

                }               
            }
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(url);
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            //response.getWriter().println(e.toString()+" Marca: "+marca+" Modelo: "+Modelo+" TamCuadro: "+TamanoCuadro+" TipoFreno: "+TipoFreno+" Ciudad: "+Ciudad+" Hora1: "+hora1 );
            url = "/direccionRegistroIncorrecto.jsp";
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(url);
            dispatcher.forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(RegistrarPeticionRevision.class.getName()).log(Level.SEVERE, null, ex);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(RegistrarPeticionRevision.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
