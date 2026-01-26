package altair.fichajes_api.lector;

import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las UID leídas por el lector NFC.
 * Almacena la última UID detectada de forma segura para su posterior consulta.
 */
@Service
public class LectorNfcFuncionalidad {

	 private volatile String ultimaUid = null;

	    /**
	     * Registra una nueva UID leída por el lector NFC.
	     * Solo se almacena si la UID no es nula ni está vacía.
	     *
	     * @param uid Identificador único leído por el lector NFC
	     */
	    public void nuevoUid(String uid) {
	        if (uid != null && !uid.isBlank()) {
	            ultimaUid = uid;
	        }
	    }

	    /**
	     * Obtiene la última UID registrada por el lector NFC.
	     *
	     * @return Última UID leída o null si no se ha registrado ninguna
	     */
	    public String obtenerUltimaUid() {
	        return ultimaUid;
	    }
}
