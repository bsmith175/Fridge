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
const modal_title = $("#modal-title");

/**
 * Executed upon page load.
 */
$(document).ready(() => {

    const result_cards = $("#result-cards");
    console.log(window.location.pathname);
    if (sessionStorage.getItem("results") !== null && window.location.pathname === "/") {
        let response = sessionStorage.getItem("results");
        const r = JSON.parse(response);
        $('#numresults')
            .val(sessionStorage.getItem("numresults"))
            .trigger('change');
        make_cards(result_cards, r, false, true, true)
    }

    $("#numresults").change(function () {
        console.log(current_response.length)
        make_cards(result_cards, current_response, false, true, true);

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
            sessionStorage.removeItem("results");

            $.post("/recipe-recommend", $.param({
                text: postParameters,
                meats: meats,
                nuts: nuts,
                dairy: dairy
            }, true), response => {
                console.log("got response");
                //parse response
                if (response === "none") {
                    console.log("error from backend");
                    $("#cookSpinner").css("display", "none");
                } else {
                    const r = JSON.parse(response);
                    console.log(r.length);
                    if (r.length === 0) {
                        alert("No results found");
                        $("#cookSpinner").css("display", "none");
                    } else {
                        //cache results and numresults
                        sessionStorage.setItem("results", response);
                        sessionStorage.setItem("numresults", document.getElementById("numresults").value);
                        make_cards(result_cards, r, false, true, true);
                        current_response = r;
                        $("#cookSpinner").css("display", "none");
                    }
                }
            });
        } else {
            console.log("no post parameters");
            $("#cookSpinner").css("display", "none");
        }
    });


    /**
     * Attach createTypeahead to first fridge-form input element.
     * Attached for rest of input when inputs are made.
     */
    createTypeahead($('.typeahead'));

});

/**
 * Calculates a color from green to red based on a percentage.
 * @param perc percentage
 * @returns {string} color
 */
function perc2color(perc) {
    var r, g, b = 0;
    if(perc < 50) {
        r = 255;
        g = Math.round(5.1 * perc);
    }
    else {
        g = 255;
        r = Math.round(510 - 5.10 * perc);
    }
    var h = r * 0x10000 + g * 0x100 + b * 0x1;
    return '#' + ('000000' + h.toString(16)).slice(-6);
}

/**
 * Makes Bootstrap cards out of recipe data and appends them to selector
 * @param selector jquery selector to append cards too
 * @param results array with recipes
 * @param true only if search is for recipe results
 */
function make_cards(selector, results, favBool, limitBool, resbool) {
    console.log(results);
    let limit = results.length
    if (limitBool) {
        selector.empty()
        limit = Math.min(document.getElementById("numresults").value, results.length);
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
        let similarity = "";
        let val = perc2color(res.percentMatch);
        if (resbool){
            similarity =  "<p> Similarity score: <span class=\"card-text\" style=\"color:"+val+";\">" + "<b>" + res.percentMatch + "</b>" + "</span></p>\n";
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
            similarity +
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



