;
;  (C) Copyright 2016  Pavel Tisnovsky
;
;  All rights reserved. This program and the accompanying materials
;  are made available under the terms of the Eclipse Public License v1.0
;  which accompanies this distribution, and is available at
;  http://www.eclipse.org/legal/epl-v10.html
;
;  Contributors:
;      Pavel Tisnovsky
;

(ns zg.html-renderer)

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

(defn render-search-field
    "Renders search box on the top side of HTML page."
    [word url-prefix]
    (form/form-to {:class "navbar-form navbar-left" :role "search"} [:get url-prefix ]
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
    [active mode]
    (if (= active mode)
    {:class "active"}))

(defn users-href
    [url-prefix mode]
    (if (= mode :whitelist)
        (str url-prefix "users-whitelist")
        (str url-prefix "users-blacklist")))

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
                        ]
                    ]
                ] ; col-md-7 ends
                [:div {:class "col-md-3"}
                    (render-name-field user-name url-prefix)
                ]
                [:div {:class "col-md-2"}
                    [:div {:class "navbar-header"}
                        [:a {:href (users-href url-prefix mode) :class "navbar-brand"} "Users"]
                    ] ; ./navbar-header
                ] ; col ends
            ] ; row ends
        ] ; /.container-fluid
]); </nav>

(defn render-error-page
    "Render error page with a 'back' button."
    [word user-name message url-prefix]
    (page/xhtml
        (render-html-header "" url-prefix nil) ; TODO change
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section user-name url-prefix nil); TODO change
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

(defn render-front-page
    [word user-name search-results message url-prefix title mode]
    (page/xhtml
        (render-html-header word url-prefix title)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section user-name url-prefix title mode)
            (if (= mode :whitelist)
                [:div {:class "container-fluid"}
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
                            (render-search-field word url-prefix)
                        ]
                        ]
                        [:br]
                        [:br]
                        (form/form-to [:post (str url-prefix "add-words-to-whitelist")]
                                [:div {:class "label label-primary"} "New words"]
                                "&nbsp;"
                                [:div {:class "label label-warning"} "(please use spaces or commas as separators)"]
                                [:br]
                                (form/text-area {:cols "80" :rows "8"} "new-words")
                                [:br]
                                (form/submit-button {:class "btn btn-danger"} "Add new words")
                                [:br]
                            )
                    ]
                [:div {:class "container-fluid"}
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
                            (render-search-field word url-prefix)
                        ]
                        ]
                        [:br]
                        [:br]
                        (form/form-to [:post (str url-prefix "add-word-to-blacklist")]
                                [:table {:style "border-collapse: separate; border-spacing: 10px;"}
                                    [:tr [:td [:div {:class "label label-primary"} "New word"]]
                                         [:td "&nbsp;"]
                                         [:td (form/text-field {:size "30"} "new-word")]]
                                    [:tr [:td [:div {:class "label label-default"} "Description (optional)"]]
                                         [:td "&nbsp;"]
                                         [:td (form/text-field {:size "50"} "description")]]
                                    [:tr [:td "&nbsp;"]
                                         [:td "&nbsp;"]
                                         [:td (form/submit-button {:class "btn btn-danger"} "Add new word")]]]
                                [:br]
                    )])
                
                (if message
                    [:div {:class "label label-warning"} message ]
                )
                (if search-results
                    [:table {:class "table table-stripped table-hover" :style "width:auto"}
                        [:tr [:th "Word"]
                             [:th "Added at"]
                             [:th "Added by"]
                             (if true
                                 [:th "Description"])
                             [:th "Status"]
                             [:th "Operation"]]
                        (for [search-result search-results]
                            (let [deleted (= (:deleted search-result) 1)
                                  word    (:word search-result)]
                                [:tr [:td word]
                                     [:td (:datetime search-result)]
                                     [:td (:user search-result)]
                                     (if true
                                         [:td (:description search-result)])
                                     [:td (if deleted "deleted" "active")]
                                     [:td (if deleted [:a {:href (str "?undelete=" word) :class "btn btn-success"} "undelete"]
                                                      [:a {:href (str "?delete="   word) :class "btn btn-danger"}  "delete"])]
                                ]))
                    ])
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

(defn render-users
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
                        [:tr [:td [:a {:href (str "user?name=" (:user stat))} (:user stat)]]
                             [:td (:cnt stat)]]
                    )
                ]
                [:br]
                [:table {:class "table table-stripped table-hover" :style "width:auto"}
                    [:tr [:th "Word"]
                         [:th "Added at"]
                         [:th "Added by"]]
                    (for [change changes]
                        [:tr [:td (:word change)]
                             [:td (:datetime change)]
                             [:td [:a {:href (str "user?name=" (:user change))} (:user change)]]])
                ]
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

(defn render-user-info
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
                         [:th "Added at"]]
                    (for [change changes]
                        [:tr [:td (:word change)]
                             [:td (:datetime change)]])
                ]
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

