<#assign content>

    <div class="col">
        <div class=" rounded profile d-flex flex-row justify-content-center">
            <div class="flex-col justify-content-center">

                <img class="profile-pic rounded-circle"
                     src="https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg"
                     alt="Card image cap" height="160" width="160">
                <h3 class="welcome rounded" id="user-name"> Please sign in!  </h3>
            </div>
        </div>
        <ul class="nav nav-tabs"  id="myTab" role="tablist">
            <li class="nav-item ">
                <a class="nav-link active" data-toggle="tab" href="#favorites" role="tab" aria-controls="home" aria-selected="true">favorites</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" data-toggle="tab" href="#pantry" role="tab" aria-controls="home" aria-selected="false">Pantry</a>

            </li>
            <li class="nav-item">
                <a class="nav-link" data-toggle="tab" href="#excluded" role="tab" aria-controls="home" aria-selected="false">Excluded</a>

            </li>
        </ul>
        <div class="tab-content" id="myTabContent">
            <div class="tab-pane fade show active" id="favorites" role="tabpanel" aria-labelledby="favorites-tab">
                <div class="row"  id="display-favs"> </div>
            </div>
            <div class="tab-pane fade show active" id="pantry" role="tabpanel" aria-labelledby="profile-tab"></div>
            <div class="tab-pane fade show active" id="excluded" role="tabpanel" aria-labelledby="contact-tab"></div>
        </div>
    </div>



</#assign>

<#include "main.ftl">