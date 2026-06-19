// Prevents additional console window on Windows in release, DO NOT REMOVE!!
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

use std::process::{Child, Command};
use std::sync::Mutex;
use std::net::TcpStream;
use std::time::Duration;
use std::thread;
use tauri::Manager;
use tauri::path::BaseDirectory;

struct BackendProcess(Mutex<Option<Child>>);

fn main() {
    tauri::Builder::default()
        .manage(BackendProcess(Mutex::new(None)))
        .setup(|app| {

            if cfg!(debug_assertions) {
                println!("Modo Desarrollo: Rust ignora el JAR porque Spring Boot se ejecuta externamente.");
            } else {
                println!("Modo Producción: Rust ejecuta el backend.jar embebido.");
                let handle = app.handle();
                
                // 1. Resolver rutas del JRE y JAR
                let mut java_exe = handle.path()
                    .resolve("resources/jre/bin/java", BaseDirectory::Resource)
                    .expect("Error crítico: No se pudo resolver la ruta de Java");
                
                let mut jar_path = handle.path()
                    .resolve("resources/backend.jar", BaseDirectory::Resource)
                    .expect("Error crítico: No se pudo resolver la ruta del JAR");

                if cfg!(debug_assertions) {
                    if !java_exe.exists() || !jar_path.exists() {
                        if let Ok(cwd) = std::env::current_dir() {
                            let dev_java = cwd.join("resources/jre/bin/java");
                            let dev_jar = cwd.join("resources/backend.jar");
                            
                            if dev_java.exists() && dev_jar.exists() {
                                java_exe = dev_java;
                                jar_path = dev_jar;
                            }
                        }
                    }
                }

                // 2. Configurar directorio de datos persistentes
                let app_data_dir = handle.path().app_data_dir()
                    .expect("Error crítico: No se pudo obtener el directorio AppData");
                std::fs::create_dir_all(&app_data_dir).unwrap();
                let app_data_str = app_data_dir.to_string_lossy().into_owned();

                // 3. Lanzar Spring Boot en segundo plano
                let child = Command::new(java_exe)
                    .arg("-jar")
                    .arg(jar_path)
                    .env("APP_DATA_DIR", &app_data_str) 
                    .spawn()
                    .expect("Error: Falló el inicio del servidor Spring Boot");

                let process_state = handle.state::<BackendProcess>();
                *process_state.0.lock().unwrap() = Some(child);

                // --- 🚀 BLOQUE DE ESPERA ACTIVA (PORT POLLING) ---
                println!("\n================ [SISTEMA] ================");
                println!("[Tauri] Sincronizando tiempos: Esperando a Spring Boot en puerto 8070...");
                
                let mut backend_ready = false;
                let socket_addr = "127.0.0.1:8070";
                
                // Reintentamos la conexión TCP: 50 intentos con pausas de 200ms = 10 segundos máximo.
                for i in 1..=50 {
                    // Intentamos un apretón de manos TCP rápido en el puerto local
                    if TcpStream::connect_timeout(&socket_addr.parse().unwrap(), Duration::from_millis(150)).is_ok() {
                        backend_ready = true;
                        println!("[Tauri] ¡Backend detectado con éxito en el intento {}! Abriendo ventana...", i);
                        break;
                    }
                    // Si falla, esperamos un poco antes del siguiente ping
                    thread::sleep(Duration::from_millis(200));
                }

                if !backend_ready {
                    eprintln!("[Tauri] ⚠️ El backend tardó más de 10 segundos o falló al levantar.");
                }
                println!("===========================================\n");
            }
            Ok(())
        })
        .on_window_event(|window, event| {
            if let tauri::WindowEvent::Destroyed = event {
                let process_state = window.state::<BackendProcess>();
                let mut lock = process_state.0.lock().unwrap();
                if let Some(mut child) = lock.take() {
                    let _ = child.kill();
                    println!("Servidor Spring Boot cerrado correctamente. Se envio solicitud para liberar el Puerto 8070");
                }
            }
        })
        .run(tauri::generate_context!())
        .expect("Error al ejecutar la aplicación Tauri");
}