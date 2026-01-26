package altair.fichajes_api.lector;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.EventoLectorDto;
import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;
import altair.fichajes_api.servicios.AsistenciaServicio;
import altair.fichajes_api.utilidades.Utilidades;

@Service
/**
 * Servicio encargado de procesar los eventos generados por el lector NFC.
 * Obtiene la UID leída y consulta si existe una matriculación válida en el
 * curso escolar actual, devolviendo la información asociada al alumno.
 */
public class LectorEventoServicio {

    @Autowired
    private LectorNfcFuncionalidad lectorNfcFuncionalidad;

    @Autowired
    private MatriculacionInterfaz matriculacionInterfaz;

 
    /**
     * Procesa el evento del lector NFC en el modo indicado. Consulta la última
     * UID detectada y verifica si está asociada a una matriculación válida en
     * el curso escolar actual.
     *
     * @param modo Modo de funcionamiento del lector
     * @return DTO con la información del evento y del alumno si existe registro
     */
    public EventoLectorDto procesarEvento(String modo) {

        EventoLectorDto dto = new EventoLectorDto();

        // SOLO CONSULTA (no consume)
        String uid = lectorNfcFuncionalidad.obtenerUltimaUid();
        if (uid == null) {
            dto.setHayUid(false);
            return dto;
        }

        dto.setHayUid(true);
        dto.setUid(uid);

        String anioActual = Utilidades.obtenerAnioEscolarActual();

        Optional<MatriculacionEntidad> matriculaOpt =
                matriculacionInterfaz.findConAlumnoCursoGrupoByUidLlaveAndAnioEscolar(uid, anioActual);

        if (matriculaOpt.isEmpty()) {
            dto.setRegistrado(false);
            return dto;
        }

        MatriculacionEntidad m = matriculaOpt.get();

        dto.setRegistrado(true);
        dto.setAlumno(m.getAlumno().getNombreAlumno() + " " + m.getAlumno().getApellidoAlumno());
        dto.setCurso(m.getCurso().getNombreCurso());
        dto.setGrupo(m.getGrupo().getNombreGrupo());

       

        return dto;
    }
}
