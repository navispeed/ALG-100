import scala.annotation.tailrec

object Main {

  /**
    * Taille maximale d'un quartier, ne devrait pas être supérieur à 9
    */
  val TAILLE_QUARTIER_MAXIMUM = 8

  /**
    * Distance entre deux point
    *
    * @param point1
    * @param point2
    * @note complexité en O(1)
    * @return Distance
    */
  def distance(point1: Double, point2: Double): Double = {
    val op = if (point1 < point2) (point1, point2) else (point2, point1)
    val res = Math.abs(op._2 - op._1)
    res
  }

  //(Length currentItem) resultList
  type PathLengthResult = ((Double, Double), List[Double])
  type Permutation = Seq[Double]

  /**
    * Calcul le temps total d'attente
    *
    * @param permutation Permutation à tester
    * @note complexité en \theta (n)
    * @return le temps total attendu en tout
    */
  def tempsTotalDattente(permutation: Seq[Double]): PathLengthResult = (permutation.foldLeft((0d, 0d))((acc, current) => (acc._1 + distance(acc._2, current), current)), permutation.toList)

  /**
    * Trouver la meilleure permutation possible pour minimiser l'attente totale
    *
    * @param permutations La liste de permutation à tester
    * @note complexité en O(n!)
    * @return un tuple de (temps total d'attente, permutation associée)
    */
  def bruteforce(permutations: Iterator[Seq[Double]]): (Double, List[Double]) = {
    val allPermutations = permutations
    val tuples: (Double, List[Double]) = allPermutations.foldLeft((Double.MaxValue, List[Double]()))((best, current) => {
      val result: PathLengthResult = tempsTotalDattente(current)
      if (best._1 > result._1._1) //Avons nous trouvé une permutation plus intéressante ?
        (result._1._1, result._2) //On remplace, on a (temps nécessaire, permutation)
      else
        best
    })
    tuples
  }

  /**
    * Divise la liste en sous liste
    * @param listeEntree Liste à diviser
    * @param objectifTaille taille maximale des sous listes
    * @return
    */
  def divise(listeEntree: Iterable[Double], objectifTaille: Int): List[List[Double]] = {
    listeEntree.foldLeft((0, 0), List[List[Double]]())((acc: ((Int, Int), List[List[Double]]), current) => {
      val indexList: Int = acc._1._1
      val list = current :: (if (acc._1._2 == 0) List() else acc._2(indexList))

      (if (acc._1._2 != 0 && acc._1._2 % (objectifTaille - 1) == 0) (acc._1._1 + 1, 0) else (acc._1._1, acc._1._2 + 1), acc._2.patch(acc._1._1, Seq(list), 1))
    })._2
  }

  /**
    * Naif, trouve le noeud le plus proche
    * @param toDo liste d'élément à explorer
    * @param cheminFinal chemin final
    * @return concaténation du meilleur chemin trouvé + le meilleur élément à visiter ensuite
    */
  @tailrec
  def exploreViaNoeudLePlusProche(toDo: List[List[Double]], cheminFinal: (List[Double], Double) = (List(0d), 0)): (List[Double], Double) = {
    val tête = toDo.head

    val teteOrdonne = bruteforce((cheminFinal._2 +: tête).permutations.filter(l => l.head == cheminFinal._2))._2.tail

    def trouveLeMeilleur(meilleurTrouvé: List[Double], actuel: List[Double]): List[Double] = {
      val distanceAvecMeilleur = distance(Statistiques.moyenne(meilleurTrouvé), Statistiques.moyenne(tête))
      val distanceAvecLActuel = distance(Statistiques.moyenne(meilleurTrouvé), Statistiques.moyenne(actuel))
      if (distanceAvecLActuel < distanceAvecMeilleur && !actuel.equals(teteOrdonne) && !actuel.equals(tête)) {
        actuel
      } else {
        meilleurTrouvé
      }
    }

    val meilleurPourLaSuite: List[Double] = toDo.tail.foldLeft(toDo.tail.headOption.getOrElse(List()))(trouveLeMeilleur); //Prochaine liste à explorer
    val reste: List[List[Double]] = toDo.tail.filter(l => !l.equals(meilleurPourLaSuite)); //On exclue la meilleure des chemins à exoplorer

    val list: List[Double] = cheminFinal._1 ++ teteOrdonne
    //    if (meilleurPourLaSuite.equals(List(0d)))
    if (reste.isEmpty)
      (list ++ meilleurPourLaSuite, teteOrdonne.reverse.head)
    else {
      exploreViaNoeudLePlusProche(meilleurPourLaSuite :: reste, (list, teteOrdonne.reverse.head))
    }
  }

  /**
    * Cherche à atteindre le noeud avec l'écart type le plus faible MAIS priorise les noeuds sur le chemin entre actuel et meilleurEcartType
    * Prerquis: àExplorer contient au moins 2 éléments, trié par écart type ascendant
    * @param àExplorer Liste de noeud à visiter, le premier noeud de la liste sera obligatoirement le prochain visité
    * @param cheminFinal le chemin final
    * @return concaténation du meilleur chemin trouvé + le meilleur élément à visiter ensuite
    */

  @tailrec
  def exploreViaNoeudEcartType(àExplorer: List[List[Double]], cheminFinal: (List[Double], Double) = (List(0d), 0)): (List[Double], Double) = {
    val tête = àExplorer.head
    val objectif = àExplorer.tail.head //Car 2nd meilleur écart type
    val ecartTupeobjectif = àExplorer.tail.head

    val teteOrdonne = bruteforce((cheminFinal._2 +: tête).permutations.filter(l => l.head == cheminFinal._2))._2.tail
    val minimumMaximum: (Double, Double) = if (Statistiques.moyenne(tête) > Statistiques.moyenne(objectif)) (Statistiques.moyenne(objectif), Statistiques.moyenne(tête)) else (Statistiques.moyenne(tête), Statistiques.moyenne(objectif))

    def trouveLeMeilleur(meilleurTrouvé: List[Double], actuel: List[Double]): List[Double] = {
      val distanceAvecMeilleur = distance(Statistiques.moyenne(meilleurTrouvé), Statistiques.moyenne(tête))
      val moyenneActuel = Statistiques.moyenne(actuel)
      val distanceAvecLActuel = distance(Statistiques.moyenne(meilleurTrouvé), moyenneActuel)

      if (distanceAvecLActuel < distanceAvecMeilleur && !actuel.equals(teteOrdonne) && !actuel.equals(tête)) {
        actuel
      } else {
        meilleurTrouvé
      }
    }

    val meilleurPourLaSuite: List[Double] = àExplorer.tail.filter(l => Statistiques.moyenne(l) > minimumMaximum._1 && Statistiques.moyenne(l) < minimumMaximum._2).foldLeft(àExplorer.tail.headOption.getOrElse(List()))(trouveLeMeilleur); //Prochaine liste à explorer
    val reste: List[List[Double]] = àExplorer.tail.filter(l => !l.equals(meilleurPourLaSuite)); //On exclue la meilleure des chemins à exoplorer

    val list: List[Double] = cheminFinal._1 ++ teteOrdonne
    if (reste.size < 2)
      exploreViaNoeudLePlusProche(meilleurPourLaSuite :: reste, (list, teteOrdonne.reverse.head))
    else {
      exploreViaNoeudEcartType(meilleurPourLaSuite :: reste, (list, teteOrdonne.reverse.head))
    }
  }


  /**
    * Reçois une liste de sous liste de "Quartiers"
    * @param divisée liste de sous liste
    * @return Le chemin final
    */
  def compute(divisée: List[List[Double]]): List[Double] = {
    val listeDivisée: Seq[(Double, Double, List[Double])] = divisée.map(l => (Statistiques.ecartType(l), Statistiques.moyenne(l), l)).sortBy(i => i._1).toStream

    val seq: List[List[Double]] = listeDivisée.map(l => l._3).toList
    val cheminFinal = if (divisée.size == 1)
      exploreViaNoeudLePlusProche(seq)
    else
      exploreViaNoeudEcartType(seq)
    cheminFinal._1
  }

  /**
    * Point d'entrée de l'application, les maisons sont récupérés depuis la liste d'argument
    * @param args
    */
  def main(args: Array[String]): Unit = {
    println(s"Nombre de maison ${args.length}")
    val input: Seq[Double] = args.map(s => s.toDouble).toSeq.sorted
    val list = divise(input, TAILLE_QUARTIER_MAXIMUM)
    val cheminFinal = compute(list)

    println("Chemin final")
    println(cheminFinal.mkString(","))
  }
}