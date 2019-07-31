
package modelo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;

/**
 * @author Super Daf
 */
public class ModeloVentas {
    private ConexiónMobel conexion = new ConexiónMobel();
    Connection con = null;
    
    //Muestra la información de los productos que se van a vender (Ventana Ventas)
    public DefaultTableModel mostrarDetalleVenta(int idVenta){
        try{
            //Para abrir conexión a la BD
            con = conexion.abrirConexion();          
            //Para generar la consulta
            Statement s = con.createStatement();
            DefaultTableModel dtm;
            //Para establecer el modelo al JTable
            try (   //Ejecutamos la consulta                  
                    ResultSet rs = s.executeQuery("SELECT ID,descripcion,venta_detalle.cantidad,valorUnitario,subtotal_venta FROM venta_detalle INNER JOIN productos ON venta_detalle.idProducto=productos.ID WHERE venta_detalle.idVenta="+idVenta+";");              
                    ) 
            {
                //Para establecer el modelo al JTable
                dtm = new DefaultTableModel(){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };                

                //Obteniendo la informacion de las columnas que estan siendo consultadas
                ResultSetMetaData rsMd = rs.getMetaData();
                //La cantidad de columnas que tiene la consulta
                int cantidadColumnas = rsMd.getColumnCount();
                //Establecer como cabeceras el nombre de las columnas
                for (int i = 1; i <= cantidadColumnas; i++) {
                    dtm.addColumn(rsMd.getColumnLabel(i));
                }   //Creando las filas para el JTable
                while (rs.next()) {
                    Object[] fila = new Object[cantidadColumnas];
                    for (int i = 0; i < cantidadColumnas; i++) {
                        fila[i]=rs.getObject(i+1);
                    }
                    dtm.addRow(fila);
                }
                //Cerrar objeto de ResultSet
            }            
            
            conexion.cerrarConexion(con);
            return dtm;        
        }
        catch(SQLException e){
            return null;
        }
    }
    
    //Consultar toda la informacion de los productos (Ventana AgregarProducto)
    public DefaultTableModel consultarProductos(){
        try{
            //Para abrir conexión a la BD
            con = conexion.abrirConexion();          
            //Para generar la consulta
            Statement s = con.createStatement();
            DefaultTableModel dtm;
            //Para establecer el modelo al JTable
            try (   //Ejecutamos la consulta                  
                    ResultSet rs = s.executeQuery("SELECT ID,descripcion,cantidad,categoria,valorUnitario FROM productos;");              
                    ) 
            {
                //Para establecer el modelo al JTable
                dtm = new DefaultTableModel(){
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };                

                //Obteniendo la informacion de las columnas que estan siendo consultadas
                ResultSetMetaData rsMd = rs.getMetaData();
                //La cantidad de columnas que tiene la consulta
                int cantidadColumnas = rsMd.getColumnCount();
                //Establecer como cabeceras el nombre de las columnas
                for (int i = 1; i <= cantidadColumnas; i++) {
                    dtm.addColumn(rsMd.getColumnLabel(i));
                }   //Creando las filas para el JTable
                while (rs.next()) {
                    Object[] fila = new Object[cantidadColumnas];
                    for (int i = 0; i < cantidadColumnas; i++) {
                        fila[i]=rs.getObject(i+1);
                    }
                    dtm.addRow(fila);
                }
                //Cerrar objeto de ResultSet
            }            
            
            conexion.cerrarConexion(con);
            return dtm;        
        }
        catch(SQLException e){
            return null;
        }
    }
    
    
    /*public int obtenerStockProducto(int idProducto) {
        int cantidad=0;        
        String sql = "SELECT cantidad FROM productos WHERE ID="+idProducto+";";

        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cantidad = rs.getInt("cantidad");
            }
        } catch (SQLException ex) {
            System.out.println("Hubo un error al obtener la cantidad de productos");
        }        
        return cantidad;
    }*/
    
    public void actualizarStockProducto(int idProducto, int nuevoStock) {
        try 
        {
            Connection con = conexion.abrirConexion();
            Statement s = con.createStatement();
            int registro = s.executeUpdate("UPDATE productos SET cantidad="+nuevoStock+" WHERE ID="+idProducto+";");
            conexion.cerrarConexion(con);
        }
        catch (SQLException e) 
        {
            System.out.println("Error al actualizar el inventario");
        }
    }
    
    //Método mágico para buscar y que autocomplete tipo Google :v
    public void buscarProductos(String buscar) {        
        DefaultTableModel modelo = (DefaultTableModel) vista.AgregarProducto.tbProductos.getModel();

        while (modelo.getRowCount() > 0) {
            modelo.removeRow(0);
        }
        String sql;
        if (buscar.equals("")) {
            sql = "SELECT * FROM productos";
        } else {
            sql = "SELECT * FROM productos WHERE descripcion LIKE '"+buscar+"%';";
        }
        String datos[] = new String[5];
        try {
            con = conexion.abrirConexion();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                datos[0] = rs.getString("ID");
                datos[1] = rs.getString("descripcion");
                datos[2] = rs.getString("cantidad");
                datos[3] = rs.getString("categoria");
                datos[4] = rs.getString("valorUnitario");
                modelo.addRow(datos);
            }
            conexion.cerrarConexion(con);
        } catch (SQLException ex) {
            System.out.println("Error :v");
        }
    }
    
    //Para obtener el siguiente número de venta
    public int obtenerNumVenta() {
        int codigo = 1;
        String SQL = "SELECT MAX(ID) FROM ventas";

        try {
            con = conexion.abrirConexion();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(SQL);
            while (rs.next()) {
                codigo = rs.getInt(1);
            }

            if (codigo > 1) {
                codigo=codigo+1;
            }
            
            conexion.cerrarConexion(con);
        } catch (SQLException ex) {
            System.out.println("Error al obtener el ID");
        }
        
        return codigo;       
    }
    
    
    /*POSIBLE USO EN EL FUTURO*/
    /*public void finalizarTransaccion(){
        try {
            con.commit();
        } catch (SQLException ex) {
            System.out.println("Error al guardar");
        }
    }
    
    public void cancelarTransaccion(){
        try {
            con.rollback();
        } catch (SQLException ex) {
            System.out.println("Error al cancelar");
        }
    }*/
}
