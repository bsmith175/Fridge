// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */

//user profile put in global scope
let userProfile = undefined;
let favorites = [];
let pantryItems = [];
let pantry = $("#pantry-item");
let suggestions = [];


$(document).ready(() => {

    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");
    const fav = $("#display-favs");
    const enjoy = $("#display-enjoy")
    pantry = $("#pantry-item");

    const excluded = $("#excluded");
    console.log($(".add-more"));
    favorites.length = 0;


    var next = 1;
    $(".add-more").click(function (e) {
        console.log("add")
        e.preventDefault();
        var addto = "#field" + next;
        var addRemove = "#field" + (next);
        next = next + 1;
        var newIn = '<input  placeholder="Ingredient" class="typeahead form-control type" id="field' + next + '" name="field' + next + '" type="text" autocomplete="off">';
        var newInput = $(newIn);
        createTypeahead(newInput);
        var removeBtn = '<button id="remove' + (next - 1) + '" class="btn remove-me" >-</button></div><div id="field">';
        var removeButton = $(removeBtn);
        $(addto).after(newInput);
        $(addRemove).after(removeButton);
        $("#field" + next).attr('data-source', $(addto).attr('data-source'));
        $("#count").val(next);

        $('.remove-me').click(function (e) {
            console.log("remove")
            e.preventDefault();
            var fieldNum = this.id.charAt(this.id.length - 1);
            var fieldID = "#field" + fieldNum;
            $(this).remove();
            $(fieldID).remove();
        });
    });


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
        return false;

    })

    //Find Recipes Button clicked
    $(".find-recipe").click(function (e) {
        console.log("find recipe")

        result_cards.empty();
        e.preventDefault();
        //add inputs to postParameters
        const postParameters = [];
        let elements = document.forms["fridge-form"].elements;
        console.log(elements);
        for (let i = 0; i < elements.length; i++) {
            if (elements[i].value != "") {
                postParameters.push(elements[i].value);

            }
        }
        console.log(postParameters)
        $.post("/recipe-recommend", $.param({text: postParameters}, true), response => {
            //parse response
            const r = JSON.parse(response);
            make_cards(result_cards, r);


        });

    });


    $('.typeahead').typeahead({
        source: function (query, process) {
            console.log("typeahead");

            return $.post('/suggest', {input: query}, function (data) {
                data = JSON.parse(data);
                console.log(data);
                return process(data);
            });
        }
    });
    createTypeahead($('typeahead'));
    $('.type')


    $('#myTab a[href="#favorites"]').on('click', function (e) {
        e.preventDefault()

        profilePage(fav, favorites);
        //$(this).tab('show')
    })

    $('#myTab a[href="#enjoy"]').on('click', function (e) {
        //getSuggestions();
        profilePage(enjoy, suggestions)
        //$(this).tab('show')
    })

    $('#myTab a[href="#pantry"]').on('click', function (e) {
        e.preventDefault();
        console.log("pantry tab clicked");
        console.log(pantryItems);
        displayPantry();
        // console.log(typeof $(this).tab('show'));
        // $(this).tab('show');

    });

    function displayPantry() {
        setTimeout(function () {
            pantry.empty();
            console.log(pantryItems.length)

            for (let i = 0; i < pantryItems.length; i++) {

                const s = "<button type=\"button\" id=\"" + i + "\" class=\"btn btn-lg btn-outline-info remove-pantry\" onclick='remove_pantry(this.id)'>\n"
                    + pantryItems[i]
                    + "<span class=\"badge badge-light\">x</span>\n"
                    + "                        </button>";
                console.log(s);

                pantry.append(s);
            }
            console.log(pantry);
        }, 1000);
    }


    //$('#myTab a[href="#excluded"]').tab('show');


    function createTypeahead($els) {
        $els.typeahead({
            source: function (query, process) {
                return $.post('/suggest', {input: query}, function (data) {
                    data = JSON.parse(data);
                    console.log(data);
                    return process(data);
                });
            }
        });
    }

    function make_cards(e, r) {
        console.log(r)

        let cards = 0; //html id for each recipe card
        for (let i = 0; i < r.length; i++) {
            const res = r[i];
            let heart_shape = "fa-heart-o";
            let length = favorites.length;

            for (let index = 0; index < length; index++) {
                if (favorites[index].id == res.id) {
                    heart_shape = "fa-heart";
                }
            }

            //html/bootstrap card for each recipe
            const card = "<div class=\"col-sm d-flex\">\n" +
                "<dv class=\"card card-body flex-fill\" style=\"width: 18rem;\">\n" +
                "  <div class=\"d-flex flex-row-reverse\">\n" +
                "<div>\n" +
                "  <i id=" + cards + " class=\"heart fa " + heart_shape + "\"></i>\n" +
                "</div> </div>" +
                "  <img class=\"card-img-top\" style = \"border: 1px green\"src=" + res.imageURL + " alt=\"Card image cap\">\n" +
                "  <div class=\"card-body\">\n" +
                "    <h5 class=\"card-title\">" + res.name + "</h5>\n" +
                "    <p class=\"card-text\">" + res.description + "</p>\n" +
                "    <button id=" + cards + " type=\"button\" class=\"btn btn-outline-success openBtn\" data-toggle=\"modal\" data-target=\".bd-example-modal-lg\">View Recipe</button>\n" +
                "  </div>\n" +
                "  </div>\n" +
                "</div>";
            //add card to result_card selector.
            e.append(card);
            cards = cards + 1;
        }

        //View a recipe button to show recipt details in modal
        $(".openBtn").click(function (e) {
            modal_title.empty();
            e.preventDefault();
            //get selected recipe by getting id
            const id = (e.target.id);
            console.log(id);
            //get recipe by indexing into r array
            const result = r[id];

            var ingredients = "";
            var instructions = "";
            //parse ingredients into html
            for (let ing of JSON.parse(result.ingredients)) {
                ingredients = ingredients + "<li>" + ing + "</li>"
            }
            //parse method into html
            for (let des of JSON.parse(result.method)) {
                instructions = instructions + "<li>" + des + "</li>"
            }
            //add recipe to modal by appending html to modal classes
            $('.modal-title').html("<h1>" + result.name + "</h1>")
            $('.description').html("<p>" + result.description + "</p>")
            $('.ingredients').html(ingredients)
            $('.instructions').html(instructions)
            $('.cook-time').html("<img src=\"data/recipe-clock.png\" alt=\"Flowers in Chania\">\n")


        });
        //like button
        $(".heart.fa").click(function (e) {
            if (sessionStorage.getItem("signedin") != "true") {

                alert("Please sign in to favorite recipes!");
                console.log("else didn't work");

            } else {
                //get recipe that was liked
                const field_id = (e.target.id);
                const recipe = r[field_id];
                const id = recipe.id;
                console.log(id);
                //craft post parameters
                const postParameters = {
                    recipe_id: id,
                    user_id: userProfile.getId()
                };

                $.post("/heart", postParameters, response => {
                    const r = JSON.parse(response);
                    console.log(r);
                    getFavs();
                    console.log(favorites);

                    $(this).toggleClass("fa-heart fa-heart-o");
                });
            }

        })
            .error(err => {
                console.log("in the .error callback");
                console.log(err);
            });

    }

    function profilePage(e, results) {
        console.log("profile");
        e.empty();
        make_cards(e, results, false);

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
    });
}


function onSignIn(googleUser) {
    // Store userprofile in global variable
    userProfile = googleUser.getBasicProfile();
    $("#user-name").text("Welcome, " + userProfile.getGivenName() + "!");

    if (sessionStorage.getItem("signedin") !== "true") {

        sessionStorage.setItem("signedin", "true");
        console.log("Signing in new");

        // Performs page specific actions after user has signed in

        const loginData = {
            uid: userProfile.getId(),
            firstName: userProfile.getName(),
            profilePicture: userProfile.getImageUrl()
        };

        getFavs();
        getSuggestions();
        getPantry();

        $.post("/login", loginData, function (response) {

        });
    } else {
        console.log("Already signed in");
        favorites = JSON.parse(sessionStorage.getItem("favorites"));
        suggestions = JSON.parse(sessionStorage.getItem("suggestions"));
        pantryItems = JSON.parse(sessionStorage.getItem("pantry"));

    }
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
        $("#user-name").text("Please sign in to view profile!");

    });
}