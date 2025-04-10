import javax.swing.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class NotificadorTareas implements Runnable {
    private final GestorTareas gestorTareas;
    private volatile boolean activo;

    public NotificadorTareas(GestorTareas gestorTareas) {
        this.gestorTareas = gestorTareas;
        this.activo = true;
    }

    public void detener() {
        this.activo = false;
    }

    @Override
    public void run() {
        while (activo) {
            List<Tarea> tareas = gestorTareas.obtenerTodasLasTareas();
            
            for (Tarea tarea : tareas) {
                verificarTarea(tarea);
            }

            try {
                Thread.sleep(30000); // Revisar cada 30 segundos
            } catch (InterruptedException e) {
                System.out.println("Notificador interrumpido");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void verificarTarea(Tarea tarea) {
        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), tarea.getFechaLimite());
        
        if (diasRestantes == 1) {
            mostrarNotificacion(tarea, "vencer MAÑANA");
        } else if (diasRestantes == 0) {
            mostrarNotificacion(tarea, "VENCER HOY");
        }
    }

    private void mostrarNotificacion(Tarea tarea, String estado) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                null,
                "¡Tarea próxima a " + estado + "!\n\n" +
                "🔹 Título: " + tarea.getTitulo() + "\n" +
                "🔹 Fecha Límite: " + tarea.getFechaLimite() + "\n" +
                "🔹 Descripción: " + (tarea.getDescripcion().isEmpty() ? "Sin descripción" : tarea.getDescripcion()),
                "Recordatorio de Tarea",
                JOptionPane.WARNING_MESSAGE
            );
        });
    }
}
