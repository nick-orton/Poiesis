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
  (is (= "( y y z)" (str (replace-free {x y} xyz ))))
  (is (= "( x x z)" (str (replace-free {y x} xyz ))))
  (is (= "( x( x x) z)"   (str (replace-free {y x} x-xy-z) )))
  (is (= "( z(λ x. x y) z)"   (str (replace-free {x z} x-lxy-z))))
  (is (= "( x(λ x. x z) z)"   (str (replace-free {y z} x-lxy-z))))
  (is (= "( y y)" (str (replace-free {x y} xy )))))

(def lzx (make-lambda [z] [z x]))
(def lx-lzx-y (make-lambda [x] [x lzx y]))
(def lx-lxy-y (make-lambda [x] [x lxy y]))

(deftest test-beta-reduce
  (is (= " y" (str (nth (beta-reduce x y (get-terms lx-lzx-y)) 0))))
  (is (= "(λ z. z y)" (str (nth (beta-reduce x y (get-terms lx-lzx-y)) 1))))
  (is (= " y" (str (nth (beta-reduce x y (get-terms lx-lzx-y)) 2))))
  (is (= " z" (str (nth (beta-reduce x z (get-terms lx-lxy-y)) 0))))
  (is (= "(λ x. x y)" (str (nth (beta-reduce x z (get-terms lx-lxy-y)) 1))))
  (is (= " y" (str (nth (beta-reduce x z (get-terms lx-lxy-y)) 2)))))

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
  ; y -> y
  (is (= y (evaluate y)))

  ; \x.y -> \x.y
  (is (= [x] (get-bound-vars (evaluate lxy))))       
  (is (= [x y] (get-terms (evaluate lxy))))       

  ; \x.(\x.x)y -> \x.y
  (def r-lx-lxx-y (evaluate lx-lxx-y))
  (is (lambda? r-lx-lxx-y))
  (is (= [x]  (get-bound-vars r-lx-lxx-y)))
  (is (= [y]  (get-terms r-lx-lxx-y)))

  ; (x y) -> (x y)       
  (is (= [] (get-bound-vars (evaluate xy))))
  (is (= [x y] (get-terms (evaluate xy))))
   
  ;( (x y) y) -> ( (x y) y)       
  (def xy-y (make-lambda '() [xy y]))
  (is (= y (nth (get-terms (evaluate xy-y)) 1 )))
  (is (= [x y] (get-terms (nth (get-terms (evaluate xy-y)) 0 ))))

  ; ( (\xy.xy) z f) -> (z f)       
  (def f (make-atom "f"))
  (def lxy-xy (make-lambda [x y] [x y]))
  (def tl-apply (make-lambda [] [lxy-xy z f]))
  (is (= [z f] (get-terms (evaluate tl-apply))))

  ; ((\xy.xy)(\x.x)z) -> z       
  (def e-lxy-xy-lx-x-z (make-lambda [] [lxy-xy lxx z]))
  (is (= z (evaluate e-lxy-xy-lx-x-z)))
 )

(deftest test-substitute-if
  (is (= :c (substitute-if {:a :b} :c)))
  (is (= :b (substitute-if {:a :b} :a))))
