package taller

import common._

class Ejercicio {

  // Tal como está todo devuelve 0 y las pruebas quedan en rojo.

  // Punto 1. El programa que se va a medir.

  // Suma de los elementos entre desde (incluida) y hasta (excluida).
  def sumaSegmento(a: Array[Int], desde: Int, hasta: Int): Int = {
    0 // Completar
  }

  // Suma del segmento por divide y vencerás, sin paralelismo: cuando el
  // segmento tiene a lo sumo umbral elementos se suma directo; si no, se
  // parte por la mitad y se suman las dos partes.
  def reduceSec(a: Array[Int], desde: Int, hasta: Int, umbral: Int): Int = {
    0 // Completar
  }

  // Lo mismo, con las dos mitades evaluadas a la vez mediante parallel.
  def reducePar(a: Array[Int], desde: Int, hasta: Int, umbral: Int): Int = {
    0 // Completar
  }

  // Punto 2. Trabajo y profundidad del reduce que baja hasta un elemento.

  // Número de sumas que hace el reduce sobre n elementos.
  def trabajoReducePar(n: Int): Int = {
    0 // Completar
  }

  // Número de niveles de sumas que van una tras otra: la altura del árbol.
  def profundidadReducePar(n: Int): Int = {
    0 // Completar
  }

  // Trabajo sobre profundidad; con un elemento o menos es 1.
  def aceleracionMaxima(n: Int): Double = {
    0.0 // Completar
  }

  // Punto 3. Tiempo con p procesadores y ley de Amdahl.

  // La profundidad más el trabajo repartido entre p procesadores.
  def tiempoEstimado(trabajo: Int, profundidad: Int, p: Int): Double = {
    0.0 // Completar
  }

  // Aceleración con p procesadores cuando la fracción f del programa es
  // serial. f va de 0 a 1 y p es al menos 1.
  def amdahl(f: Double, p: Int): Double = {
    0.0 // Completar
  }

  // El techo de la aceleración con infinitos procesadores. f va de 0
  // (excluido) a 1.
  def amdahlInfinito(f: Double): Double = {
    0.0 // Completar
  }

  // La mayor fracción serial con la que p procesadores todavía alcanzan la
  // aceleración pedida. p es al menos 2 y la aceleración va de 1 a p.
  def fraccionSerialMaxima(p: Int, aceleracion: Double): Double = {
    0.0 // Completar
  }

  // Verdadero cuando la aceleración de Amdahl llega al mínimo pedido.
  def valeLaPenaParalelizar(f: Double, p: Int, aceleracionMinima: Double = 1.5): Boolean = {
    false // Completar
  }

  // Punto 4. Medir bien: la estadística aparte del reloj.

  // Quita el peor 10 % de los tiempos, es decir, size / 10 de los mayores.
  def descartarAtipicos(tiempos: List[Double]): List[Double] = {
    List() // Completar
  }

  // Promedio de la lista; 0 si está vacía.
  def promedio(tiempos: List[Double]): Double = {
    0.0 // Completar
  }

  // Desviación estándar de la lista, dividiendo por el tamaño; 0 si está
  // vacía.
  def desviacion(tiempos: List[Double]): Double = {
    0.0 // Completar
  }

  // Promedio y desviación de los tiempos que quedan tras descartar los
  // atípicos.
  def resumen(tiempos: List[Double]): (Double, Double) = {
    (0.0, 0.0) // Completar
  }

  // Milisegundos que tarda una evaluación del bloque.
  def medirUna[A](bloque: => A): Double = {
    0.0 // Completar
  }

  // Evalúa el bloque calentamiento veces sin medir, después lo mide
  // repeticiones veces y devuelve el resumen de esos tiempos.
  def medirSinAtipicos[A](repeticiones: Int, calentamiento: Int)(bloque: => A): (Double, Double) = {
    (0.0, 0.0) // Completar
  }
}
