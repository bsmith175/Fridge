<#assign content>

    <div class="modal fade bd-example-modal-lg" id="myModal" tabindex="-1" role="dialog"
         aria-labelledby="myLargeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <div class="modal-title">

                    </div>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="description">

                    </div>
                    <h4>Ingredients</h4>
                    <ol class="ingredients">

                    </ol>
                    <h4>Instructions</h4>
                    <ul class="instructions">

                    </ul>
                    <h4>CookTime</h4>
                    <div class="cook-time">

                    </div>
                </div>
                <div class="modal-footer">
                </div>
            </div>
        </div>
    </div>
    <div id="recipe-modal">
    </div>
    <div class="col">
    <div class=" rounded profile d-flex flex-row justify-content-center">
        <div class="flex-col justify-content-center">

            <img class="profile-pic rounded-circle"
                 src="https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg"
                 alt="Card image cap" height="160" width="160">
            <h3 class="rounded welcome"> Welcome Ben Smith ! </h3>
        </div>
    </div>


    <div class="food-form rounded">
        <div class="container">
            <div class="d-flex justify-content-center ">
                <h1>Time to Cook!</h1>

            </div>
        </div>
        <div class="container">

            <form name="fridge-form">
                <div class="typeahead form-group">
                    <input type="text" class="form-control typeahead" id="field1" name="field1"
                           placeholder="Ingredient" autocomplete="off"/>
                    <button id="b1" class="btn add-more" type="button">+</button>
                </div>
                <button type="submit" class="btn btn-outline-success find-recipe ">Find Recipes</button>
            </form>
        </div>


    </div>

    <div><p></p></div>
    <div class="container">
        <div class="row" id="result-cards">
        </div>

    </div>


</#assign>

<#include "main.ftl">