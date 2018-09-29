import Numeric.Implicits._

object Statistiques {
  def moyenne[T: Numeric](xs: Iterable[T]): Double = xs.sum.toDouble / xs.size

  def variance[T: Numeric](xs: Iterable[T]): Double = {
    val avg = moyenne(xs)

    xs.map(_.toDouble).map(a => math.pow(a - avg, 2)).sum / xs.size
  }

  def ecartType[T: Numeric](xs: Iterable[T]): Double = math.sqrt(variance(xs))

}
