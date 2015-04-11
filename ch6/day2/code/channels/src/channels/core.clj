;---
; Excerpted from "Seven Concurrency Models in Seven Weeks",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
;---
(ns channels.core
  (:require [clojure.core.async :as async :refer :all
              :exclude [map into reduce merge partition partition-by take]]))

(defn readall!! [ch]
  (loop [coll []]
    (if-let [x (<!! ch)]
      (recur (conj coll x))
      coll)))

(defn writeall!! [ch coll]
  (doseq [x coll]
    (>!! ch x))
  (close! ch))

(defn go-add [x y]
  (<!! (nth (iterate #(go (inc (<! %))) (go x)) y)))

(defn map-chan [f from]
  (let [to (chan)]
    (go-loop []
      (when-let [x (<! from)]
        (>! to (f x))
        (recur))
      (close! to))
    to))

(defn my-map> [f to]
  (let [from (chan)]
    (go-loop []
      (when-let [x (<! from)]
        (>! to (f x))
        (recur)))
    from))

(defn ch-pmap [f from]
  (let [seq-ch (to-chan from)]
    (let [results (chan)]
      (go-loop []
        (when-let [x (<! seq-ch)]
          (>! results (f x))
          (recur))
        (close! results))
      results)))

(defn my-merge [channels]
  (let [result (chan)]
    (go-loop [active channels]
      (when (> (count active) 0)
        (let [[x, ch] (alts! active)]
          (when x
            (>! result x)
            (recur active))
          (recur (remove #{ch} active))))
        (close! result))
    result))
