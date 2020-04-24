// BEGIN REDACT
/**
 * Front end logic for providing real time autocorrect suggestions.
 */

$(document).ready(() => {
<<<<<<< HEAD


=======
>>>>>>> feat/user
    //TODO: get the jquery selectors for the list where the suggestions should go and the input box where we're typing
    //HINT: look at the hTML
    const suggestionList = $("#suggestions");
    const input = $("#autocorrect-input");
    const result_cards = $("#result-cards");
    const modal_title = $("#modal-title");

<<<<<<< HEAD

=======
>>>>>>> feat/user
    input.keyup(event => {
        //TODO: empty the suggestionList (you want new suggestions each time someone types something new)
        suggestionList.empty();

        const postParameters = {
            //TODO: get the text inside the input box
            text: input.val()
        };
        console.log(postParameters.text)

        //TODO: make a post request to the url to handle this request you set in your Main.java
        $.post("/result", postParameters, response => {
            const r = JSON.parse(response);
            //const text = JSON.parse(response)["text"];
            for (let res of r.results) {
                console.log(res)

                suggestionList.append("<li>" + res + "</li>");

            }

<<<<<<< HEAD

            $("li").click(function (e) {
                console.log(e.target);

                // const target = e.target.val();


                input.val(e.target.innerHTML);
            });

=======
            $("li").click(function (e) {
                console.log(e.target);
                // const target = e.target.val();
                input.val(e.target.innerHTML);
            });
>>>>>>> feat/user
        })
            .error(err => {
                console.log("in the .error callback");
                console.log(err);
            });
        //HINT: check out the GET, POST, and JSON section of the lab
        //HINT: all of the following should happen within the post requst

        //TODO: using the response object, use JSON to parse it
        //HINT: remember to get the specific field in the JSON you want to use
<<<<<<< HEAD

        //TODO: for each element in the set of results, append it to the suggestionList

=======
        //TODO: for each element in the set of results, append it to the suggestionList
>>>>>>> feat/user
        //TODO: add an click handler to each of the elements you added to the suggestionList
        // with a function which will replace whatever is in input with the suggestion that
        // was clicked
    });

<<<<<<< HEAD

=======
>>>>>>> feat/user
    var next = 1;
    $(".add-more").click(function (e) {
        e.preventDefault();
        var addto = "#field" + next;
        var addRemove = "#field" + (next);
        next = next + 1;
        var newIn = '<input  placeholder="Ingredient" class="form-control" id="field' + next + '" name="field' + next + '" type="text">';
<<<<<<< HEAD

=======
>>>>>>> feat/user
        var newInput = $(newIn);
        var removeBtn = '<button id="remove' + (next - 1) + '" class="btn btn-danger remove-me" >-</button></div><div id="field">';
        var removeButton = $(removeBtn);
        $(addto).after(newInput);
        $(addRemove).after(removeButton);
        $("#field" + next).attr('data-source', $(addto).attr('data-source'));
        $("#count").val(next);
<<<<<<< HEAD

=======
>>>>>>> feat/user
        $('.remove-me').click(function (e) {
            e.preventDefault();
            var fieldNum = this.id.charAt(this.id.length - 1);
            var fieldID = "#field" + fieldNum;
            $(this).remove();
            $(fieldID).remove();
        });
    });

    $(".btn-primary").click(function (e) {
        result_cards.empty();
<<<<<<< HEAD

        e.preventDefault();

=======
        e.preventDefault();
>>>>>>> feat/user
        let elements = document.forms["fridge-form"].elements;
        for (i = 0; i < elements.length; i++) {
            console.log(elements[i].value);
        }
<<<<<<< HEAD

=======
>>>>>>> feat/user
        const postParameters = {
            //TODO: get the text inside the input box
            text: ""
        };

        $.post("/recipe", postParameters, response => {

            const r = JSON.parse(response);
            //const text = JSON.parse(response)["text"];
            console.log(r.results[0]);
            console.log("post");

            var cards = 0;

            for (let res of r.results) {
                console.log(res.img_url)
<<<<<<< HEAD
               const card = "<div class=\"col-sm d-flex\">\n" +
                   "<div class=\"card card-body flex-fill\" style=\"width: 18rem;\">\n" +
=======
                const card = "<div class=\"col-sm d-flex\">\n" +
                    "<div class=\"card card-body flex-fill\" style=\"width: 18rem;\">\n" +
>>>>>>> feat/user
                    "  <img class=\"card-img-top\" src=" + res.img_url +" alt=\"Card image cap\">\n" +
                    "  <div class=\"card-body\">\n" +
                    "    <h5 class=\"card-title\">" + res.name +"</h5>\n" +
                    "    <p class=\"card-text\">" + res.description +"</p>\n" +
                    "    <button id="+cards+" type=\"button\" class=\"btn btn-outline-success openBtn\" data-toggle=\"modal\" data-target=\".bd-example-modal-lg\">View Recipe</button>\n" +
                    "  </div>\n" +
<<<<<<< HEAD
                   "  </div>\n" +
=======
                    "  </div>\n" +
>>>>>>> feat/user
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
                const id =(e.target.id);
                console.log(id);

                const result =r.results[id] ;
                console.log(result);
                var ingredients = "";
                var instructions = "";

                for(let ing of result.ingredients){
                    ingredients = ingredients + "<li>" + ing + "</li>"
                }

                for(let des of result.method){
                    instructions = instructions + "<li>" + des + "</li>"
                }
                $('.modal-title').html("<h1>" +result.name + "</h1>")
                //$('.modal-header').html( "<img class='d-flex' src=" + result.img_url +" alt=\"Card image cap\">\n"  );

                $('.description').html("<p>" +result.description + "</p>" )

                $('.ingredients').html( ingredients)
                $('.instructions').html( instructions)
                $('.cook-time').html( "<img src=\"data/recipe-clock.png\" alt=\"Flowers in Chania\">\n")



            })
<<<<<<< HEAD





=======
>>>>>>> feat/user
        })
            .error(err => {
                console.log("in the .error callback");
                console.log(err);
            });

<<<<<<< HEAD



    });


=======
    });
>>>>>>> feat/user
});