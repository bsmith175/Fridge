// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */

//user profile put in global scope
let userProfile = undefined;
let favorites = [];
let restriction_toggles = [];
let meats = false;
let dairy = false;
let nuts = false;
let pantryItems = [];
let pantry = $("#pantry-item");
let suggestions = [];
let user_name = $("#user-name");
let current_response = [];
let next = 3;

/**
 * Exevuted upon page load.
 */
$(document).ready(() => {

    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");
    const fav = $("#display-favs");
    const enjoy = $("#display-enjoy");
    pantry = $("#pantry-item");

    $("#numresults").change(function () {
        console.log(current_response.length)
        make_cards(result_cards, current_response, false, true);

    })


    $(".no-meats").click(function (e) {
        console.log("meats_clicked")
        meats = !meats;
    })
    $(".no-dairy").click(function (e) {
        console.log("dairy_clicked")
        dairy = !dairy;
    })
    $(".no-nuts").click(function (e) {
        console.log("nuts_clicked")
        nuts = !nuts;
    })

    //Find Recipes Button clicked
    /**
     * Click function for add-to-pantry selector.
     * Triggered when pantry form is submitted to
     * add a item to the pantry.
     */
    let pantry_counter = 0;

    $(".add-to-pantry").click(function () {
        console.log("add to pantry")
        let postParameters = "";
        let elements = document.forms["pantry-form"].elements;
        for (let i = 0; i < elements.length; i++) {

            if (elements[i].value != "" && elements[i].value != " ") {
                console.log((elements[i]).value);

                postParameters = elements[i].value;
            }
        }
        if (postParameters != "") {
            $.post('/add-pantry', {text: postParameters, uid: userProfile.getId()}, function (data) {
                data = JSON.parse(data);
                console.log(data)
                //getPantry();
                //displayPantry();

                const s = "<button type=\"button\" id=\"" + pantry_counter + "\" name=\"" + postParameters +
                    "\" class=\"btn btn-lg btn-outline-info remove-pantry\" " +
                    "onclick='remove_pantry(this.id)'>\n"
                    + postParameters
                    + " <span class=\"badge badge-light\">x</span>\n"
                    + "                        </button>";
                pantry.append(s);
                pantryItems.push(postParameters);
                console.log(pantryItems);


            });
            pantry_counter = pantry_counter + 1;
        }
        document.forms["pantry-form"].reset();
        // sessionStorage.setItem("pantry", pantryItems);

        return false;

    })

    /**
     * Adds all the items in pantryItems to pantry selector.
     * Uses timeout function to make sure that if getPantry is called before,
     * displayPantry will be called after it finishes setting pantryItems.
     */
    function displayPantry() {
        setTimeout(function () {
            pantry.empty();
            console.log(pantryItems.length);

            for (let i = 0; i < pantryItems.length; i++) {

                const s = "<button type=\"button\" id=\"" + pantry_counter + "\" name=\"" + pantryItems[i] +
                    "\" class=\"btn btn-lg btn-outline-info remove-pantry\" " +
                    "onclick='remove_pantry(this.id,setSessionPantry)'>\n"
                    + pantryItems[i]
                    + " <span class=\"badge badge-light\">x</span>\n"
                    + "                        </button>";
                pantry.append(s);
                pantry_counter = pantry_counter + 1;

            }
        }, 400);
    }

    /**
     * Click function for find-recipe selector.
     * When fridge-from submitted, gets all the inputs from form and sends them to backend
     * to get result recipes. Recipes are then made into cards and displayed.
     */
    $(".find-recipe").click(function (e) {
        console.log("find recipe")
        $("#cookSpinner").css("display", "inline-block");

        result_cards.empty();
        e.preventDefault();
        //add inputs to postParameters
        let postParameters = [];
        let elements = document.forms["fridge-form"].elements;

        console.log(elements);
        for (let i = 0; i < elements.length; i++) {
            if (elements[i].value != "") {
                postParameters.push(elements[i].value);
            }
        }
        postParameters = postParameters.concat(pantryItems);
        console.log(postParameters)
        if (postParameters.length !== 0) {
            $.post("/recipe-recommend", $.param({
                text: postParameters,
                meats: meats,
                nuts: nuts,
                dairy: dairy
            }, true), response => {
                //parse response
                if (response === "none") {
                    $("#cookSpinner").css("display", "none");
                } else {
                    const r = JSON.parse(response);
                    make_cards(result_cards, r, false, true);
                    current_response = r;
                    $("#cookSpinner").css("display", "none");
                }
            });
        } else {
            $("#cookSpinner").css("display", "none");
        }

    });


    /**
     * Attach createTypeahead to first fridge-form input element.
     * Attached for rest of input when inputs are made.
     */
    createTypeahead($('.typeahead'));

    /**
     * Click handler for favorites tab in profile page.
     * Displays favorites.
     */
    $('#myTab a[href="#favorites"]').on('click', function (e) {
        $("#new-suggest").css("visibility", "hidden");

        if (favorites.length == 0) {
            $('.favorite-explanation').text("You haven't added any favorites yet!");
        } else {
            $('.favorite-explanation').text("Your favorites!");
        }

        e.preventDefault()
        profilePage(fav, [...favorites], true);
        //$(this).tab('show')
    })
    /**
     * Click handler for enjoy tab in profile page.
     * Displays suggested recipes based on favorites.
     */
    $('#myTab a[href="#enjoy"]').on('click', function (e) {
        let newButton = $("#new-suggest");
        newButton.css("visibility", "visible");
        $("#enjoySpinner").css("display", "inline-block");

        if (typeof userProfile === 'undefined') {
            $("#enjoySpinner").css("display", "none");

            $('.enjoy-explanation').text("Login to see recommended recipes!");
            profilePage(enjoy, suggestions, false)

        } else if (suggestions === null || !suggestions.length) {

            $('.enjoy-explanation').text("Add more Favorites so that we can recommend you some recipes!");
            enjoy.empty();
            let callback = function () {
                e.preventDefault();
                console.log("should have removed");
                $("#enjoySpinner").css("display", "none");
                profilePage(enjoy, suggestions, false)
            }
            getSuggestions(callback);
        } else {
            e.preventDefault();
            console.log("should have removed");
            $("#enjoySpinner").css("display", "none");
            profilePage(enjoy, suggestions, false)
        }

    })


    /**
     * Click handler for pantry tab in profile page.
     * Displays items stored in pantry.
     */
    $('#myTab a[href="#pantry"]').on('click', function (e) {
        e.preventDefault();
        $("#new-suggest").css("visibility", "hidden");
        console.log("pantry tab clicked");
        console.log(pantryItems);
        displayPantry();

    });


    /**
     * Makes Bootstrap cards out of recipe data and appends them to selector
     * @param selector jquery selector to append cards too
     * @param results array with recipes
     */
    function make_cards(selector, results, favBool, limitBool) {
        console.log(results);
        let limit = results.length
        if (limitBool) {
            selector.empty()
            limit = document.getElementById("numresults").value
        }

        let cards = 0; //html id for each recipe card
        for (let i = 0; i < limit; i++) {
            const res = results[i];
            let heart_shape = "fa-heart-o";
            let length = favorites.length;

            for (let index = 0; index < length; index++) {
                if (favorites[index].id == res.id) {
                    heart_shape = "fa-heart";
                }
            }

            //html/bootstrap card for each recipe
            const card = "<div id=" + cards + " class=\"col-md-4 col-sm-6 mb-4 d-flex\">\n" +
                "<div class=\"card card-body flex-fill\" style=\"width: 20rem;\">\n" +
                "  <div class=\"d-flex flex-row-reverse\">\n" +
                "<div>\n" +
                "  <i id=" + cards + " class=\"heart fa " + heart_shape + "\"></i>\n" +
                "</div> </div>" +
                "  <img class=\"card-img-top\" style = \"border: 1px green\"src=" + res.imageURL +
                " alt=\"Card image cap\">\n" +
                "  <div class=\"card-body\">\n" +
                "    <h5 class=\"card-title\">" + res.name + "</h5>\n" +
                "    <p class=\"card-text\">" + res.description + "</p>\n" +
                "    <button id=" + cards + " type=\"button\" class=\"btn btn-outline-success openBtn\" " +
                "data-toggle=\"modal\" data-target=\"#myModal\">View Recipe</button>\n" +
                "  </div>\n" +
                "  </div>\n" +
                "</div>";
            //add card to result_card selector.
            selector.append(card);
            cards = cards + 1;
        }

        /**
         * Click handler to view a recipe in modal.
         */
        $(".openBtn").click(function (e) {
            modal_title.empty();
            e.preventDefault();
            //get selected recipe by getting id
            const id = (e.target.id);
            //get recipe by indexing into r array
            const result = results[id];
            console.log(result);

            let ingredients = "";
            let instructions = "";
            //parse ingredients into html
            for (let ing of JSON.parse(result.ingredients)) {
                ingredients = ingredients + "<li>" + ing + "</li>"
            }
            //parse method into html
            for (let des of JSON.parse(result.method)) {
                instructions = instructions + "<li>" + des + "</li>"
            }
            let time = (JSON.parse(result.time)[0]).cook;
            console.log(time);
            let min = "";
            let hrs = "";
            if (time.mins !== null) {
                min = time.mins;
            }
            if (time.hrs !== null) {
                hrs = time.hrs;
            }
            console.log(min);
            console.log(hrs);

            //add recipe to modal by appending html to modal classes
            $('#recipe-title').html("<h1>" + result.name + "</h1>");
            $('#recipe-description').html("<p>\"" + result.description + "\"</p>");
            $('.ingredients-list').html(ingredients);
            $('.instructions').html(instructions);
            if (hrs !== "" || min !== "") {
                console.log("add time");
                $('.cook-time').empty();
                $('.cook-time').html("<img src=\"https://i.ibb.co/SxT1Qpw/recipe-clock.png\" " +
                    "alt=\"clock\" height=\"40\" width=\"40\">\n" +
                    "<h4>CookTime " + hrs + " " + min + "</h4>\n ");
            }
            $('.servings').html("<h4>" + "<img src=\"https://i.ibb.co/NmJwjjv/servings.png\" alt=\"servings\" height=\"40\" width=\"55\">" + result.servings + "  </h4>");


        });
        /**
         * click handler for heart button.
         * Adds/removes the recipe from favorites, POSTS change to backend
         * and toggle's the heart from/to empty/full.
         *
         */
        $(".heart.fa").click(function (event) {
            if (sessionStorage.getItem("signedin") !== "true") {

                alert("Please sign in to favorite recipes!");
                console.log("else didn't work");

            } else {
                //get recipe that was liked
                const field_id = (event.target.id);
                const recipe = results[field_id];
                const id = recipe.id;
                //craft post parameters
                const postParameters = {
                    recipe_id: id,
                    user_id: userProfile.getId()
                };
                if (favorites.includes(recipe)) {
                    console.log("Already in favorites");
                    console.log(recipe.id);
                    for (let i = 0; i < favorites.length; i++) {
                        if (favorites[i].id === recipe.id) {
                            favorites.splice(i, 1);
                        }
                    }
                    if (favBool) {
                        selector.find("#" + field_id).remove();
                    }
                } else {
                    console.log("Adding to favorites");
                    favorites.push(recipe);
                }
                sessionStorage.setItem("favorites", JSON.stringify(favorites));

                $(this).toggleClass("fa-heart fa-heart-o");
                $.post("/heart", postParameters, response => {
                    console.log(response);
                });

            }
        })
            .error(err => {
                console.log("in the .error callback");
                console.log(err);
            });
    }


    /**
     *
     * @param e
     * @param results
     */
    function profilePage(e, results, favBool) {
        console.log("profile");
        e.empty();
        make_cards(e, results, favBool, false);
    }

    $("#new-suggest").on("click", function () {
        if (typeof userProfile !== 'undefined') {
            let newButton = $("#new-suggest");
            let spinner = $("#loadSpinner");
            $("#loaderText").text("Loading...");
            spinner.css("display", "inline-block");
            suggestions = [];
            let load = function () {
                console.log(suggestions);
                profilePage(enjoy, suggestions, false)
                spinner.css("display", "none");
                $("#loaderText").html("Suggest new recipes!");

            }
            getSuggestions(load);
        } else {
            $('.enjoy-explanation').text("Login to see recommended recipes!");

            profilePage(enjoy, suggestions, false);
        }
    });


});


/**
 * Gets favorites from backend and sets favorite array
 *
 */
function getFavs() {
    console.log("Getting favorites and setting storage");
    favorites.length = 0;
    console.log(userProfile.getId());
    $.post("/favorites", {"uid": userProfile.getId()}, response => {
        if (response !== "error") {
            const r = JSON.parse(response);
            console.log(r);
            sessionStorage.setItem("favorites", response);
            for (let res of r) {
                favorites.push(res);
            }
            console.log(favorites);
        }
    });
}

/**
 * Click function for add-more selector.
 * When + button clicked in recipe form, dynamically adds
 * input boxes and -/+ buttons.
 */
function add_more() {
    console.log("add")
    next = next + 1;
    let newInput = $('<input  placeholder="Type Ingredient..." class="typeahead ' +
        'form-control type\"  name="field' + next +
        '" type="text" autocomplete="off"/>');
    createTypeahead(newInput);

    let removeBtn = $('<button id="' + (next) +
        '" class="btn remove-me" onclick=\"remove_me(this.id)\">Remove</button>');

    //let add = $("<button id=\""+next+"\" class=\"btn add-more\" onclick=\"add_more(this.id)\" type=\"button\">+ Add Ingredient</button>\n");

    let str = $("<div class=\"input-group\"  id=\"field" + next +
        "\" name=\"field" + next + "\"></div>");
    str.append(newInput);
    str.append(removeBtn);
    let divs = $(".ingredients").children();
    console.log(next);
    console.log(divs[divs.length - 1].id);
    let pre_last = $("#" + divs[divs.length - 1].id);
    //pre_last.find("button").remove();
    //pre_last.append(removeBtn);
    //pre_last.insertAfter(str);
    $("#modify-buttons").before(str);
    //$(".ingredients").append(str);


}

/**
 * Click function for remove-me selector.
 * When - button clicked in recipe form, dynamically removes
 * input boxes and - button.
 */
function remove_me(id) {
    console.log("remove")
    let fieldID = "#field" + id;
    //need to Keep both(last one needs to be clicked twice??)
    console.log(fieldID);
    $(fieldID).remove();
    return false;
};

/**
 * Removes a pantry item.
 *
 * @param {*} clicked_id id of ingrdient clicked
 */
function remove_pantry(clicked_id, callback) {
    console.log("removing item from pantry");
    console.log(clicked_id);
    console.log(pantryItems);
    let postParameters = $('#' + clicked_id).attr('name');
    console.log(postParameters);
    $.post('/remove-pantry', {text: postParameters, uid: userProfile.getId()}, function (data) {
        data = JSON.parse(data);
        console.log(data);
    })
    pantry.find("#" + clicked_id).remove();
    for (let i = 0; i < pantryItems.length; i++) {
        if (pantryItems[i] === postParameters) {
            pantryItems.splice(i, 1);
        }
    }
    console.log(pantryItems);
    if (typeof callback !== 'undefined') {
        callback();
    }


}

function setSessionPantry() {
    // sessionStorage.setItem("pantry", pantryItems);
}


/**
 * Gets pantry items and stores them in pantryItems.
 *
 */
function getPantry() {
    console.log("getting pantry and setting in storage");
    console.log(userProfile.getId());
    pantryItems.length = 0;
    const params = {
        uid: userProfile.getId(),
    };
    $.post("/pantry", params, response => {

        const r = JSON.parse(response);


        sessionStorage.setItem("pantry", response);

        for (let res of r) {
            pantryItems.push(res);
        }

    });

}

/**
 * Attaches typeahead function to selector to allow autofill.
 * Gets autofill data from /suggest
 * @param $els jquery selector for an input
 */
function createTypeahead($els) {
    $els.typeahead({
        source: function (query, process) {
            return $.post('/suggest', {input: query}, function (data) {
                data = JSON.parse(data);
                return process(data);
            });
        }
    });
}

function getSuggestions(callback) {
    console.log("getting suggested recipes and setting in storage");
    $.post("/suggested-recipes", {
        "uid": userProfile.getId()
    }, response => {
        if (response !== "error") {
            sessionStorage.setItem("suggestions", response);
            suggestions = JSON.parse(response);
            console.log(suggestions);
            if (typeof callback !== 'undefined') {
                callback();
            }
        }
    });

}

/**
 * Function called by google sign in to sign a user in.
 * SessionStorage and loginData are configured.
 * @param googleUser
 */
function onSignIn(googleUser) {
    // Store userprofile in global variable
    userProfile = googleUser.getBasicProfile();
    var id_token = googleUser.getAuthResponse().id_token;
    const loginData = {
        idToken: id_token,
    };

    $.post("/login", loginData, function (response) {
        const responseData = JSON.parse(response);
        if (responseData.success) {
            $("#user-name").text("Welcome, " + userProfile.getGivenName() + "!");
            $(".replace-image").empty();
            $(".replace-image").append("<img class=\"profile-pic rounded-circle\"\n" +
                "                     src=\"" + userProfile.getImageUrl() + "\"\n" +
                "                     alt=\"Card image cap\" height=\"160\" width=\"160\">");
            console.log(sessionStorage.getItem("signedin"));

            if (sessionStorage.getItem("signedin") !== "true") {

                sessionStorage.setItem("signedin", "true");
                console.log("Signing in new");
                user_name.text("Welcome, " + userProfile.getGivenName() + "!");


                // Performs page specific actions after user has signed in
                getFavs();
                console.log(suggestions);
                getPantry();

            } else {
                console.log("Already signed in");

                favorites = JSON.parse(sessionStorage.getItem("favorites"));
                if (favorites === null || favorites.length === 0) {
                    favorites = [];
                    getFavs();
                }
                console.log(favorites);

                suggestions = JSON.parse(sessionStorage.getItem("suggestions"));


                //pantryItems = JSON.parse(sessionStorage.getItem("pantry"));
                getPantry();


            }

            $(".g-signin2").hide();
            //Sign out option appears on nav bar
            $('.navbar-nav').find("#sign-out").remove();
            $('.navbar-nav').append("<a class=\"nav-item nav-link nav-main\" id=\"sign-out\" onclick=\"signOut();\">Sign out</a>");

        } else {
            console.log("Error authenticating user");
        }

    });


}

/**
 * Handles sign in errors.
 *
 * @param {*} error
 */
function onFailure(error) {
    console.log(error);
}

/**
 * Sign out the user.
 */
function signOut() {
    console.log("Signing out");
    let auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        // Reset userProfile variable
        userProfile = undefined;
        sessionStorage.clear();
        favorites = [];
        pantryItems = [];
        suggestions = [];
        meats = false;
        nuts = false;
        dairy = false;
        $('.navbar-nav').find("#sign-out").remove();
        $(".replace-image").empty();
        $(".replace-image").append("<img class=\"profile-pic rounded-circle\"\n" +
            "                     src=\"https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg\"\n" +
            "                     alt=\"Card image cap\" height=\"160\" width=\"160\">");


        user_name.text("Please sign in to view profile!");
        $(".g-signin2").show();

    });
}