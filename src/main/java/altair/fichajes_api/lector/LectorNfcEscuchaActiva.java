package altair.fichajes_api.lector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.servicios.AsistenciaServicio;
import jakarta.annotation.PostConstruct;


/**
 * Servicio que activa la escucha de eventos del lector NFC de manera continua.
 * Detecta UID de tarjetas y registra asistencia automáticamente.
 */
@Service
public class LectorNfcEscuchaActiva {

	@Autowired
	AsistenciaServicio asistenciaServicio;
	private final LectorTarjetaFuncionalidad lectorTarjetaFuncionalidad;
	private final LectorNfcFuncionalidad lectorNfcFuncionalidad;

	public LectorNfcEscuchaActiva(LectorTarjetaFuncionalidad lectorTarjetaFuncionalidad,
			LectorNfcFuncionalidad lectorNfcService) {
		this.lectorTarjetaFuncionalidad = lectorTarjetaFuncionalidad;
		this.lectorNfcFuncionalidad = lectorNfcService;
	}

	
	 /**
     * Inicializa la escucha activa del lector de tarjetas.
     * Cada UID detectada se procesa para registrar asistencia y notificar al sistema.
     */
	@PostConstruct
	public void iniciar() {
		lectorTarjetaFuncionalidad.iniciarEscucha(uid -> {
			//System.out.println("UID detectado: " + uid); // log
			try {
				// Intentamos fichar automáticamente
				asistenciaServicio.ficharPorUidTarjeta(uid);
				//System.out.println("Fichaje realizado para UID: " + uid);
			} catch (Exception e) {
				//System.err.println("No se pudo fichar: " + e.getMessage());
			}

			// Guardar también en memoria si quieres notificar al frontend
			lectorNfcFuncionalidad.nuevoUid(uid);
		});
	}
}
