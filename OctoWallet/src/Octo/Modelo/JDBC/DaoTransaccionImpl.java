package Octo.Modelo.JDBC;

import Octo.Exceptions.OctoNotFound;
import Octo.Modelo.DAO.DaoTransaccion;
import Octo.Modelo.Entidad.Activo;
import Octo.Modelo.Entidad.Moneda;
import Octo.Modelo.Entidad.Stock;
import Octo.Modelo.Entidad.Transaccion;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DaoTransaccionImpl implements DaoTransaccion {

    @Override
    public void comprarCriptoMonedas(String cripto, String fiat, double cantidad) {
        // vamos a usar rollback
        // consultar por transaccion fallida si debe documentarse.
        // obtengo las monedas utilizadas para comprar
        SQLManager factory = SQLManager.getInstancia();
        Moneda monFiat = factory.getMoneda().obtener(fiat);
        Moneda monCripto = factory.getMoneda().obtener(cripto);
        // obtengo la cantidad de $$$ a utilizar para comprar cripto
        double valorAGastar = monFiat.getCotizacion() * cantidad;
        double cantAComprar = valorAGastar / monCripto.getCotizacion();
        // obtengo los stocks y activos requeridos
        Stock stCripto = factory.getStock().obtener(cripto);
        Activo actiFiat = factory.getFiat().obtener(fiat);
        try {
            Conexion.getConexion().setAutoCommit(false);
            if ((valorAGastar < actiFiat.getSaldo()) && (cantAComprar < stCripto.getMonto())) { // esto verifica que lo que voy a comprar puede ser cubierto por la app
                Activo actiCripto = factory.getCrypto().obtener(cripto);
                if (actiCripto == null) {
                    factory.getCrypto().crear(new Activo("CRYPTO",cripto, cantAComprar));
                } else {
                    factory.getCrypto().actualizar(cantAComprar, cripto);
                }
                factory.getFiat().actualizar((-1)*cantidad, fiat);
                Transaccion transaccion = new Transaccion("se compraron " + cantAComprar + "criptomonedas " + monCripto.getNomenclatura() + " gastando  $" + valorAGastar + " de la moneda FIAT: " + monFiat.getNomenclatura(), LocalDateTime.now());
                crear(transaccion);
                Conexion.getConexion().commit();
            }else{
                System.out.println("problemas con los valores! no hay suficiente stock");
            }
        }catch (SQLException e){
            System.out.println("error durante la carga!");
            try {
                Conexion.getConexion().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
     finally {
            try {
                Conexion.getConexion().setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("error! no se pudo modificar el commit");;
            }
        }
    }
    @Override
    public void swap(String criptoOriginal, double cantidad, String criptoEsperada) {
        // verificar que criptoOriginal y criptoEsperada existan como activos en mis activos
        SQLManager factory = SQLManager.getInstancia();
        try {

            Moneda monedaOriginal =  factory.getMoneda().obtener(criptoOriginal);
            Moneda monedaEsperada = factory.getMoneda().obtener(criptoEsperada);

            Conexion.getConexion().setAutoCommit(false);
            // Calculate the equivalent amount of the expected cryptocurrency
            double valorOriginal = monedaOriginal.getCotizacion() * cantidad;
            double cantidadEsperada = valorOriginal / monedaEsperada.getCotizacion();

            if( factory.getStock().obtener(criptoEsperada).getMonto() >= cantidadEsperada) {
                factory.getCrypto().actualizar(cantidadEsperada,criptoEsperada);
                factory.getCrypto().actualizar(-cantidad,criptoOriginal);
                Transaccion transaccion = new Transaccion("se Intercambiaron " + cantidad + " de criptomonedas " + monedaOriginal.getNomenclatura() + " por " + cantidadEsperada + " de la criptomoneda " + monedaOriginal.getNomenclatura(), LocalDateTime.now());
                crear(transaccion);
                Conexion.getConexion().commit();
            }
        }catch (SQLException e){
            System.out.println("error durante la carga!");
            try {
                Conexion.getConexion().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        finally {
            try {
                Conexion.getConexion().setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("error! no se pudo modificar el commit");;
            }
        }
    }

    @Override
    public void crear(Transaccion dato) {
        // crear transaccion va a ser con la ayuda de todos los de acá
        // a subir la transaccion finalmente con los datos correspondientes
        try {
            Statement st = Conexion.getConexion().createStatement();
            String sql = "INSERT INTO TRANSACCION (RESUMEN, FECHA_HORA)" +
                    "VALUES('" + dato.getResumen() + "', '"+ Timestamp.valueOf(dato.getFechaHora())
                    +"');";
            // se puede usar sets de Statement y los campos para evitar errores de tipeo.
            st.executeUpdate(sql);
            st.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    private Transaccion convertir(ResultSet rs) throws SQLException {
        Transaccion tr = new Transaccion();
        tr.setResumen(rs.getString("RESUMEN"));
        tr.setFechaHora(LocalDateTime.of(rs.getDate("FECHA_HORA").toLocalDate(), LocalTime.MIDNIGHT));
        return tr;
    }

    @Override
    public List<Transaccion> listar() {
        List<Transaccion> transacciones = new ArrayList<>(); Moneda moneda;
        try {
            Statement st= Conexion.getConexion().createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM TRANSACCION");
            while( res.next()) {
                transacciones.add(convertir(res));
            }
            res.close();
            st.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return transacciones;
    }

    @Override
    public Transaccion obtener(String id) {
        Transaccion transaccion = null;
        try {
            String str = "SELECT * FROM STOCK WHERE id = ?";
            PreparedStatement st = Conexion.getConexion().prepareStatement(str);
            st.setInt(1,Integer.parseInt(id));
            ResultSet res = st.executeQuery();
            if (res.next()) {
                transaccion = convertir(res);
            }
            res.close();
            st.close();
        } catch (SQLException e) {
            throw new OctoNotFound("error! no se encontró la transacción con id: " + id);
        }
        return transaccion;
    }
}
