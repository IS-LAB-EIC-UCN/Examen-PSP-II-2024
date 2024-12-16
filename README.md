# Exámen II-2024 
<hr>

### Asignatura: Patrones de Software y Programación <br> Profesor: Daniel San Martín <br> Fecha: 17-12-2024

La prueba tiene 100pts y está dividida en 3 partes de 34%, 33% y 33%. Hay un BONUS que da 10 pts en la sección 2. 
<hr>

### Enunciado
<p style='text-align: justify;'>
Una empresa de telecomunicaciones está desarrollando un sistema para la gestión y personalización de servicios de mensajería. 
El sistema debe ser lo suficientemente flexible para adaptarse a las necesidades específicas de los clientes, 
permitiéndoles agregar o combinar funcionalidades como encriptación, compresión y marcas de tiempo en los mensajes enviados.
</p>
<p style='text-align: justify;'>
El objetivo principal es crear un sistema modular y extensible que facilite la integración de nuevas funcionalidades sin 
alterar el núcleo del sistema, manteniendo el código limpio y respetando principios de diseño. Además, para garantizar la 
mantenibilidad y escalabilidad del sistema, la empresa ha decidido implementar estas funcionalidades como bundles OSGi, 
permitiendo una gestión dinámica de los módulos en tiempo de ejecución.
</p>

El sistema base proporciona una funcionalidad para enviar mensajes. Inicialmente, el servicio solo envía un mensaje simple con el siguiente formato:

    [Mensaje enviado]: <contenido del mensaje>

El código base del sistema está definido como:

```java
public interface MessageService {
    String sendMessage(String message);
}

public class BasicMessageService implements MessageService {

    @Override
    public String sendMessage(String message) {
        return "[Mensaje enviado]: " + message;
    }
}
```

### Ejercicios a Resolver: Parte I (34%)

1. Refactorice el sistema base para que pueda soportar la adición de nuevas funcionalidades sin modificar el código existente.
   (**20pts**)   

2. Implemente las siguientes funcionalidades como módulos independientes (**20pts**):

    1. **Encriptación**: Transforma el mensaje en un formato cifrado, asegurando su privacidad.

    2. **Compresión**: Reduce el tamaño del mensaje, optimizando el uso de recursos de red.

    3. **Marca de tiempo**: Agrega la fecha y hora exacta en que se envió el mensaje, útil para auditorías.

El sistema debe ser capaz de combinar estas funcionalidades según las preferencias del cliente. Para esto construya una 
clase Main de ejemplo, donde de su uso esperado seria algo similar al siguiente código:

```java
public class Main{
    
    public static void main(String[] args) {

        MessageService service = new BasicMessageService(new TimeStampService());
        service.sendMessage("Hello, World");
    }
    
    
}
```
Salida esperada:

    [Mensaje enviado]: < Mensaje con marca de tiempo >

3. El diseño debe permitir agregar futuras funcionalidades sin afectar las ya existentes. Explique la solución del problema
de la siguiente forma (**20pts**):
   1. Patrón escogido para dar la solución a la problemática, en que consiste este patrón y su diagrama de clases usando las
   clases creadas.
   2. Principios de diseño que atiende el patrón y explíquelos.


### Ejercicios a Resolver: Parte II  (33%)

1. Crea tres bundles OSGi independientes, cada uno implementando una de las funcionalidades especificadas (**20pts**):

    1. **Bundle de Encriptación:** Proporciona la funcionalidad de cifrado.
    2. **Bundle de Compresión:** Proporciona la funcionalidad de compresión.
    3. **Bundle de Marca de Tiempo:** Proporciona la funcionalidad de agregar marcas de tiempo.

2. Diseña un bundle principal que registre dinámicamente las funcionalidades en el servicio OSGi. Este bundle debe permitir
que un cliente configure el servicio de mensajería con la combinación de funcionalidades que desee (por ejemplo, compresión + encriptación, o solo marca de tiempo).
   Esto no es OBLIGATORIO, pero suma puntos. (**10pts**)

Ejemplo de interacción:

1. El cliente activa la funcionalidad de compresión y encriptación a través del bundle principal.
2. Los mensajes enviados a través del sistema aplican estas funcionalidades automáticamente.

### Ejercicios a Resolver: Parte III  (33%)

1. Implementa un bundle de pruebas que utilice JUnit para validar:

    1. Que cada funcionalidad (encriptación, compresión, marca de tiempo) opera correctamente de forma independiente (**10pts**).
    2. Que las combinaciones de funcionalidades producen los resultados esperados (**10pts**)..
   

Ejemplo de prueba básica:

```java
@Test
public void testEncryptService() {
    MessageService service = new BasicMessageService();
    service = new EncryptService(service);
    String result = service.sendMessage("Prueba");
    assertNotNull(result);
    // Valida que el mensaje esté cifrado.
}
```

### Aspectos Técnicos

1. Para la implementación del patrón, cree un nuevo módulo en el proyecto nombrado como **Patron**. Construya un árbol de 
paquetes para un mejor entendimiento de la solución.
2. Para la implementación de los bundles, utilice la siguiente configuración del POM.xml

```xml
<parent>
    <groupId>cl.psp</groupId>
    <artifactId>Examen-PSP-II-2024</artifactId>
    <version>1.0</version>
  </parent>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <build>
    <plugins>
      <plugin>
        <groupId>org.apache.felix</groupId>
        <artifactId>maven-bundle-plugin</artifactId>
        <version>5.1.9</version>
        <extensions>true</extensions>
        <configuration>
          <instructions>
            <Bundle-SymbolicName>${project.artifactId}</Bundle-SymbolicName>
            <Private-Package>${private.packages}</Private-Package>
            <Import-Package>${import.packages}</Import-Package> <!-- defined in bundle.properties -->
            <Export-Package>${export.packages}</Export-Package> <!-- define in bundle.properties -->
          </instructions>
          <supportIncrementalBuild>true</supportIncrementalBuild>
        </configuration>
        <executions>
          <execution>
            <id>bundle-manifest</id>
            <phase>process-classes</phase>
            <goals>
              <goal>manifest</goal>
            </goals>
            <configuration>
              <manifestLocation>META-INF</manifestLocation>
              <instructions>
                <_noee>true</_noee>
                <_removeheaders>Import-Service,Export-Service</_removeheaders>
              </instructions>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.13.0</version>
        <configuration>
          <source>17</source>
          <target>17</target>
        </configuration>
      </plugin>
      <plugin>
        <!-- Simply read properties from file -->
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>properties-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
          <execution>
            <phase>initialize</phase>
            <goals>
              <goal>read-project-properties</goal>
            </goals>
            <configuration>
              <files>
                <file>bundle.properties</file>
              </files>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```

3. Para el registro dinámico de los bundles utilize el ejemplo de Activador que se encuentra en la clase **Activator.java**
El funcionamiento es el siguiente:

   1. Inicio del Bundle, se ejecuta el método start, que:

      1. Carga el archivo config.properties.
      2. Lee la configuración del cliente.
      3. Aplica las decoraciones correspondientes al servicio base.
      4. Registra el servicio configurado en el Service Registry.
      
   2. Detención del Bundle:

      1. Se ejecuta el método stop, que desregistra el servicio del Service Registry.

Supongamos que el archivo **config.properties** contiene **messaging.config=1,3**. Al iniciar el bundle debe mostrarse 
en consola Felix:

      Iniciando bundle y configurando servicio de mensajería...
      Funcionalidad de encriptación añadida.
      Funcionalidad de marca de tiempo añadida.
      Servicio configurado y registrado exitosamente.


4. Para el bundle de testing, utilice las siguientes configuraciones en el POM.xml

```xml

 <parent>
        <groupId>cl.psp</groupId>
        <artifactId>Examen-PSP-2024</artifactId>
        <version>1.0</version>
    </parent>

    <dependencies>
        <!-- Sling Testing PaxExam -->
        <dependency>
            <groupId>org.apache.sling</groupId>
            <artifactId>org.apache.sling.testing.paxexam</artifactId>
            <version>4.0.0</version>
            <scope>test</scope>
        </dependency>
        <!-- an OSGi framework -->
        <dependency>
            <groupId>org.apache.felix</groupId>
            <artifactId>org.apache.felix.framework</artifactId>
            <version>7.0.5</version>
            <scope>test</scope>
        </dependency>

        <!-- JUnit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
        <!-- Pax Exam -->
        <dependency>
            <groupId>org.ops4j.pax.exam</groupId>
            <artifactId>pax-exam</artifactId>
            <version>4.13.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.ops4j.pax.exam</groupId>
            <artifactId>pax-exam-cm</artifactId>
            <version>4.13.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.ops4j.pax.exam</groupId>
            <artifactId>pax-exam-container-forked</artifactId>
            <version>4.13.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.ops4j.pax.exam</groupId>
            <artifactId>pax-exam-junit4</artifactId>
            <version>4.13.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.ops4j.pax.logging</groupId>
            <artifactId>pax-logging-log4j2</artifactId>
            <version>2.2.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.ops4j.pax.logging</groupId>
            <artifactId>pax-logging-log4j2-extra</artifactId>
            <version>2.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.ops4j.pax.exam</groupId>
            <artifactId>pax-exam-link-mvn</artifactId>
            <version>4.13.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.6.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.16</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-core</artifactId>
            <version>2.24.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j2-impl</artifactId>
            <version>2.24.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-api</artifactId>
            <version>2.24.1</version>
        </dependency>
        <dependency>
            <groupId>cl.psp</groupId>
            <artifactId>org.poliza</artifactId>
            <version>1.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>cl.psp</groupId>
            <artifactId>org.polizaxml</artifactId>
            <version>1.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>5.14.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.felix</groupId>
                <artifactId>maven-bundle-plugin</artifactId>
                <version>5.1.9</version>
                <extensions>true</extensions>
                <configuration>
                    <instructions>
                        <Bundle-SymbolicName>${project.artifactId}</Bundle-SymbolicName>
                        <Private-Package>${private.packages}</Private-Package>
                        <Import-Package>${import.packages}</Import-Package> <!-- defined in bundle.properties -->
                        <Export-Package>${export.packages}</Export-Package> <!-- define in bundle.properties -->
                    </instructions>
                    <remoteOBR>repo-rel</remoteOBR>
                    <prefixUrl>
                        file:///home/dsanmartins/releases
                    </prefixUrl>
                    <supportIncrementalBuild>true</supportIncrementalBuild>
                </configuration>
                <executions>
                    <execution>
                        <id>bundle-manifest</id>
                        <phase>process-classes</phase>
                        <goals>
                            <goal>manifest</goal>
                        </goals>
                        <configuration>
                            <manifestLocation>META-INF</manifestLocation>
                            <instructions>
                                <_noee>true</_noee>
                                <_removeheaders>Import-Service,Export-Service</_removeheaders>
                            </instructions>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            <plugin>
                <!-- Simply read properties from file -->
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>properties-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <phase>initialize</phase>
                        <goals>
                            <goal>read-project-properties</goal>
                        </goals>
                        <configuration>
                            <files>
                                <file>bundle.properties</file>
                            </files>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```