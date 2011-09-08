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

(defn terms-of-2nd-exp [exp] (get-terms (nth (get-terms exp) 1)))

(deftest test-replace-free
  (is (= [y y z] (get-terms (replace-free x y xyz ))))
  (is (= [x x z] (get-terms (replace-free y x xyz ))))
  (def replaced-x-xy-z (replace-free y x x-xy-z))
  (is (= [x x]   (terms-of-2nd-exp replaced-x-xy-z )))
  (is (= [x y]   (terms-of-2nd-exp (replace-free x z x-lxy-z))))
  (is (= [x z]   (terms-of-2nd-exp (replace-free y z x-lxy-z))))
  (is (= [y y] (get-terms (replace-free x y xy )))))

(def lzx (make-lambda [z] [z x]))
(def lx-lzx-y (make-lambda [x] [x lzx y]))
(def lx-lxy-y (make-lambda [x] [x lxy y]))

(deftest test-beta-reduce
  (def result (beta-reduce x y (get-terms lx-lzx-y)))
  (is (= y (nth result 0)))
  (is (= [z y] (get-terms (nth result 1))))
  (def no-bound-sub-terms (beta-reduce x z (get-terms lx-lxy-y)))
  (is (= z (nth no-bound-sub-terms 0)))
  (is (= [x y] (get-terms (nth no-bound-sub-terms 1)))))

(deftest test-apply-var
  (def result (get-terms (apply-var lx-lzx-y y)))
  (is (= y (nth result 0)))
  (is (= [z y] (get-terms (nth result 1))))
  (def no-bound-sub-terms (get-terms (apply-var lx-lxy-y z)))
  (is (= z (nth no-bound-sub-terms 0)))
  (is (= [x y] (get-terms (nth no-bound-sub-terms 1)))))

(def lxx (make-lambda [x] [x]))
(def lx-lxx-y (make-lambda [x] [lxx y]))

(deftest test-evaluate
  (is (= y (evaluate y)))
  (is (= [x] (get-bound-vars (evaluate lxy))))       
  (is (= [x y] (get-terms (evaluate lxy))))       
; test that a single lambda gets its guts evaled
  (def r-lx-lxx-y (evaluate lx-lxx-y))
  (is (lambda? r-lx-lxx-y))
  (is (= [x]  (get-bound-vars r-lx-lxx-y)))
  (is (= [y]  (get-terms r-lx-lxx-y)))

         )
