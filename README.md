# Algorithme du chemin le plus efficace d’un chasse neige à Montréal	

## Objectif :

À partir d&#39;une liste de décimaux, représentant l&#39;emplacement de maison dans la ville de Montréal, trouver une approximation du chemin le plus efficace pour un chasse neige, afin de limiter le temps d&#39;attente moyen pour chaque maison.

Définition du temps d&#39;attente : avec la liste suivante : 0,2,3,-1, le temps d&#39;attente moyen serait de (2+1+6)/3=3, alors que dans avec celle-ci : 0,-1,2,3, il serait de (1+3+1)/3≈2.

## Algorithmes envisagés :

| Nom | Bruteforce |
| --- | --- |
| Complexité | O(n!) |
| Avantage | Trouve le meilleur chemin |
| Inconvénient | Complexité plus qu&#39;exponentielle, ne marche pas sur des instances de plus de 10 éléments. |

Le problème ressemblant assez au problème du voyageur de commerce, il aurait été tentant d&#39;utiliser des méthodes permettant d&#39;approximer une solution pour ce problème.

Or, on peut trouver de bonnes approximations de solutions au problème du voyageur de commerce avec un arbre de poids minimum puis le parcours en pré-ordre de l&#39;arbre obtenu. Mais on se rend alors compte que ce problème n&#39;est pas compatible avec le nôtre.

- Une solution pour le problème du voyageur de commerce permettrait d&#39;avoir le chemin le plus rapide pour explorer tous les points, mais sans prendre en compte le temps moyen d&#39;attente avec l&#39;exploration.

Une solution trouvée pour résoudre le problème du chasse neige est alors la suivante :



## Explication

Pour diminuer le temps d&#39;attente moyen dans ce problème, une des solutions est de créer des regroupements de maison, des « quartiers », et d&#39;explorer en premier les quartiers les plus « denses », c&#39;est à dire ceux avec l&#39;écart type le plus faible. Sachant que l&#39;on travaille avec des listes de 10 éléments, il est possible d&#39;utiliser l&#39;algorithme de bruteforce sur le dernier élément visité + le quartier pour déterminer le meilleur ordre de visite.

Enfin, plutôt que de visiter les _quartiers_ dans l&#39;ordre d&#39;écart type, il est intéressant de visiter ceux qui sont présent sur la route et de les supprimer de la liste des _quartiers_ à visiter.

## Implémentation

L’implémentation utilise le langage Scala, choisi pour sa capacité à faire de la programmation fonctionnelle : Utilisation des tuples, de la récursion finale et possibilité d’utiliser des monades et des accumulateurs.
# ALG-100
