(in-ns 'clojure.reader)

(import '(java.io StringReader))

(use 'clojure.contrib.test-is)


(defn make-reader [contents]
  (LineNumberingPushbackReader. (StringReader. contents)))

(deftest macro?
  (doseq [char "\";'@^`~()[]{}\\%#"]
    (is (true? (macro? char))))
  (doseq [char "aA1-=?"]
    (is (false? (macro? char)))))

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

(defn test-read [content]
  (read-unprotected (make-reader content) false ::eof false))

(deftest read-unprotected-eof
  (is (= (test-read "") ::eof))
  (throws Exception (read-unprotected (make-reader "") true nil false)))

(deftest read-unprotected-whitespace
  (is (= (test-read " ") ::eof))
  (is (= (test-read ",") ::eof))
  (is (= (test-read ",,,   ") ::eof))
  (is (= (test-read ",\f\t\r\n ") ::eof)))

(defn test-read-number [nr]
  (read-number (make-reader (apply str (rest nr))) (first nr)))

(deftest read-number
  (is (= (test-read-number "0") 0))
  (is (= (test-read-number "1") 1))
  (is (= (test-read-number "-1") -1)))

(deftest read-unprotected-number)