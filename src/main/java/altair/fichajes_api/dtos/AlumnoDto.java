package altair.fichajes_api.dtos;

import java.util.List;

public class AlumnoDto {
	private Long idAlumno;
    private String nombreAlumno;
    private String apellidoAlumno;
    private List<Long> matriculacionIds;
  
    
    
    
	public Long getIdAlumno() {
		return idAlumno;
	}
	public void setIdAlumno(Long idAlumno) {
		this.idAlumno = idAlumno;
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
	public List<Long> getMatriculacionIds() {
		return matriculacionIds;
	}
	public void setMatriculacionIds(List<Long> matriculacionIds) {
		this.matriculacionIds = matriculacionIds;
	}
	
	
    
    
}
