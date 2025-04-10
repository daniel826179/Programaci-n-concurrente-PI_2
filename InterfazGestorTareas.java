import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class InterfazGestorTareas extends JFrame {
    private final GestorTareas gestorTareas = new GestorTareas();
    private NotificadorTareas notificador;
    private DefaultTableModel modeloTabla;
    private JTable tablaTareas;
    
    // Colores personalizados
    private final Color COLOR_FONDO = new Color(240, 248, 255);
    private final Color COLOR_BOTONES = new Color(100, 149, 237);
    private final Color COLOR_TITULOS = new Color(70, 130, 180);
    private final Color COLOR_TABLA = Color.WHITE;
    private final Color COLOR_SELECCION = new Color(173, 216, 230);
    private final Color COLOR_COMPLETADA = new Color(200, 255, 200);
    private final Color COLOR_VENCIDA = new Color(255, 200, 200);

    public InterfazGestorTareas() {
        configurarVentanaPrincipal();
        initComponentes();
        actualizarTabla();
        iniciarNotificador();
    }

    private void configurarVentanaPrincipal() {
        setTitle("Gestor de Tareas");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion();
            }
        });
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
    }

    private void iniciarNotificador() {
        notificador = new NotificadorTareas(gestorTareas);
        Thread hiloNotificador = new Thread(notificador, "Hilo-Notificador");
        hiloNotificador.setDaemon(true);
        hiloNotificador.start();
    }

    private void cerrarAplicacion() {
        notificador.detener();
        dispose();
        System.exit(0);
    }

    private void initComponentes() {
        // Panel de entrada (Norte)
        JPanel panelEntrada = crearPanelEntrada();
        add(panelEntrada, BorderLayout.NORTH);

        // Tabla de tareas (Centro)
        JScrollPane panelTabla = crearPanelTabla();
        add(panelTabla, BorderLayout.CENTER);

        // Panel de botones (Sur)
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(COLOR_FONDO);

        JLabel lblTitulo = crearLabel("Título:");
        JTextField campoTitulo = new JTextField();
        JLabel lblDescripcion = crearLabel("Descripción:");
        JTextArea areaDescripcion = new JTextArea(3, 20);
        JLabel lblFecha = crearLabel("Fecha Límite (AAAA-MM-DD):");
        JTextField campoFecha = new JTextField();
        JButton btnAgregar = crearBoton("Agregar Tarea", COLOR_BOTONES);

        areaDescripcion.setLineWrap(true);
        areaDescripcion.setWrapStyleWord(true);

        panel.add(lblTitulo);
        panel.add(campoTitulo);
        panel.add(lblDescripcion);
        panel.add(new JScrollPane(areaDescripcion));
        panel.add(lblFecha);
        panel.add(campoFecha);
        panel.add(new JLabel());
        panel.add(btnAgregar);

        btnAgregar.addActionListener(e -> {
            try {
                Tarea nuevaTarea = new Tarea(
                    campoTitulo.getText(),
                    areaDescripcion.getText(),
                    LocalDate.parse(campoFecha.getText())
                );
                gestorTareas.agregarTarea(nuevaTarea);
                actualizarTabla();
                campoTitulo.setText("");
                areaDescripcion.setText("");
                campoFecha.setText("");
                mostrarMensaje("Tarea agregada con éxito!");
            } catch (Exception ex) {
                mostrarError("Formato de fecha inválido. Use AAAA-MM-DD");
            }
        });

        return panel;
    }

    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna "Completada" es editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 6 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };

        String[] columnas = {"ID", "Título", "Descripción", "Fecha Creación", "Fecha Límite", "Estado", "Completada"};
        modeloTabla.setColumnIdentifiers(columnas);

        tablaTareas = new JTable(modeloTabla);
        personalizarTabla();

        // Listener para cambios en el checkbox
        modeloTabla.addTableModelListener(e -> {
            if (e.getColumn() == 6) {
                int id = (int) modeloTabla.getValueAt(e.getFirstRow(), 0);
                boolean completada = (boolean) modeloTabla.getValueAt(e.getFirstRow(), 6);
                gestorTareas.marcarComoCompletada(id, completada);
            }
        });

        return new JScrollPane(tablaTareas);
    }

    private void personalizarTabla() {
        tablaTareas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                                                         boolean isSelected, boolean hasFocus, 
                                                         int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (row < table.getRowCount()) {
                    boolean completada = (boolean) table.getModel().getValueAt(row, 6);
                    
                    if (completada) {
                        c.setBackground(COLOR_COMPLETADA);
                    } else {
                        try {
                            LocalDate fechaLimite = LocalDate.parse(table.getModel().getValueAt(row, 4).toString());
                            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaLimite);
                            c.setBackground(diasRestantes < 0 ? COLOR_VENCIDA : COLOR_TABLA);
                        } catch (Exception e) {
                            c.setBackground(COLOR_TABLA);
                        }
                    }
                    
                    if (isSelected) {
                        c.setBackground(COLOR_SELECCION);
                    }
                }
                
                return c;
            }
        });

        tablaTareas.setRowHeight(30);
        tablaTareas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaTareas.getTableHeader().setBackground(COLOR_TITULOS);
        tablaTareas.getTableHeader().setForeground(Color.WHITE);
    }

    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(COLOR_FONDO);

        JButton btnEditar = crearBoton("Editar", new Color(255, 165, 0));
        JButton btnEliminar = crearBoton("Eliminar", new Color(220, 80, 80));
        JButton btnMarcar = crearBoton("Marcar Completadas", new Color(144, 238, 144));

        btnEditar.addActionListener(e -> editarTarea());
        btnEliminar.addActionListener(e -> eliminarTarea());
        btnMarcar.addActionListener(e -> marcarTareasSeleccionadas());

        panel.add(btnEditar);
        panel.add(btnEliminar);
        panel.add(btnMarcar);

        return panel;
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Tarea> tareas = gestorTareas.obtenerTodasLasTareas();
        
        for (Tarea t : tareas) {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), t.getFechaLimite());
            String estado = diasRestantes > 1 ? "Pendiente" : 
                          diasRestantes == 1 ? "Por vencer" : 
                          "Vencida";
            
            modeloTabla.addRow(new Object[]{
                t.getId(),
                t.getTitulo(),
                t.getDescripcion(),
                t.getFechaCreacion(),
                t.getFechaLimite(),
                estado,
                t.isCompletada()
            });
        }
    }

    private void marcarTareasSeleccionadas() {
        int[] filas = tablaTareas.getSelectedRows();
        if (filas.length == 0) {
            mostrarError("Seleccione al menos una tarea");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Marcar " + filas.length + " tarea(s) como completadas?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            for (int fila : filas) {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                gestorTareas.marcarComoCompletada(id, true);
            }
            actualizarTabla();
        }
    }

    private void editarTarea() {
        int fila = tablaTareas.getSelectedRow();
        if (fila >= 0) {
            // Lógica de edición...
        } else {
            mostrarError("Seleccione una tarea");
        }
    }

    private void eliminarTarea() {
    int filaSeleccionada = tablaTareas.getSelectedRow();
    if (filaSeleccionada >= 0) {
        // Confirmación antes de eliminar
        int confirmacion = JOptionPane.showConfirmDialog(
            this, 
            "¿Está seguro de eliminar esta tarea?", 
            "Confirmar eliminación", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(filaSeleccionada, 0);
            gestorTareas.eliminarTarea(id);
            
            // Actualizar modelo y tabla
            modeloTabla.removeRow(filaSeleccionada);
            mostrarMensaje("Tarea eliminada correctamente");
        }
    } else {
        mostrarError("Por favor seleccione una tarea para eliminar");
    }
}

    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(COLOR_TITULOS);
        return label;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return boton;
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
