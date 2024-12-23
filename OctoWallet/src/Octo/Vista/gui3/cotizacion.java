package Octo.Vista.gui3;

import javax.swing.*;

import Octo.Controlador.Sesion;
import Octo.Controlador.Vistas.ControllerCotizacion;
import Octo.Modelo.Entidad.Moneda;
import Octo.Modelo.JDBC.SQLManager;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class cotizacion extends JPanel {
    
    private static final long serialVersionUID = 1L;
    private JPanel mainPanel;
    private JPanel mainPanel1;
    ControllerCotizacion control;
    public cotizacion(JPanel mainPanel, ControllerCotizacion controller) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Panel superior - Título y sesión
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Billetera Virtual - Cotizaciones", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 14));
        topPanel.add(title, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userName = new JLabel("Juan Perez");
        userName.setIcon(new ImageIcon(cotizacion.class.getResource("/imagenes/pulpito.png")));
        controller.setUserNameLabel(userName);

        JButton logoutButton = new JButton("Cerrar sesión");
        logoutButton.addActionListener(controller.getCerrarSesion());
        styleButton(logoutButton, new Color(151, 177, 249)); // Azul
        userPanel.add(userName);
        userPanel.add(logoutButton);
        userPanel.setBackground(Color.WHITE);
        topPanel.add(userPanel, BorderLayout.EAST);

        // Panel principal - Contenedor de criptos
        JPanel mainPanel1 = new JPanel(new GridBagLayout());
        mainPanel1.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.BOTH;

        // Header de la tabla
        gbc.gridy = 0;
        gbc.gridx = 0;
        addLabel(mainPanel1, "Cripto", Font.BOLD, gbc);
        gbc.gridx = 1;
        addLabel(mainPanel1, "Precio de Compra", Font.BOLD, gbc);
        gbc.gridx = 2;
        addLabel(mainPanel1, "Acciones", Font.BOLD, gbc);

        // Datos de las criptos
        String[][] cryptoData = {
                {"Bitcoin (BTC)", "$66,960.39", "bitcoin.png"},
                {"Ethereum (ETH)", "$2,478.33", "ethereum.png"},
                {"Usdc (USDC)", "$0.9999", "usdc.png"},
                {"Tether (USDT)", "$0.9986", "tether.png"},
                {"Dogecoin (DOGE)", "$0.1359", "dogecoin.png"}
        };

        for (int i = 0; i < cryptoData.length; i++) {
            gbc.gridy = i + 1;

            // Columna 1: Imagen y nombre
            gbc.gridx = 0;
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.setBackground(Color.WHITE);
            JLabel icon = new JLabel(new ImageIcon(cryptoData[i][2])); // Imagen
            JLabel name = new JLabel(cryptoData[i][0]);
            name.setFont(new Font("Arial", Font.PLAIN, 14));
            namePanel.add(icon);
            namePanel.add(name);
            mainPanel1.add(namePanel, gbc);

            // Columna 2: Precio
            gbc.gridx = 1;
            JLabel price = new JLabel(cryptoData[i][1]);
            price.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel1.add(price, gbc);

            // Columna 3: Botones
            gbc.gridx = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            JButton buyButton = new JButton("Comprar");
            styleButton(buyButton, new Color(67, 160, 71)); // Verde
            buyButton.addActionListener(controller.getComprarActionListener());
            buttonPanel.add(buyButton);

            // Swap solo para BTC y DOGE
            if (cryptoData[i][0].contains("Bitcoin") || cryptoData[i][0].contains("Dogecoin")) {
                JButton swapButton = new JButton("Swap");
                styleButton(swapButton, new Color(255, 87, 34)); // Naranja
                swapButton.addActionListener(controller.getSwapActionListener());
                buttonPanel.add(swapButton);
            }

            mainPanel1.add(buttonPanel, gbc);
        }

        // Botón volver
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);
        JButton backButton = new JButton("Volver");
        styleButton(backButton, new Color(151, 177, 249)); // Azul
        backButton.addActionListener(controller.getVolverActionListener());
        bottomPanel.add(backButton);

        // Agregar todo al panel
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel1), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        this.mainPanel1= mainPanel1;
        control = controller;
        control.iniciarActualizaciones(this);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                controller.ModificarUserName();
            }
        });
    }
    public void actualizarCotizaciones(List<Moneda> nuevasCotizaciones) {
        // Asumiendo que tienes un panel principal llamado mainPanel1 donde se muestran las cotizaciones
        mainPanel1.removeAll(); // Elimina todos los componentes actuales

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Header de la tabla
        gbc.gridy = 0;
        gbc.gridx = 0;
        addLabel(mainPanel1, "Cripto", Font.BOLD, gbc);
        gbc.gridx = 1;
        addLabel(mainPanel1, "Precio de Compra", Font.BOLD, gbc);
        gbc.gridx = 2;
        addLabel(mainPanel1, "Acciones", Font.BOLD, gbc);

        // Itera sobre las nuevas cotizaciones y actualiza la vista
        int row = 1;
        for (Moneda cripto : nuevasCotizaciones) {
            gbc.gridy = row++;

            // Columna 1: Imagen y nombre
            gbc.gridx = 0;
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.setBackground(Color.WHITE);
            JLabel icon = null; // Suponiendo que Moneda tiene un método getImagen()
            try {
                icon = new JLabel(new ImageIcon(new ImageIcon(new URL(SQLManager.getInstancia().getMoneda().obtener(cripto.getNomenclatura()).getImagen())).getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH)));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            JLabel name = new JLabel(cripto.getNombre());
            name.setFont(new Font("Arial", Font.PLAIN, 14));
            namePanel.add(icon);
            namePanel.add(name);
            mainPanel1.add(namePanel, gbc);

            // Columna 2: Precio
            gbc.gridx = 1;
            JLabel price = new JLabel(String.format("$%.2f", cripto.getCotizacion())); // Suponiendo que Moneda tiene un método getPrecioCompra()
            price.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel1.add(price, gbc);

            // Columna 3: Botones
            gbc.gridx = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            JButton buyButton = new JButton("Comprar");
            styleButton(buyButton, new Color(67, 160, 71)); // Verde
            buyButton.addActionListener(control.getComprarActionListener());
            buttonPanel.add(buyButton);

            // Swap solo para BTC y DOGE
            if (cripto.getNombre().contains("bitcoin") || cripto.getNombre().contains("dogecoin")) {
                JButton swapButton = new JButton("Swap");
                styleButton(swapButton, new Color(255, 87, 34)); // Naranja
                swapButton.addActionListener(control.getSwapActionListener());
                buttonPanel.add(swapButton);
            }

            mainPanel1.add(buttonPanel, gbc);
        }

        // Refresca el panel para mostrar los cambios
        mainPanel1.revalidate();
        mainPanel1.repaint();
    }

/*
    public void actualizarVistaCotizaciones(JPanel mainPanel1, List<Moneda> cotizaciones) {
        // Limpia el panel principal para evitar duplicados
        mainPanel1.removeAll();

        // Layout del mainPanel1
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(CryptoCotizacion, 5, 5, 5); // Espaciado
        gbc.weightx = 1;

        // Títulos
        gbc.gridy = 0;
        gbc.gridx = 0;
        mainPanel1.add(new JLabel("Nombre"), gbc);
        gbc.gridx = 1;
        mainPanel1.add(new JLabel("Precio"), gbc);
        gbc.gridx = 2;
        mainPanel1.add(new JLabel("Acciones"), gbc);

        // Rellenar los datos dinámicamente
        for (int i = 0; i < cotizaciones.size(); i++) {
            Moneda cripto = cotizaciones.get(i);

            gbc.gridy = i + 1;

            // Columna 1: Imagen y nombre
            gbc.gridx = 0;
            JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            namePanel.setBackground(Color.WHITE);
            JLabel icon = new JLabel(new ImageIcon(new ImageIcon(new URL(SQLManager.getInstancia().getMoneda().obtener(activo.getMoneda().getNomenclatura()).getImagen())).getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH)),); // Imagen
            JLabel name = new JLabel(cripto.getNombre());
            name.setFont(new Font("Arial", Font.PLAIN, 14));
            namePanel.add(icon);
            namePanel.add(name);
            mainPanel1.add(namePanel, gbc);

            // Columna 2: Precio
            gbc.gridx = 1;
            JLabel price = new JLabel(String.valueOf(cripto.getCotizacion()));
            price.setFont(new Font("Arial", Font.BOLD, 14));
            mainPanel1.add(price, gbc);

            // Columna 3: Botones
            gbc.gridx = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.setBackground(Color.WHITE);
            JButton buyButton = new JButton("Comprar");
            styleButton(buyButton, new Color(67, 160, 71)); // Verde
            buyButton.addActionListener(controller.getComprarActionListener());
            buttonPanel.add(buyButton);

            // Swap solo para Bitcoin y Dogecoin
            if (cripto.getNombre().contains("Bitcoin") || cripto.getNombre().contains("Dogecoin")) {
                JButton swapButton = new JButton("Swap");
                styleButton(swapButton, new Color(255, 87, 34)); // Naranja
                swapButton.addActionListener(controller.getSwapActionListener());
                buttonPanel.add(swapButton);
            }

            mainPanel1.add(buttonPanel, gbc);
        }

        // Refrescar el panel
        mainPanel1.revalidate();
        mainPanel1.repaint();
    }
    */
    private void addLabel(JPanel panel, String text, int style, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, 16));
        panel.add(label, gbc);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }

}