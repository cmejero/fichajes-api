package altair.fichajes_api.lector;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;

/**
 * Servicio que vincula una tarjeta NFC con una matrícula.
 */
@Service
public class TarjetaMatriculaFuncionalidad {

    @Autowired
    private MatriculacionInterfaz matriculacionInterfaz;

    public MatriculacionEntidad obtenerMatriculaPorUidYAnio(String uidTarjeta, String anioEscolarActual) {
        return matriculacionInterfaz.findByUidLlaveAndAnioEscolar(uidTarjeta, anioEscolarActual)
            .orElseThrow(() -> new RuntimeException(
                "La tarjeta no pertenece a ningún alumno del año escolar activo: " + anioEscolarActual));
    }

}
