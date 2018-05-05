(ns {{ns}}.service.user)


(defn get-user-id-from-req [req]
  (-> req :identity :user-id))
