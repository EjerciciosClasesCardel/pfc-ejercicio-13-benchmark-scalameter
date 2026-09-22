package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AppSuite extends AnyFunSuite {
  test("El arreglo que se mide va de 1 a 100 y vuelve a empezar") {
    assert(App.arreglo(5).toList == List(1, 2, 3, 4, 5))
    assert(App.arreglo(200).sum == 10100)
    assert(App.procesadores >= 1)
  }

  test("Cada fila de la tabla lleva la versión y el tamaño") {
    val fila = App.fila("reduceSec", 1000, 2.0, 2.0)
    assert(fila.contains("reduceSec"))
    assert(fila.contains("1000"))
  }
}
