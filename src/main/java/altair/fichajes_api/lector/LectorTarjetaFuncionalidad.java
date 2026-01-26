package altair.fichajes_api.lector;

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import altair.fichajes_api.logs.Logs;
import altair.fichajes_api.servicios.AsistenciaServicio;
import jakarta.annotation.PostConstruct;

@Service
/**
 * Servicio encargado de gestionar la lectura de tarjetas NFC mediante PC/SC.
 * Detecta tarjetas, lee su UID y lanza los procesos asociados evitando
 * lecturas duplicadas consecutivas.
 */
public class LectorTarjetaFuncionalidad {

    @Autowired
    private AsistenciaServicio asistenciaServicio;

    @Autowired
    private LectorNfcFuncionalidad lectorNfcFuncionalidad;

    private String ultimaUid = null;
    private long ultimaDeteccion = 0;

    @PostConstruct
    /**
     * Inicia automáticamente la escucha del lector NFC al arrancar la aplicación.
     * Configura el callback para procesar las UID detectadas.
     */
    public void iniciarEscucha() {
        iniciarEscucha(uid -> procesarUid(uid));
    }

    @Async
    /**
     * Procesa de forma asíncrona la UID leída por el lector NFC.
     * Registra la UID y ejecuta el fichaje asociado al alumno.
     *
     * @param uid Identificador único leído de la tarjeta NFC
     */
    public void procesarUid(String uid) {
        lectorNfcFuncionalidad.nuevoUid(uid);
        try {
            asistenciaServicio.ficharPorUidTarjeta(uid);
        } catch (Exception e) {
            Logs.ficheroLog("No se pudo fichar: " + e.getMessage());
        }
    }

    /**
     * Interfaz callback utilizada para notificar la detección de una tarjeta NFC.
     */
    public interface LectorCallback {
        void tarjetaDetectada(String uid);
    }

    public void iniciarEscucha(LectorCallback callback) {

        new Thread(() -> {

            while (true) {
                try {
                    ejecutarBucleLectura(callback);
                } catch (Exception e) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

        }, "hilo-lector-nfc").start();
    }
    
    
    /**
     * Ejecuta el bucle principal de lectura del lector NFC.
     * Gestiona la conexión al lector, la detección de tarjetas y
     * el control de errores y reconexiones.
     *
     * @param callback Acción a ejecutar cuando se detecta una UID
     */
    private void ejecutarBucleLectura(LectorCallback callback) {

        while (true) {

            try {
                TerminalFactory factory = TerminalFactory.getDefault();
                List<CardTerminal> terminals = factory.terminals().list();

                if (terminals.isEmpty()) {
                    Logs.ficheroLog("BUSCANDO LECTOR...");
                    Thread.sleep(1000);
                    continue;
                }

                CardTerminal terminal = terminals.get(0);
                Logs.ficheroLog("Lector NFC conectado: " + terminal.getName());

                // Bucle interno para leer mientras exista lector
                while (true) {

                    try {
                        if (terminal.isCardPresent()) {

                            String uid = leerUidTarjeta(terminal);

                            long ahora = System.currentTimeMillis();
                            if (uid != null && (!uid.equals(ultimaUid) || ahora - ultimaDeteccion > 2000)) {
                                ultimaUid = uid;
                                ultimaDeteccion = ahora;
                                callback.tarjetaDetectada(uid);
                            }

                            terminal.waitForCardAbsent(1000);
                        }

                        Thread.sleep(100);

                    } catch (CardException e) {
                        Logs.ficheroLog("Lector desconectado o fallo: " + e.getMessage());
                        break; // vuelve a buscar lector
                    }
                }

            } catch (Exception e) {
                Logs.ficheroLog("Error general lector NFC: " + e.getMessage());
            }

            try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }


    /**
     * Lee la UID de una tarjeta NFC presente en el terminal indicado.
     *
     * @param terminal Terminal NFC conectado
     * @return UID de la tarjeta en formato hexadecimal
     * @throws Exception Si ocurre un error durante la lectura
     */
    public String leerUidTarjeta(CardTerminal terminal) throws Exception {

        Card card = null;
        try {
            card = terminal.connect("T=1");
            CardChannel channel = card.getBasicChannel();

            byte[] command = {(byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x00};
            ResponseAPDU response = channel.transmit(new CommandAPDU(command));

            byte[] uidBytes = response.getData();
            if (uidBytes == null || uidBytes.length == 0) {
                throw new RuntimeException("UID vacío");
            }

            StringBuilder sb = new StringBuilder();
            for (byte b : uidBytes) {
                sb.append(String.format("%02X", b));
            }

            return sb.toString();

        } finally {
            if (card != null) {
                try {
                    card.disconnect(false);
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Lee la UID de una tarjeta NFC utilizando el primer lector disponible.
     *
     * @return UID de la tarjeta en formato hexadecimal
     * @throws RuntimeException Si no se detecta lector o falla la lectura
     */
    public String leerUidTarjeta() {
        try {
        	TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
        	List<CardTerminal> terminals = factory.terminals().list();


            if (terminals.isEmpty()) {
                throw new RuntimeException("No se detectó ningún lector NFC");
            }

            return leerUidTarjeta(terminals.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo tarjeta NFC", e);
        }
    }
    
   

}
