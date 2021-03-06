;---
; Excerpted from "Seven Concurrency Models in Seven Weeks",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/pb7con for more book information.
;---
(ns sum.core
  (:require [clojure.core.reducers :as r]))

(defn recursive-sum [numbers]
  (if (empty? numbers)
    0
    (loop [sum 0 ind 0]
      (if (= ind (count numbers))
        sum
        (recur (+ (numbers ind) sum) (inc ind) )))))


(defn reduce-sum [numbers]
  (reduce #(+ %1 %2) 0 numbers))

(defn sum [numbers]
  (reduce + numbers))

(defn apply-sum [numbers]
  (apply + numbers))

(defn parallel-sum [numbers]
  (r/fold + numbers))
