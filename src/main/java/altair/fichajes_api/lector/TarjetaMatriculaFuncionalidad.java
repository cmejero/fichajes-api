package altair.fichajes_api.lector;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;

/**
 * Servicio que vincula tarjetas NFC con matriculaciones.
 * Permite obtener la matrícula asociada a una UID y año escolar específico.
 */
@Service
public class TarjetaMatriculaFuncionalidad {

    @Autowired
    private MatriculacionInterfaz matriculacionInterfaz;

    /**
     * Obtiene la matrícula asociada a una tarjeta NFC y un año escolar.
     *
     * @param uidTarjeta UID de la tarjeta NFC.
     * @param anioEscolarActual Año escolar en formato "AAAA-AAAA".
     * @return MatriculacionEntidad correspondiente a la UID y año escolar.
     * @throws RuntimeException si no existe matrícula asociada.
     */
    public MatriculacionEntidad obtenerMatriculaPorUidYAnio(String uidTarjeta, String anioEscolarActual) {
        return matriculacionInterfaz.findByUidLlaveAndAnioEscolar(uidTarjeta, anioEscolarActual)
            .orElseThrow(() -> new RuntimeException(
                "La tarjeta no pertenece a ningún alumno del año escolar activo: " + anioEscolarActual));
    }

}