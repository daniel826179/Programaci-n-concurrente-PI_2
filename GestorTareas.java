import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GestorTareas {
    private final List<Tarea> tareas = new ArrayList<>();
    private final AtomicInteger contadorId = new AtomicInteger(1);

    public void agregarTarea(Tarea tarea) {
        tarea.setId(contadorId.getAndIncrement());
        tareas.add(tarea);
    }

    public void eliminarTarea(int id) {
        tareas.removeIf(t -> t.getId() == id);
    }

    public void actualizarTarea(Tarea tareaActualizada) {
        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getId() == tareaActualizada.getId()) {
                tareas.set(i, tareaActualizada);
                break;
            }
        }
    }

    public void marcarComoCompletada(int id, boolean completada) {
        tareas.stream()
              .filter(t -> t.getId() == id)
              .findFirst()
              .ifPresent(t -> t.setCompletada(completada));
    }

    public List<Tarea> obtenerTodasLasTareas() {
        return new ArrayList<>(tareas);
    }

    public Tarea buscarTareaPorId(int id) {
        return tareas.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
