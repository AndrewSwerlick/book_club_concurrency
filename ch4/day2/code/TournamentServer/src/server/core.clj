;---
; Excerpted from "Seven Concurrency Models in Seven Weeks",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
;---
(ns server.core
  (:require [compojure.core         :refer :all]
            [compojure.handler      :refer [site]]
            [ring.util.response     :refer [response status]]
            [ring.adapter.jetty     :refer [run-jetty]]
            [cheshire.core          :as json]))

(def players (atom ()))
(def games (atom ()))

(defn list-players []
  (response (json/encode @players)))

(defn list-games []
  (response (json/encode @games)))

(defn create-player [player-name]
  (swap! players conj player-name)
  (status (response "") 201))

(defn next-players []
  (take 2 (filter
    (fn [item] (not (some #{item} (flatten @games)))) @players)))

(defn create-game []
  (let [game (next-players)]
    (if (= (count game) 2)
      (swap! games conj game)
      (throw (Exception. "Not enough players for a game")))
    (response (json/encode game))))

(defroutes app-routes
  (GET "/players" [] (list-players))
  (PUT "/players/:player-name" [player-name] (create-player player-name))
  (GET "/games" [] (list-games))
  (POST "/games/start" [] (create-game)
  (POST "/games/finish/:winner")))
(defn -main [& args]
  (run-jetty (site app-routes) {:port 3000}))
