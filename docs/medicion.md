# Medición

Los tiempos salen de correr `./gradlew run` con los cuatro puntos ya
resueltos. `App` imprime el número de procesadores y una fila por versión;
copie cada fila en la tabla. Las celdas de tiempo van en milisegundos con los
decimales que imprime el programa; la aceleración es el tiempo de `reduceSec`
dividido por el de la fila.

Procesador y sistema operativo:

Procesadores disponibles (lo imprime `App`):

| Versión                    |          n |  Tiempo (ms) | Aceleración |
|----------------------------|------------|--------------|-------------|
| reduceSec                  |   10000000 |              |        1.00 |
| reducePar, umbral 10000000 |   10000000 |              |             |
| reducePar, umbral 100000   |   10000000 |              |             |
| reducePar, umbral 1024     |   10000000 |              |             |
| reduceSec                  |       1000 |              |        1.00 |
| reducePar, umbral 1        |       1000 |              |             |

## Lo que se responde con la tabla

1. Con diez millones de elementos, ¿qué umbral dio la mejor aceleración?
   ¿Qué le pasa a la fila del umbral igual a `n`, y por qué?
2. La aceleración que obtuvo con el mejor umbral, comparada con el número de
   procesadores: ¿qué fracción serial `f` tendría que tener el programa para
   que la ley de Amdahl explique esa cifra? Despéjela con
   `fraccionSerialMaxima(procesadores, aceleracion)`.
3. Con mil elementos y umbral 1, ¿la aceleración quedó por debajo de 1?
   Explique con el costo de crear una tarea por cada suma.
