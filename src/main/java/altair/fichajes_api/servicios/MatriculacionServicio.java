package altair.fichajes_api.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.MatriculacionDto;
import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.AlumnoInterfaz;
import altair.fichajes_api.repositorios.CursoInterfaz;
import altair.fichajes_api.repositorios.GrupoInterfaz;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;

@Service
public class MatriculacionServicio {

    @Autowired
    private MatriculacionInterfaz matriculacionInterfaz;

    @Autowired
    private AlumnoInterfaz alumnoInterfaz;

    @Autowired
    private CursoInterfaz cursoInterfaz;

    @Autowired
    private GrupoInterfaz grupoInterfaz;

    // Crear nueva matriculación
    public MatriculacionEntidad crearMatriculacion(MatriculacionDto dto) {
        MatriculacionEntidad matricula = new MatriculacionEntidad();

        alumnoInterfaz.findById(dto.getAlumnoId()).ifPresent(matricula::setAlumno);
        cursoInterfaz.findById(dto.getCursoId()).ifPresent(matricula::setCurso);
        grupoInterfaz.findById(dto.getGrupoId()).ifPresent(matricula::setGrupo);

        matricula.setAnioEscolar(dto.getAnioEscolar());
        matricula.setUidLlave(dto.getUidLlave());

        return matriculacionInterfaz.save(matricula);
    }

    // Obtener todas las matriculaciones
    public List<MatriculacionDto> obtenerTodasDto() {
        List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findAll();

        return matriculas.stream().map(m -> {
            MatriculacionDto dto = new MatriculacionDto();
            dto.setIdMatriculacion(m.getIdMatriculacion());
            dto.setAlumnoId(m.getAlumno().getIdAlumno());
            dto.setCursoId(m.getCurso().getIdCurso());
            dto.setGrupoId(m.getGrupo().getIdGrupo());
            dto.setAnioEscolar(m.getAnioEscolar());
            dto.setUidLlave(m.getUidLlave());
            return dto;
        }).toList();
    }


    // Obtener una por ID
    public Optional<MatriculacionEntidad> obtenerPorId(Long id) {
        return matriculacionInterfaz.findById(id);
    }

    // Eliminar matriculación
    public boolean eliminarMatriculacion(Long id) {
        if (matriculacionInterfaz.existsById(id)) {
            matriculacionInterfaz.deleteById(id);
            return true;
        }
        return false;
    }
}
