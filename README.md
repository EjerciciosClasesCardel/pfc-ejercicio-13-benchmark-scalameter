# Ejercicio 13 — Analizar y medir el paralelismo

Fundamentos de Programación Funcional y Concurrente
Escuela de Ingeniería de Sistemas y Computación, Universidad del Valle
Carlos Andrés Delgado Saavedra

Cuatro puntos sobre una misma pregunta: cuánto se gana al repartir un cómputo
entre varios procesadores. Primero se escribe el programa que se va a
estudiar, un `reduce` por divide y vencerás en secuencial y en paralelo.
Después se responde la pregunta de dos maneras que se complementan: con lápiz,
contando trabajo y profundidad y aplicando la ley de Amdahl, y con reloj,
midiendo en la máquina virtual como se debe, con calentamiento, repeticiones
y descarte de atípicos.

## Analizar y medir

**Analizar.** El trabajo `W` es el número total de operaciones: lo que
tardaría el programa en un solo procesador. La profundidad `D` es la cadena
más larga de operaciones que van una tras otra: lo que tardaría con
procesadores ilimitados. Para una expresión secuencial las dos se suman; en
`parallel(e1, e2)` el trabajo se suma y la profundidad pasa a ser el máximo
de las dos. Ahí nace la ganancia.

Sumar ocho enteros uno tras otro son siete sumas encadenadas: `W = 7` y
`D = 7`, y con mil procesadores sigue tardando siete pasos. En árbol son las
mismas siete sumas en tres niveles: `W = 7` y `D = 3`. El programa que hace
eso para un arreglo de tamaño `n` es este:

```scala
def reducePar(a: Array[Int], i: Int, j: Int): Int =
  if (j - i <= 1) a(i)
  else {
    val m = i + (j - i) / 2
    val (r1, r2) = parallel(reducePar(a, i, m), reducePar(a, m, j))
    r1 + r2
  }
```

Con `p` procesadores el tiempo se estima como la parte que no se puede
repartir más la que sí:

```
T(p) ≈ D + W / p
```

La ley de Amdahl mira lo mismo desde otro lado. Si una fracción `f` del
programa es serial y el resto se reparte entre `p` procesadores, la
aceleración, el cociente entre el tiempo con uno y el tiempo con `p`, no pasa
de

```
S(p) = 1 / (f + (1 - f) / p)          S(∞) = 1 / f
```

Con `f = 0.40` el techo es 2.5 por más procesadores que se compren.

**Medir.** El análisis no ve el costo de crear una tarea, ni la caché, ni lo
que hace la máquina virtual mientras compila el código sobre la marcha. Para
eso hay que ejecutar, y una sola corrida no dice nada: la primera es varias
veces más lenta que las siguientes porque el código todavía corre
interpretado. La rutina es calentar, repetir, ordenar los tiempos, descartar
el peor 10 % y quedarse con el promedio y la desviación de lo que queda.
ScalaMeter hace el calentamiento por su cuenta: `withWarmer(new
Warmer.Default) measure { bloque }` ejecuta el bloque hasta que la varianza
entre tiempos consecutivos se estabiliza y solo entonces mide.

## Lo que hay que resolver

Todo va en `app/src/main/scala/taller/Ejercicio.scala`. Las tablas usan
estas definiciones de las pruebas:

```scala
val a8 = Array(1, 2, 3, 4, 5, 6, 7, 8)
val x = Array.tabulate(1000)(i => i % 7 - 3)
val y = Array.tabulate(1023)(i => (i * 7919) % 201 - 100)
```

### Punto 1: el programa que se va a medir

```scala
def sumaSegmento(a: Array[Int], desde: Int, hasta: Int): Int
def reduceSec(a: Array[Int], desde: Int, hasta: Int, umbral: Int): Int
def reducePar(a: Array[Int], desde: Int, hasta: Int, umbral: Int): Int
```

`sumaSegmento` suma los elementos entre `desde`, incluida, y `hasta`,
excluida; con un rango vacío da 0. `reduceSec` es el divide y vencerás sin
paralelismo: cuando el segmento tiene a lo sumo `umbral` elementos lo suma
directo con `sumaSegmento`, y si no lo parte por la mitad y suma las dos
partes. `reducePar` es el mismo programa con las dos mitades dentro de
`parallel`, que viene en `common`. El umbral es lo que en clase se llamó
`limite`: bajar hasta segmentos de un elemento crea una tarea por cada suma,
y eso cuesta más que la suma. `umbral` es al menos 1.

| Llamada | Resultado |
|---|---|
| `sumaSegmento(Array(1, 2, 3, 4, 5), 1, 4)` | 9 |
| `sumaSegmento(a8, 0, 8)` | 36 |
| `sumaSegmento(Array(-5, 4, -3), 0, 3)` | -4 |
| `sumaSegmento(Array(1, 2, 3), 2, 2)` | 0 |
| `reduceSec(a8, 0, 8, 1)` | 36 |
| `reduceSec(a8, 2, 6, 1)` | 18 |
| `reduceSec(a8, 5, 8, 1)` | 21 |
| `reducePar(a8, 0, 8, 3)` | 36 |
| `reducePar(a8, 5, 8, 1)` | 21 |
| `reducePar(a8, 3, 8, 1)` | 30 |
| `reducePar(x, 0, 1000, 64)` | -3 |
| `reducePar(x, 100, 900, 16)` | -1 |
| `reducePar(y, 0, 1023, 1)` | 189 |

Las filas que empiezan en 5 y en 3 descubren una mitad mal calculada: la
mitad es la del segmento, `desde + (hasta - desde) / 2`, no la del arreglo.
Los arreglos `x` e `y` tienen 1000 y 1023 elementos, que no son potencias de
dos, así que las dos partes de una división no siempre miden lo mismo.

### Punto 2: trabajo y profundidad

```scala
def trabajoReducePar(n: Int): Int
def profundidadReducePar(n: Int): Int
def aceleracionMaxima(n: Int): Double
```

Son las cuentas del `reducePar` de arriba, el que baja hasta un elemento,
sobre un arreglo de tamaño `n`. El trabajo cuenta las sumas: cada nodo
interno del árbol hace una, y con un elemento no hay ninguna. La profundidad
es la altura del árbol, porque las sumas de un mismo nivel van a la vez y las
de niveles distintos van una tras otra. Las dos se escriben como
recurrencias sobre las dos mitades, `n / 2` y `n - n / 2`, con `n <= 1`
como caso base; el trabajo suma las dos y agrega uno, la profundidad toma la
mayor y agrega uno. `aceleracionMaxima` es trabajo sobre profundidad: la
mayor aceleración posible, la que se tendría con procesadores ilimitados.
Con un elemento o menos no hay sumas que repartir y vale 1.

| Llamada | Resultado |
|---|---|
| `trabajoReducePar(1)` | 0 |
| `trabajoReducePar(8)` | 7 |
| `trabajoReducePar(1000)` | 999 |
| `trabajoReducePar(1024)` | 1023 |
| `trabajoReducePar(1000000)` | 999999 |
| `profundidadReducePar(1)` | 0 |
| `profundidadReducePar(5)` | 3 |
| `profundidadReducePar(8)` | 3 |
| `profundidadReducePar(1000)` | 10 |
| `profundidadReducePar(1024)` | 10 |
| `profundidadReducePar(1000000)` | 20 |
| `aceleracionMaxima(1)` | 1.0 |
| `aceleracionMaxima(3)` | 1.0 |
| `aceleracionMaxima(8)` | 2.3333 |
| `aceleracionMaxima(32)` | 6.2 |
| `aceleracionMaxima(1024)` | 102.3 |

Con `n` potencia de dos el trabajo es `n - 1` y la profundidad `log₂ n`.
Cuando no lo es, la fila de 1000 es la que hay que mirar: la profundidad es
10, la misma que la de 1024, porque la rama más larga del árbol es la de las
mitades grandes y `log₂ 1000` redondea hacia arriba, no hacia abajo. Y con
tres elementos la aceleración es 1: una de las dos sumas tiene que esperar a
la otra de todas maneras.

### Punto 3: tiempo estimado y ley de Amdahl

```scala
def tiempoEstimado(trabajo: Int, profundidad: Int, p: Int): Double
def amdahl(f: Double, p: Int): Double
def amdahlInfinito(f: Double): Double
def fraccionSerialMaxima(p: Int, aceleracion: Double): Double
def valeLaPenaParalelizar(f: Double, p: Int, aceleracionMinima: Double = 1.5): Boolean
```

`tiempoEstimado` aplica `D + W / p` a un trabajo y una profundidad
cualesquiera, así sirve para el `reducePar` del punto 2 y para cualquier otro
programa del que se conozcan las dos cuentas.

`amdahl(f, p)` recibe la fracción **serial** `f`, la que no se puede
repartir, y devuelve la aceleración con `p` procesadores; `amdahlInfinito(f)`
es el techo `1 / f`. En el ejercicio de condiciones de carrera, el de los
principios de concurrencia, la función del mismo nombre recibía la fracción
**paralela**: por eso allí `amdahl(1.0, 8)` daba 8, todo el programa se
repartía, y aquí da 1, todo el programa es serial. Es la misma ley con la
fracción nombrada al revés, y la que se usa en esta sesión es la serial.
`amdahl` exige `f` entre 0 y 1 y `p` al menos 1; `amdahlInfinito`, `f` mayor
que 0. Fuera de eso lanzan `IllegalArgumentException` con `require`.

`fraccionSerialMaxima(p, aceleracion)` despeja `f` de la ley: es la mayor
fracción serial con la que `p` procesadores todavía alcanzan esa aceleración.
Exige `p` al menos 2 y una aceleración entre 1 y `p`; pedir más que `p` no
tiene solución, ni con `f = 0`. `valeLaPenaParalelizar` responde sí o no:
verdadero cuando `amdahl(f, p)` llega al mínimo pedido, que si no se indica
es 1.5.

| Llamada | Resultado |
|---|---|
| `tiempoEstimado(7, 3, 1)` | 10.0 |
| `tiempoEstimado(7, 3, 4)` | 4.75 |
| `tiempoEstimado(1023, 10, 4)` | 265.75 |
| `tiempoEstimado(1023, 10, 1000000)` | 10.001023 |
| `tiempoEstimado(999999, 20, 4)` | 250019.75 |
| `amdahl(0.05, 2)` | 1.9048 |
| `amdahl(0.10, 4)` | 3.0769 |
| `amdahl(0.20, 8)` | 3.3333 |
| `amdahl(0.40, 32)` | 2.3881 |
| `amdahl(0.25, 16)` | 3.3684 |
| `amdahl(1.0, 8)` | 1.0 |
| `amdahl(0.0, 8)` | 8.0 |
| `amdahl(0.5, 1)` | 1.0 |
| `amdahlInfinito(0.25)` | 4.0 |
| `amdahlInfinito(0.40)` | 2.5 |
| `fraccionSerialMaxima(16, 10.0)` | 0.04 |
| `fraccionSerialMaxima(4, 2.0)` | 0.3333 |
| `fraccionSerialMaxima(16, 16.0)` | 0.0 |
| `fraccionSerialMaxima(16, 1.0)` | 1.0 |
| `valeLaPenaParalelizar(0.30, 4)` | true |
| `valeLaPenaParalelizar(0.60, 4)` | false |
| `valeLaPenaParalelizar(0.25, 16, 10.0)` | false |
| `valeLaPenaParalelizar(0.03, 16, 10.0)` | true |
| `valeLaPenaParalelizar(0.0, 1)` | false |

La fila de `999999, 20, 4` es el `reducePar` sobre un millón de elementos con
cuatro procesadores: la profundidad aporta 20 y el trabajo repartido
249999.75, así que con `p` fijo y `n` grande domina `W / p` y el tiempo
crece como `n`. La de `1023, 10, 1000000` es el otro extremo: por muchos
procesadores que haya, el tiempo no baja de la profundidad.

Las filas de 16 procesadores cuentan una historia. Un equipo promete ir diez
veces más rápido con 16 núcleos, y midió que el 25 % del tiempo se va en una
etapa que no se reparte. Con `f = 0.25` la aceleración es 3.37 y el techo con
infinitos procesadores es 4: la promesa no se cumple ni comprando máquinas.
Para llegar a 10 con 16 núcleos la fracción serial tendría que bajar al 4 %.
Reescribir la etapa serial rinde más que agregar núcleos.

### Punto 4: medir bien

```scala
def descartarAtipicos(tiempos: List[Double]): List[Double]
def promedio(tiempos: List[Double]): Double
def desviacion(tiempos: List[Double]): Double
def resumen(tiempos: List[Double]): (Double, Double)
def medirUna[A](bloque: => A): Double
def medirSinAtipicos[A](repeticiones: Int, calentamiento: Int)(bloque: => A): (Double, Double)
```

La estadística va separada del reloj para que se pueda probar con listas de
tiempos dadas. `descartarAtipicos` ordena los tiempos y quita los `size / 10`
mayores, que con menos de diez mediciones es ninguno. `promedio` y
`desviacion` son las de siempre, la desviación dividiendo por el tamaño de la
lista, y las dos valen 0 con la lista vacía. `resumen` junta las tres: el
promedio y la desviación de lo que queda tras descartar.

`medirUna` evalúa el bloque una vez y devuelve los milisegundos que tardó,
con `System.nanoTime`. `medirSinAtipicos` evalúa el bloque `calentamiento`
veces sin medir, lo mide `repeticiones` veces acumulando los tiempos en una
lista y devuelve el `resumen` de esa lista. El bloque se recibe por nombre:
si se recibiera por valor se evaluaría una sola vez antes de entrar y no
quedaría nada que medir.

| Llamada | Resultado |
|---|---|
| `descartarAtipicos(List(5.0, 1.0, 9.0, 2.0, 8.0, 3.0, 7.0, 4.0, 6.0, 100.0))` | `List(1.0, 2.0, …, 9.0)` |
| `descartarAtipicos(List(3.0, 1.0, 2.0))` | `List(1.0, 2.0, 3.0)` |
| `descartarAtipicos(Nil)` | `Nil` |
| `descartarAtipicos(List.tabulate(20)(i => (20 - i).toDouble)).size` | 18 |
| `promedio(List(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0))` | 5.0 |
| `desviacion(List(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0))` | 2.0 |
| `promedio(Nil)` | 0.0 |
| `desviacion(List(3.0))` | 0.0 |
| `resumen(List(9.0, 10.0, 11.0, 10.0, 9.0, 11.0, 10.0, 9.0, 11.0, 100.0))` | `(10.0, 0.8165)` |
| `resumen(List.fill(9)(4.0) :+ 40.0)` | `(4.0, 0.0)` |
| `medirUna(throw new IllegalStateException("evaluado"))` | lanza `IllegalStateException` |
| `medirSinAtipicos(0, 0)(throw new IllegalStateException("evaluado"))` | `(0.0, 0.0)` |
| `medirSinAtipicos(3, 0)(throw new IllegalStateException("evaluado"))` | lanza `IllegalStateException` |
| `medirSinAtipicos(0, 1)(throw new IllegalStateException("evaluado"))` | lanza `IllegalStateException` |

En la primera fila el 100 está al final y en la prueba también aparece al
comienzo de la lista: las dos dan lo mismo, porque se descarta el mayor, no
el último. La fila de `resumen` con el 100 es el caso que justifica todo el
punto: con él dentro el promedio sería 19; sin él, 10.

Las cuatro últimas filas no miran el reloj. Un tiempo depende de la máquina y
de lo que corra al lado, así que una prueba que lo compare con un número
falla unos días y pasa otros. En su lugar el bloque lanza una excepción, y la
excepción solo sale si la función evaluó el bloque: `medirUna` la deja pasar;
`medirSinAtipicos` sin repeticiones ni calentamiento no llega a tocarlo y
devuelve el resumen de la lista vacía; con tres repeticiones, o con un solo
calentamiento, la excepción sale. Es el mismo hecho del párrafo anterior visto
desde las pruebas: un bloque por nombre no se evalúa hasta que alguien lo usa.
Con un bloque que sí termina, las pruebas piden únicamente que el tiempo, el
promedio y la desviación no sean negativos.

## Medir de verdad

`App.scala` ya está escrito y usa ScalaMeter: construye un arreglo con
valores de 1 a 100, mide `reduceSec` como referencia y `reducePar` con varios
umbrales, con `withWarmer(new Warmer.Default) measure { … }`, e imprime una
tabla con el tiempo y la aceleración de cada fila. Corre con los cuatro
puntos resueltos:

```bash
./gradlew run
```

Copie las filas en la tabla de `docs/medicion.md` y responda las preguntas
que hay debajo. Dos cosas para mirar: con diez millones de elementos la
aceleración queda lejos del número de procesadores, y la ley de Amdahl dice
qué fracción serial explica esa distancia; con mil elementos y umbral 1, el
paralelo es más lento que el secuencial, porque crear una tarea por cada suma
cuesta más que sumar. Antes de paralelizar, medir el secuencial.

Las pruebas no comparan tiempos entre versiones: un tiempo depende de la
máquina y de lo que esté corriendo al lado, y el lugar para anotarlo es la
tabla de `docs/medicion.md`.

## Cómo está organizado el proyecto

```
app/src/main/scala/common/package.scala   parallel y task
app/src/main/scala/taller/
    App.scala          la medición con ScalaMeter, ya escrita
    Ejercicio.scala    aquí van los cuatro puntos

app/src/test/scala/taller/
    AppSuite.scala        comprueba que el entorno quedó bien
    EjercicioTest.scala   los casos de las cuatro tablas

docs/medicion.md          la tabla que se llena con los tiempos de App
```

Su código va en `main`. Las pruebas viven aparte y no se tocan.

## Cómo se ejecuta

```bash
./gradlew test    # corre las pruebas
./gradlew run     # toma los tiempos con ScalaMeter
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
