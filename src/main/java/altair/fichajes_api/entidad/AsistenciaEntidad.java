package altair.fichajes_api.entidad;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "asistencia")
public class AsistenciaEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asistencia")
    private Long idAsistencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matriculacion_id")
    private MatriculacionEntidad matriculacion;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalDateTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name="justificar_modificacion")
    private String justificarModificacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
 

    // Getters y Setters
    
    

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public Long getIdAsistencia() {
		return idAsistencia;
	}
	public void setIdAsistencia(Long idAsistencia) {
		this.idAsistencia = idAsistencia;
	}
	
	public MatriculacionEntidad getMatriculacion() {
		return matriculacion;
	}
	public void setMatriculacion(MatriculacionEntidad matriculacion) {
		this.matriculacion = matriculacion;
	}
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public LocalDateTime getHoraEntrada() {
		return horaEntrada;
	}
	public void setHoraEntrada(LocalDateTime horaEntrada) {
		this.horaEntrada = horaEntrada;
	}
	public LocalDateTime getHoraSalida() {
		return horaSalida;
	}
	public void setHoraSalida(LocalDateTime horaSalida) {
		this.horaSalida = horaSalida;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getJustificar_modificacion() {
		return justificarModificacion;
	}
	public void setJustificar_modificacion(String justificar_modificacion) {
		this.justificarModificacion = justificar_modificacion;
	}
	public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
