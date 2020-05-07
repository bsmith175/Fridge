let pantry_counter = 0;

$(document).ready(() => {
    const fav = $("#display-favs");
    const enjoy = $("#display-enjoy");
    pantry = $("#pantry-item");
    initProfilePage(fav, enjoy);

});


/**
 *
 * @param e
 * @param results
 */
function profilePage(e, results, favBool) {
    console.log("profile");
    e.empty();
    make_cards(e, results, favBool, false, false);
}

function initProfilePage(fav, enjoy) {
    /**
     * Click function for add-to-pantry selector.
     * Triggered when pantry form is submitted to
     * add a item to the pantry.
     */

    $(".add-to-pantry").click(function () {
        if (typeof userProfile === 'undefined') {
            alert("Sign in to add to your pantry!");
        } else {
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
        }
        return false;

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