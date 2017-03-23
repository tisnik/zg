;
;  (C) Copyright 2016  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns zg.html-renderer
    "Module that contains functions used to render HTML pages sent back to the browser.")

(require '[hiccup.core            :as hiccup])
(require '[hiccup.page            :as page])
(require '[hiccup.form            :as form])

(defn render-html-header
    "Renders part of HTML page - the header."
    [word url-prefix title]
    [:head
        [:title (str title ": " word)]
        [:meta {:name "Author"    :content "Pavel Tisnovsky"}]
        [:meta {:name "Generator" :content "Clojure"}]
        [:meta {:http-equiv "Content-type" :content "text/html; charset=utf-8"}]
        ;(page/include-css "http://torment.usersys.redhat.com/openjdk/style.css")]
        (page/include-css (str url-prefix "bootstrap.min.css"))
        (page/include-css (str url-prefix "smearch.css"))
        (page/include-js  (str url-prefix "bootstrap.min.js"))
    ] ; head
)

(defn render-html-footer
    "Renders part of HTML page - the footer."
    []
    [:div "<br /><br /><br /><br />Author: Pavel Tisnovsky &lt;<a href='mailto:ptisnovs@redhat.com'>ptisnovs@redhat.com</a>&gt"])

(defn mode->str
    "The actual zg mode (a keyword) is transformed into string that will be used in URL"
    [mode]
    (case mode
        :whitelist    "whitelist"
        :blacklist    "blacklist"
        :atomic-typos "atomic-typos"
        :glossary     "glossary"
                      "whitelist")) ; default

(defn search-href
    "Generator of href destination for the 'search' input field."
    [url-prefix mode]
    (str url-prefix (mode->str mode)))

(defn render-search-field
    "Renders search box on the top side of HTML page."
    [word url-prefix mode]
    (form/form-to {:class "navbar-form navbar-left" :role "search"} [:get (search-href url-prefix mode)]
        [:div {:class "input-group"}
            [:span {:class "input-group-addon"} "Search"]
            (form/text-field {:size "40" :class "form-control" :placeholder "Search for word"} "word" (str word))
            [:div {:class "input-group-btn"}
                (form/submit-button {:class "btn btn-default"} "Search")]]))

(defn render-name-field
    "Renders box for typing user name on the top side of HTML page."
    [user-name url-prefix]
    (form/form-to {:class "navbar-form navbar-left" :role "search"} [:get url-prefix ]
        [:div {:class "input-group"}
            ;[:span {:class "input-group-addon"} "Name"]
            (form/text-field {:size "10" :class "form-control" :placeholder "User name"} "user-name" (str user-name))
            [:div {:class "input-group-btn"}
                (form/submit-button {:class "btn btn-default"} "Remember me")]]))

(defn tab-class
    "In the tab menu on top, only one item could be actived."
    [active mode]
    (if (= active mode)
        {:class "active"}))

(defn user-href
    "Generator for href to the page containing user info."
    [user-name mode]
    (let [destination (if (= mode :whitelist)
                          "user-whitelist"
                          "user-blacklist")]
        [:a {:href (str destination "?name=" user-name)} user-name]))

(defn users-href
    "Generator for URL used by 'Users' menu item."
    [url-prefix mode]
    (condp = mode
        :whitelist    (str url-prefix "users-whitelist")
        :blacklist    (str url-prefix "users-blacklist")
        :atomic-typos (str url-prefix "users-atomic-typos")
        :glossary     (str url-prefix "users-glossary")
                      nil))

(defn remember-me-href
    "Generator for URL used by 'remember me' button."
    [url-prefix mode]
    (condp = mode
        :whitelist    (str url-prefix "whitelist")
        :blacklist    (str url-prefix "blacklist")
        :atomic-typos (str url-prefix "atomic-typos")
        :glossary     (str url-prefix "glossary")
                      nil))

(defn render-navigation-bar-section
    "Renders whole navigation bar."
    [user-name url-prefix title mode]
    [:nav {:class "navbar navbar-inverse navbar-fixed-top" :role "navigation"} ; use navbar-default instead of navbar-inverse
        [:div {:class "container-fluid"}
            [:div {:class "row"}
                [:div {:class "col-md-7"}
                    [:div {:class "navbar-header"}
                        [:a {:href url-prefix :class "navbar-brand"} title]
                    ] ; ./navbar-header
                    [:div {:class "navbar-header"}
                        [:ul {:class "nav navbar-nav"}
                            [:li (tab-class :whitelist mode) [:a {:href (str url-prefix "whitelist")} "Whitelist"]]
                            [:li (tab-class :blacklist mode) [:a {:href (str url-prefix "blacklist")} "Blacklist"]]
                            [:li (tab-class :atomic-typos mode) [:a {:href (str url-prefix "atomic-typos")} "Atomic typos"]]
                            [:li (tab-class :glossary mode) [:a {:href (str url-prefix "glossary")} "Glossary"]]
                        ]
                    ]
                ] ; col-md-7 ends
                [:div {:class "col-md-3"}
                    (render-name-field user-name (remember-me-href url-prefix mode))
                ]
                [:div {:class "col-md-2"}
                    [:div {:class "navbar-header"}
                        [:a {:href (users-href url-prefix mode) :class "navbar-brand"} "Users"]
                    ] ; ./navbar-header
                ] ; col ends
            ] ; row ends
        ] ; </div .container-fluid>
]); </nav>

(defn render-error-page
    "Render error page with a 'back' button."
    [word user-name message url-prefix]
    (page/xhtml
        (render-html-header "" url-prefix nil) ; TODO change, it might not work correctly (title, mode)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section user-name url-prefix nil); TODO change, it might not work correctly (title, mode)
                [:div {:class "col-md-10"}
                    [:h2 "Sorry, error occured in zg"]
                    [:p message]
                    [:button {:class "btn btn-primary" :onclick "window.history.back()" :type "button"} "Back"]
                ]
                [:br][:br][:br][:br]
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

(defn render-front-page-for-whitelist
    [word url-prefix emender-page mode]
    [:div {:class "container-fluid"}
        [:div {:class "row"}
            "The CCS Custom Dictionary Whitelist - allows you to add and remove words that should not be reported as errors in Red Hat documentation by Emender (" [:a {:href emender-page} "see Emender page" ] ")"
        ]
        [:br]
        [:div {:class "row"}
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "all-words-in-whitelist") :class "navbar-brand"} "All words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "active-words-in-whitelist") :class "navbar-brand"} "Active words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "deleted-words-in-whitelist") :class "navbar-brand"} "Deleted words"]
            ] ; ./navbar-header
            [:div {:style "width:80%"}
                (render-search-field word url-prefix mode)
            ]
        ]
        [:br]
        [:br]
        (form/form-to [:post (str url-prefix "add-words-to-whitelist")]
                [:div {:class "_label _label-primary"} "New words  (separate them with spaces, commas, or enter them on a new line)"]
                (form/text-area {:cols "80" :rows "8"} "new-words")
                [:br]
                (form/submit-button {:class "btn btn-primary"} "Add new words")
                [:br])
        [:br]])

(defn render-front-page-for-blacklist
    [word url-prefix emender-page mode]
    [:div {:class "container-fluid"}
        [:div {:class "row"}
            "The CCS Custom Dictionary Blacklist &ndash; allows you to add and remove words that are normally correct but should be reported as errors in Red Hat documentation by Emender (" [:a {:href emender-page} "see Emender page" ] ")"
        ]
        [:br]
        [:div {:class "row"}
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "all-words-in-blacklist") :class "navbar-brand"} "All words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "active-words-in-blacklist") :class "navbar-brand"} "Active words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "deleted-words-in-blacklist") :class "navbar-brand"} "Deleted words"]
            ] ; ./navbar-header
            [:div {:style "width:80%"}
                (render-search-field word url-prefix mode)
            ]
        ]
        [:br]
        [:br]
        (form/form-to [:post (str url-prefix "add-word-to-blacklist")]
                [:table {:style "border-collapse: separate; border-spacing: 10px;"}
                    [:tr [:td [:div {:class "_label _label-primary"} "New word:"]]
                         [:td "&nbsp;"]
                         [:td (form/text-field {:size "30"} "new-word")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Description (optional):"]]
                         [:td "&nbsp;"]
                         [:td (form/text-field {:size "50"} "description")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Class:"]]
                         [:td "&nbsp;"]
                         [:td (form/drop-down "class" ["N/A" "Noun" "Verb" "Adjective" "Adverb" "Pronoun" "Preposition" "Conjunction" "Determiner" "Exclamation"])]]
                    [:tr [:td [:div {:class "_label _label-default"} "Internal:"]]
                         [:td "&nbsp;"]
                         [:td (form/check-box "internal")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Copyright:"]]
                         [:td "&nbsp;"]
                         [:td (form/check-box "copyright")]]
                   ;[:tr [:td [:div {:class "_label _label-default"} "Source:"]]
                   ;     [:td "&nbsp;"]
                   ;     [:td (form/drop-down "source" (map (fn [i] [(:source i)(:id i)]) sources) )]]
                    [:tr [:td "&nbsp;"]
                         [:td "&nbsp;"]
                         [:td (form/submit-button {:class "btn btn-primary"} "Add new word")]]]
                [:br])
        [:br]])

(defn render-front-page-for-atomic-typos
    [word url-prefix emender-page mode]
    [:div {:class "container-fluid"}
        [:div {:class "row"}
            "The CCS Custom Dictionary Atomic Typos &ndash; allows you to add and remove words that are normally correct but should be reported as errors in Red Hat documentation by Emender (" [:a {:href emender-page} "see Emender page" ] ")"
        ]
        [:br]
        [:div {:class "row"}
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "all-words-in-atomic-typos") :class "navbar-brand"} "All words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "active-words-in-atomic-typos") :class "navbar-brand"} "Active words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "deleted-words-in-atomic-typos") :class "navbar-brand"} "Deleted words"]
            ] ; ./navbar-header
            [:div {:style "width:80%"}
                (render-search-field word url-prefix mode)
            ]
        ]
        [:br]
        [:br]
        (form/form-to [:post (str url-prefix "add-word-to-atomic-typos")]
                [:table {:style "border-collapse: separate; border-spacing: 10px;"}
                    [:tr [:td [:div {:class "_label _label-primary"} "Word with typo(s):"]]
                         [:td "&nbsp;"]
                         [:td (form/text-field {:size "30"} "new-word")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Correct form:"]]
                         [:td "&nbsp;"]
                         [:td (form/text-field {:size "30"} "correct-word")]]
                    [:tr [:td "&nbsp;"]
                         [:td "&nbsp;"]
                         [:td (form/submit-button {:class "btn btn-primary"} "Add new word")]]]
                [:br])
        [:br]])

(defn render-front-page-for-glossary
    [word url-prefix emender-page mode sources]
    [:div {:class "container-fluid"}
        [:div {:class "row"}
            "Glossary"
        ]
        [:br]
        [:div {:class "row"}
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "all-words-in-glossary") :class "navbar-brand"} "All words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "active-words-in-glossary") :class "navbar-brand"} "Active words"]
            ] ; ./navbar-header
            [:div {:class "navbar-header"}
                [:a {:href (str url-prefix "deleted-words-in-glossary") :class "navbar-brand"} "Deleted words"]
            ] ; ./navbar-header
            [:div {:style "width:80%"}
                (render-search-field word url-prefix mode)
            ]
        ]
        [:br]
        [:br]
        (form/form-to [:post (str url-prefix "add-word-to-glossary")]
                [:table {:style "border-collapse: separate; border-spacing: 10px;"}
                    [:tr [:td [:div {:class "_label _label-primary"} "New word:"]]
                         [:td "&nbsp;"]
                         [:td (form/text-field {:size "30"} "new-word")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Description (optional):"]]
                         [:td "&nbsp;"]
                         [:td (form/text-field {:size "50"} "description")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Class:"]]
                         [:td "&nbsp;"]
                         [:td (form/drop-down "class" ["N/A" "Noun" "Verb" "Adjective" "Adverb" "Pronoun" "Preposition" "Conjunction" "Determiner" "Exclamation"])]]
                    [:tr [:td [:div {:class "_label _label-default"} "Internal:"]]
                         [:td "&nbsp;"]
                         [:td (form/check-box "internal")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Copyright:"]]
                         [:td "&nbsp;"]
                         [:td (form/check-box "copyright")]]
                    [:tr [:td [:div {:class "_label _label-default"} "Source:"]]
                         [:td "&nbsp;"]
                         [:td (form/drop-down "source" (map (fn [i] [(:source i)(:id i)]) sources) )]]
                    [:tr [:td "&nbsp;"]
                         [:td "&nbsp;"]
                         [:td (form/submit-button {:class "btn btn-primary"} "Add new word")]]]
                [:br])
        [:br]])

(defn yes-no-with-caution
    [key-name search-results]
    (condp = (get search-results key-name)
        1 "yes"
        0 "no"
        2 "with caution"))

(defn handle-null
    [key-name search-results]
    (or (get search-results key-name) "N/A"))

(defn render-front-page
    "Render front page of this application."
    [word user-name search-results sources message url-prefix title emender-page mode]
    (println user-name)
    (println url-prefix)
    (println title)
    (println mode)
    (page/xhtml
        (render-html-header word url-prefix title)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section user-name url-prefix title mode)
            (case mode
                :whitelist    (render-front-page-for-whitelist word url-prefix emender-page mode)
                :blacklist    (render-front-page-for-blacklist word url-prefix emender-page mode)
                :atomic-typos (render-front-page-for-atomic-typos word url-prefix emender-page mode)
                :glossary     (render-front-page-for-glossary word url-prefix emender-page mode sources)
                )
                
                (if message
                    [:div {:class "label label-warning"} message ]
                )
                (if search-results
                    [:table {:class "table table-stripped table-hover" :style "width:auto"}
                        [:tr [:th "Word"]
                             (if (= mode :atomic-typos)
                                 [:th "Correct form(s)"])
                             [:th "Added at"]
                             [:th "Added by"]
                             (if (= mode :blacklist)
                                 [:th "Description"])
                             [:th "Class"]
                             [:th "Use it"]
                             [:th "Internal"]
                             [:th "Copyright"]
                             [:th "Source"]
                             [:th "Status"]
                             [:th "Operation"]]
                        (for [search-result search-results]
                            (let [deleted (= (:deleted search-result) 1)
                                  word    (:word search-result)]
                                [:tr [:td word]
                                     (if (= mode :atomic-typos)
                                         [:td (:correct search-result)])
                                     [:td (:datetime search-result)]
                                     [:td (user-href (:user search-result) mode)]
                                     (if (= mode :blacklist)
                                         [:td (:description search-result)])
                                     [:td (handle-null :class search-result)]
                                     [:td (yes-no-with-caution :use search-result)]
                                     [:td (handle-null :internal search-result)]
                                     [:td (handle-null :copyright search-result)]
                                     [:td (handle-null :source search-result)]
                                     [:td {:style (if deleted "color:red" "color:green")} (if deleted "deleted" "active")]
                                     [:td (if deleted [:a {:href (str "?undelete=" word) :class "btn btn-default"} "undelete"]
                                                      [:a {:href (str "?delete="   word) :class "btn btn-default"}  "delete"])]
                                ]))
                    ])
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

(defn dictionary-name
    [dictionary-type]
    (condp = dictionary-type
        "w" "whitelist"
        "b" "blacklist"
        "a" "atomic typos"
        "g" "glossary"
        "u" "glossary"
            "unknown"))

(defn render-users
    "Render page containing list of all users."
    [user-name statistic changes url-prefix title mode]
    (page/xhtml
        (render-html-header nil url-prefix title)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section user-name url-prefix title mode)

                [:table {:class "table table-stripped table-hover" :style "width:auto"}
                    [:tr [:th "User name"]
                         [:th "Changes made"]]
                    (for [stat statistic]
                        [:tr [:td (user-href (:user stat) mode)]
                             [:td (:cnt stat)]]
                    )
                ] ; </table>
                [:br]
                [:table {:class "table table-stripped table-hover" :style "width:auto"}
                    [:tr [:th "Word"]
                         [:th "Dictionary"]
                         [:th "Added at"]
                         [:th "Added by"]]
                    (for [change changes]
                        [:tr [:td (:word change)]
                             [:td (dictionary-name (:dictionary change))]
                             [:td (:datetime change)]
                             [:td (user-href (:user change) mode)]])
                ] ; </table>
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

(defn render-user-info
    "Render page containing info about one selected user."
    [user-name changes url-prefix title mode]
    (page/xhtml
        (render-html-header nil url-prefix title)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section user-name url-prefix title mode)
                [:h1 (str "Changes made by " user-name)]

                [:br]
                [:table {:class "table table-stripped table-hover" :style "width:auto"}
                    [:tr [:th "Word"]
                         [:th "Dictionary"]
                         [:th "Added at"]]
                    (for [change changes]
                        [:tr [:td (:word change)]
                             [:td (dictionary-name (:dictionary change))]
                             [:td (:datetime change)]])
                ] ; </table>
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

