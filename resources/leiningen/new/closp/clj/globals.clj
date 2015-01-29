(ns {{ns}}.globals)

(def hostname "http://localhost/")
(def mail-from "info@{{name}}")
(def mail-type :sendmail)
(def activation-mail-subject "Please activate your account.")
(def activation-mail-body "Please click on this link to activate your account: {{activationlink}}
Best Regards,Your {{name}}-Team")
(def ^:const activation-placeholder "{{activationlink}}")
(def smtp-data {}) ; passed directly to postmap like {:host "postfix"}
