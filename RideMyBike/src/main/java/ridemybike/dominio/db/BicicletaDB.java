package ridemybike.dominio.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import javax.servlet.http.Part;
import ridemybike.dominio.Bicicleta;
import ridemybike.dominio.EstadoBicicleta;
import ridemybike.dominio.Freno;



public class BicicletaDB{

  public static int insertarBicicleta(Bicicleta bicicleta) throws SQLException, IOException{
          if(bicicleta == null){
              throw new IllegalArgumentException("Bicicleta igual a null");
          }
          ConnectionPool pool = ConnectionPool.getInstance();
          Connection connection = pool.getConnection();
          PreparedStatement ps;
          String query = "INSERT INTO Bicicleta(codigoBici, descripcion, tamCuadro, imagen, marca, freno, latitud, longitud, usuarioPropietario, estado, codigoActivacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
          try {
              ps = connection.prepareStatement(query);
              ps.setString(1, bicicleta.getcodigoBici());
              ps.setString(2, bicicleta.getDescripcion());
              ps.setString(3, bicicleta.getTamCuadro() +"");
              ps.setBlob(4, bicicleta.getImagen().getInputStream());
              ps.setString(5, bicicleta.getMarca());
              ps.setString(6, bicicleta.getFreno()+"");
              ps.setString(7, bicicleta.getLatitud()+"");
              ps.setString(8, bicicleta.getLongitud()+"");
              ps.setString(9, bicicleta.getUsuarioPropietario());
              ps.setString(10, bicicleta.getEstado()+"");
              ps.setString(11, bicicleta.getCodigoActivacion());
              int res = ps.executeUpdate();
              ps.close();
              pool.freeConnection(connection);
              return res;
          } catch (SQLException e) {
              e.printStackTrace();
              return 0;
          }
      }
  public static Bicicleta selectBicicleta(String codigoBici) {
          if(codigoBici == null){
              throw new IllegalArgumentException("El codigo de la bicicleta es igual a null");
          }
          ConnectionPool pool = ConnectionPool.getInstance();
          Connection connection= pool.getConnection();
          PreparedStatement ps= null;
          ResultSet rs = null;
          String query= "SELECT * FROM Bicicleta WHERE codigoBici = ?";
          try {
              ps = connection.prepareStatement(query);
              ps.setString(1, codigoBici);
              rs = ps.executeQuery();
              Bicicleta bicicleta = null;
              if  (rs.next()) {
                  bicicleta = new Bicicleta();
                  bicicleta.setCodigoBici(rs.getString("codigoBici"));
                  bicicleta.setDescripcion(rs.getString("descripcion"));
                  bicicleta.setTamCuadro(Double.parseDouble(rs.getString("tamCuadro")));
                  bicicleta.setImagen((Part)rs.getBlob("imagen"));
                  bicicleta.setMarca(rs.getString("marca"));
                  bicicleta.setFreno(Freno.valueOf(rs.getString("freno")));
                  bicicleta.setLatitud(Double.parseDouble(rs.getString("latitud")));
                  bicicleta.setLongitud(Double.parseDouble(rs.getString("longitud")));
                  bicicleta.setUsuarioPropietario(rs.getString("usuarioPropietario"));
                  bicicleta.setEstado(EstadoBicicleta.valueOf(rs.getString("estado")));
                  bicicleta.setCodigoActivacion(rs.getString("codigoAcivacion"));
              }
              rs.close();
              ps.close();
              pool.freeConnection(connection);
              return bicicleta;
          }catch (SQLException e) {
              e.printStackTrace();
              return null;
          }
      }
  
  /**
   * Funcion que devuelve las bicicletas de un usuario que tienen un estado determinado
   * @param nombreUsuario Es el identificador del usuario dueno de las bicis
   * @param estado Es el tipo EstadoBicicleta que hace referencia al estado de las bicis a recuperar 
   * @return Un ArrayList formado por todas las bicicletas de un dueno segun su estado.
   */
  public static ArrayList<Bicicleta> getBicicletasEstado(String nombreUsuario, EstadoBicicleta estado){
      ArrayList<Bicicleta> lista = new ArrayList<Bicicleta>();
      lista = getBicicletasRegistradas(nombreUsuario);
      ArrayList<Bicicleta> listaResultado = new ArrayList<Bicicleta>();
      for( int i = 0; i < lista.size(); i++ ){
          if(lista.get(i).getEstado() == estado){
              listaResultado.add(lista.get(i));
          }
      }     
      return listaResultado;
  }
  
  
  /**
   * Funcion que devuelve todas las bicicletas de un usuario en concreto
   * @param nombreUsuario Es el string que determina que usuario es el dueno de las bicicletas
   * @return un ArrayList con las bicicletas del usuario
   */
  public static ArrayList<Bicicleta> getBicicletasRegistradas(String nombreUsuario){
    if (nombreUsuario == null || nombreUsuario.equals("")){
        throw new IllegalArgumentException("Nombre de usuario no aceptado ");
    }
    ArrayList<Bicicleta> lista= new ArrayList<Bicicleta>();
    ConnectionPool pool = ConnectionPool.getInstance();
    Connection connection= pool.getConnection();
    
    try{
        String query= "SELECT * FROM Bicicleta WHERE usuarioPropietario = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, nombreUsuario);
        
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            Bicicleta bicicleta = new Bicicleta();
            bicicleta.setCodigoBici(rs.getString("codigoBici"));
            bicicleta.setDescripcion(rs.getString("descripcion"));
            bicicleta.setTamCuadro(Double.parseDouble(rs.getString("tamCuadro")));            
            bicicleta.setMarca(rs.getString("marca"));
            bicicleta.setFreno(Freno.valueOf(rs.getString("freno")));
            bicicleta.setLatitud(Double.parseDouble(rs.getString("latitud")));
            bicicleta.setLongitud(Double.parseDouble(rs.getString("longitud")));
            bicicleta.setUsuarioPropietario(rs.getString("usuarioPropietario"));
            bicicleta.setEstado(EstadoBicicleta.valueOf(rs.getString("estado")));
            bicicleta.setCodigoActivacion(rs.getString("codigoAcivacion"));
            bicicleta.setImagen((Part) rs.getBlob("imagen"));
            lista.add(bicicleta);
        }
        
        rs.close();
        ps.close();
        pool.freeConnection(connection);
        return lista;
    }catch (Exception e) {
        e.printStackTrace();
        
    }
    return lista;
  }
}
