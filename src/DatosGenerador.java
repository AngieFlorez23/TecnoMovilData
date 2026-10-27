
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

public class DatosGenerador {

    // Generacion normal utilizada en Main.java
    public static List<RegistroTransporte> generar(
            int cantidad) {

        return generar(cantidad, 0);
    }

    // Generacion por lotes con indice inicial
    public static List<RegistroTransporte> generar(
            int cantidad, long indiceInicial) {

        if (cantidad < 0 || indiceInicial < 0) {
            throw new IllegalArgumentException(
                    "La cantidad y el indice deben ser positivos."
            );
        }

        LocalDateTime fechaInicial =
                LocalDateTime.of(2026, 10, 27, 6, 0);

        List<String> estaciones = List.of(
                "Centro",
                "Norte",
                "Sur",
                "Universidad",
                "Terminal"
        );

        return IntStream.range(0, cantidad)
                .mapToObj(posicion -> {

                    // Indice unico entre todos los lotes
                    long i = indiceInicial + posicion;

                    long numeroUsuario = i / 2;

                    String usuario =
                            "U" + (numeroUsuario + 1);

                    String ruta =
                            "R0" + (numeroUsuario % 3 + 1);

                    boolean esEntrada = i % 2 == 0;

                    String accion = esEntrada
                            ? "entrada"
                            : "salida";

                    int indiceEstacion =
                            (int) (numeroUsuario % estaciones.size());

                    String estacion = esEntrada
                            ? estaciones.get(indiceEstacion)
                            : estaciones.get(
                            (indiceEstacion + 1) % estaciones.size()
                    );

                    int minutoInicial =
                            (int) (numeroUsuario % 720);

                    int duracion =
                            5 + (int) (numeroUsuario % 26);

                    LocalDateTime fecha = fechaInicial
                            .plusMinutes(minutoInicial)
                            .plusMinutes(esEntrada ? 0 : duracion);

                    return new RegistroTransporte(
                            usuario,
                            ruta,
                            estacion,
                            accion,
                            fecha
                    );
                })
                .toList();
    }
}
