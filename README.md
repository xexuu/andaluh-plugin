# Andaluh Minecraft Plugin

Plugin de Minecraft que transcribe el chat del espanol al andaluh (EPA) usando `andaluh-java`.

## Comandos

- `/andaluh on` activa la transcripcion para tu usuario.
- `/andaluh off` desactiva la transcripcion para tu usuario.
- `/andaluh modo <estandar|ceceo|seseo|heheo>` cambia el modo de salida.
- `/andaluh test <texto>` prueba la transliteracion sin usar el chat.

Notas sobre modos:

- `estandar`: usa `ç` como salida para /s/ (EPA por defecto).
- `ceceo`: fuerza `z` como salida.
- `seseo`: fuerza `s` como salida.
- `heheo`: fuerza `h` como salida.

Se acepta `zezeo` como alias de `ceceo`.

El estado por jugador se guarda automaticamente en `config.yml`.

Para depurar, puedes activar `debug: true` en `config.yml` y el plugin imprimira los mensajes antes y despues de la transliteracion.

## Compatibilidad y Java

- Objetivo: Paper 1.21.11 (Minecraft 1.21.x).
- Requiere Java 21 para compilar y ejecutar.

## Build local

```bash
mvn -q -DskipTests package
```

El jar queda en `target/andaluh-plugin.jar`.

## Releases en GitHub

El workflow `Build and Release` crea una release automaticamente cuando haces push de un tag `v*`.

Ejemplo:

```bash
git tag v0.1.0
git push origin v0.1.0
```
