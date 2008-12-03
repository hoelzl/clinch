(in-ns 'clojure.reader)

(import '(java.io StringReader))

(use 'clojure.contrib.test-is)


(defn make-reader [contents]
  (LineNumberingPushbackReader. (StringReader. contents)))

(deftest get-macro
  (is (not (get-macro \a)))
  (is (not (get-macro \1)))
  (is (not (get-macro \-)))
  (is (= (get-macro \") read-string))
  (is (= (get-macro \;) read-comment))
  (is (= (get-macro \') read-quote))
  (is (= (get-macro \@) read-deref))
  (is (= (get-macro \^) read-meta))
  (is (= (get-macro \`) read-syntax-quote))
  (is (= (get-macro \~) read-unquote))
  (is (= (get-macro \() read-list))
  (is (= (get-macro \)) read-unmatched-delimiter))
  (is (= (get-macro \[) read-vector))
  (is (= (get-macro \]) read-unmatched-delimiter))
  (is (= (get-macro \{) read-map))
  (is (= (get-macro \}) read-unmatched-delimiter))
  (is (= (get-macro \\) read-character))
  (is (= (get-macro \%) read-arg))
  (is (= (get-macro \#) read-dispatch)))

(deftest clojure-whitespace?
  (is (clojure-whitespace? \space))
  (is (clojure-whitespace? \tab))
  (is (clojure-whitespace? \newline))
  (is (clojure-whitespace? \formfeed))
  (is (clojure-whitespace? \,))
  (is (not (clojure-whitespace? \a)))
  (is (not (clojure-whitespace? \-)))
  (is (not (clojure-whitespace? \!))))

(deftest read-unprotected-eof
  (is (= (read-unprotected (make-reader "") false ::eof false) ::eof)))

(deftest read-uprotected-whitespace
  (is (= (read-unprotected (make-reader " ") false ::eof false) ::eof))
  (is (= (read-unprotected (make-reader ",") false ::eof false) ::eof))
  (is (= (read-unprotected (make-reader ",,,   ") false ::eof false) ::eof))
  (is (= (read-unprotected (make-reader ",\f\t\r\n ") false ::eof false) ::eof))
  )