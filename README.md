# Andaluh Chat para Minecraft

Un plugin de Minecraft (para Paper/Spigot) que permite a los jugadores transcribir automáticamente sus mensajes en el chat del español estándar a la **Propuesta de Ortografía Andaluza (EPA)**.

## Créditos y Origen

El motor de traducción que da vida a este plugin está basado en el increíble trabajo de la comunidad de **Andalugeeks**. 
- El proyecto original de transliteración es [andalugeeks/andaluh-py](https://github.com/andalugeeks/andaluh-py).
- La librería de Java utilizada internamente es un *fork* actualizado y mantenido en este repositorio, basado originalmente en [andalugeeks/andaluh-java](https://github.com/andalugeeks/andaluh-java).

Todos los créditos por la creación de las reglas ortográficas, la lógica base y el proyecto original pertenecen a **Andalugeeks**. Este plugin para Minecraft es un proyecto derivado construido para llevar esa funcionalidad a los servidores de juego.

## Características Principales

- 🔄 **Transliteración en Tiempo Real**: Traduce el texto al andaluz conforme los jugadores envían sus mensajes.
- 🎨 **Soporte para Múltiples Modos**: Los usuarios pueden elegir entre diferentes variaciones dialectales: `estándar` (uso de la cedilla `ç`), `seseo` (`s`), `ceceo` (`z`) o `heheo` (`h`).
- 🛡️ **Protección de Enlaces y Expresiones**: El plugin es inteligente y evita corromper enlaces web (HTTP/HTTPS), menciones, hashtags o emojis de texto como `xD`, `xd`, `XD`.
- 🌈 **Compatibilidad con Códigos de Color**: Soporte total para los colores de Minecraft (`&c`, `§a`, Códigos Hexadecimales `&#FF0000`, y formato MiniMessage `<red>`). ¡El andaluz respetará los colores que le pongas!
- 🤝 **Compatible con Plugins de Formato**: Funciona a la perfección con plugins de gestión de chat como **EssentialsX Chat**, aplicando la transliteración antes de que se añadan los prefijos, rangos y sufijos.
- 💾 **Persistencia Automática**: Las preferencias de cada jugador (si tienen el modo activado y qué variación dialectal usan) se guardan automáticamente en `config.yml`.

## Comandos Disponibles

El comando principal es `/andaluh` (alias: `/epa`). Requiere el permiso `andaluh.use` (concedido por defecto a todos los jugadores).

- `/andaluh on` - Activa la transcripción para tu usuario.
- `/andaluh off` - Desactiva la transcripción para tu usuario.
- `/andaluh modo <estandar|ceceo|seseo|heheo>` - Cambia la forma en la que se representa el sonido de tu andaluz.
- `/andaluh test <texto>` - Traduce un texto en tu pantalla para ver cómo quedaría sin llegar a enviarlo por el chat público.
- `/andaluh debug <on|off>` - (Solo Administradores) Imprime en la consola del servidor un desglose avanzado del procesamiento de mensajes.

## Instalación

1. Descarga el archivo `andaluh-chat.jar` de la pestaña de **Releases** en GitHub.
2. Colócalo en la carpeta `plugins` de tu servidor de Minecraft.
3. Reinicia o arranca el servidor. ¡Listo para usarse!

## Compilación Local

Para compilar el proyecto tú mismo desde el código fuente:

- **Objetivo**: Paper 1.21.x (Minecraft 1.21)
- **Requisito**: Java 21+ instalado.

```bash
# Clonar el repositorio y su submódulo (andaluh-java)
git clone --recursive https://github.com/xexuu/andaluh-chat.git
cd andaluh-chat

# Compilar con Maven
mvn clean package -DskipTests
```

El archivo compilado lo encontrarás en `target/andaluh-chat.jar`.
