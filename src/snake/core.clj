(ns snake.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [snake.dynamic :as dynamic]))

(defn run
  [options]
  (q/defsketch draw-array-sketch
               :size [dynamic/x-max dynamic/y-max]
               :setup dynamic/setup
               :update dynamic/sketch-update
               :draw dynamic/draw
               :features [:keep-on-top]
               :middleware [m/fun-mode]
               :key-pressed dynamic/key-pressed))

