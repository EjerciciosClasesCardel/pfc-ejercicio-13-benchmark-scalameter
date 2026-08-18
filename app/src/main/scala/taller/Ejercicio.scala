package taller

import common._

class Ejercicio {

  // Tal como está todo devuelve 0 y las pruebas quedan en rojo.

  /** Suma de los productos entre desde (incluida) y hasta (excluida). */
  def productoPuntoSegmento(x: Array[Int], y: Array[Int],
                            desde: Int, hasta: Int): Int = {
    0 // Completar
  }

  /** Producto punto secuencial: la referencia contra la que se compara. */
  def productoPuntoSecuencial(x: Array[Int], y: Array[Int]): Int = {
    0 // Completar
  }

  /** Producto punto repartido en `partes` segmentos con `parallel`.
    *
    * Con partes = 1 se resuelve en secuencial; con 2, 4 u 8 se reparte
    * dividiendo el rango por la mitad tantas veces como haga falta.
    */
  def productoPuntoParalelo(x: Array[Int], y: Array[Int], partes: Int): Int = {
    0 // Completar
  }

  /** El trabajo de un algoritmo de dividir y vencer sobre n elementos: el
    * número total de operaciones, que no baja por repartirlo.
    */
  def trabajo(n: Int): Int = {
    0 // Completar
  }

  /** La profundidad del mismo algoritmo: la cadena más larga de operaciones
    * que tienen que hacerse una tras otra. Con n elementos y divisiones a la
    * mitad, es el número de niveles del árbol de división.
    */
  def profundidad(n: Int): Int = {
    0 // Completar
  }

  /** El tiempo estimado con p procesadores: la profundidad más el trabajo
    * repartido entre ellos.
    */
  def tiempoEstimado(n: Int, p: Int): Double = {
    0.0 // Completar
  }
}
