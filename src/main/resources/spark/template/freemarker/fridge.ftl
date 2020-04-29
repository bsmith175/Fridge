<#assign content>
    <div class="food-images">
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/recipe-image-legacy-id--363924_12.jpg?itok=wogfslT7"
                 alt="Smiley face" height="200" width="200">
        </div>
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/recipe-image-legacy-id--950601_10.jpg?itok=sUyqwn1u"
                 alt="Smiley face" height="200" width="200">
        </div>
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/pimms.jpg?itok=ID6NXljc"
                 alt="Smiley face" height="200" width="200">
        </div>
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe/recipe-image/2016/11/buttered-carrots.jpg?itok=WRqLPW9L"
                 alt="Smiley face" height="200" width="200">
        </div>
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/roasted-summer-veg-casserole.png?itok=FAol_Lmf"
                 alt="Smiley face" height="200" width="200">
        </div>
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/ombre-cake.jpg?itok=E1WBbs3O"
                 alt="Smiley face" height="200" width="200">
        </div>
    </div>
    <div class="bbc"> "Delicious Recipes from BBC Good Food!"</div>
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