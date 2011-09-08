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
