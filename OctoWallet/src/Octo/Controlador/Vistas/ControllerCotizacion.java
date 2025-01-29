package Octo.Controlador.Vistas;

import Octo.Controlador.Sesion;
import Octo.Servicios.AppServices.CacheCryptoService;
import Octo.Servicios.AppServices.DBStatus;
import Octo.Vista.gui3.cotizacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControllerCotizacion {
        private JLabel userNameLabel;
        private JPanel mainPanel;
        private CacheCryptoService cachemoneda = CacheCryptoService.getInstancia();
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        public ControllerCotizacion(JPanel mainPanel) {
            this.mainPanel = mainPanel;
            Sesion.getInstance().setMonedasDisponibles(cachemoneda.getCacheMonedas());
        }
    public void iniciarActualizaciones(cotizacion c) {
        Runnable tareaActualizacion = () -> {
            cachemoneda.ActualizarCotizaciones();
            SwingUtilities.invokeLater(() -> {
                c.actualizarCotizaciones(cachemoneda.getCacheMonedas());
            });
        };
        scheduler.scheduleAtFixedRate(tareaActualizacion, 0, 20, TimeUnit.SECONDS);
    }
    public void detenerActualizaciones() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ActionListener getCerrarSesion(){
          return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sesion.getInstance().cerrarSesion();
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "login");
            }
        };
       }

        public ActionListener getVolverActionListener() {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    CardLayout cl = (CardLayout)mainPanel.getLayout();
                    cl.show(mainPanel, "misActivos");
                }
            };
        }
        public ActionListener getComprarActionListener() {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Sesion.getInstance().setCriptoCompra(e.getActionCommand());
                    CardLayout cl = (CardLayout)mainPanel.getLayout();
                    cl.show(mainPanel, "comprita");
                }
            };
        }
        public ActionListener getSwapActionListener() {
            return new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Sesion.getInstance().setCriptoCompra(e.getActionCommand());
                    CardLayout cl = (CardLayout)mainPanel.getLayout();
                    cl.show(mainPanel, "intercambio");
                }
            };
        }
      public void setUserNameLabel(JLabel label) {
        this.userNameLabel = label;
      }

      public void ModificarUserName() {
        String nombre = Sesion.getInstance().getUser().getNombres() + " " + Sesion.getInstance().getUser().getApellidos();
        this.userNameLabel.setText(nombre);
      }
}
