(in-ns 'clojure.reader)

(gen-class :name clojure.reader.ReaderException
	   :extends Exception
	   :init init-reader-exception
	   :constructors {[int, Throwable] [Throwable]}
	   :state line)

(comment (compile 'clojure.reader.ReaderException))
