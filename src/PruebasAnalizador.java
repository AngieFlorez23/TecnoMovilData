
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PruebasAnalizador {

    public static void main(String[] args) {

        // Datos simulados de prueba
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

        System.out.println("=== PRUEBAS TECNOMOVIL DATA ===");

        // Prueba 1: Afluencia
        Map<String, Long> afluencia =
                AnalizadorTransporte.calcularAfluencia(registros);

        verificar(
                "Afluencia por estacion",
                afluencia.get("Centro") == 1L
                        && afluencia.get("Norte") == 1L
                        && afluencia.get("Sur") == 1L
                        && !afluencia.containsKey("Universidad")
        );

        // Prueba 2: Horas pico
        Map<Integer, Long> horas =
                AnalizadorTransporte.calcularFlujoPorHora(registros);

        verificar(
                "Flujo por hora",
                horas.get(7) == 3L && horas.get(8) == 1L
        );

        // Prueba 3: Rutas mas utilizadas
        Map<String, Long> rutas =
                AnalizadorTransporte.calcularUsoPorRuta(registros);

        verificar(
                "Uso por ruta",
                rutas.get("R01") == 2L
                        && rutas.get("R02") == 1L
        );

        // Prueba 4: Patrones de viaje
        Map<String, List<String>> patrones =
                AnalizadorTransporte.obtenerPatronesViaje(registros);

        verificar(
                "Patrones por usuario",
                patrones.get("U001").equals(
                        List.of("Centro", "Universidad")
                )
        );

        // Prueba 5: Tiempo promedio
        Map<String, Double> tiempos =
                AnalizadorTransporte.calcularTiempoPromedio(registros);

        verificar(
                "Tiempo promedio entre estaciones",
                Math.abs(
                        tiempos.get("Centro -> Universidad") - 20.0
                ) < 0.001
        );

        // Prueba 6: Rutas criticas
        Map<String, Boolean> criticas =
                AnalizadorTransporte.detectarRutasCriticas(registros, 1);

        verificar(
                "Deteccion de sobrecarga",
                criticas.get("R01")
                        && !criticas.get("R02")
        );

        // =====================================
        // PRUEBA 7: LISTA VACIA
        // =====================================

        verificar(
                "Procesamiento de lista vacia",
                AnalizadorTransporte
                        .calcularAfluencia(List.of())
                        .isEmpty()
                        &&
                        AnalizadorTransporte
                                .calcularUsoPorRuta(List.of())
                                .isEmpty()
                        &&
                        AnalizadorTransporte
                                .calcularTiempoPromedio(List.of())
                                .isEmpty()
        );

        // =====================================
        // PRUEBA 8: UMBRAL INVALIDO
        // =====================================

        boolean errorDetectado = false;

        try {
            AnalizadorTransporte.detectarRutasCriticas(
                    registros, -1
            );
        } catch (IllegalArgumentException e) {
            errorDetectado = true;
        }

        verificar(
                "Validacion de umbral negativo",
                errorDetectado
        );

        // =====================================
        // PRUEBA 9: INMUTABILIDAD
        // =====================================

        boolean mapaInmutable = false;

        try {
            afluencia.put("NuevaEstacion", 100L);
        } catch (UnsupportedOperationException e) {
            mapaInmutable = true;
        }

        verificar(
                "Resultados inmutables",
                mapaInmutable
        );

        System.out.println(
                "\nTodas las pruebas finalizaron correctamente."
        );
    }

    private static void verificar(
            String descripcion, boolean condicion) {

        if (!condicion) {
            throw new AssertionError(
                    "FALLO: " + descripcion
            );
        }

        System.out.println(
                "APROBADA: " + descripcion
        );
    }
}
