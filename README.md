
# TecnoMovil Data

## Descripción

TecnoMovil Data es un proyecto académico desarrollado en Java
para analizar registros simulados del sistema de transporte
urbano de TecnoValle.

El proyecto aplica conceptos de programación funcional,
como funciones puras, expresiones lambda, Streams e
inmutabilidad.

## Funcionalidades

1. Calcular la afluencia de pasajeros por estación.
2. Identificar los horarios de mayor movimiento.
3. Determinar las rutas más utilizadas.
4. Analizar los patrones de viaje por usuario.
5. Calcular el tiempo promedio entre estaciones.
6. Identificar rutas críticas mediante un umbral simulado.

## Tecnologías

- Java
- IntelliJ IDEA
- Java Streams y expresiones lambda
- Git y GitHub

## Estructura del proyecto

- Main.java: ejecución principal del sistema.
- RegistroTransporte.java: estructura de cada registro.
- AnalizadorTransporte.java: funciones de análisis.
- DatosGenerador.java: generación de datos simulados.
- PruebasAnalizador.java: pruebas automatizadas.
- PruebaCarga.java: procesamiento por lotes.

## Ejecución

Abrir el proyecto en IntelliJ IDEA y ejecutar:

- Main: demostración y análisis de registros.
- PruebasAnalizador: validación de las funcionalidades.
- PruebaCarga: procesamiento de seis millones de registros.

## Resultados obtenidos

Se realizaron pruebas con datos simulados:

- 9 pruebas automatizadas aprobadas.
- 1.000.000 de registros en la prueba integral.
- 6.000.000 de registros procesados en 60 lotes.
- Tiempo registrado en una prueba de carga: 12,790 segundos.

Los tiempos de ejecución pueden variar según el equipo.

## Consideraciones

Los datos son simulados y no representan movimientos
reales de pasajeros.

La detección de rutas críticas utiliza un umbral sobre
la cantidad de entradas registradas. Por lo tanto,
no representa la ocupación simultánea real de los vehículos.

## Propósito académico

Proyecto desarrollado como evidencia de aprendizaje
sobre programación funcional en Java.
