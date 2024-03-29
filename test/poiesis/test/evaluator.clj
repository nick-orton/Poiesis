(ns poiesis.test.evaluator
  (:use [poiesis.evaluator])
  (:use [poiesis.forms])
  (:use [clojure.test]))

(def x (make-word "x"))
(def y (make-word "y"))
(def z (make-word "z"))
(def f (make-word "f"))
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
  (is (= "( z([ x] x y) z)"   (str (replace-free {x z} x-lxy-z))))
  (is (= "( x([ x] x z) z)"   (str (replace-free {y z} x-lxy-z))))
  (is (= "( y y)" (str (replace-free {x y} xy )))))

(deftest test-apply-var
  (def lzx (make-lambda [z] [z x]))
  (def lx-lzx-y (make-lambda [x] [x lzx y]))
  (def lx-lxy-y (make-lambda [x] [x lxy y]))
  (is (= "( y([ z] z y) y)" (str (beta-reduce lx-lzx-y y {}))))
  (is (= "( z([ x] x f) f)" (str (beta-reduce lx-lxy-y z {y f}))))
  (is (= "( z([ x] x y) y)" (str (beta-reduce lx-lxy-y z {})))))

(def lxx (make-lambda [x] [x]))
(def lx-lxx-y (make-lambda [x] [lxx y]))

(deftest test-evaluate
  ; y -> y
  (is (= y (evaluate y {})))
  (is (= z (evaluate y {y z})))

  ; \x.y -> \x.y
  (is (= "([ x] x y)" (str (evaluate lxy {}))))       
  (is (= "([ x] x z)" (str (evaluate lxy {y z}))))       
  (is (= "([ x] x y)" (str (evaluate lxy {x z}))))       

  ; \x.(\x.x)y -> \x.y
  (def r-lx-lxx-y (evaluate lx-lxx-y {}))
  (is (lambda? r-lx-lxx-y))
  (is (= "([ x] y)"  (str r-lx-lxx-y)))
  (is (= "([ x] z)"  (str (evaluate lx-lxx-y {y z}))))

  ; (x y) -> (x y)       
  (is (= "( x y)" (str (evaluate xy {}))))
  (is (= "( z y)" (str (evaluate xy {x z}))))
  (is (= " y" (str (evaluate xy {x lxx}))))
   
  ;( (x y) y) -> ( (x y) y)       
  (def xy-y (make-lambda '() [xy y]))
  (is (= "(( x y) y)" (str (evaluate xy-y {})) ))
  (is (= "(( z y) y)" (str (evaluate xy-y {x z})) ))
  (is (= "(( x z) z)" (str (evaluate xy-y {y z})) ))

  ; ( (\xy.xy) z f) -> (z f)       
  (def lxy-xy (make-lambda [x y] [x y]))
  (def tl-apply (make-lambda [] [lxy-xy z f]))
  (is (= "( z f)" (str (evaluate tl-apply {}))))
  (is (= "( f f)" (str (evaluate tl-apply {z f}))))
  (is (= "( z z)" (str (evaluate tl-apply {f z}))))

  ; ((\xy.xy)(\x.x)z) -> z       
  (is (= z (evaluate (make-lambda [] [lxy-xy lxx z]) {})))
  (is (= f (evaluate (make-lambda [] [lxy-xy lxx z]) {z f}))))

(deftest test-substitute-if
  (is (= :c (substitute-if {:a :b} :c)))
  (is (= :b (substitute-if {:a :b} :a))))
