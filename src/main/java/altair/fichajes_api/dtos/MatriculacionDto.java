package altair.fichajes_api.dtos;

/**
 * Clase que se encarga de los campos de matriculacion
 */
public class MatriculacionDto {
	
	
	private Long idMatriculacion;
	private Long alumnoId;
	private Long cursoId;
	private String nombreCurso;
	private Long grupoId;
	private String nombreGrupo;
	private String anioEscolar;
	private String uidLlave;

	
	
	public Long getIdMatriculacion() {
		return idMatriculacion;
	}
	public void setIdMatriculacion(Long idMatriculacion) {
		this.idMatriculacion = idMatriculacion;
	}
	public Long getAlumnoId() {
		return alumnoId;
	}
	public void setAlumnoId(Long alumnoId) {
		this.alumnoId = alumnoId;
	}
	public Long getCursoId() {
		return cursoId;
	}
	public void setCursoId(Long cursoId) {
		this.cursoId = cursoId;
	}
	public Long getGrupoId() {
		return grupoId;
	}
	public void setGrupoId(Long grupoId) {
		this.grupoId = grupoId;
	}
	public String getAnioEscolar() {
		return anioEscolar;
	}
	public void setAnioEscolar(String anioEscolar) {
		this.anioEscolar = anioEscolar;
	}
	public String getUidLlave() {
		return uidLlave;
	}
	public void setUidLlave(String uidLlave) {
		this.uidLlave = uidLlave;
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
	
	
	
}
