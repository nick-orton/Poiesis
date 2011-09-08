(ns poiesis.test.evaluator
  (:use [poiesis.evaluator])
  (:use [poiesis.forms])
  (:use [clojure.test]))

(def x (make-atom "x"))
(def y (make-atom "y"))
(def z (make-atom "z"))
(def xy (make-lambda [] [x y]))
(def lxy (make-lambda [x] [x y]))
(def yy (make-lambda [] [y y]))
(def xyz (make-lambda [] [x y z]))
(def x-xy-z (make-lambda [] [x xy z]))
(def x-lxy-z (make-lambda [] [x lxy z]))

(deftest test-replace-free
  (is (= [y y z] (get-terms (replace-free x y xyz ))))
  (is (= [x x z] (get-terms (replace-free y x xyz ))))
  (def replaced-x-xy-z (replace-free y x x-xy-z))
  (defn terms-of-2nd-exp [exp] (get-terms (nth (get-terms exp) 1)))
  (is (= [x x]   (terms-of-2nd-exp replaced-x-xy-z )))
  (is (= [x y]   (terms-of-2nd-exp (replace-free x z x-lxy-z))))
  (is (= [x z]   (terms-of-2nd-exp (replace-free y z x-lxy-z))))
  (is (= [y y] (get-terms (replace-free x y xy )))))
