<#assign content>

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
        <div class="row" id="result-cards"></div>
    </div>

</#assign>

<#include "main.ftl">