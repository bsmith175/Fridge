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

/**
 * Exevuted upon page load.
 */
$(document).ready(() => {

    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");
    const fav = $("#display-favs");
    const enjoy = $("#display-enjoy");
    pantry = $("#pantry-item");

    let next = 1;
    /**
     * Click function for add-more selector.
     * When + button clicked in recipe form, dynamically adds
     * input boxes and -/+ buttons.
     */
    $(".add-more").click(function (e) {

        console.log("add")
        e.preventDefault();
        next = next + 1;
        let newInput = $('<input  placeholder="Ingredient" class="typeahead ' +
            'form-control type\"  name="field' + next +
            '" type="text" autocomplete="off"/>');
        createTypeahead(newInput);

        let removeBtn = $('<button id="' + (next) +
            '" class="btn remove-me" >-</button>');


        let str = $("<div class=\"input-group\"  id=\"field" + next +
            "\" name=\"field" + next + "\"></div>");
        str.append(newInput);
        str.append(removeBtn);
        $(".ingredients").append(str);

        $('.remove-me').click(function (e) {
            console.log("remove")
            e.preventDefault();
            let fieldID = "#field" + this.id;
            //need to Keep both(last one needs to be clicked twice??)
            $(fieldID).remove();
            $(fieldID).remove();
        });
    });

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
    $(".add-to-pantry").click(function (e) {
        console.log("add to pantry")
        let postParameters = "";
        let elements = document.forms["pantry-form"].elements;
        for (let i = 0; i < elements.length; i++) {
            if (elements[i].value != "") {
                postParameters = elements[i].value;
            }
        }
        $.post('/add-pantry', {text: postParameters, uid: userProfile.getId()}, function (data) {
            data = JSON.parse(data);
            getPantry();
            displayPantry();

        })
        document.forms["pantry-form"].reset();
        return false;

    })
    /**
     * Click function for find-recipe selector.
     * When fridge-from submitted, gets all the inputs from form and sends them to backend
     * to get result recipes. Recipes are then made into cards and displayed.
     */
    $(".find-recipe").click(function (e) {
        console.log("find recipe")

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
        $.post("/recipe-recommend", $.param({text: postParameters, meats: meats, nuts: nuts, dairy: dairy}, true), response => {
            //parse response
            const r = JSON.parse(response);
            make_cards(result_cards, r, false);

        });

    });

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
        if (favorites.length == 0) {
            $('.favorite-explanation').text("You haven't added any favorites yet!");
        } else {
            $('.favorite-explanation').text("Your favorites!");
        }

        e.preventDefault()
        profilePage(fav,  [...favorites], true);
        //$(this).tab('show')
    })
    /**
     * Click handler for enjoy tab in profile page.
     * Displays suggested recipes based on favorites.
     */
    $('#myTab a[href="#enjoy"]').on('click', function (e) {

        if (!suggestions.length) {
            $('.enjoy-explanation').text("Add more Favorites so that we can recommend you some recipes!");
            enjoy.empty();
        } else {
            e.preventDefault();
            profilePage(enjoy, suggestions, false)
        }

    })
    /**
     * Click handler for pantry tab in profile page.
     * Displays items stored in pantry.
     */
    $('#myTab a[href="#pantry"]').on('click', function (e) {
        e.preventDefault();
        console.log("pantry tab clicked");
        console.log(pantryItems);
        displayPantry();

    });

    /**
     * Adds all the items in pantryItems to pantry selector.
     * Uses timeout function to make sure that if getPantry is called before,
     * displayPantry will be called after it finishes setting pantryItems.
     */
    function displayPantry() {
        setTimeout(function () {
            pantry.empty();
            console.log(pantryItems.length)

            for (let i = 0; i < pantryItems.length; i++) {

                const s = "<button type=\"button\" id=\"" + i + "\" " +
                    "class=\"btn btn-lg btn-outline-info remove-pantry\" " +
                    "onclick='remove_pantry(this.id)'>\n"
                    + pantryItems[i]
                    + " <span class=\"badge badge-light\">x</span>\n"
                    + "                        </button>";
                pantry.append(s);
            }
        }, 200);
    }


    /**
     * Makes Bootstrap cards out of recipe data and appends them to selector
     * @param selector jquery selector to append cards too
     * @param results array with recipes
     */
    function make_cards(selector, results, favBool) {
        console.log(results)

        let cards = 0; //html id for each recipe card
        for (let i = 0; i < results.length; i++) {
            const res = results[i];
            let heart_shape = "fa-heart-o";
            let length = favorites.length;

            for (let index = 0; index < length; index++) {
                if (favorites[index].id == res.id) {
                    heart_shape = "fa-heart";
                }
            }

            //html/bootstrap card for each recipe
            const card = "<div id=" + cards + " class=\"col-sm d-flex\">\n" +
                "<dv class=\"card card-body flex-fill\" style=\"width: 18rem;\">\n" +
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
                "data-toggle=\"modal\" data-target=\".bd-example-modal-lg\">View Recipe</button>\n" +
                "  </div>\n" +
                "  </div>\n" +
                "</div>";
            //add card to result_card selector.
            selector.append(card);
            cards = cards + 1;
        }

        //View a recipe button to show recipt details in modal
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
            $('.modal-title').html("<h1>" + result.name + "</h1>");
            $('.description').html("<p>\"" + result.description + "\"</p>");
            $('.ingredients-list').html(ingredients);
            $('.instructions').html(instructions);
            if (hrs !== "" && min !== "") {
                $('.cook-time').html("<h4>CookTime</h4>\n " +
                    "<p class='cook-time'>" + hrs + " " + min + "  </p>");
            }
            $('.servings').html("<p>" + result.servings + "  </p>");


        });
        //like button
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
                    for (var i = 0; i < favorites.length; i++) {
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
                //getFavs();
                //console.log(favorites);


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
        make_cards(e, results, favBool);
    }

});

/**
 * Gets favorites from backend and sets favorite array
 *
 */
function getFavs() {
    console.log("Getting favorites and setting storage");
    favorites.length = 0;
    const params = {
        userID: userProfile.getId(),
        name: userProfile.getName(),
        email: userProfile.getEmail(),
        profilePic: userProfile.getImageUrl()
    };
    $.post("/favorites", {"uid": userProfile.getId()}, response => {
        const r = JSON.parse(response);
        sessionStorage.setItem("favorites", response);
        for (let res of r) {
            favorites.push(res);
        }
        console.log(favorites);

    });
}


/**
 * Removes a pantry item.
 *
 * @param {*} clicked_id id of ingrdient clicked
 */
function remove_pantry(clicked_id) {
    console.log("removing item from pantry");
    console.log(clicked_id);
    console.log(pantryItems);

    let postParameters = pantryItems[clicked_id];
    console.log(postParameters);
    $.post('/remove-pantry', {text: postParameters, uid: userProfile.getId()}, function (data) {
        data = JSON.parse(data);

    })
    pantry.find("#" + clicked_id).remove();
    getPantry();
}

/**
 * Gets pantry items and stores them in pantryItems.
 *
 */
function getPantry() {
    console.log("getting pantry and setting in storage");
    pantryItems.length = 0;
    const params = {
        uid: userProfile.getId(),
    };
    $.post("/pantry", params, response => {
        const r = JSON.parse(response)
        sessionStorage.setItem("pantry", response);

        for (let res of r) {
            pantryItems.push(res);
        }

    });

}

function getSuggestions() {
    console.log("getting suggested recipes and setting in storage");
    $.post("/suggested-recipes", {
        "uid": userProfile.getId()
    }, response => {
        sessionStorage.setItem("suggestions", response);
        suggestions = JSON.parse(response);
        console.log(suggestions);

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

            if (sessionStorage.getItem("signedin") !== "true") {

                sessionStorage.setItem("signedin", "true");
                console.log("Signing in new");
                user_name.text("Welcome, " + userProfile.getGivenName() + "!");


                // Performs page specific actions after user has signed in
                getFavs();
                getSuggestions();
                console.log(suggestions);
                getPantry();

            } else {
                console.log("Already signed in");
                favorites = JSON.parse(sessionStorage.getItem("favorites"));
                suggestions = JSON.parse(sessionStorage.getItem("suggestions"));
                if (suggestions === null) {
                    getSuggestions();
                }
                pantryItems = JSON.parse(sessionStorage.getItem("pantry"));

            }

            $(".g-signin2").hide();
            //Sign out option appears on nav bar
            $('.navbar-nav').find("#sign-out").remove();
            $('.navbar-nav').append("<a class=\"nav-item nav-link\" id=\"sign-out\" onclick=\"signOut();\">Sign out</a>");

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
        sessionStorage.setItem("signedin", false);
        favorites = [];
        pantryItems = [];
        suggestions = [];
        meats=false;
        nuts=false;
        dairy=false;
        $('.navbar-nav').find("#sign-out").remove();
        $(".replace-image").empty();
        $(".replace-image").append("<img class=\"profile-pic rounded-circle\"\n" +
            "                     src=\"https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg\"\n" +
            "                     alt=\"Card image cap\" height=\"160\" width=\"160\">");


        user_name.text("Please sign in to view profile!");
        $(".g-signin2").show();

    });
}
