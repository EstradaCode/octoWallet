package Octo.Modelo.JDBC;

import Octo.Exceptions.OctoNotFound;
import Octo.Modelo.DAO.DaoStock;
import Octo.Modelo.Entidad.Stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DaoStockImpl implements DaoStock {
    @Override
    public void crear(Stock dato) {
        try {
            Statement st = Conexion.getConexion().createStatement();
            String sql = "INSERT INTO STOCK (NOMENCLATURA,CANTIDAD)" +
                         "VALUES('" + dato.getNomenclaturaMoneda() + "', '"+ dato.getMonto() + "');";
            // se puede usar sets de Statement y los campos para evitar errores de tipeo.
             st.executeUpdate(sql);
             st.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
    @Override
    public int actualizar(double valor, String nomenclatura ){
        int res = -1;
        try{
            String sql = "UPDATE STOCK SET CANTIDAD = CANTIDAD - ? WHERE NOMENCLATURA = ?";
            PreparedStatement st = Conexion.getConexion().prepareStatement(sql);
            st.setDouble(1,valor);
            st.setString(2,nomenclatura);
            res = st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
    @Override
    public List<Stock> listar() {
        List <Stock> stocks = new ArrayList<>();
        try {
            Statement st = Conexion.getConexion().createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM STOCK");
            while (res.next()) {
                stocks.add(convertir(res));
            }
            res.close();
            st.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return stocks;
    }
    private Stock convertir(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setMonto(rs.getInt("CANTIDAD"));
        stock.setNomenclaturaMoneda(rs.getString("NOMENCLATURA"));
        return stock;
    }
    @Override
    public Stock obtener(String nomenclatura) {
        Stock stock = null;
        try {
            String str = "SELECT * FROM STOCK WHERE NOMENCLATURA = ?";
            PreparedStatement st = Conexion.getConexion().prepareStatement(str);
            st.setString(1,nomenclatura);
            ResultSet res = st.executeQuery();
            if (res.next()) {
                stock = convertir(res);
            }
            res.close();
            st.close();
        } catch (SQLException e) {
            throw new OctoNotFound("error! no se encontró el Stock con nomenclatura: " + nomenclatura);
        }
        return stock;
    }
}
