(ns blackjack.core
  (:require [blackjack.simple-hand :refer [deal new-hand up-card add-card total]]))

(declare play-game)

(defn stupid-strategy [hand opponent-up-card]
  (> opponent-up-card 5))

(defn test-strategy
  ([player-strategy house-strategy]
     (test-strategy player-strategy house-strategy 100))
  ([player-strategy house-strategy n]
     "plays n games and returns how many times the player won as a percentage of n"
     (float (/ (total (repeatedly n #(play-game player-strategy house-strategy))) n))
     ))
     

(defn stop-at-17 [hand opponent-up-card]
   (if (< (total hand) 17) true false) 
  )

(defn stop-at [n]
  "Returns a strategy that hits until the total is n"
 (fn [hand opponent-up-card] (if (< (total hand) n) true false))
  )

(defn watched [strategy]
    (fn [hand opponent-up-card] (let [hit-or-not (strategy hand opponent-up-card)] 
      (println [hand, opponent-up-card, hit-or-not])
    hit-or-not
    ))
    )

(defn smart-strategy [hand opponent-up-card]
  (if (< opponent-up-card 7) ((stop-at 13) hand opponent-up-card)
    ((stop-at 17) hand opponent-up-card)
  ))

(defn play-hand [strategy hand opponent-up-card]
  (cond (> (total hand) 21)
        hand

        (strategy hand opponent-up-card) ; Asks 'should I hit?', returns true/false
        (recur strategy
               (add-card hand (deal)) ; Recurs, adding a card
               opponent-up-card)

        :else ;the strategy said not to hit, return the hand as is
        hand))

(defn play-game [player-strategy house-strategy]
  (let [house-initial-hand (new-hand)
        player-hand (play-hand player-strategy
                               (new-hand)
                               (up-card house-initial-hand))]
    (if (> (total player-hand) 21)
      0 ; Player bust
      (let [house-hand (play-hand house-strategy
                                  house-initial-hand
                                  (up-card player-hand))]
        (cond (> (total house-hand) 21)
              1 ; House bust

              (> (total player-hand) (total house-hand))
              1 ; House lost

              :else
              0 ; Player lost
              )))))
