(ns poiesis.test.parser
  (:use [poiesis.parser])
  (:use [poiesis.forms])
  (:use [clojure.test]))

(deftest test-build-expr
  (is (lambda? (build-expression '((:LAMBDA-BINDING :A :B) :C :D )))) 
  (is (lambda? (build-expression '((:LAMBDA-BINDING :A :B) :C :D )))) 
         )

(deftest test-cons-lambda-bindings
  (is (= '((:LAMBDA-BINDING :A :B)) (cons-lambda-bindings [:B :A "[" ])))
  (is (= '((:LAMBDA-BINDING :A :B) :C) (cons-lambda-bindings [:B :A "[" :C]))))


(deftest test-cons-expr
  (is (= "(λ. :A:B )" (str (first (cons-expr '(:B :A "(") )))))
  (is (= "(λ:C. :A:B )" (str (first (cons-expr '(:B :A (:LAMBDA-BINDING :C) "(") )))))
  (is (= ":D" (str (second (cons-expr '(:B :A (:LAMBDA-BINDING :C) "(" :D) ))))))

(deftest test-parse-l
  (is (= "(λ. Atom:A Atom:B  )"  (str (parse-l '("(" "A" "B" ")"))))))

