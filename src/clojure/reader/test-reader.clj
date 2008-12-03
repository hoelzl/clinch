(in-ns 'clojure.reader)

(use 'clojure.contrib.test-is)

(deftest test-get-macro
  (is (not (get-macro \a)))
  (is (not (get-macro \1)))
  (is (not (get-macro \-)))
  (is (= (get-macro \") read-string))
  (is (= (get-macro \;) read-comment))
  (is (= (get-macro \() read-list)))
