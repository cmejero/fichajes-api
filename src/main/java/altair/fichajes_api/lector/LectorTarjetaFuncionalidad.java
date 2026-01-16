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

import altair.fichajes_api.servicios.AsistenciaServicio;
import jakarta.annotation.PostConstruct;


/**
 * Servicio que gestiona la interacción con el lector de tarjetas NFC.
 * Permite la lectura de UID, la escucha activa y el fichaje automático
 * de alumnos mediante sus tarjetas.
 */
@Service
public class LectorTarjetaFuncionalidad {

	@Autowired
    private AsistenciaServicio asistenciaServicio;

    @Autowired
    private LectorNfcFuncionalidad lectorNfcFuncionalidad;
    
    // Control de última UID detectada y timestamp
    private String ultimaUid = null;
    private long ultimaDeteccion = 0; // en ms

    /**
     * Inicia la escucha automática del lector NFC y llama al callback
     * cuando se detecta una tarjeta nueva.
     *
     * @param callback Callback a invocar al detectar una tarjeta.
     */
    @PostConstruct
    public void iniciarEscucha() {
        iniciarEscucha(uid -> procesarUid(uid));
    }
    
    /**
     * Procesa una UID detectada: registra la UID y realiza el fichaje.
     *
     * @param uid UID de la tarjeta detectada.
     */
    @Async
    public void procesarUid(String uid) {
        lectorNfcFuncionalidad.nuevoUid(uid);
        try {
            asistenciaServicio.ficharPorUidTarjeta(uid);
        } catch (Exception e) {
           // System.err.println("No se pudo fichar: " + e.getMessage());
        }
    }
    
    
    /**
     * Lee el UID de una tarjeta conectada a un terminal específico.
     *
     * @param terminal Terminal donde está insertada la tarjeta.
     * @return UID de la tarjeta como cadena hexadecimal.
     * @throws Exception si no se puede leer la tarjeta.
     */
    public String leerUidTarjeta(CardTerminal terminal) throws Exception {
        Card card = null;
        try {
            card = terminal.connect("T=1"); // o "T=0"
            CardChannel channel = card.getBasicChannel();

            byte[] command = new byte[]{(byte)0xFF, (byte)0xCA, 0x00, 0x00, 0x00};
            ResponseAPDU response = channel.transmit(new CommandAPDU(command));
            byte[] uidBytes = response.getData();

            if (uidBytes == null || uidBytes.length == 0) {
                throw new RuntimeException("No se pudo leer el UID");
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
                } catch (CardException ex) {
                   // System.err.println("Advertencia al desconectar: " + ex.getMessage());
                }
            }
        }
    }

    
    /**
     * Método de utilidad para leer el UID de la primera tarjeta disponible
     * sin usar la escucha continua.
     *
     * @return UID de la tarjeta como cadena hexadecimal.
     */
    public interface LectorCallback {
        void tarjetaDetectada(String uid);
    }

    /**
     * Inicia la escucha continua del lector NFC.
     * Llama al callback solo una vez por tarjeta pasada.
     */
    public void iniciarEscucha(LectorCallback callback) {
        new Thread(() -> {
            try {
                TerminalFactory factory = TerminalFactory.getDefault();
                List<CardTerminal> terminals = factory.terminals().list();

                if (terminals.isEmpty()) {
                    System.err.println("No se detectó ningún lector NFC");
                    return;
                }

                CardTerminal terminal = terminals.get(0);
              //  System.out.println("Lector NFC listo: " + terminal.getName());

                while (true) {
                    try {
                        if (terminal.isCardPresent()) {
                            String uid = null;
                            try {
                                uid = leerUidTarjeta(terminal);
                            } catch (Exception e) {
                              //  System.err.println("Error leyendo tarjeta: " + e.getMessage());
                            }

                            long ahora = System.currentTimeMillis();
                            // Procesa UID solo si es diferente o han pasado 2 segundos
                            if (uid != null && (!uid.equals(ultimaUid) || (ahora - ultimaDeteccion) > 2000)) {
                                ultimaUid = uid;
                                ultimaDeteccion = ahora;
                               // System.out.println("UID detectado: " + uid);
                                callback.tarjetaDetectada(uid);
                            }

                            // Espera a que tarjeta se retire antes de continuar
                            try {
                                terminal.waitForCardAbsent(0);
                            } catch (CardException e) {
                              //  System.err.println("Error esperando a que tarjeta se retire: " + e.getMessage());
                            }
                        }
                    } catch (CardException e) {
                      //  System.err.println("Error terminal: " + e.getMessage());
                        try {
                            Thread.sleep(1000); // espera antes de reintentar
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    Thread.sleep(100); 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Método de utilidad para leer una sola tarjeta (no escucha continua)
     */
    public String leerUidTarjeta() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = factory.terminals().list();

            if (terminals.isEmpty()) {
                throw new RuntimeException("No se detectó ningún lector NFC");
            }

            return leerUidTarjeta(terminals.get(0));
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo la tarjeta NFC", e);
        }
    }
}
