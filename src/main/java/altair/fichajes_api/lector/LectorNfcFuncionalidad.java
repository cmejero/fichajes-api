package altair.fichajes_api.lector;

import org.springframework.stereotype.Service;

/**
 * Servicio que gestiona la última UID leída por el lector NFC. Permite
 * almacenar temporalmente la UID y consumirla desde los endpoints.
 */
@Service
public class LectorNfcFuncionalidad {
	
	// volatile asegura que, si varios hilos acceden a ultimoUid al mismo
	// tiempo, siempre vean el valor más reciente.
	private volatile String ultimoUid;

	/**
	 * Registra un nuevo UID detectado por el lector.
	 *
	 * @param uid UID detectada.
	 */
	public synchronized void nuevoUid(String uid) {
		this.ultimoUid = uid;
		// System.out.println("UID detectado: " + uid);
	}

	/**
	 * Devuelve la última UID registrada y la marca como consumida.
	 *
	 * @return Última UID leída o null si no hay ninguna.
	 */
	public synchronized String consumirUid() {
		String uid = ultimoUid;
		ultimoUid = null;
		return uid;
	}
}