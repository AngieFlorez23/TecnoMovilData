
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.Duration;
import java.util.Comparator;
import java.util.stream.IntStream;

public class AnalizadorTransporte {

    // ==========================================
    // FUNCIONALIDAD 1: AFLUENCIA POR ESTACION
    // ==========================================

    public static Map<String, Long> calcularAfluencia(
            List<RegistroTransporte> registros) {

        return Map.copyOf(
                registros.stream()
                        .filter(registro ->
                                registro.accion()
                                        .equalsIgnoreCase("entrada")
                        )
                        .collect(Collectors.groupingBy(
                                RegistroTransporte::estacion,
                                Collectors.counting()
                        ))
        );
    }

    // ==========================================
    // FUNCIONALIDAD 2: FLUJO POR HORA
    // ==========================================

    public static Map<Integer, Long> calcularFlujoPorHora(
            List<RegistroTransporte> registros) {

        return Map.copyOf(
                registros.stream()
                        .collect(Collectors.groupingBy(
                                registro ->
                                        registro.timestamp().getHour(),
                                Collectors.counting()
                        ))
        );
    }

    // ==========================================
    // FUNCIONALIDAD 3: RUTAS MAS UTILIZADAS
    // ==========================================

    public static Map<String, Long> calcularUsoPorRuta(
            List<RegistroTransporte> registros) {

        return Map.copyOf(
                registros.stream()

                        // Contar unicamente las entradas
                        .filter(registro ->
                                registro.accion()
                                        .equalsIgnoreCase("entrada")
                        )

                        // Agrupar registros por ruta
                        .collect(Collectors.groupingBy(
                                RegistroTransporte::ruta,

                                // Contar entradas por ruta
                                Collectors.counting()
                        ))
        );

    }

    // ==========================================
    // FUNCIONALIDAD 4: PATRONES DE VIAJE
    // ==========================================

    public static Map<String, List<String>> obtenerPatronesViaje(
            List<RegistroTransporte> registros) {

        return Map.copyOf(
                registros.stream()

                        // Ordenar movimientos por fecha y hora
                        .sorted(
                                java.util.Comparator.comparing(
                                        RegistroTransporte::timestamp
                                )
                        )

                        // Agrupar los movimientos por usuario
                        .collect(Collectors.groupingBy(
                                RegistroTransporte::idUsuario,

                                // Obtener las estaciones visitadas
                                Collectors.mapping(
                                        RegistroTransporte::estacion,
                                        Collectors.toUnmodifiableList()
                                )
                        ))
        );
    }

    // ==========================================
    // FUNCIONALIDAD 5:
    // TIEMPO PROMEDIO ENTRE ESTACIONES
    // ==========================================

    public static Map<String, Double> calcularTiempoPromedio(
            List<RegistroTransporte> registros) {

        return Map.copyOf(
                registros.stream()

                        // Agrupar movimientos por usuario
                        .collect(Collectors.groupingBy(
                                RegistroTransporte::idUsuario
                        ))

                        .values()
                        .stream()

                        // Procesar los movimientos de cada usuario
                        .flatMap(movimientos -> {

                            // Ordenar cronologicamente
                            List<RegistroTransporte> ordenados =
                                    movimientos.stream()
                                            .sorted(Comparator.comparing(
                                                    RegistroTransporte::timestamp
                                            ))
                                            .toList();

                            // Crear pares de movimientos consecutivos
                            return IntStream.range(
                                            0, ordenados.size() - 1
                                    )

                                    // Excluir movimientos en la misma
                                    // estacion o sin tiempo transcurrido
                                    .filter(i ->
                                            !ordenados.get(i).estacion()
                                                    .equals(ordenados.get(i + 1).estacion())
                                                    &&
                                                    ordenados.get(i + 1).timestamp()
                                                            .isAfter(ordenados.get(i).timestamp())
                                    )

                                    // Calcular minutos por recorrido
                                    .mapToObj(i -> Map.entry(

                                            ordenados.get(i).estacion()
                                                    + " -> "
                                                    + ordenados.get(i + 1).estacion(),

                                            Duration.between(
                                                    ordenados.get(i).timestamp(),
                                                    ordenados.get(i + 1).timestamp()
                                            ).toSeconds() / 60.0
                                    ));
                        })

                        // Agrupar recorridos iguales y promediar
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.averagingDouble(
                                        Map.Entry::getValue
                                )
                        ))
        );
    }

    // ==========================================
    // FUNCIONALIDAD 6:
    // DETECCION DE SOBRECARGA EN RUTAS
    // ==========================================

    public static Map<String, Boolean> detectarRutasCriticas(
            List<RegistroTransporte> registros,
            long umbral) {

        // Validar que el umbral sea correcto
        if (umbral < 0) {
            throw new IllegalArgumentException(
                    "El umbral no puede ser negativo."
            );
        }

        // Reutilizar el calculo de entradas por ruta
        Map<String, Long> usoPorRuta =
                calcularUsoPorRuta(registros);

        // Clasificar las rutas segun el umbral
        return usoPorRuta.entrySet()
                .stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entrada -> entrada.getValue() > umbral
                ));
    }

}
