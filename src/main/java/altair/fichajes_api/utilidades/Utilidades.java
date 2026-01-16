package altair.fichajes_api.utilidades;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase con metodos que usaremos varias veces en la aplicación
 */
public class Utilidades {

	/**
	 * Método que devuelve el nombre del archivo de log basado en la fecha actual.
	 * 
	 * @return el nombre del archivo de log
	 */
	public static final String nombreArchivoLog() {
		try {
			LocalDate fechaActual = LocalDate.now();
			String fechaStr = fechaActual.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
			return "log-" + fechaStr + ".txt";
		} catch (Exception e) {
			return "log-error.txt";
		}
	}

	/**
	 * Método que devuelve el nombre de la carpeta basada en la fecha actual.
	 * 
	 * @return el nombre de la carpeta basada en la fecha
	 */
	public static final String nombreCarpetaFecha() {
		try {
			LocalDate fechaActual = LocalDate.now();
			return fechaActual.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
		} catch (Exception e) {
			return "errorFecha";
		}
	}

	/**
	 * Obtiene el año escolar actual en formato "AAAA-AAAA". Considera que el año
	 * escolar inicia en septiembre y termina en junio.
	 *
	 * @return String con el año escolar actual.
	 */

	public static String obtenerAnioEscolarActual() {
		LocalDate hoy = LocalDate.now();
		int inicio;
		int fin;

		if (hoy.getMonthValue() >= 9) {
			inicio = hoy.getYear();
			fin = hoy.getYear() + 1;
		} else {
			inicio = hoy.getYear() - 1;
			fin = hoy.getYear();
		}

		return inicio + "-" + fin;
	}
}
