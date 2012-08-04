(ns madness.config)

(defonce default-config
  {:template {:default "default.html"
              :atom "atom.xml"}
   :dirs {:posts "resources/posts"
          :pages "resources/pages"
          :output "public/"}
   :recent-posts {:columns 3
                  :rows 2}
   :archive-posts {:columns 3
                   :rows 0}
   :atom {:base-url "http://localhost"
          :title nil}})

(defonce config
  (reduce
   (fn [o, n]
     (let [k (first n), vo (second o), vn (second n)]
       (update-in o [k] merge vn)))
   default-config
   (eval (read-string (slurp "settings.clj")))))

(defn template
  [& id]

  (str "templates/" ((or (first id) :default) (:template config))))

(defmulti recent-posts
  identity)

(defmethod recent-posts :default [setting]
  (-> config :recent-posts setting))

(defmethod recent-posts :total [_]
  (inc (* (recent-posts :columns)
          (recent-posts :rows))))

(defmethod recent-posts :span [_]
  (int (/ 12 (recent-posts :columns))))

(defmulti archive-posts
  identity)

(defmethod archive-posts :default [setting]
  (-> config :archive-posts setting))

(defmethod archive-posts :span [_]
  (int (/ 12 (archive-posts :columns))))

(defn dirs
  [role]

  (-> config :dirs role))

(defn atom-feed
  [setting]

  (-> config :atom setting))
