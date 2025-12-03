package altair.fichajes_api.logs;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import altair.fichajes_api.utilidades.Utilidades;

/**
* Clase que se encarga de escribir registros (logs) en archivos.
*/
public class Logs {

   private static final DateTimeFormatter FORMATEADOR_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

   private static final String RUTA_BASE_LOGS = "C:\\Users\\Carlo\\OneDrive\\Escritorio\\FICHEROS\\apiFichajesAltairLog\\";

   /**
    * Método que se encarga de escribir un mensaje en un archivo log.
    * Crea la carpeta y el archivo si no existen, y añade el mensaje con fecha y hora.
    *
    * @param mensaje Texto que se desea registrar.
    */
   public static void ficheroLog(String mensaje) {
       try {
           String marcaDeTiempo = LocalDateTime.now().format(FORMATEADOR_FECHA);
           String entradaLog = "[" + marcaDeTiempo + "] " + mensaje;

           String nombreCarpeta = Utilidades.nombreCarpetaFecha();
           String nombreArchivo = Utilidades.nombreArchivoLog();

           String rutaCarpeta = RUTA_BASE_LOGS + nombreCarpeta;
           File carpeta = new File(rutaCarpeta);
           if (!carpeta.exists()) {
               carpeta.mkdirs();
           }

           String rutaArchivo = rutaCarpeta + File.separator + nombreArchivo;
           FileWriter escritor = new FileWriter(rutaArchivo, true);
           escritor.write(entradaLog + "\n");
           escritor.close();

       } catch (Exception e) {
 
       }
   }
}