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
    (let [term (first terms)]
         (if (atomic? term)
           (if (bound-by? term parent)
               (cons term (replace-free* context parent (rest terms)))
               (cons (substitute-if context term) 
                     (replace-free* context parent (rest terms))))
           (cons (replace-free context term) 
                 (replace-free* context parent (rest terms)))))))

(defn replace-free
  [context term]  
  (if (atomic? term) 
    term
    (make-lambda 
      (get-bound-vars term) 
      (replace-free* context term (get-terms term)))))

(defn beta-reduce ;TODO take context
  [variable arg terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
           (let [head (if (eq? variable term) arg term)]
             (cons head (beta-reduce variable arg (rest terms))))
           (cons (replace-free {variable arg} term) 
                 (beta-reduce variable arg (rest terms)))))))


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
               
