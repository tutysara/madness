(ns madness.config)

(defonce config (eval (read-string (slurp "settings.clj"))))

(defn template
  ([] (str "templates/" (or (-> config :template :default) "default.html")))

  ([role]
     (cond
      (= role :default) (template)
      (= role :atom) (str "templates/" (or (-> config :template :atom) "atom.xml")))))

(defn recent-posts [setting]
  (cond
   (= setting :columns) (or (-> config :recent-posts :columns) 3)
   (= setting :rows) (or (-> config :recent-posts :rows) 2)
   (= setting :total) (inc (* (recent-posts :columns)
                              (recent-posts :rows)))
   (= setting :span) (int (/ 12 (recent-posts :columns)))))

(defn archive-posts [setting]
  (cond
   (= setting :columns) (or (-> config :archive-posts :columns) 3)
   (= setting :rows) (or (-> config :archive-posts :rows) 0)
   (= setting :span) (int (/ 12 (archive-posts :columns)))))

(defn dirs [role]
  (cond
   (= role :posts) (or (-> config :dirs :posts) "resources/posts")
   (= role :pages) (or (-> config :dirs :pages) "resources/pages")
   (= role :output) (or (-> config :dirs :output) "public/")))

(defn atom-feed [setting]
  (cond
   (= setting :base-url) (or (-> config :atom :base-url) "http://localhost")
   (= setting :title) (-> config :atom :title)))
