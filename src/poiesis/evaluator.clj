(ns poiesis.evaluator
  (:use poiesis.forms))


(declare replace-free evaluate eval-lambda evaluate*)

(defn substitute-if [context atm]
  (let [replacement (context atm)]
    (if (nil? replacement)
      atm
      replacement)))


(defn- replace-free*
  [context parent terms]
  (if (empty? terms)
    '()
    (let [term (first terms)
          head (cond 
                 (not (atomic? term)) (replace-free context term)
                 (bound-by? term parent)  term
                 :else (substitute-if context term))]
      (cons head (replace-free* context parent (rest terms))))))

(defn replace-free
  [context term]  
    (make-lambda 
      (get-bound-vars term) 
      (replace-free* context term (get-terms term))))

;REFACTORING:
;  remove beta-reduce
;  replace with (replace-free context (make-l (rest bound) terms))

(defn beta-reduce ;TODO take context
  [variable arg terms]
  (if (empty? terms)
    '()
    (let [term (first terms)
          context {variable arg} ; TODO move to fun param
          head (if (not (atomic? term)) 
                 (replace-free context term)
                 (substitute-if context term))]
      (cons head (beta-reduce variable arg (rest terms))))))


(defn simplify [bound-vars terms]
  (if (and (empty? bound-vars) (= 1 (count terms)))
    (first terms)
    (make-lambda bound-vars terms)))

(defn eval-expr [lambda]
  (simplify (get-bound-vars lambda) (evaluate* (get-terms lambda))))

(defn evaluate* [terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
           (cons term (evaluate* (rest terms)))
           (if (lambda? term)
             (eval-lambda term (rest terms))
             (cons (eval-expr term)
                   (evaluate* (rest terms))))))))
    
(defn evaluate [term]
  (if (atomic? term)
    term
    (eval-expr term)))

(defn apply-var
  [lambda arg]
  (let [vars (get-bound-vars lambda)
        new-terms (beta-reduce (first vars) arg (get-terms lambda))]
       (simplify (rest vars) new-terms)))
 
(defn eval-lambda
  [l ts]
  (loop [lambda l
         terms ts]
    (if (empty? terms)
      (cons (eval-expr lambda) '()) 
      (let [lambda* (evaluate (apply-var lambda (first terms)))]
        (if (lambda? lambda*)
          (recur lambda* (rest terms))
          (cons lambda* (evaluate* (rest terms))))))))
               
