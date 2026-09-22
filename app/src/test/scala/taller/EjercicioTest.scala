package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EjercicioTest extends AnyFunSuite {
  val obj = new Ejercicio()

  val a8 = Array(1, 2, 3, 4, 5, 6, 7, 8)
  val x = Array.tabulate(1000)(i => i % 7 - 3)
  val y = Array.tabulate(1023)(i => (i * 7919) % 201 - 100)

  def cerca(valor: Double, esperado: Double): Boolean =
    math.abs(valor - esperado) < 0.0001

  // Punto 1: sumaSegmento, reduceSec y reducePar

  test("sumaSegmento suma solo entre desde y hasta") {
    assert(obj.sumaSegmento(Array(1, 2, 3, 4, 5), 1, 4) == 9)
    assert(obj.sumaSegmento(a8, 0, 8) == 36)
    assert(obj.sumaSegmento(Array(-5, 4, -3), 0, 3) == -4)
  }

  test("sumaSegmento con un rango vacío devuelve 0 y con uno de un elemento, ese elemento") {
    assert(obj.sumaSegmento(Array(1, 2, 3), 0, 0) == 0)
    assert(obj.sumaSegmento(Array(1, 2, 3), 2, 2) == 0)
    assert(obj.sumaSegmento(Array(), 0, 0) == 0)
    assert(obj.sumaSegmento(Array(1, 2, 3), 2, 3) == 3)
  }

  test("reduceSec da lo mismo con cualquier umbral") {
    assert(obj.reduceSec(a8, 0, 8, 1) == 36)
    assert(obj.reduceSec(a8, 0, 8, 3) == 36)
    assert(obj.reduceSec(a8, 0, 8, 8) == 36)
    assert(obj.reduceSec(Array(), 0, 0, 1) == 0)
  }

  test("reduceSec sobre un segmento que no empieza en cero") {
    assert(obj.reduceSec(a8, 2, 6, 1) == 18)
    assert(obj.reduceSec(a8, 5, 8, 1) == 21)
    assert(obj.reduceSec(x, 100, 900, 16) == -1)
  }

  test("reducePar coincide con la suma en el arreglo de ocho") {
    assert(obj.reducePar(a8, 0, 8, 1) == 36)
    assert(obj.reducePar(a8, 0, 8, 3) == 36)
    assert(obj.reducePar(a8, 0, 8, 8) == 36)
  }

  test("reducePar parte el segmento por su propia mitad, no por la del arreglo") {
    assert(obj.reducePar(a8, 5, 8, 1) == 21)
    assert(obj.reducePar(a8, 3, 8, 1) == 30)
    assert(obj.reducePar(x, 100, 900, 16) == -1)
  }

  test("reducePar con tamaños que no son potencia de dos") {
    assert(obj.reducePar(x, 0, 1000, 1) == -3)
    assert(obj.reducePar(x, 0, 1000, 64) == -3)
    assert(obj.reducePar(x, 0, 1000, 5000) == -3)
    assert(obj.reducePar(y, 0, 1023, 1) == 189)
    assert(obj.reducePar(y, 0, 1023, 100) == 189)
  }

  // Punto 2: trabajo, profundidad y aceleración máxima

  test("trabajoReducePar es una suma por cada nodo interno del árbol") {
    assert(obj.trabajoReducePar(1) == 0)
    assert(obj.trabajoReducePar(2) == 1)
    assert(obj.trabajoReducePar(8) == 7)
    assert(obj.trabajoReducePar(1024) == 1023)
    assert(obj.trabajoReducePar(1000000) == 999999)
  }

  test("trabajoReducePar con n que no es potencia de dos y con n = 0") {
    assert(obj.trabajoReducePar(0) == 0)
    assert(obj.trabajoReducePar(3) == 2)
    assert(obj.trabajoReducePar(5) == 4)
    assert(obj.trabajoReducePar(1000) == 999)
  }

  test("profundidadReducePar es la altura del árbol") {
    assert(obj.profundidadReducePar(1) == 0)
    assert(obj.profundidadReducePar(2) == 1)
    assert(obj.profundidadReducePar(8) == 3)
    assert(obj.profundidadReducePar(1024) == 10)
    assert(obj.profundidadReducePar(1000000) == 20)
  }

  test("profundidadReducePar redondea hacia arriba cuando n no es potencia de dos") {
    assert(obj.profundidadReducePar(0) == 0)
    assert(obj.profundidadReducePar(3) == 2)
    assert(obj.profundidadReducePar(5) == 3)
    assert(obj.profundidadReducePar(1000) == 10)
  }

  test("aceleracionMaxima es trabajo sobre profundidad") {
    assert(cerca(obj.aceleracionMaxima(4), 1.5))
    assert(cerca(obj.aceleracionMaxima(8), 2.3333))
    assert(cerca(obj.aceleracionMaxima(16), 3.75))
    assert(cerca(obj.aceleracionMaxima(32), 6.2))
    assert(cerca(obj.aceleracionMaxima(1024), 102.3))
  }

  test("aceleracionMaxima con un elemento es 1 y con tres también") {
    assert(cerca(obj.aceleracionMaxima(1), 1.0))
    assert(cerca(obj.aceleracionMaxima(3), 1.0))
    assert(cerca(obj.aceleracionMaxima(1000), 99.9))
  }

  // Punto 3: tiempo estimado, Amdahl y decidir

  test("tiempoEstimado con un procesador es profundidad más trabajo") {
    assert(cerca(obj.tiempoEstimado(7, 3, 1), 10.0))
    assert(cerca(obj.tiempoEstimado(1023, 10, 1), 1033.0))
  }

  test("tiempoEstimado reparte el trabajo y deja la profundidad quieta") {
    assert(cerca(obj.tiempoEstimado(7, 3, 4), 4.75))
    assert(cerca(obj.tiempoEstimado(1023, 10, 4), 265.75))
    assert(cerca(obj.tiempoEstimado(1023, 10, 1000000), 10.001023))
    assert(cerca(obj.tiempoEstimado(999999, 20, 4), 250019.75))
    assert(cerca(obj.tiempoEstimado(0, 0, 8), 0.0))
  }

  test("amdahl reproduce la tabla de la sesión") {
    assert(cerca(obj.amdahl(0.05, 2), 1.9048))
    assert(cerca(obj.amdahl(0.10, 4), 3.0769))
    assert(cerca(obj.amdahl(0.20, 8), 3.3333))
    assert(cerca(obj.amdahl(0.40, 32), 2.3881))
    assert(cerca(obj.amdahl(0.25, 16), 3.3684))
  }

  test("amdahl recibe la fracción serial: con f = 1 no se gana nada") {
    assert(cerca(obj.amdahl(1.0, 8), 1.0))
    assert(cerca(obj.amdahl(0.0, 8), 8.0))
    assert(cerca(obj.amdahl(0.5, 1), 1.0))
    assert(cerca(obj.amdahl(0.40, 1000000), 2.5))
  }

  test("amdahlInfinito es el techo con infinitos procesadores") {
    assert(cerca(obj.amdahlInfinito(0.05), 20.0))
    assert(cerca(obj.amdahlInfinito(0.10), 10.0))
    assert(cerca(obj.amdahlInfinito(0.25), 4.0))
    assert(cerca(obj.amdahlInfinito(0.40), 2.5))
    assert(cerca(obj.amdahlInfinito(1.0), 1.0))
  }

  test("amdahl y amdahlInfinito rechazan fracciones fuera de rango") {
    assertThrows[IllegalArgumentException](obj.amdahl(1.5, 4))
    assertThrows[IllegalArgumentException](obj.amdahl(0.2, 0))
    assertThrows[IllegalArgumentException](obj.amdahlInfinito(0.0))
  }

  test("fraccionSerialMaxima es la inversa de amdahl") {
    assert(cerca(obj.fraccionSerialMaxima(16, 10.0), 0.04))
    assert(cerca(obj.fraccionSerialMaxima(4, 2.0), 0.3333))
    assert(cerca(obj.fraccionSerialMaxima(8, 4.0), 0.1429))
    assert(cerca(obj.amdahl(obj.fraccionSerialMaxima(16, 10.0), 16), 10.0))
    assert(cerca(obj.amdahl(obj.fraccionSerialMaxima(8, 4.0), 8), 4.0))
  }

  test("fraccionSerialMaxima en los extremos y fuera de ellos") {
    assert(cerca(obj.fraccionSerialMaxima(16, 16.0), 0.0))
    assert(cerca(obj.fraccionSerialMaxima(16, 1.0), 1.0))
    assertThrows[IllegalArgumentException](obj.fraccionSerialMaxima(16, 17.0))
    assertThrows[IllegalArgumentException](obj.fraccionSerialMaxima(1, 1.0))
  }

  test("valeLaPenaParalelizar con el mínimo de 1.5 y con uno propio") {
    assert(obj.valeLaPenaParalelizar(0.30, 4))
    assert(!obj.valeLaPenaParalelizar(0.60, 4))
    assert(!obj.valeLaPenaParalelizar(0.25, 16, 10.0))
    assert(obj.valeLaPenaParalelizar(0.03, 16, 10.0))
    assert(!obj.valeLaPenaParalelizar(0.0, 1))
  }

  // Punto 4: la estadística de la medición

  test("descartarAtipicos ordena antes de quitar el peor 10 %") {
    val diez = List(5.0, 1.0, 9.0, 2.0, 8.0, 3.0, 7.0, 4.0, 6.0, 100.0)
    assert(obj.descartarAtipicos(diez) == List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0))
    assert(obj.descartarAtipicos(100.0 :: diez.init) == List(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0))
  }

  test("descartarAtipicos con menos de diez no quita nada y con veinte quita dos") {
    assert(obj.descartarAtipicos(List(3.0, 1.0, 2.0)) == List(1.0, 2.0, 3.0))
    assert(obj.descartarAtipicos(Nil) == Nil)
    assert(obj.descartarAtipicos(List.tabulate(20)(i => (20 - i).toDouble)).size == 18)
    assert(obj.descartarAtipicos(List.tabulate(20)(i => (20 - i).toDouble)).max == 18.0)
  }

  test("promedio y desviacion sobre listas dadas") {
    val ts = List(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0)
    assert(cerca(obj.promedio(ts), 5.0))
    assert(cerca(obj.desviacion(ts), 2.0))
    assert(cerca(obj.promedio(List(1.5, 2.5)), 2.0))
    assert(cerca(obj.desviacion(List(3.0)), 0.0))
  }

  test("promedio y desviacion de la lista vacía son 0, y de una lista constante, la media y cero") {
    assert(cerca(obj.promedio(Nil), 0.0))
    assert(cerca(obj.desviacion(Nil), 0.0))
    assert(cerca(obj.promedio(List(4.0, 4.0, 4.0)), 4.0))
    assert(cerca(obj.desviacion(List(4.0, 4.0, 4.0)), 0.0))
  }

  test("resumen descarta el atípico antes de promediar") {
    val (media, sd) = obj.resumen(List(9.0, 10.0, 11.0, 10.0, 9.0, 11.0, 10.0, 9.0, 11.0, 100.0))
    assert(cerca(media, 10.0))
    assert(cerca(sd, 0.8165))
    val (media2, sd2) = obj.resumen(List.fill(9)(4.0) :+ 40.0)
    assert(cerca(media2, 4.0))
    assert(cerca(sd2, 0.0))
  }

  // Un bloque por nombre que lanza una excepción solo la lanza si la función
  // lo evalúa: así se comprueba qué se ejecuta sin mirar el reloj.

  test("medirUna evalúa el bloque que recibe") {
    assertThrows[IllegalStateException](obj.medirUna(throw new IllegalStateException("evaluado")))
    assert(obj.medirUna(obj.sumaSegmento(x, 0, 1000)) >= 0.0)
  }

  test("medirSinAtipicos sin repeticiones ni calentamiento no evalúa el bloque") {
    assert(obj.medirSinAtipicos(0, 0)(throw new IllegalStateException("evaluado")) == ((0.0, 0.0)))
    // Con repeticiones sí mide: un bloque que duerme dos milisegundos deja
    // una media de más de uno. El temporizador puede despertar antes.
    assert(obj.medirSinAtipicos(3, 0)(Thread.sleep(2))._1 >= 1.0)
  }

  test("medirSinAtipicos evalúa el bloque en el calentamiento y en las repeticiones") {
    assertThrows[IllegalStateException](obj.medirSinAtipicos(3, 0)(throw new IllegalStateException("evaluado")))
    assertThrows[IllegalStateException](obj.medirSinAtipicos(0, 1)(throw new IllegalStateException("evaluado")))
    val (media, sd) = obj.medirSinAtipicos(10, 1)(obj.sumaSegmento(x, 0, 1000))
    assert(media >= 0.0)
    assert(sd >= 0.0)
  }
}
