package altair.fichajes_api.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Clase que se encarga de los campos de asistencia
 */
public class AsistenciaDto {
	private Long idAsistencia;
	private Long matriculacionId;
	private Long alumnoId;
	private String nombreCompletoAlumno;
	private String nombreCurso;
	private String nombreGrupo;
	private LocalDate fecha;
	private LocalDateTime fechaModificacion;
	private LocalDateTime horaEntrada;
	private LocalDateTime horaSalida;
	private String justificarModificacion;
	private String estado;
	private String anioEscolar;

	public Long getIdAsistencia() {
		return idAsistencia;
	}

	public void setIdAsistencia(Long idAsistencia) {
		this.idAsistencia = idAsistencia;
	}

	public Long getMatriculacionId() {
		return matriculacionId;
	}

	public void setMatriculacionId(Long matriculacionId) {
		this.matriculacionId = matriculacionId;
	}
	

	public Long getAlumnoId() {
		return alumnoId;
	}

	public void setAlumnoId(Long alumnoId) {
		this.alumnoId = alumnoId;
	}

	public String getNombreCompletoAlumno() {
		return nombreCompletoAlumno;
	}

	public void setNombreCompletoAlumno(String nombreCompletoAlumno) {
		this.nombreCompletoAlumno = nombreCompletoAlumno;
	}



	public String getNombreCurso() {
		return nombreCurso;
	}

	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalDateTime getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(LocalDateTime fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
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

	public String getJustificarModificacion() {
		return justificarModificacion;
	}

	public void setJustificarModificacion(String justificarModificacion) {
		this.justificarModificacion = justificarModificacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getAnioEscolar() {
		return anioEscolar;
	}

	public void setAnioEscolar(String anioEscolar) {
		this.anioEscolar = anioEscolar;
	}
	
	

}
