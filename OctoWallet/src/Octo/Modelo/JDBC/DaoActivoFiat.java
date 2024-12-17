package Octo.Modelo.JDBC;
import Octo.Controlador.Sesion;
import Octo.Exceptions.OctoNotFound;
import Octo.Modelo.Entidad.Activo;

import java.sql.*;

public class DaoActivoFiat extends DaoActivoImpl{
    @Override
    public long crear(Activo dato){
        String sql = "INSERT INTO ACTIVO_FIAT (ID_USUARIO, ID_MONEDA, CANTIDAD)" + "VALUES(?,?,?);";

        try {
            PreparedStatement st = Conexion.getConexion().prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            st.setLong(1, Sesion.getInstance().getUserResult().getUserId());
            st.setLong(2, dato.getMoneda().getIdM());
            st.setDouble(3, dato.getSaldo());
            st.executeUpdate(sql);
            try (ResultSet generatedKeys = st.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            st.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    public int actualizar(double valor, long idUser, long idMoneda){
        int res = -1;
        try{
            String sql = "UPDATE ACTIVO_FIAT SET CANTIDAD = CANTIDAD + ? WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
            PreparedStatement st = Conexion.getConexion().prepareStatement(sql);
            st.setDouble(1,valor);
            st.setLong(2,idUser);
            st.setLong(3,idMoneda);
            res = st.executeUpdate();
            st.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
    public Activo obtener(long id, long idMoneda){
        Activo activo = null;
        try {
            String str = "SELECT * FROM ACTIVO_FIAT WHERE ID_USUARIO = ? AND ID_MONEDA = ?";
            PreparedStatement st = Conexion.getConexion().prepareStatement(str);
            st.setLong(1,id);
            st.setLong(2,idMoneda);
            ResultSet res = st.executeQuery();
            if (res.next()){
                activo = convertir(res);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return activo;
    }
    @Override
    public Activo obtener(String nomenclatura){
        Activo activo = null;
        try {
            String str = "SELECT * FROM ACTIVO_FIAT WHERE NOMENCLATURA = ?";
            PreparedStatement st = Conexion.getConexion().prepareStatement(str);
            st.setString(1,nomenclatura);
            ResultSet res = st.executeQuery();
            if (res.next()){
                activo = convertir(res);
            }
        } catch (SQLException e) {
            throw new OctoNotFound("error! no se encontró el activo con nomenclatura: " + nomenclatura);
        }
        return activo;
    }

}