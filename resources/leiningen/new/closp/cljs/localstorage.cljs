(ns {{ns}}.localstorage)




(def local-storage-jwt-token-key "jwt-token")

(def local-storage-jwt-token-key-with-ns "{{ns}}-jwt-token")


(defn set-item!
  "Set `key' in browser's localStorage to `val`."
  [key val]
  (.setItem (.-localStorage js/window) (str "{{ns}}-" key) val))

(defn get-item
  "Returns value of `key' from browser's localStorage."
  [key]
  (.getItem (.-localStorage js/window) (str "{{ns}}-" key)))

(defn remove-item!
  "Remove the browser's localStorage value for the given `key`"
  [key]
  (.removeItem (.-localStorage js/window) (str "{{ns}}-" key)))
