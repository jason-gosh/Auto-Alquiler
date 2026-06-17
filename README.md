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

## 📋 Requisitos Previos (Prerequisites)

Antes de intentar compilar el proyecto, asegúrate de tener instaladas las siguientes herramientas en tu sistema operativo:

### 1. Entorno de Java
* **JDK 17 o superior** (Se recomienda GraalVM o OpenJDK 17/21).
* Asegúrate de que la variable de entorno `JAVA_HOME` esté correctamente configurada en tu sistema.

### 2. Entorno de Rust
* **Rustup y Cargo** (Última versión estable). Puedes instalarlo ejecutando:
    ```bash
    curl --proto '=https' --tlsv1.2 -sSf [https://sh.rustup.rs](https://sh.rustup.rs) | sh
    ```

### 3. Entorno de Node.js & Frontend
* **Node.js** (Versión 18 LTS o superior) junto con `npm`.

### 4. Dependencias del Sistema Operativo (Requerido por Tauri)

#### En Linux (Distribuciones basadas en Arch/Ubuntu):
Debes instalar las herramientas de desarrollo esenciales y las librerías de desarrollo webkit de GTK:
```bash
sudo pacman -S --needed base-devel curl wget file openssl appmenu-gtk-module libappindicator-gtk3 webkit2gtk-4.1
