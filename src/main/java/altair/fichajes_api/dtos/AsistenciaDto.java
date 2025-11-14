package altair.fichajes_api.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AsistenciaDto {
	private Long idAsistencia;
	private Long matriculacionId;
	private String nombreAlumno;
	private String apellidoAlumno;
	private String nombreCurso;
	private String nombreGrupo;
	private LocalDate fecha;
	private LocalDateTime fechaModificacion;
	private LocalDateTime horaEntrada;
	private LocalDateTime horaSalida;
	private String justificarModificacion;
	private String estado;

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

	public String getNombreAlumno() {
		return nombreAlumno;
	}

	public void setNombreAlumno(String nombreAlumno) {
		this.nombreAlumno = nombreAlumno;
	}

	public String getApellidoAlumno() {
		return apellidoAlumno;
	}

	public void setApellidoAlumno(String apellidoAlumno) {
		this.apellidoAlumno = apellidoAlumno;
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

}
