package altair.fichajes_api.entidad;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "grupo")
public class GrupoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_grupo")
    private Long idGrupo;

    @Column(name = "nombre_grupo", nullable = false, length = 50)
    private String nombreGrupo;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private CursoEntidad curso;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
    private List<MatriculacionEntidad> matriculacion = new ArrayList<>();

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public CursoEntidad getCurso() {
		return curso;
	}

	public void setCurso(CursoEntidad curso) {
		this.curso = curso;
	}

	public List<MatriculacionEntidad> getMatriculacion() {
		return matriculacion;
	}

	public void setMatriculacion(List<MatriculacionEntidad> matriculacion) {
		this.matriculacion = matriculacion;
	}

 
    
}
