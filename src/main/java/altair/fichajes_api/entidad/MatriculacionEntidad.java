package altair.fichajes_api.entidad;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "matriculacion")
public class MatriculacionEntidad {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_matriculacion")
    private Long idMatriculacion;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    @JsonBackReference
    private AlumnoEntidad alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonBackReference
    

    private CursoEntidad curso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    @JsonBackReference

    private GrupoEntidad grupo;

    @Column(name = "anio_escolar", nullable = false)
    private String anioEscolar;

    @Column(name = "uid_llave", length = 100)
    private String uidLlave;

    @OneToMany(mappedBy = "matriculacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AsistenciaEntidad> asistencias = new ArrayList<>();

    // Getters y Setters
    public Long getIdMatriculacion() {
        return idMatriculacion;
    }

    public void setIdMatriculacion(Long idMatriculacion) {
        this.idMatriculacion = idMatriculacion;
    }

    public AlumnoEntidad getAlumno() {
        return alumno;
    }

    public void setAlumno(AlumnoEntidad alumno) {
        this.alumno = alumno;
    }

    public CursoEntidad getCurso() {
        return curso;
    }

    public void setCurso(CursoEntidad curso) {
        this.curso = curso;
    }

    public GrupoEntidad getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoEntidad grupo) {
        this.grupo = grupo;
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

    public List<AsistenciaEntidad> getAsistencias() {
        return asistencias;
    }

    public void setAsistencias(List<AsistenciaEntidad> asistencias) {
        this.asistencias = asistencias;
    }
}
