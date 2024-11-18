package proyecto2java;

//librerias que vamos a usar
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

//metodo para implemnetar el mqtt 
public class MqttToFile {

    private static final String BROKER = "tcp://192.168.0.16:1883";
    private static final String CLIENT_ID = "MqttToFileClient";
    private static final String TOPIC = "sensor/luz";
    private static final int QOS = 1;
    private static final String OUTPUT_FILE = "mensajes.txt";
    
    public static void main(String[] args) {
        try {
            // Crear cliente MQTT
            MqttClient client = new MqttClient(BROKER, CLIENT_ID, new MqttDefaultFilePersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Conectando al broker: " + BROKER);
            client.connect(connOpts);
            System.out.println("Conectado al broker");

            // Configurar callback para manejar mensajes
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.err.println("Conexión perdida: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String receivedMessage = new String(message.getPayload());
                    System.out.println("Mensaje recibido del tema " + topic + ": " + receivedMessage);

                    // Guardar mensaje en archivo
                    try (FileWriter writer = new FileWriter(OUTPUT_FILE, true)) {
                        writer.write("Tema: " + topic + ", Mensaje: " + receivedMessage + "\n");
                    } catch (IOException e) {
                        System.err.println("Error al guardar el mensaje en el archivo: " + e.getMessage());
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Entrega completada");
                }
            });

            // Suscribirse al tema
            client.subscribe(TOPIC, QOS);
            System.out.println("Suscrito al tema: " + TOPIC);

        } catch (MqttException e) {
            System.err.println("Error en el cliente MQTT: " + e.getMessage());
            e.printStackTrace();
        
    }
}
