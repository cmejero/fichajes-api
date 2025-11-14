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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "alumno")
public class AlumnoEntidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alumno")
    private Long idAlumno;

    @Column(name = "nombre_alumno", nullable = false, length = 100)
    private String nombreAlumno;

    @Column(name = "apellido_alumno", nullable = false, length = 100)
    private String apellidoAlumno;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MatriculacionEntidad> matriculaciones = new ArrayList<>();

    // Getters y Setters
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

    public List<MatriculacionEntidad> getMatriculaciones() {
        return matriculaciones;
    }

    public void setMatriculaciones(List<MatriculacionEntidad> matriculaciones) {
        this.matriculaciones = matriculaciones;
    }
}
