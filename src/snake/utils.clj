(ns snake.utils)

(defn dissoc-in
  [m ks]
  (update-in m (butlast ks) dissoc (last ks)))
