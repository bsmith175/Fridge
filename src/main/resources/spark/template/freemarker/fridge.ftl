<#assign content>
    <div class="food-images">
        <div class="item">
            <img src="//www.bbcgoodfood.com/sites/default/files/styles/recipe/public/recipe_images/shortcake_0.jpg?itok=VkRq0Fgj"
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

    <div class="bbc"> What's In My Fridge is a personalized web-application that recommends delicious recipes based on
        what you have in your fridge. Our goal is to maximize yumminess and convenience for the user with minimal pain
        when deciding what to eat.
    </div>
    <div class="food-form rounded">
        <div class="container">
            <div class="d-flex flex-row justify-content-center ">
                <h1>Time to Cook!</h1>
            </div>
            <div class="d-flex justify-content-center ">

                <p>Enter the ingredients you have in your fridge below</p>
            </div>
        </div>
        <div class="container">


            <form name="fridge-form">
                <div class="typeahead form-group">
                    <div class="row ingredients">
                        <div class="input-group" id="field1" name="field1">
                            <input type="text" class="form-control typeahead"
                                   name="field1" id="field1"
                                   placeholder="Type Ingredient..." autocomplete="off"/>
                            <button id="1" class="btn remove-me" onclick="remove_me(this.id)">Remove</button>

                        </div>
                        <div class="input-group" id="field2" name="field2">
                            <input type="text" class="form-control typeahead"
                                   name="field2" id="field2"
                                   placeholder="Type Ingredient..." autocomplete="off"/>
                            <button id="2" class="btn remove-me" onclick="remove_me(this.id)">Remove</button>

                        </div>
                        <div class="input-group" id="field3" name="field2">
                            <input type="text" class="form-control typeahead"
                                   name="field3" id="field3"
                                   placeholder="Type Ingredient..." autocomplete="off"/>
                            <button id="3" class="btn remove-me" onclick="remove_me(this.id)">Remove</button>

                        </div>
                        <p id="modify-buttons">
                        <button  class="btn add-more" onclick="add_more()" type="button">+ Add Ingredient</button>
                            <button type="button"
                                    class="btn btn-outline-danger no-meats"
                                    data-toggle="button"
                                    aria-pressed="false" autocomplete="off">No Meat
                                <img src="https://i.ibb.co/NWqs0NM/meat.png" alt="meat" height="30" width="25">

                            </button>
                            <button type="button"
                                    class="btn btn-outline-primary no-dairy"
                                    data-toggle="button"
                                    aria-pressed="false" autocomplete="off">No Dairy
                                <img src="https://i.ibb.co/9ZzTBBT/milk.png" alt="milk" height="30" width="25">

                            </button>
                            <button type="button"
                                    class="btn btn-outline-secondary no-nuts"
                                    data-toggle="button"
                                    aria-pressed="false" autocomplete="off">No Nuts
                                <img src="https://i.ibb.co/12pZXpx/peanut.png" alt="peanut" height="30" width="25">

                            </button>

                        </div>
                    </p>
                </div>

                <div class="col fridge-buttons">

                    <button type="submit"
                            class="btn btn-outline-success find-recipe btn-lg"
                            data-toggle="tooltip" data-placement="left"
                            title="Looks for recipes containing the ingredients above">
                        Start Cooking!
                        <img src="https://i.ibb.co/bsFSdGy/carrot.png" alt="carrot" height="30" width="30">

                    </button>
                    <span id="cookSpinner" class="spinner-border spinner-border-sm" role="status"
                          aria-hidden="true"></span>

                </div>

            </form>
            <div style="position: relative; left: 84%; bottom: 110px">
                <label for="numresults">Number of recipes:</label>
                <select id="numresults">
                    <option>20</option>
                    <option>40</option>
                    <option>60</option>
                    <option>100</option>
                </select>
            </div>
        </div>


    </div>

    <div><p></p></div>
    <div class="container">
        <div class="row" id="result-cards"></div>
    </div>




</#assign>

<#include "main.ftl">