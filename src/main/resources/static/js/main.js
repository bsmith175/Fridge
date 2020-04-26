// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */

const favorites = [];
favorites.concat(1);
$(document).ready(() => {

    //TODO: get the jquery selectors for the list where the suggestions should go and the input box where we're typing
    //HINT: look at the hTML
    const suggestionList = $("#suggestions");
    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");


    var next = 1;
    $(".add-more").click(function (e) {
        e.preventDefault();
        var addto = "#field" + next;
        var addRemove = "#field" + (next);
        next = next + 1;
        var newIn = '<input  placeholder="Ingredient" class="typeahead form-control type" id="field' + next + '" name="field' + next + '" type="text" autocomplete="off">';

        var newInput = $(newIn);
        createTypeahead(newInput)
        var removeBtn = '<button id="remove' + (next - 1) + '" class="btn btn-danger remove-me" >-</button></div><div id="field">';
        var removeButton = $(removeBtn);
        $(addto).after(newInput);
        $(addRemove).after(removeButton);
        $("#field" + next).attr('data-source', $(addto).attr('data-source'));
        $("#count").val(next);

        $('.remove-me').click(function (e) {
            e.preventDefault();
            var fieldNum = this.id.charAt(this.id.length - 1);
            var fieldID = "#field" + fieldNum;
            $(this).remove();
            $(fieldID).remove();
        });
    });


    $(".btn-outline-success").click(function (e) {
        result_cards.empty();
        e.preventDefault();
        const postParameters = [];
        let elements = document.forms["fridge-form"].elements;
        for (i = 0; i < elements.length; i++) {
            if (elements[i].value != "") {
                postParameters.push(elements[i].value);

            }
        }

        $('.like-button').click(function () {
            $(this).toggleClass('is-active');
        })
        $.post("/recipe-recommend", $.param({text: postParameters}, true), response => {

            const r = JSON.parse(response);
            //const text = JSON.parse(response)["text"];
            console.log(r[0]);
            console.log("post");

            var cards = 0;
            let heart_shape = "fa-heart-o";
            for (let res of r) {
                if (favorites.includes(cards)) {
                    heart_shape = "fa-heart";
                }
                console.log(favorites.includes(cards));
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

                result_cards.append(card);
                cards = cards + 1;

            }
            // function showModal(data)
            // {
            //     //you can do anything with data, or pass more data to this function. i set this data to modal header for example
            //     $("#myModal .modal-title").html(data.name)
            //     $("#myModal").modal();
            // }

            $(".openBtn").click(function (e) {
                modal_title.empty();
                e.preventDefault();
                const id = (e.target.id);
                console.log(id);
                const result = r[id];
                console.log(result.ingredients);

                var ingredients = "";
                var instructions = "";

                for (let ing of JSON.parse(result.ingredients)) {
                    ingredients = ingredients + "<li>" + ing + "</li>"
                }

                for (let des of JSON.parse(result.method)) {
                    instructions = instructions + "<li>" + des + "</li>"
                }
                $('.modal-title').html("<h1>" + result.name + "</h1>")
                //$('.modal-header').html( "<img class='d-flex' src=" + result.img_url +" alt=\"Card image cap\">\n"  );

                $('.description').html("<p>" + result.description + "</p>")

                $('.ingredients').html(ingredients)
                $('.instructions').html(instructions)
                $('.cook-time').html("<img src=\"data/recipe-clock.png\" alt=\"Flowers in Chania\">\n")


            })
            $(".heart.fa").click(function (e) {
                const id = (e.target.id);
                console.log(id);
                const postParameters = {
                    recipeId: id,
                    userId: ""

                };
                if ($(this).hasClass("fa-heart-o")) {
                    console.log("saving")
                    //TODO: make a post request to the url to handle this request you set in your Main.java
                    $.post("/addFav", postParameters, response => {
                    });
                    favorites.concat(id)
                    //adding to saved
                } else {
                    //removing from saved
                    console.log("Unsaving")
                    $.post("/remFav", postParameters, response => {
                    });
                    favorites.e


                }
                $(this).toggleClass("fa-heart fa-heart-o");
            });

        })
            .error(err => {
                console.log("in the .error callback");
                console.log(err);
            });


    });
    function createTypeahead($els){
        $els.typeahead({
            source: function (query, process) {
                return $.post('/suggest', {input: query}, function (data){
                    data = $.parseJSON(data);
                    console.log(data);
                    return process(data);
                });
            }
        });
    }
    $('.typeahead').typeahead({
        source: function (query, process) {
            return $.post('/suggest', {input: query}, function (data){
                data = $.parseJSON(data);
                console.log(data);
                return process(data);
            });
        }
    });
    createTypeahead($('typeahead'));
    $('.type')


});