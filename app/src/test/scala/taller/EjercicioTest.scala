package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EjercicioTest extends AnyFunSuite {
  val obj = new Ejercicio()

  def esperado(x: Array[Int], y: Array[Int]): Int =
    x.zip(y).map { case (a, b) => a * b }.sum

  val x = Array.tabulate(1024)(i => i % 17 - 8)
  val y = Array.tabulate(1024)(i => i % 23 - 11)

  test("Un segmento cubre solo su rango") {
    assert(obj.productoPuntoSegmento(Array(1, 2, 3, 4), Array(5, 6, 7, 8), 1, 3) == 33)
    assert(obj.productoPuntoSegmento(Array(1, 2, 3), Array(4, 5, 6), 0, 0) == 0)
  }

  test("El producto punto secuencial") {
    assert(obj.productoPuntoSecuencial(Array(1, 2, 3), Array(4, 5, 6)) == 32)
    assert(obj.productoPuntoSecuencial(x, y) == esperado(x, y))
    assert(obj.productoPuntoSecuencial(Array(), Array()) == 0)
  }

  test("El paralelo coincide con el valor esperado en una, dos, cuatro y ocho partes") {
    assert(obj.productoPuntoParalelo(x, y, 1) == esperado(x, y))
    assert(obj.productoPuntoParalelo(x, y, 2) == esperado(x, y))
    assert(obj.productoPuntoParalelo(x, y, 4) == esperado(x, y))
    assert(obj.productoPuntoParalelo(x, y, 8) == esperado(x, y))
  }

  test("El paralelo con un tamaño que no es múltiplo del número de partes") {
    val a = Array.tabulate(1000)(i => i % 7 - 3)
    val b = Array.tabulate(1000)(i => i % 5 - 2)
    assert(obj.productoPuntoParalelo(a, b, 8) == esperado(a, b))
  }

  test("Trabajo: crece con el número de elementos") {
    assert(obj.trabajo(1) == 1)
    assert(obj.trabajo(8) == 8)
    assert(obj.trabajo(1024) == 1024)
  }

  test("Profundidad: crece con el logaritmo") {
    assert(obj.profundidad(1) == 0)
    assert(obj.profundidad(2) == 1)
    assert(obj.profundidad(8) == 3)
    assert(obj.profundidad(1024) == 10)
  }

  test("Tiempo estimado con un procesador es el trabajo más la profundidad") {
    assert(math.abs(obj.tiempoEstimado(8, 1) - 11.0) < 0.0001)
  }

  test("Tiempo estimado con varios procesadores") {
    assert(math.abs(obj.tiempoEstimado(1024, 4) - (10 + 256.0)) < 0.0001)
    assert(math.abs(obj.tiempoEstimado(1024, 8) - (10 + 128.0)) < 0.0001)
  }

  test("Con muchísimos procesadores el tiempo tiende a la profundidad") {
    assert(obj.tiempoEstimado(1024, 1000000) < 11.0)
    assert(obj.tiempoEstimado(1024, 1000000) >= 10.0)
  }
}
