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
                 (not (word? term)) (replace-free context term)
                 (bound-by? term parent)  term
                 :else (substitute-if context term))]
      (cons head (replace-free* context parent (rest terms))))))


(defn simplify [bound-vars terms context]
  (if (and (empty? bound-vars) (= 1 (count terms)))
    (first terms)
    (make-lambda bound-vars terms)))

(defn- bind-to-self [context vars]
  (loop [c context
         bvs vars] 
    (if (empty? bvs) c
      (recur (assoc c (first bvs) (first bvs)) (rest bvs)))))

(defn eval-expr [expr contxt]
  (let [bound-vars (get-bound-vars expr)
        context (bind-to-self contxt bound-vars)]
  (simplify (get-bound-vars expr) (evaluate* (get-terms expr) context) context)))

(defn evaluate* [terms context]
  (if (empty? terms)
    '()
    (let [term (first terms)]
      (cond 
        (word? term)
          (let [term* (substitute-if context term)
                result (cons term* (evaluate* (rest terms) context))]
            (if (= term term*)
              result 
              (evaluate* result context)))
        (lambda? term)
          (eval-lambda term (rest terms) context)
        :else 
          (cons (eval-expr term context)
                (evaluate* (rest terms) context))))))

 (defn replace-free
  [context term]  
    (make-lambda 
      (get-bound-vars term) 
      (replace-free* context term (get-terms term))))
   
(defn evaluate [term context]
  (if (word? term)
    (substitute-if context term)
    (eval-expr term context)))

(defn beta-reduce
  [lambda arg context]
  (let [vars (get-bound-vars lambda)
        context* (assoc context (first vars) arg)
        new-l (replace-free context* 
                            (make-lambda (rest (get-bound-vars lambda)) 
                                         (get-terms lambda)))]
    (simplify (get-bound-vars new-l) (get-terms new-l) context)))
 
(defn eval-lambda
  [l ts context]
  (loop [lambda l
         terms ts]
    (if (empty? terms)
      (cons (eval-expr lambda context) '()) 
      (let [lambda* (evaluate (beta-reduce lambda (first terms) context) 
                              context)]
        (if (lambda? lambda*)
          (recur lambda* (rest terms))
          (cons lambda* (evaluate* (rest terms) context)))))))
               
