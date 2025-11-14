package altair.fichajes_api.entidad;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "curso")
public class CursoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long idCurso;

    @Column(name = "nombre_curso", nullable = false, length = 50)
    private String nombreCurso;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoEntidad> grupos = new ArrayList<>();

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MatriculacionEntidad> matriculaciones = new ArrayList<>();

	public Long getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(Long idCurso) {
		this.idCurso = idCurso;
	}

	public String getNombreCurso() {
		return nombreCurso;
	}

	public void setNombreCurso(String nombreCurso) {
		this.nombreCurso = nombreCurso;
	}

	public List<GrupoEntidad> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoEntidad> grupos) {
		this.grupos = grupos;
	}

	public List<MatriculacionEntidad> getMatriculacion() {
		return matriculaciones;
	}

	public void setMatriculacion(List<MatriculacionEntidad> matriculacion) {
		this.matriculaciones = matriculacion;
	}

  
   
}
