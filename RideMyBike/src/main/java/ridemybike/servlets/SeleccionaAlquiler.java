/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ridemybike.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ridemybike.dominio.Alquiler;
import ridemybike.dominio.ValoracionBicicleta;
import ridemybike.dominio.db.AlquilerDB;
import ridemybike.dominio.db.ValoracionBicicletaDB;

/**
 * Servlet para poder extraer alquileres de la BD con los codigos de los Alquileres
 * @author Alberto
 */
@WebServlet(name = "SeleccionaAlquiler", urlPatterns = {"/SeleccionaAlquiler"})
public class SeleccionaAlquiler extends HttpServlet {

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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = "/ValoracionesBicicleta.jsp";
        String codigoBicicleta = request.getParameter("codigoBicicleta");
        ArrayList<ValoracionBicicleta> listaOpiniones = new ArrayList<ValoracionBicicleta>();
        listaOpiniones = ValoracionBicicletaDB.getValoraciones(codigoBicicleta);
        ArrayList<Alquiler> lista = new ArrayList<Alquiler>();
        for(int i = 0; i < listaOpiniones.size(); i++){
            lista.add(AlquilerDB.selectAlquiler(listaOpiniones.get(i).getCodigo()));
        }
        request.setAttribute("listaAlquileres", lista);
        RequestDispatcher dispatcher=getServletContext().getRequestDispatcher(url);
        dispatcher.forward(request, response);
        
        url = "/HistorialAlquileres.jsp";
        dispatcher = getServletContext().getRequestDispatcher(url);
        dispatcher.forward(request, response);
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
        processRequest(request, response);
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
        processRequest(request, response);
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
