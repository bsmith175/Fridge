// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */


$(document).ready(() => {

    var favorites = [];
    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");
    const fav = $("#display-favs");
    console.log(result_cards);
    console.log(fav);
    const pantry = $("#pantry");
    const excluded = $("#excluded");
    function getFavs() {
        $.post("/favorites", {"uid": -1}, response => {
            const r = JSON.parse(response);
            for (let res of r) {
                favorites.push(res);
            }
            console.log(favorites);
            profilePage();

        });

    }



    favorites.length = 0;
    getFavs();
    //TODO: get the jquery selectors for the list where the suggestions should go and the input box where we're typing
    $('#myTab a[href="#favorites"]').on('click', function (e) {
        e.preventDefault()
        profilePage();
        $(this).tab('show')
    })
    // $('#myTab a[href="#favorites"]').tab('show');

    $('#myTab a[href="#excluded"]').tab('show');
   $('#myTab a[href="#pantry"]').tab('show');


    function profilePage() {
        console.log("profile");
        console.log(favorites);
        fav.empty();
        make_cards(fav, favorites, false);
        console.log(fav);

    }


    function make_cards(e, r, profile) {
        if (profile == true){
            r = favorites
            console.log(JSON.stringify(r));
        }

        console.log(r)

        let cards = 0; //html id for each recipe card
        for (let i = 0; i < r.length; i++) {
            const res = r[i];
            let heart_shape = "fa-heart-o";
            let length = favorites.length;
            console.log(r)

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
                "</div>"
            //add card to result_card selector.
            e.append(card);
            console.log(e);
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


        })
        //like button
        $(".heart.fa").click(function (e) {
            //get recipe that was liked
            const field_id = (e.target.id);
            const recipe = r[field_id];
            const id = recipe.id;
            console.log(id);
            //craft post parameters
            const postParameters = {
                recipe_id: id,
                user_id: "-1"
            };

            $.post("/heart", postParameters, response => {
                const r = JSON.parse(response);
                console.log(r);
                favorites.length = 0;
                getFavs();
                console.log(favorites);

                $(this).toggleClass("fa-heart fa-heart-o");
            });

        })
            .error(err => {
                console.log("in the .error callback");
                console.log(err);
            });

    }

    var next = 1;
    $(".add-more").click(function (e) {
        e.preventDefault();
        let addto = "#field" + next;
        let addRemove = "#field" + (next);
        next = next + 1;
        let newIn = '<input  placeholder="Ingredient" class="typeahead form-control type" id="field' + next + '" name="field' + next + '" type="text" autocomplete="off">';

        let newInput = $(newIn);
        createTypeahead(newInput)
        let removeBtn = '<button id="remove' + (next - 1) + '" class="btn remove-me" >-</button></div><div id="field">';
        let removeButton = $(removeBtn);
        $(addto).after(newInput);
        $(addRemove).after(removeButton);
        $("#field" + next).attr('data-source', $(addto).attr('data-source'));
        $("#count").val(next);

        $('.remove-me').click(function (e) {
            e.preventDefault();
            let fieldNum = this.id.charAt(this.id.length - 1);
            let fieldID = "#field" + fieldNum;
            $(this).remove();
            $(fieldID).remove();
        });
    });

    //Find Recipes Button clicked
    $(".btn-outline-success").click(function (e) {
        profilePage();

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
                    data = $.parseJSON(data);
                    console.log(data);
                    return process(data);
                });
            }
        });
    }

    $('.typeahead').typeahead({
        source: function (query, process) {
            return $.post('/suggest', {input: query}, function (data) {
                data = $.parseJSON(data);
                console.log(data);
                return process(data);
            });
        }
    });
    createTypeahead($('typeahead'));
    $('.type')


});