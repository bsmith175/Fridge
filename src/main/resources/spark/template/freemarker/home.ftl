<#assign content>

    <div class="col">

        <div class=" rounded profile d-flex flex-row justify-content-center">
            <div class="flex-col justify-content-center align-items-center replace-image">

                <img class="profile-pic rounded-circle"
                     src="https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg"
                     alt="Card image cap" height="160" width="160">

            </div>

        </div>
        <div class="d-flex justify-content-center align-items-center;">
            <h3 class="welcome rounded" id="user-name">Please sign in to view profile! </h3>
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
            <li class="nav-item">
                <a class="nav-link" data-toggle="tab" href="#excluded" role="tab" aria-controls="home"
                   aria-selected="false">Excluded</a>

            </li>

        </ul>
        <div class="tab-content" id="myTabContent">
            <div class="tab-pane fade" id="favorites" role="tabpanel" aria-labelledby="favorites-tab">
                <div class="col d-flex justify-content-center">
                    <p class="enjoy-explanation favorite-explanation" id="enjoy-explanation">""</p>
                </div>
                <div class="row" id="display-favs"></div>
            </div>
            <div class="tab-pane fade" id="enjoy" role="tabpanel" aria-labelledby="enjoy-tab">
                <div class="col d-flex justify-content-center">
                    <p class="enjoy-explanation " id="enjoy-explanation">"Based on your past favorites,
                        we've put together a couple recipes we thought you might like"</p>
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
                            <input type="text" class="form-control typeahead" id="field1" name="field1"
                                   placeholder="Ingredient" autocomplete="off"/>
                        </div>
                        <button type="submit" class="btn btn-outline-info add-to-pantry ">Add To Pantry</button>
                    </form>
                </div>
            </div>
            <div class="tab-pane fade" id="excluded" role="tabpanel" aria-labelledby="contact-tab">
            </div>
        </div>
    </div>



</#assign>

<#include "main.ftl">