package cl.psp;

import core.BasicMessageService;
import core.MessageService;
import extensions.CompressService;
import extensions.EncryptService;
import extensions.TimeStampService;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServiceRegistrationActivator implements BundleActivator {
    private ServiceRegistration<?> configuredServiceRegistration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Iniciando bundle y configurando servicio de mensajería...");

        // Carga las configuraciones desde el archivo de propiedades
        String[] features = loadConfiguration();

        // Instancia base del servicio
        MessageService baseService = new BasicMessageService();

        // Configura el servicio dinámicamente
        MessageService configuredService = configureService(baseService, features);

        // Registra el servicio configurado
        configuredServiceRegistration = context.registerService(MessageService.class.getName(), configuredService, null);

        System.out.println("Servicio configurado y registrado exitosamente.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (configuredServiceRegistration != null) {
            configuredServiceRegistration.unregister();
            System.out.println("Servicio desregistrado del Service Registry.");
        }
    }

    /**
     * Carga las configuraciones desde el archivo config.properties.
     * @return Arreglo con las funcionalidades seleccionadas.
     */
    private String[] loadConfiguration() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            String config = properties.getProperty("messaging.config", "1,2,3"); // Valores por defecto
            return config.split(",");
        } catch (IOException e) {
            System.err.println("Error cargando archivo de configuración: " + e.getMessage());
            return new String[]{}; // Sin configuraciones si ocurre un error
        }
    }

    /**
     * Configura el servicio dinámicamente según las opciones especificadas.
     * @param baseService Servicio base.
     * @param features Funcionalidades seleccionadas.
     * @return Servicio configurado.
     */
    private MessageService configureService(MessageService baseService, String[] features) {
        MessageService configuredService = baseService;

        for (String feature : features) {
            switch (feature.trim()) {
                case "1":
                    configuredService = new EncryptService(configuredService);
                    System.out.println("Funcionalidad de encriptación añadida.");
                    break;
                case "2":
                    configuredService = new CompressService(configuredService);
                    System.out.println("Funcionalidad de compresión añadida.");
                    break;
                case "3":
                    configuredService = new TimeStampService(configuredService);
                    System.out.println("Funcionalidad de marca de tiempo añadida.");
                    break;
                default:
                    System.out.println("Opción desconocida: " + feature);
                    break;
            }
        }

        return configuredService;
    }
}
