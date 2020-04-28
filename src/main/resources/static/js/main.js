// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */

//user profile put in global scope
let userProfile = undefined;
let favorites = [];

$(document).ready(() => {

    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");
    const fav = $("#display-favs");
    const pantry = $("#pantry");
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

    //Find Recipes Button clicked
    $(".find-recipe").click(function (e) {
        console.log("find recipe")

        result_cards.empty();
        e.preventDefault();
        //add inputs to postParameters
        const postParameters = [];
        let elements = document.forms["fridge-form"].elements;
        for (let i = 0; i < elements.length; i++) {
            if (elements[i].value != "") {
                postParameters.push(elements[i].value);

            }
        }
        console.log(postParameters)
        $.post("/recipe-recommend", $.param({text: postParameters}, true), response => {
            //parse response
            const r = JSON.parse(response);
            make_cards(result_cards, r, false);


        });

    });

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



    function profilePage() {
        console.log("profile");
        fav.empty();
        make_cards(fav, favorites, false);

    }


    function make_cards(e, r, profile) {
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

            if (localStorage.getItem("signedIn") !== true) {
                alert("Please sign in to favorite recipes!");
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
                favorites.length = 0;
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
    $('.active[data-toggle="tab"]').trigger('click');

    $('#myTab a[href="#favorites"]').on('click', function (e) {
        e.preventDefault()
        profilePage();
        $(this).tab('show')
    })
    $('#myTab a[href="#excluded"]').tab('show');
    $('#myTab a[href="#pantry"]').tab('show');



});

function getFavs() {
    const params = {
        userID: userProfile.getId(),
        name: userProfile.getName(),
        email: userProfile.getEmail(),
        profilePic: userProfile.getImageUrl()
    };
    $.post("/favorites", {"uid": userProfile.getId()}, response => {
        const r = JSON.parse(response);
        for (let res of r) {
            favorites.push(res);
        }
        console.log(favorites);
        //profilePage();

    });
}

function onSignIn(googleUser) {
    // Store userprofile in global variable
    userProfile = googleUser.getBasicProfile();

    localStorage.setItem("signedin", true);
    console.log("signed in is true");
    $("#user-name").text("Welcome, " + userProfile.getGivenName() + "!");

    // Performs page specific actions after user has signed in

    const loginData = {
        uid: userProfile.getId(),
        firstName: userProfile.getName(),
        profilePicture: userProfile.getImageUrl()
    };

    getFavs();
    $.post("/login", loginData, function (response) {

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
    let auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        // Reset userProfile variable
        userProfile = undefined;
        localStorage.setItem("signedIn", false);
        favorites = [];
        $("#user-name").text("Please sign in to view profile!");

    });
}