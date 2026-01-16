package altair.fichajes_api.dtos;

import altair.fichajes_api.entidad.MatriculacionEntidad;

/**
 * Clase que se encarga de los campos de alumno y matriculacion
 */
public class AlumnoConMatriculacionDto {

    private Long idAlumno;
    private Long idMatriculacion;
    private String nombreAlumno;
    private String apellidoAlumno;
    private Long cursoId;
    private Long grupoId;
    private String anioEscolar;
    private String uidLlave;
    
    
    public static AlumnoConMatriculacionDto mapearADto(MatriculacionEntidad matricula) {
        AlumnoConMatriculacionDto dto = new AlumnoConMatriculacionDto();
        dto.setIdAlumno(matricula.getAlumno().getIdAlumno());
        dto.setIdMatriculacion(matricula.getIdMatriculacion());
        dto.setNombreAlumno(matricula.getAlumno().getNombreAlumno());
        dto.setApellidoAlumno(matricula.getAlumno().getApellidoAlumno());
        dto.setCursoId(matricula.getCurso().getIdCurso());
        dto.setGrupoId(matricula.getGrupo().getIdGrupo());
        dto.setAnioEscolar(matricula.getAnioEscolar());
        dto.setUidLlave(matricula.getUidLlave());
        return dto;
    }
    
	public Long getIdAlumno() {
		return idAlumno;
	}
	public void setIdAlumno(Long idAlumno) {
		this.idAlumno = idAlumno;
	}
	public Long getIdMatriculacion() {
		return idMatriculacion;
	}
	public void setIdMatriculacion(Long idMatriculacion) {
		this.idMatriculacion = idMatriculacion;
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
    
    
}
