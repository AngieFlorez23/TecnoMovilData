
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PruebaCarga {

    public static void main(String[] args) {

        System.out.println(
                "===================================="
        );
        System.out.println(
                "    TECNOMOVIL DATA - PRUEBA DE CARGA"
        );
        System.out.println(
                "===================================="
        );

        int cantidadTotal = 6_000_000;
        int tamanoLote = 100_000;
        int numeroLotes = cantidadTotal / tamanoLote;

        // Resultados acumulados
        Map<String, Long> afluenciaTotal = new HashMap<>();
        Map<Integer, Long> flujoTotal = new HashMap<>();
        Map<String, Long> rutasTotal = new HashMap<>();

        // Acumuladores para promedios ponderados
        Map<String, Double> minutosTotales = new HashMap<>();
        Map<String, Long> tramosTotales = new HashMap<>();

        long registrosProcesados = 0;
        long usuariosProcesados = 0;
        long lotesConRutasCriticas = 0;

        long inicio = System.nanoTime();

        // Procesar los 60 lotes
        for (int lote = 0; lote < numeroLotes; lote++) {

            long indiceInicial = (long) lote * tamanoLote;

            List<RegistroTransporte> registros =
                    DatosGenerador.generar(
                            tamanoLote, indiceInicial
                    );

            // FUNCIONALIDAD 1: Afluencia
            Map<String, Long> afluencia =
                    AnalizadorTransporte.calcularAfluencia(
                            registros
                    );

            afluencia.forEach((estacion, cantidad) ->
                    afluenciaTotal.merge(
                            estacion, cantidad, Long::sum
                    )
            );

            // FUNCIONALIDAD 2: Flujo por hora
            Map<Integer, Long> flujo =
                    AnalizadorTransporte.calcularFlujoPorHora(
                            registros
                    );

            flujo.forEach((hora, cantidad) ->
                    flujoTotal.merge(
                            hora, cantidad, Long::sum
                    )
            );

            // FUNCIONALIDAD 3: Uso por ruta
            Map<String, Long> rutas =
                    AnalizadorTransporte.calcularUsoPorRuta(
                            registros
                    );

            rutas.forEach((ruta, cantidad) ->
                    rutasTotal.merge(
                            ruta, cantidad, Long::sum
                    )
            );

            // FUNCIONALIDAD 4: Patrones de viaje
            Map<String, List<String>> patrones =
                    AnalizadorTransporte.obtenerPatronesViaje(
                            registros
                    );

            usuariosProcesados += patrones.size();

            // Contar los recorridos por par de estaciones.
            // El generador crea dos movimientos por usuario.
            Map<String, Long> conteosTramos =
                    patrones.values()
                            .stream()
                            .filter(p -> p.size() == 2)
                            .filter(p -> !p.get(0).equals(p.get(1)))
                            .collect(Collectors.groupingBy(
                                    p -> p.get(0) + " -> " + p.get(1),
                                    Collectors.counting()
                            ));

            // FUNCIONALIDAD 5: Tiempos promedio
            Map<String, Double> tiempos =
                    AnalizadorTransporte.calcularTiempoPromedio(
                            registros
                    );

            // Acumular minutos y cantidad de recorridos
            // para obtener el promedio global correcto.
            tiempos.forEach((tramo, promedio) -> {

                long cantidad =
                        conteosTramos.getOrDefault(tramo, 0L);

                minutosTotales.merge(
                        tramo,
                        promedio * cantidad,
                        Double::sum
                );

                tramosTotales.merge(
                        tramo,
                        cantidad,
                        Long::sum
                );
            });

            // FUNCIONALIDAD 6: Sobrecarga simulada
            Map<String, Boolean> criticas =
                    AnalizadorTransporte.detectarRutasCriticas(
                            registros, 15_000
                    );

            boolean hayCriticas = criticas.values()
                    .stream()
                    .anyMatch(Boolean::booleanValue);

            if (hayCriticas) {
                lotesConRutasCriticas++;
            }

            registrosProcesados += registros.size();

            // Mostrar progreso cada 10 lotes
            if ((lote + 1) % 10 == 0) {
                System.out.println(
                        "Lotes procesados: " + (lote + 1)
                                + " de " + numeroLotes
                                + " | Registros: " + registrosProcesados
                );
            }
        }

        long fin = System.nanoTime();

        // ==========================================
        // MOSTRAR RESULTADOS FINALES
        // ==========================================

        System.out.println(
                "\n========== RESULTADOS FINALES =========="
        );

        System.out.println(
                "Total registros: " + registrosProcesados
        );

        System.out.println(
                "Total usuarios: " + usuariosProcesados
        );

        System.out.println(
                "Afluencia por estacion: " + afluenciaTotal
        );

        System.out.println(
                "Movimientos por hora: " + flujoTotal
        );

        System.out.println(
                "Entradas por ruta: " + rutasTotal
        );

        // Mostrar tiempos promedio globales
        System.out.println(
                "\nTIEMPOS PROMEDIO GLOBALES:"
        );

        minutosTotales.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entrada -> {

                    String tramo = entrada.getKey();
                    long cantidad = tramosTotales.get(tramo);

                    double promedio =
                            entrada.getValue() / cantidad;

                    System.out.printf(
                            "%s: %.2f minutos%n",
                            tramo, promedio
                    );
                });

        // Clasificacion global simulada
        long umbralGlobal = 900_000;

        long rutasCriticasGlobales =
                rutasTotal.values()
                        .stream()
                        .filter(cantidad -> cantidad > umbralGlobal)
                        .count();

        System.out.println(
                "\nRutas criticas globales: "
                        + rutasCriticasGlobales
        );

        System.out.println(
                "Umbral global simulado: "
                        + umbralGlobal + " entradas"
        );

        System.out.println(
                "Lotes con rutas criticas: "
                        + lotesConRutasCriticas
        );

        System.out.printf(
                "Tiempo total: %.3f segundos%n",
                (fin - inicio) / 1_000_000_000.0
        );

        System.out.println(
                "\nPRUEBA DE CARGA FINALIZADA"
        );
    }
}

