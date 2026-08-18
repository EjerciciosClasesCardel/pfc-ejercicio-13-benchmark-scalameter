# Ejercicio 13 — Medir el paralelismo

Fundamentos de Programación Funcional y Concurrente
Escuela de Ingeniería de Sistemas y Computación, Universidad del Valle
Carlos Andrés Delgado Saavedra

El mismo producto punto de la sesión anterior, ahora repartido en un número
variable de partes, y las cuentas que dicen de antemano cuánto se puede
ganar.

## Las dos formas de estimar el desempeño

**Analizar.** El trabajo es el número total de operaciones: lo que tardaría
en un solo procesador. La profundidad es la cadena más larga de operaciones
que tienen que hacerse una tras otra: lo que tardaría con procesadores
ilimitados. El tiempo con `p` procesadores se estima como

```
profundidad + trabajo / p
```

El primer término es lo que no se puede repartir; el segundo, lo que sí.

**Medir.** El análisis no captura el costo de crear tareas, ni el
comportamiento de la caché, ni lo que hace la máquina virtual mientras
optimiza el código sobre la marcha. Para eso hay que ejecutar, y hacerlo
bien: calentar antes de medir, repetir muchas veces y descartar las corridas
atípicas.

Las dos se complementan. El análisis dice si vale la pena intentarlo; la
medición dice qué se consiguió de verdad.

## Lo que hay que resolver

Todo va en `app/src/main/scala/taller/Ejercicio.scala`.

### El cálculo

```scala
def productoPuntoSegmento(x: Array[Int], y: Array[Int], desde: Int, hasta: Int): Int
def productoPuntoSecuencial(x: Array[Int], y: Array[Int]): Int
def productoPuntoParalelo(x: Array[Int], y: Array[Int], partes: Int): Int
```

`productoPuntoParalelo` recibe en cuántas partes repartir. Con `partes = 1`
resuelve en secuencial; con 2, 4 u 8 divide el rango a la mitad tantas veces
como haga falta y usa `parallel`, que viene en `common`.

Hay una prueba con un arreglo de 1000 elementos repartido en 8 partes. Como
1000 no es múltiplo de 8, es la que descubre una división que pierde
elementos en el último segmento.

### Las cuentas

```scala
def trabajo(n: Int): Int
def profundidad(n: Int): Int
def tiempoEstimado(n: Int, p: Int): Double
```

Para este algoritmo, el trabajo sobre `n` elementos es `n`: hay una
multiplicación por posición, y repartirlas no cambia cuántas son. La
profundidad es el número de niveles del árbol de división, o sea el logaritmo
en base dos de `n`.

| `n` | Trabajo | Profundidad |
|---|---|---|
| 1 | 1 | 0 |
| 2 | 2 | 1 |
| 8 | 8 | 3 |
| 1024 | 1024 | 10 |

`tiempoEstimado(n, p)` aplica la fórmula. Con 1024 elementos:

| Procesadores | Tiempo estimado |
|---|---|
| 1 | 10 + 1024 |
| 4 | 10 + 256 |
| 8 | 10 + 128 |
| un millón | algo por encima de 10 |

La última fila es el punto: por muchos procesadores que haya, el tiempo no
baja de la profundidad. Ese es el techo del paralelismo de este algoritmo, y
se conoce sin ejecutar nada.

## Medir de verdad

`App.scala` es el lugar para tomar los tiempos de la versión secuencial y de
las paralelas y comparar. Para que la medición sirva, ejecute varias veces
antes de empezar a contar y quédese con la mediana en lugar del promedio: la
primera corrida siempre es más lenta porque la máquina virtual todavía no ha
optimizado el código.

Las pruebas no miden tiempos. Un tiempo depende de la máquina y de lo que
esté corriendo al lado, así que una prueba que lo compare fallaría sin que
nada esté mal.

## Cómo está organizado el proyecto

```
app/src/main/scala/common/package.scala   parallel y task
app/src/main/scala/taller/
    App.scala          aquí se toman los tiempos
    Ejercicio.scala    aquí va el ejercicio

app/src/test/scala/taller/
    AppSuite.scala        comprueba que el entorno quedó bien
    EjercicioTest.scala   los casos de arriba
```

Su código va en `main`. Las pruebas viven aparte y no se tocan.

## Cómo se ejecuta

```bash
./gradlew test    # corre las pruebas
```

Las pruebas arrancan en rojo y el trabajo es ponerlas en verde. El informe
completo queda en `app/build/reports/tests/test/index.html`.

## Cómo se trabaja

1. Haga fork de este repositorio.
2. En su fork, abra la pestaña **Actions** y habilítelas. GitHub las deja
   desactivadas en las copias hasta que el dueño lo confirme.
3. Clone, resuelva, haga commit y suba a `main`.
4. Verifique en **Actions** que la última ejecución quedó en verde.

## Restricciones

Este curso trabaja sin estado mutable: nada de `var`, `while`, `return` ni
variables que cambien. El resultado correcto por el camino equivocado no
cuenta como resultado correcto.
