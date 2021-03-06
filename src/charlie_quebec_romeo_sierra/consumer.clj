(ns charlie-quebec-romeo-sierra.consumer
  (:require [kinsky.client :as client]
            [kinsky.async :as async]
            [charlie-quebec-romeo-sierra.event :as event]
            [clojure.core.async :refer [go-loop <! put!]]))

(defprotocol ConsumerController
  (output-channel [this])
  (subscribe [this topic])
  (unsubscribe [this])
  (commit [this])
  (stop [this]))

(deftype KafkaConsumerController
  [output control]
  ConsumerController
  (output-channel [this]
    output)
  (subscribe [this topic]
    (put! control {:op :subscribe :topic topic}))
  (unsubscribe [this]
    (put! control {:op :unsubscribe}))
  (commit [this]
    (put! control {:op :commit}))
  (stop [this]
    (put! control {:op :stop})))

(defn- listen
  [type_of consumer handler]
  (go-loop
    []
    (when-let [record (<! (.output-channel consumer))]
      (when (= (:type record) :record)
        (let [event (select-keys record [:key :value])]
          (handler event)
          (doseq [event_handler (event/find-handler type_of)]
            (.handle event_handler event))))
      (recur)))
  (.subscribe consumer type_of)
  (.commit consumer))

(defn consumer
  [type_of handler]
  (let [[output control] (async/consumer
                           {:bootstrap.servers "localhost:9092"
                            :group.id type_of}
                           (client/keyword-deserializer)
                           (client/edn-deserializer))
        consumer (->KafkaConsumerController
                   output
                   control)]
    (listen type_of
            consumer
            handler)
    consumer))
