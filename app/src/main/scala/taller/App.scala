package taller

import org.scalameter._

object App {

  val ejercicio = new Ejercicio()

  val procesadores: Int = Runtime.getRuntime.availableProcessors

  // Los valores van de 1 a 100, así la suma de diez millones cabe en un Int.
  def arreglo(n: Int): Array[Int] = Array.tabulate(n)(i => i % 100 + 1)

  // ScalaMeter espera a que la JVM se estabilice y devuelve milisegundos.
  def medir[A](bloque: => A): Double =
    (withWarmer(new Warmer.Default) measure { bloque }).value

  def fila(version: String, n: Int, ms: Double, msSecuencial: Double): String =
    f"| $version%-26s | $n%10d | $ms%12.4f | ${msSecuencial / ms}%11.2f |"

  // Mide reduceSec como referencia y reducePar con cada umbral de la lista.
  def comparar(n: Int, umbrales: List[Int]): Unit = {
    val a = arreglo(n)
    val tSec = medir(ejercicio.reduceSec(a, 0, n, n))
    println(fila("reduceSec", n, tSec, tSec))
    umbrales.foreach { u =>
      val tPar = medir(ejercicio.reducePar(a, 0, n, u))
      println(fila(s"reducePar, umbral $u", n, tPar, tSec))
    }
  }

  def main(args: Array[String]): Unit = {
    println(s"Procesadores disponibles: $procesadores")
    println()
    println("| Versión                    |          n |  Tiempo (ms) | Aceleración |")
    println("|----------------------------|------------|--------------|-------------|")
    comparar(10000000, List(10000000, 100000, 1024))
    comparar(1000, List(1))
  }
}
