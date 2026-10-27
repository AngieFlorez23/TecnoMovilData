
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("         TECNOMOVIL DATA");
        System.out.println(" Sistema de Transporte Urbano");
        System.out.println("================================");

        // Crear registros simulados
        List<RegistroTransporte> registros = List.of(

                new RegistroTransporte(
                        "U001", "R01", "Centro", "entrada",
                        LocalDateTime.of(2026, 10, 27, 7, 30)
                ),

                new RegistroTransporte(
                        "U002", "R01", "Norte", "entrada",
                        LocalDateTime.of(2026, 10, 27, 7, 35)
                ),

                new RegistroTransporte(
                        "U001", "R01", "Universidad", "salida",
                        LocalDateTime.of(2026, 10, 27, 7, 50)
                ),

                new RegistroTransporte(
                        "U003", "R02", "Sur", "entrada",
                        LocalDateTime.of(2026, 10, 27, 8, 10)
                )
        );

        // Mostrar registros de transporte
        System.out.println("\nREGISTROS DE TRANSPORTE:");

        registros.forEach(registro ->
                System.out.println(
                        "Usuario: " + registro.idUsuario()
                                + " | Ruta: " + registro.ruta()
                                + " | Estacion: " + registro.estacion()
                                + " | Accion: " + registro.accion()
                                + " | Fecha: " + registro.timestamp()
                )
        );

        System.out.println("\nTotal de registros: "
                + registros.size());

        // =====================================
        // FUNCIONALIDAD 1: AFLUENCIA
        // =====================================

        System.out.println("\nAFLUENCIA POR ESTACION:");

        AnalizadorTransporte.calcularAfluencia(registros)
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada ->
                        System.out.println(
                                entrada.getKey() + ": "
                                        + entrada.getValue() + " entradas"
                        )
                );

// =====================================
// FUNCIONALIDAD 2: HORAS PICO
// =====================================

        System.out.println("\nFLUJO DE PASAJEROS POR HORA:");

        Map<Integer, Long> flujoPorHora =
                AnalizadorTransporte.calcularFlujoPorHora(registros);

// Mostrar las horas ordenadas
        flujoPorHora.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada ->
                        System.out.printf(
                                "%02d:00 - %d movimientos%n",
                                entrada.getKey(),
                                entrada.getValue()
                        )
                );

// Identificar la hora con mayor flujo
        flujoPorHora.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .ifPresentOrElse(
                        horaPico -> System.out.printf(
                                "HORA PICO: %02d:00 (%d movimientos)%n",
                                horaPico.getKey(),
                                horaPico.getValue()
                        ),
                        () -> System.out.println(
                                "No hay movimientos registrados."
                        )
                );

// =====================================
// FUNCIONALIDAD 3: RUTAS MAS UTILIZADAS
// =====================================

        System.out.println("\nRUTAS MAS UTILIZADAS:");

        Map<String, Long> usoPorRuta =
                AnalizadorTransporte.calcularUsoPorRuta(registros);

// Ordenar rutas de mayor a menor uso
        usoPorRuta.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue(
                                java.util.Comparator.reverseOrder()
                        ).thenComparing(Map.Entry.comparingByKey())
                )
                .forEach(entrada ->
                        System.out.println(
                                "Ruta " + entrada.getKey()
                                        + ": " + entrada.getValue()
                                        + " entradas"
                        )
                );

// Identificar la ruta con mayor uso
        usoPorRuta.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue(
                                java.util.Comparator.reverseOrder()
                        ).thenComparing(Map.Entry.comparingByKey())
                )
                .findFirst()
                .ifPresent(ruta ->
                        System.out.println(
                                "RUTA MAS UTILIZADA: " + ruta.getKey()
                                        + " (" + ruta.getValue() + " entradas)"
                        )
                );

// =====================================
// FUNCIONALIDAD 4: PATRONES DE VIAJE
// =====================================

        System.out.println("\nPATRONES DE VIAJE POR USUARIO:");

        Map<String, List<String>> patrones =
                AnalizadorTransporte.obtenerPatronesViaje(registros);

// Mostrar usuarios ordenados por identificador
        patrones.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada ->
                        System.out.println(
                                "Usuario " + entrada.getKey()
                                        + ": "
                                        + String.join(" -> ", entrada.getValue())
                        )
                );

// =====================================
// FUNCIONALIDAD 5:
// TIEMPO PROMEDIO ENTRE ESTACIONES
// =====================================

        System.out.println(
                "\nTIEMPO PROMEDIO ENTRE ESTACIONES:"
        );

        Map<String, Double> tiempos =
                AnalizadorTransporte.calcularTiempoPromedio(registros);

// Mostrar resultados ordenados por recorrido
        tiempos.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada ->
                        System.out.printf(
                                "%s: %.2f minutos%n",
                                entrada.getKey(),
                                entrada.getValue()
                        )
                );

// Verificar si existen recorridos calculados
        if (tiempos.isEmpty()) {
            System.out.println(
                    "No hay recorridos suficientes para calcular tiempos."
            );
        }

        // =====================================
        // FUNCIONALIDAD 6:
        // DETECCION DE SOBRECARGA EN RUTAS
        // =====================================

        System.out.println(
                "\nDETECCION DE SOBRECARGA EN RUTAS:"
        );

        // Umbral simulado para nuestros datos
        long umbral = 1;

        Map<String, Boolean> rutasCriticas =
                AnalizadorTransporte.detectarRutasCriticas(
                        registros, umbral
                );

        // Mostrar cada ruta y su clasificacion
        rutasCriticas.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada -> {

                    String estado = entrada.getValue()
                            ? "CRITICA"
                            : "NORMAL";

                    System.out.println(
                            "Ruta " + entrada.getKey()
                                    + " | Entradas: "
                                    + usoPorRuta.get(entrada.getKey())
                                    + " | Estado: " + estado
                    );
                });

        System.out.println(
                "Umbral simulado: " + umbral
        );

// ==========================================
// PRUEBA DE VOLUMEN - 100.000 REGISTROS
// ==========================================

        System.out.println(
                "\nPRUEBA DE PROCESAMIENTO MASIVO"
        );

        int cantidad = 1_000_000;

// Medir tiempo de generacion
        long inicio = System.nanoTime();

        List<RegistroTransporte> datosMasivos =
                DatosGenerador.generar(cantidad);

        long finGeneracion = System.nanoTime();

// Procesar los registros con Streams
        long inicioAnalisis = System.nanoTime();

        Map<String, Long> afluenciaMasiva =
                AnalizadorTransporte.calcularAfluencia(
                        datosMasivos
                );

        Map<String, Long> rutasMasivas =
                AnalizadorTransporte.calcularUsoPorRuta(
                        datosMasivos
                );

        long finAnalisis = System.nanoTime();

// Mostrar resultados
        System.out.println(
                "Registros generados: " + datosMasivos.size()
        );

        System.out.printf(
                "Tiempo de generacion: %.3f segundos%n",
                (finGeneracion - inicio) / 1_000_000_000.0
        );

        System.out.printf(
                "Tiempo de analisis: %.3f segundos%n",
                (finAnalisis - inicioAnalisis) / 1_000_000_000.0
        );

        System.out.println("Afluencia: " + afluenciaMasiva);

        System.out.println("Uso por rutas: " + rutasMasivas);

        // ==========================================
        // PRUEBA INTEGRAL DE LAS 6 FUNCIONALIDADES
        // ==========================================

        System.out.println(
                "\nPRUEBA INTEGRAL - TECNOMOVIL DATA"
        );

        // Usar una muestra de los datos masivos
        List<RegistroTransporte> muestra =
                datosMasivos;

        long inicioIntegral = System.nanoTime();

        // 1. Afluencia por estacion
        var afluenciaIntegral =
                AnalizadorTransporte.calcularAfluencia(muestra);

        // 2. Flujo por hora
        var horasIntegral =
                AnalizadorTransporte.calcularFlujoPorHora(muestra);

        // 3. Rutas mas utilizadas
        var rutasIntegral =
                AnalizadorTransporte.calcularUsoPorRuta(muestra);

        // 4. Patrones de viaje
        var patronesIntegral =
                AnalizadorTransporte.obtenerPatronesViaje(muestra);

        // 5. Tiempo promedio entre estaciones
        var tiemposIntegral =
                AnalizadorTransporte.calcularTiempoPromedio(muestra);

        // 6. Deteccion de rutas criticas
        long umbralIntegral = 166_666;

        var criticasIntegral =
                AnalizadorTransporte.detectarRutasCriticas(
                        muestra, umbralIntegral
                );

        long finIntegral = System.nanoTime();

        // Mostrar un resumen de resultados
        System.out.println(
                "Registros analizados: " + muestra.size()
        );

        System.out.println(
                "Estaciones analizadas: "
                        + afluenciaIntegral.size()
        );

        System.out.println(
                "Horas analizadas: "
                        + horasIntegral.size()
        );

        System.out.println(
                "Rutas analizadas: "
                        + rutasIntegral.size()
        );

        System.out.println(
                "Usuarios analizados: "
                        + patronesIntegral.size()
        );

        System.out.println(
                "Recorridos con tiempo promedio: "
                        + tiemposIntegral.size()
        );

        long totalCriticas = criticasIntegral.values()
                .stream()
                .filter(Boolean::booleanValue)
                .count();

        System.out.println(
                "Rutas criticas: " + totalCriticas
        );

        System.out.printf(
                "Tiempo integral: %.3f segundos%n",
                (finIntegral - inicioIntegral)
                        / 1_000_000_000.0
        );

        // ==========================================
        // DIAGNOSTICO DE MEMORIA JAVA
        // ==========================================

        System.out.println(
                "\nDIAGNOSTICO DE MEMORIA"
        );

        Runtime memoria = Runtime.getRuntime();

        long memoriaMaxima =
                memoria.maxMemory() / (1024 * 1024);

        long memoriaAsignada =
                memoria.totalMemory() / (1024 * 1024);

        long memoriaUtilizada =
                (memoria.totalMemory() - memoria.freeMemory())
                        / (1024 * 1024);

        System.out.println(
                "Memoria maxima JVM: "
                        + memoriaMaxima + " MB"
        );

        System.out.println(
                "Memoria asignada JVM: "
                        + memoriaAsignada + " MB"
        );

        System.out.println(
                "Memoria utilizada JVM: "
                        + memoriaUtilizada + " MB"
        );

    } // Fin del metodo main

} // Fin de la clase Main
