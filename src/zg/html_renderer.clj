(ns zg.html-renderer)

(require '[hiccup.core            :as hiccup])
(require '[hiccup.page            :as page])
(require '[hiccup.form            :as form])

(defn render-html-header
    "Renders part of HTML page - the header."
    [package]
    [:head
        [:title "zg" package]
        [:meta {:name "Author"    :content "Pavel Tisnovsky"}]
        [:meta {:name "Generator" :content "Clojure"}]
        [:meta {:http-equiv "Content-type" :content "text/html; charset=utf-8"}]
        ;(page/include-css "http://torment.usersys.redhat.com/openjdk/style.css")]
        (page/include-css "bootstrap.min.css")
        (page/include-css "smearch.css")
        (page/include-js  "bootstrap.min.js")
    ] ; head
)

(defn render-html-footer
    "Renders part of HTML page - the footer."
    []
    [:div "<br /><br /><br /><br />Author: Pavel Tisnovsky &lt;<a href='mailto:ptisnovs@redhat.com'>ptisnovs@redhat.com</a>&gt"])

(defn render-search-field
    "Renders search box on the top side of HTML page."
    [word]
    (form/form-to {:class "navbar-form navbar-left" :role "search"} [:get "/" ]
        [:div {:class "input-group"}
            [:span {:class "input-group-addon"} "Search"]
            (form/text-field {:size "40" :class "form-control" :placeholder "Search for word"} "word" (str word))
            [:div {:class "input-group-btn"}
                (form/submit-button {:class "btn btn-default"} "Search")]]))

(defn render-name-field
    "Renders box for typing user name on the top side of HTML page."
    [user-name]
    (form/form-to {:class "navbar-form navbar-left" :role "search"} [:get "/" ]
        [:div {:class "input-group"}
            ;[:span {:class "input-group-addon"} "Name"]
            (form/text-field {:size "10" :class "form-control" :placeholder "User name"} "user-name" (str user-name))
            [:div {:class "input-group-btn"}
                (form/submit-button {:class "btn btn-default"} "Remember me")]]))

(defn render-navigation-bar-section
    "Renders whole navigation bar."
    [word user-name]
    [:nav {:class "navbar navbar-inverse navbar-fixed-top" :role "navigation"}
        [:div {:class "container-fluid"}
            [:div {:class "row"}
                [:div {:class "col-md-1"}
                    [:div {:class "navbar-header"}
                        [:a {:href "/" :class "navbar-brand"} "zg"]
                    ] ; ./navbar-header
                ] ; col ends
                [:div {:class "col-md-3"}
                    (render-search-field word)
                ] ; col ends
                [:div {:class "col-md-3"}
                    [:div {:class "navbar-header"}
                        [:a {:href "/all-words" :class "navbar-brand"} "All words"]
                    ] ; ./navbar-header
                    [:div {:class "navbar-header"}
                        [:a {:href "/active-words" :class "navbar-brand"} "Active words"]
                    ] ; ./navbar-header
                    [:div {:class "navbar-header"}
                        [:a {:href "/deleted-words" :class "navbar-brand"} "Deleted words"]
                    ] ; ./navbar-header
                ] ; col ends
                [:div {:class "col-md-3"}
                    (render-name-field user-name)
                ]
                [:div {:class "col-md-1"}
                    [:div {:class "navbar-header"}
                        [:a {:href "/users" :class "navbar-brand"} "Users"]
                    ] ; ./navbar-header
                ] ; col ends
            ] ; row ends
        ] ; /.container-fluid
]); </nav>

(defn render-error-page
    "Render error page with a 'back' button."
    [word user-name message]
    (page/xhtml
        (render-html-header "")
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section word user-name)
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
    [word user-name search-results message url-prefix]
    (page/xhtml
        (render-html-header word)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section word user-name)

                (form/form-to [:post "/add-words"]
                        [:div {:class "label label-primary"} "New words"]
                        "&nbsp;"
                        [:div {:class "label label-warning"} "(please use spaces or commas as separators)"]
                        [:br]
                        (form/text-area {:cols "120" :rows "8"} "new-words")
                        [:br]
                        (form/submit-button {:class "btn btn-danger"} "Add new words")
                        [:br]
                    )

                        [:br]
                        [:br]
                (if message
                    [:div {:class "label label-warning"} message ]
                )
                (if search-results
                    [:table {:class "table table-stripped table-hover" :style "width:auto"}
                        [:tr [:th "Word"]
                             [:th "Added at"]
                             [:th "Added by"]
                             [:th "Status"]
                             [:th "Operation"]]
                        (for [search-result search-results]
                            (let [deleted (= (:deleted search-result) 1)
                                  word    (:word search-result)]
                                [:tr [:td word]
                                     [:td (:datetime search-result)]
                                     [:td (:user search-result)]
                                     [:td (if deleted "deleted" "active")]
                                     [:td (if deleted [:a {:href (str "?undelete=" word) :class "btn btn-success"} "undelete"]
                                                      [:a {:href (str "?delete=" word)   :class "btn btn-danger"}  "delete"])]
                                ]))
                    ])
                (render-html-footer)
            ] ; </div class="container">
        ] ; </body>
    ))

(defn render-users
    [user-name statistic changes url-prefix]
    (page/xhtml
        (render-html-header nil)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section nil user-name)

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
    [user-name changes url-prefix]
    (page/xhtml
        (render-html-header nil)
        [:body
            [:div {:class "container"}
                (render-navigation-bar-section nil user-name)
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

