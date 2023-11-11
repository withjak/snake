(ns snake.dynamic
  (:require [quil.core :as q]
            [snake.utils :as utils]))

(def bg-color 255)
(def food-color [0 255 0])
(def snake-color [255 0 0])
(def frame-rate 10)
(def x-max 400)
(def y-max 400)
(def size 20)
(def valid-keys #{:left :right :up :down})

(defn space
  []
  "All possible coordinates."
  (zipmap
    (range 0 x-max size)
    (repeat (zipmap
              (range 0 y-max size)
              (repeat true)))))

(def initial-state
  (let [snake [[(* size (* (rand-int (/ x-max size))))
                (* size (* (rand-int (/ y-max size))))]]
        space (reduce (fn [s p] (utils/dissoc-in s p)) (space) snake)
        food-x (rand-nth (keys space))
        food-y (rand-nth (keys (get space food-x)))]
    {:snake       snake
     :space       space
     :stop-loop   false
     :food        [food-x food-y]
     :direction   :right
     :key-presses []}))

(defn setup
  []
  (q/background bg-color)
  (q/frame-rate frame-rate)
  initial-state)

(defn get-direction
  [previous-direction key]
  (let [invalid-direction {:left  :right
                           :right :left
                           :up    :down
                           :down  :up}]
    (if (or (= (invalid-direction key) previous-direction) (nil? key))
      previous-direction
      key)))

(defn move-snake
  [snake direction]
  (let [[x y] (last snake)
        new-head (condp = direction
                   :right [(mod (+ x size) x-max) y]
                   :left [(mod (- x size) x-max) y]
                   :up [x (mod (- y size) y-max)]
                   :down [x (mod (+ y size) y-max)])]
    (concat snake [new-head])))

(defn key-pressed
  [state {:keys [key]}]
  (if (contains? valid-keys key)
    (update state :key-presses concat [key])
    state))

(defn sketch-update
  [{:keys [snake space food direction key-presses] :as state}]
  (let [new-direction (get-direction direction (first key-presses))
        new-snake (move-snake snake new-direction)
        head (last new-snake)
        old-tail (first new-snake)]
    (if (and (seq space) (or (get-in space head) (= head old-tail)))
      (if (= food head)
        (let [food-x (rand-nth (keys space))
              food-y (rand-nth (keys (get space food-x)))
              new-food [food-x food-y]]
          (-> state
              (utils/dissoc-in (concat [:space] head))
              (assoc :snake new-snake)
              (assoc :food new-food)
              (assoc :direction new-direction)
              (update :key-presses rest)))
        (-> state
            (utils/dissoc-in (concat [:space] head))
            (assoc-in (concat [:space] old-tail) true)
            (assoc :snake (rest new-snake))
            (assoc :direction new-direction)
            (update :key-presses rest)))
      (assoc state :stop-loop true))))

(defn point
  [x y]
  (q/stroke-weight size)
  (apply q/stroke snake-color)
  (q/point x y))

(defn draw-snake
  "Redrawing snake every time. Optimization needed"
  [snake]
  (mapv #(apply point %) snake))

(defn draw-food
  [x y]
  (q/stroke-weight size)
  (apply q/stroke food-color)
  (q/point x y))

(defn draw
  [{:keys [snake stop-loop food]}]
  (if stop-loop
    (q/no-loop)
    (do
      (q/background bg-color)
      (draw-snake snake)
      (when food
        (apply draw-food food)))))