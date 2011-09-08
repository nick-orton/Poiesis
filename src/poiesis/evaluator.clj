(ns poiesis.evaluator
  (:use poiesis.forms))


(declare replace-free)

(defn- replace-free*
  [variable arg parent terms]
  (if (empty? terms)
    '()
    (let [term (first terms)]
         (if (atomic? term)
             (if (and (not (bound-by? term parent))(eq? variable term))
                 (cons arg (replace-free* variable arg parent (rest terms)))
                 (cons term (replace-free* variable arg parent (rest terms))))
             (cons (replace-free variable arg term) 
                   (replace-free* variable arg parent (rest terms)))))))

(defn replace-free
  [variable arg term]
  (if (atomic? term) 
      term
      (make-lambda 
        (get-bound-vars term) 
        (replace-free* variable arg term (get-terms term)))))

(defn beta-reduce
  [variable arg terms]
  (if (empty? terms)
      '()
      (let [term (first terms)]
           (if (atomic? term)
               (if (eq? variable term)
                   (cons arg (beta-reduce variable arg (rest terms)))
                   (cons term (beta-reduce variable arg (rest terms))))
               (cons (replace-free variable arg term) (beta-reduce variable arg (rest terms)))))))

(defn apply-var
  [lambda arg]
  (let [vars (get-bound-vars lambda)
        terms (get-terms lambda)]
       (if (empty? vars)
           lambda
           (let [variable (first vars)
                 new-terms (beta-reduce variable arg terms)]
                (make-lambda (rest vars) new-terms)))))
                
       
