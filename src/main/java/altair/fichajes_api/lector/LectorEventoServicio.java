package altair.fichajes_api.lector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;
import altair.fichajes_api.servicios.AsistenciaServicio;
import altair.fichajes_api.utilidades.Utilidades;

/**
 * Servicio encargado de procesar eventos del lector NFC. Permite leer la UID,
 * verificar si está asociada a una matrícula y registrar asistencia si
 * corresponde.
 */
@Service
public class LectorEventoServicio {

	@Autowired
	private LectorNfcFuncionalidad lectorNfcFuncionalidad;

	@Autowired
	private MatriculacionInterfaz matriculacionInterfaz;

	@Autowired
	private AsistenciaServicio asistenciaServicio;

	/**
	 * Procesa la UID leída por el lector NFC.
	 *
	 * @param modo "formulario" para solo consultar, cualquier otro valor para
	 *             registrar asistencia.
	 * @return Map con información de la UID y, si aplica, del alumno, curso y
	 *         grupo.
	 */
	public Map<String, Object> procesarEvento(String modo) {

		String uid = lectorNfcFuncionalidad.consumirUid();
		if (uid == null) {
			return Map.of("hayUid", false);
		}

		String anioActual = Utilidades.obtenerAnioEscolarActual();
		boolean esFormulario = "formulario".equals(modo);

		Optional<MatriculacionEntidad> matriculaOpt = matriculacionInterfaz
				.findConAlumnoCursoGrupoByUidLlaveAndAnioEscolar(uid, anioActual);

		if (matriculaOpt.isEmpty()) {
			return Map.of("hayUid", true, "registrado", false, "uid", uid);
		}

		MatriculacionEntidad m = matriculaOpt.get();

		String nombreAlumno = m.getAlumno().getNombreAlumno() + " " + m.getAlumno().getApellidoAlumno();

		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("hayUid", true);
		respuesta.put("registrado", true);
		respuesta.put("uid", uid);
		respuesta.put("alumno", nombreAlumno);
		respuesta.put("curso", m.getCurso().getNombreCurso());
		respuesta.put("grupo", m.getGrupo().getNombreGrupo());

		if (!esFormulario) {
			try {
				asistenciaServicio.ficharPorMatriculacion(m.getIdMatriculacion());
			} catch (RuntimeException e) {
				respuesta.put("mensaje", e.getMessage());
			}
		}

		return respuesta;
	}
}