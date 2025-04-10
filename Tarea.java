import java.time.LocalDate;

public class Tarea {
    private int id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    private boolean completada; // Nuevo campo

    public Tarea(String titulo, String descripcion, LocalDate fechaLimite) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = LocalDate.now();
        this.fechaLimite = fechaLimite;
        this.completada = false;
    }

    public Tarea(int id, String titulo, String descripcion, LocalDate fechaCreacion, LocalDate fechaLimite, boolean completada) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.fechaLimite = fechaLimite;
        this.completada = completada;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public boolean isCompletada() { return completada; } // Nuevo getter
    public void setCompletada(boolean completada) { this.completada = completada; } // Nuevo setter
}
