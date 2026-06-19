# AutoAlquiler - Sistema de Gestión de Alquiler de Vehículos

¡Bienvenido al repositorio central de **AutoAlquiler**! Este sistema es una solución tecnológica híbrida diseñada para centralizar, automatizar y gestionar el ciclo completo de alquiler de **cualquier medio de transporte humano**. 

Nuestra definición de "vehículo" es de amplio espectro: abarca desde micromovilidad ecológica (bicicletas, monopatines eléctricos) hasta vehículos de combustión o eléctricos de última generación (motocicletas, autos particulares, camionetas).

---

## 🏗️ Arquitectura del Proyecto

El sistema está construido bajo una arquitectura de **Monorepo Híbrido** que fusiona la robustez empresarial de un backend corporativo con la agilidad y ligereza de una aplicación de escritorio nativa:

* **Núcleo de Negocio (Backend):** Desarrollado en **Java (Spring Boot)** utilizando el patrón MVC con **Thymeleaf** y JavaScript para la interactividad dinámica de la interfaz web.
* **Contenedor de Escritorio (Frontend/Core Nativo):** Desarrollado en **Tauri v2 (Rust)**. Rust actúa como el orquestador del sistema operativo: levanta de forma silenciosa e independiente el servidor de Java, valida la disponibilidad de los puertos mediante *port polling* síncrono, y renderiza la interfaz web embebida dentro de un WebView nativo de alta eficiencia energética y de memoria.
* **Persistencia:** Base de datos relacional integrada (**SQLite**), lo que elimina la necesidad de instalar motores de bases de datos pesados (como PostgreSQL o MySQL) en la máquina de desarrollo o del cliente final.

---

## 📋 Requisitos Previos 
Antes de intentar compilar el proyecto, asegúrate de tener instaladas las siguientes herramientas en tu sistema operativo:

### 1. Entorno de Java
* **JDK 17 o superior** (Se recomienda GraalVM o OpenJDK 21).
* Asegúrate de que la variable de entorno `JAVA_HOME` esté correctamente configurada en tu sistema.

### 2. Entorno de Rust
* **Rustup y Cargo** (Última versión estable). Puedes instalarlo ejecutando:
```bash
  $ curl --proto '=https' --tlsv1.2 -sSf [https://sh.rustup.rs](https://sh.rustup.rs) | sh
```

### 3. Entorno de Node.js & Frontend
* **Node.js** (Versión 18 LTS o superior) junto con `npm`.

### 4. Dependencias del Sistema Operativo (Requerido por Tauri)

#### En Linux (Distribuciones basadas en Arch/Ubuntu):
Debes instalar las herramientas de desarrollo esenciales y las librerías de desarrollo webkit de GTK:
```bash
  $ sudo pacman -S --needed base-devel curl wget file openssl appmenu-gtk-module libappindicator-gtk3 webkit2gtk-4.1
```

En Windows:

  Instala las Herramientas de compilación de C++ de Visual Studio.
  WebView2 runtime (ya viene por defecto en Windows 11).

---
## 🚀 Guía de Instalación y Configuración 

Sigue estos pasos en orden estricto para compilar y ejecutar el entorno de desarrollo.

**Paso 1:** Clonar el Repositorio
```Bash
  $ git clone [https://github.com/tu-usuario/AutoAlquiler.git](https://github.com/tu-usuario/AutoAlquiler.git)

  $ cd AutoAlquiler
```
**Paso 2:** Compilar el Backend de Spring Boot

Necesitamos generar el archivo empaquetado .jar ejecutable de nuestra aplicación Java. En la raíz del proyecto ejecute:
Bash

### En Linux/macOS
```bash
  $./mvnw clean package -DskipTests
```
### En Windows (PowerShell)
```bash
  $ .\mvnw.cmd clean package -DskipTests
```
  *Esto creará un archivo ejecutable en la ruta: `target/AutoAlquiler-0.0.1-SNAPSHOT.jar` (o similar).*

  Copie este archivo .jar y péguelo dentro de la carpeta de recursos de Tauri renombrándolo como backend.jar
  Ruta destino: `autoalquiler-desktop/src-tauri/resources/backend.jar`

**Paso 3:** Construir el JRE Modular Personalizado

  Para que el cliente final no requiera tener Java instalado en su computadora, distribuimos un entorno de ejecución de Java (JRE) minificado y empaquetado dentro de la app mediante jlink.

  Ejecuta el siguiente comando desde la raíz del proyecto para generar este JRE liviano con los módulos de seguridad indispensables (java.security.jgss para Tomcat):



### 1. Asegurar que la carpeta destino esté limpia
```bash
  $ rm -rf autoalquiler-desktop/src-tauri/resources/jre
```

### 2. Generar el JRE modularizado
```bash
  $ jlink \
    --add-modules java.base,java.logging,java.xml,java.sql,java.naming,java.management,java.desktop,java.instrument,java.net.http,jdk.unsupported,java.security.jgss \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --output autoalquiler-desktop/src-tauri/resources/jre
```
### 3. Asignar permisos de ejecución nativos al binario de Java (Solo Linux/macOS)
```bash
  $ chmod +x autoalquiler-desktop/src-tauri/resources/jre/bin/java
```

**Paso 4:** Instalar Dependencias de `Node.js`

Muévase a la carpeta del proyecto de escritorio e instale los módulos de Node necesarios para la CLI de Tauri:


```bash
  $ cd autoalquiler-desktop

  $ npm install
```


💻 Ejecución en Entorno de Desarrollo (Dev Mode)

Una vez completada la instalación de los recursos en los pasos previos, puedes iniciar el entorno de desarrollo unificado.

Asegúrate de estar dentro de la carpeta autoalquiler-desktop y ejecuta:

```bash
  $ npm run tauri dev
```

#### ¿Qué sucederá internamente?

1. **Rust (`Tauri`)** se compilará e iniciará de inmediato.

2. El hilo principal de Rust leerá el JRE incrustado y despertará el proceso de `backend.jar` en segundo plano.

3. Spring Boot inicializará su servidor embebido **Tomcat en el puerto `8070`** e inyectará/creará automáticamente la base de datos SQLite dentro del directorio local persistente del usuario (`~/.local/share/com.autoalquiler.app` en Linux o `AppData` en Windows).

4. Un algoritmo de **Espera Activa (Port Polling)** en Rust mantendrá en pausa el despliegue visual de la ventana de escritorio. Realizará pings TCP constantes al puerto `8070`.

5. En el instante exacto en que Spring Boot termine de levantar (aprox. 4 segundos), Rust detectará la conexión exitosa, liberará la ventana y renderizará el sistema de gestión de vehículos de forma inmediata, evitando pantallas de error de red.
---

## 📂 Estructura General del Directorio

```text
AutoAlquiler/                     <-- Raíz del Proyecto (Contexto Spring Boot)
├── .mvn/                         <-- Archivos de configuración de Maven Wrapper
├── src/                          <-- Código fuente de Java (Controladores, Servicios, Capa de Dominio)
│   ├── main/
│   │   ├── java/com/json/        <-- Lógica de negocio de Alquiler de Vehículos
│   │   └── resources/
│   │       ├── templates/        <-- Vistas HTML de Thymeleaf
│   │       └── static/           <-- Estilos CSS y funciones de JavaScript nativo
├── pom.xml                       <-- Archivo de dependencias de Maven (Java)
│
└── autoalquiler-desktop/         <-- Contenedor de Escritorio (Contexto Tauri)
    ├── package.json              <-- Scripts de Node y dependencias de la CLI de Tauri
    └── src-tauri/                <-- Código nativo del instalador
        ├── Cargo.toml            <-- Dependencias y manifiesto de Rust
        ├── tauri.conf.json       <-- Configuración de ventanas y permisos de Tauri
        ├── src/
        │   └── main.rs           <-- Código de ciclo de vida, Port Polling y persistencia de procesos
        └── resources/            <-- Binarios embebidos (Creados en el paso a paso de la instalación)
            ├── backend.jar       <-- Archivo compilado de Spring Boot
            └── jre/              <-- JRE modularizado exclusivo para la ejecución local
```
---
## 🛠️ Solución de Problemas Comunes (Troubleshooting)

### 1. Error: `Connection Refused` en la ventana de la aplicación
* **Causa:** La ventana gráfica de Tauri se abrió antes de que la JVM de Spring Boot terminara de inicializar el puerto `8070`.
* **Solución:** Asegúrate de estar utilizando la última versión de `src-tauri/src/main.rs` la cual cuenta con el bucle síncrono de verificación de sockets (`TcpStream`) que retiene la ventana hasta que el backend responde de manera afirmativa.

### 2. Excepción: `java.lang.NoClassDefFoundError: org/ietf/jgss/GSSException`
* **Causa:** El JRE embebido fue compilado sin el módulo de seguridad que requiere el servidor Tomcat por defecto.
* **Solución:** Elimina la carpeta `jre/` vieja y vuelve a ejecutar detalladamente el comando `jlink` provisto en el **Paso 3** de esta guía, verificando que incluya la bandera `java.security.jgss`.

### 3. Error: `No existe el fichero o el directorio` al iniciar el subproceso
* **Causa:** Las rutas relativas de Tauri fallaron al mapear en modo desarrollo (`debug`) o los archivos no se copiaron a la carpeta de recursos de la compilación de Cargo.
* **Solución:** El código cuenta con un bloque de contingencia inteligente que busca los archivos en la raíz del espacio de trabajo físico de desarrollo si no los encuentra en los temporales de compilación. Verifica que los nombres de los archivos en `src-tauri/resources/` coincidan estrictamente con minúsculas y mayúsculas (`jre/bin/java` y `backend.jar`).

---
## ⚖️ Licencia (License)

Este proyecto está bajo la licencia **GNU General Public License v3.0 (GPL-3.0)**. Elegimos esta licencia para honrar las fuentes universitarias que hicieron posible este sistema, garantizando que el software permanezca **100% libre, abierto y protegido** para su uso público, científico y académico. Cualquier trabajo derivado o modificación de este código debe ser distribuido obligatoriamente bajo esta misma licencia.

Para leer los términos legales completos, consulta el archivo [LICENSE](LICENSE) en la raíz de este repositorio.