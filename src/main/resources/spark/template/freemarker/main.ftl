<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <title>${title}</title>
    <link rel="icon" href="https://i.ibb.co/zsZQCCg/my-Fridge-2.png" height="80" width="45">

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="google-signin-client_id" content="${google_client_id}">

    <!-- In real-world webapps, css is usually minified and
         concatenated. Here, separate normalize from our code, and
         avoid minification for clarity. -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons"
          rel="stylesheet">
    <link rel="stylesheet" href="css/normalize.css">
    <link rel="stylesheet" href="css/html5bp.css">
    <link rel="stylesheet" href="css/main.css">
    <link href="https://fonts.googleapis.com/css2?family=Cinzel+Decorative&family=Fredericka+the+Great&display=swap"
          rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Abel&display=swap" rel="stylesheet">
    <link href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css" rel="stylesheet">

</head>
<body style="background-color: #cee5f2">
<nav class="navbar navbar-expand-lg navbar-dark ">
    <a class="navbar-brand" href="/">
        <img src="https://i.ibb.co/3h8sWjb/myFridge.png" height="80" width="45" class="d-inline-block align-top" alt="">
    </a>
    <a class="navbar-brand" href="/">
        What's In My Fridge?
    </a>

    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup"
            aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
        <div class="navbar-nav">
            <a class="nav-item nav-link nav-main active" href="/">Home <span class="sr-only">(current)</span></a>
            <div class="dropdown-divider"></div>
            <a class="nav-item nav-link nav-main" id="profile" href="/home">Profile</a>
        </div>
    </div>
        <div id="info-button" class="icon-img" data-toggle="modal" data-target="#button-modal">
            <div id="overlay"></div>
        </div>
       </div>

    <#--  Google Sign In  -->
    <a class="g-signin2" data-onsuccess="onSignIn" data-onfailure="onFailure" data-longtitle="true"></a>
</nav>
<div class="modal fade" id="button-modal" tabindex="-1" role="dialog"
     aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header info-header">
                <div class="modal-title">
                    <h2><b>About What's in My Fridge</b></h2>
                </div>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body info-body">
                What's In My Fridge is a personalized web-application that recommends
                delicious recipes based on what you have in your fridge. Our goal is to
                maximize yumminess and convenience for the user with minimal pain when
                deciding what to eat. Enter some ingredients and start cooking!
                <h3><b>Recipes</b></h3>
                <div>All our delicious recipes come from <a href = "https://www.bbcgoodfood.com/" target="_blank">BBC Good Food</a>.
                    We accessed the recipes through <b>Open Recipe</b>, which can be found
                    <a href = "https://github.com/dspray95/open-recipe" target="_blank">here.</a> </div>
                <h3><b>Privacy</b></h3>
                <h4>Cookies</h4>
                <div>To optimize your experience, we store limited information during
                your session using HTML web storage. You can disable use of web storage by disabling cookies
                    in Chrome, Firefox, and Safari.</div>
                <h4>Personal Information</h4>
                <div>If you choose to create an account, we store information provided to us
                by Google including your full name and Google profile picture.</div>
                <h3><b>About Us</b></h3>
                <div> We are a group of college students from Brown University that all really
                    love to cook. We decided to make this website to help people feel more inspired
                    and empowered when it comes to cooking. We hope you enjoy our product and find use
                    in it!
                </div>
            </div>


        </div>
    </div>
</div>

<div class="modal fade bd-example-modal-lg" id="myModal" tabindex="-1" role="dialog"
     aria-labelledby="myLargeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title" id="recipe-title">

                </div>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="description" id="recipe-description">

                </div>
                <h4><img src="https://i.ibb.co/ykfXN3r/carrot.png" alt="carrot" height="60" width="55">
                    Ingredients</h4>
                <ul class="ingredients-list">

                </ul>
                <h4><img src="https://i.ibb.co/ZHG2WsB/instructions.png" alt="instructions" height="60" width="43">
                    Instructions</h4>
                <ol class="instructions">

                </ol>
                <div class="cook-time d-flex row">

                </div>
                <div class="servings">

                </div>
            </div>
            <div class="modal-footer">
            </div>
        </div>
    </div>
</div>
${content}

<!-- Again, we're serving up the unminified source for clarity. -->
<script src="/js/jquery-2.1.1.js"></script>
<script src="/js/main.js"></script>
<script src="/js/profile.js"></script>
<script src="/js/signin.js"></script>


<script src="https://apis.google.com/js/platform.js" async defer></script>

<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
        integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n"
        crossorigin="anonymous"></script>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
        integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
        crossorigin="anonymous"></script>

<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
        integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
        crossorigin="anonymous"></script>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
<script src="https://use.fontawesome.com/843dae523c.js"></script>
<script src="/js/bootstrap3-typeahead.js"></script>
<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

${message}
</body>
<!-- See http://html5boilerplate.com/ for a good place to start
     dealing with real world issues like old browsers.  -->
</html>