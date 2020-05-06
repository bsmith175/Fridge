/**
 * Function called by google sign in to sign a user in.
 * SessionStorage and loginData are configured.
 * @param googleUser
 */
function onSignIn(googleUser) {
    // Store userprofile in global variable
    userProfile = googleUser.getBasicProfile();
    var id_token = googleUser.getAuthResponse().id_token;
    const loginData = {
        idToken: id_token,
    };

    $.post("/login", loginData, function (response) {
        const responseData = JSON.parse(response);
        if (responseData.success) {
            $("#user-name").text("Welcome, " + userProfile.getGivenName() + "!");
            $(".replace-image").empty();
            $(".replace-image").append("<img class=\"profile-pic rounded-circle\"\n" +
                "                     src=\"" + userProfile.getImageUrl() + "\"\n" +
                "                     alt=\"Card image cap\" height=\"160\" width=\"160\">");
            console.log(sessionStorage.getItem("signedin"));

            if (sessionStorage.getItem("signedin") !== "true") {

                sessionStorage.setItem("signedin", "true");
                console.log("Signing in new");
                user_name.text("Welcome, " + userProfile.getGivenName() + "!");


                // Performs page specific actions after user has signed in
                getFavs();
                console.log(suggestions);
                getPantry();

            } else {
                console.log("Already signed in");

                favorites = JSON.parse(sessionStorage.getItem("favorites"));
                if (favorites === null || favorites.length === 0) {
                    favorites = [];
                    getFavs();
                }
                console.log(favorites);

                suggestions = JSON.parse(sessionStorage.getItem("suggestions"));


                //pantryItems = JSON.parse(sessionStorage.getItem("pantry"));
                getPantry();


            }

            $(".g-signin2").hide();
            //Sign out option appears on nav bar
            $('.navbar-nav').find("#sign-out").remove();
            $('.navbar-nav').append("<a class=\"nav-item nav-link nav-main\" id=\"sign-out\" onclick=\"signOut();\">Sign out</a>");

        } else {
            console.log("Error authenticating user");
        }

    });


}

/**
 * Handles sign in errors.
 *
 * @param {*} error
 */
function onFailure(error) {
    console.log(error);
}

/**
 * Sign out the user.
 */
function signOut() {
    console.log("Signing out");
    let auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        // Reset userProfile variable
        userProfile = undefined;
        sessionStorage.clear();
        favorites = [];
        pantryItems = [];
        suggestions = [];
        meats = false;
        nuts = false;
        dairy = false;
        $('.navbar-nav').find("#sign-out").remove();
        $(".replace-image").empty();
        $(".replace-image").append("<img class=\"profile-pic rounded-circle\"\n" +
            "                     src=\"https://png.pngtree.com/png-vector/20190711/ourlarge/pngtree-cook-icon-for-your-project-png-image_1541448.jpg\"\n" +
            "                     alt=\"Card image cap\" height=\"160\" width=\"160\">");


        user_name.text("Please sign in to view profile!");
        $(".g-signin2").show();

    });
}