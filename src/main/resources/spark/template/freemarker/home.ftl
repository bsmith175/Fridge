<#assign content>

    <div class="col">
        <div class="row food-icons">
            <div class="item">
                <img src="https://i.ibb.co/Czn7Rn9/cake.png" alt="cake" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/YPL5cJ8/banana.png" alt="banana" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/bsFSdGy/carrot.png" alt="carrot" height="30" width="30">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/LrJJywc/icecream.png" alt="icecream" height="40" width="40">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/3RZftJy/jam.png" alt="jam" height="50" width="50">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/qFqPvHb/grapeJam.png" alt="jam" height="60" width="70">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/Czn7Rn9/cake.png" alt="cake" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/YPL5cJ8/banana.png" alt="banana" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/bsFSdGy/carrot.png" alt="carrot" height="60" width="60">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/LrJJywc/icecream.png" alt="icecream" height="40" width="40">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/3RZftJy/jam.png" alt="jam" height="50" width="50">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/qFqPvHb/grapeJam.png" alt="jam" height="60" width="70">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/Czn7Rn9/cake.png" alt="cake" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/YPL5cJ8/banana.png" alt="banana" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/bsFSdGy/carrot.png" alt="carrot" height="60" width="60">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/LrJJywc/icecream.png" alt="icecream" height="40" width="40">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/3RZftJy/jam.png" alt="jam" height="50" width="50">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/qFqPvHb/grapeJam.png" alt="jam" height="60" width="70">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/Czn7Rn9/cake.png" alt="cake" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/YPL5cJ8/banana.png" alt="banana" height="60" width="80">
            </div>
            <div class="item">
                <img src="https://i.ibb.co/bsFSdGy/carrot.png" alt="carrot" height="60" width="60">
            </div>
        </div>

        <div class=" profile d-flex flex-row justify-content-center">
            <div class="flex-col">
                <div class="flex-col justify-content-center align-items-center replace-image">
                    <img class="profile-pic rounded-circle"
                         src="https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg"
                         alt="Card image cap" height="160" width="160">
                </div>
            </div>
        </div>
        <div class="d-flex justify-content-center align-items-center;">
            <h3 class="welcome rounded" id="user-name">Please sign in to view profile! </h3>

        </div>
        <div class="d-flex row justify-content-center align-items-center;">
            <p>You may view your favorite recipes, ingredients stored in your pantry and recipes we have
                recommended for you!</p>
        </div>



        <ul class="nav nav-tabs" id="myTab" role="tablist">
            <li class="nav-item">
                <a class="nav-link " data-toggle="tab" href="#pantry" role="tab" aria-controls="home"
                   aria-selected="false">Pantry</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" data-toggle="tab" href="#enjoy" role="tab" aria-controls="home"
                   aria-selected="false">Recipes you might enjoy</a>
            </li>
            <li class="nav-item ">
                <a class="nav-link" data-toggle="tab" href="#favorites" role="tab" aria-controls="home"
                   aria-selected="true">Favorites</a>
            </li>
            <button class="btn btn-primary pull-left btn-sm" id="new-suggest" type="button" style="justify-content: center">
                <span id="loadSpinner" class="spinner-border spinner-border-sm"  role="status" aria-hidden="true"></span>
                <div id="loaderText" display="inline-block">Suggest new recipes!</div>
            </button>

        </ul>


        <div class="tab-content" id="myTabContent">
            <div class="tab-pane fade" id="favorites" role="tabpanel" aria-labelledby="favorites-tab">
                <div class="col d-flex justify-content-center">
                    <p class="enjoy-explanation favorite-explanation" id="enjoy-explanation">""</p>
                </div>
                <div class="row" id="display-favs"></div>
            </div>
            <div class="tab-pane fade" id="enjoy" role="tabpanel" aria-labelledby="enjoy-tab">
                <div class="row d-flex justify-content-center" style="margin-bottom: 4px">
                    <p class="enjoy-explanation " id="enjoy-explanation">"Based on your past favorites,
                        we've put together a couple recipes we thought you might like"</p>
                </div>
                <div  class="col d-flex justify-content-center">
                    <span id="enjoySpinner" class="spinner-border spinner-border-sm"  role="status" aria-hidden="true"></span>
                </div>
                <div class="row" id="display-enjoy"></div>
            </div>
            <div class="tab-pane fade" id="pantry" role="tabpanel" aria-labelledby="profile-tab">
                <div class="col">
                    <p class="pantry-items">Items Stored in Pantry: </p>
                    <p class="pantry-explanation">Items stored in Pantry will automatically be added to your ingredients
                        list
                        when you look for recipes.</p>
                    <div class="pantry-buttons" id="pantry-item">

                    </div>
                    <p class="pantry-add-more"> Add More Ingredients: </p>

                    <form name="pantry-form">
                        <div class="typeahead form-group">
                            <input type="text" class="form-control typeahead pantry-type" id="field1" name="field1"
                                   placeholder="Ingredient" autocomplete="off"/>
                        </div>
                        <button type="submit" class="btn btn-outline-info add-to-pantry ">Add To Pantry</button>
                    </form>
                </div>
            </div>
        </div>
    </div>



</#assign>

<#include "main.ftl">