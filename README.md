
What's In My Fridge is a personalized web-application that recommends delicious recipes based on what you have in your fridge. Our goal is to maximize yumminess and convenience for the user with minimal pain when deciding what to eat. The website can be found at https://www.whatsinmyfridgeco.com/.

# How to build/run our program from the command line
In order to build our program, run `mvn package`. To run it, use `./run` The gui will be hosted at http://localhost:4567/.
Along the way we used arguments in order to isolate certain functionality. For example `./run --init` does a bunch of our one time set up like creating the database. 

# Functionality 
### Recommending based on ingredients :
Recommending from user ingredients using recipe vector-sum algo and filtering on dietary restrictions.

### Pantry :
Users can maintain a list of ingredients with their account to avoid reentering.

### Favorites :
Users can record and review recipes they like easily in their account.

### Google Sign In :
Customizes user experience, lets them use the same account across devices

### Recommending based on favorites:
Recommending from user favorite recipes using the second approach


# Databases
We use heroku postgres to store our SQL tables: recipe, guser, favorite, pantry, exclude.
Recipes in recipe table were scraped from BBC Good Foods with the help of this script -> https://github.com/dspray95/open-recipe.

# How to run our tests
In order to run our unit tests (without recompiling), run `mvn test`. To recompile and test, run `mvn package`. In order to run our system tests, run the cs32-test executable with any system test located in the ./tests directory. Many of the system tests will require 60 seconds, so use the `-t 60` flag.

# System Tests
Made sure our gui supported all our commands and functionality. We created a list of commands that our gui should be able to perform and then filmed all of them to ensure that they worked and documented it. 
Below are the two videos we took:
https://drive.google.com/file/d/1wfpJoXDLN48OlmcRmGEZlP6FVdvGc-W5/view?usp=drivesdk
https://drive.google.com/file/d/1zJc7uVdnF0xxCmb_DitFdSrEOvUUOSEL/view?usp=drivesdk
https://docs.google.com/document/d/14hCH3-k2Tm8l9KTy_AbMS0pbzlgn_PhZnhA7_VvH0KQ/edit?usp=sharing

# Partner division of labor
#### Nate 
Nearest Neighbors KDTree Alg, Word Embeddings, recommended recipes
#### Eyal 
Ingredient-based algorithm, Word Embeddings, Restriction food groups
#### Shalan
Initial recipe sqlite DB + tokenizing. Front end Gui design and functionality.
#### Ben
Front end caching and functionality, databse management, word suggestion alg and tokenizing, Google Sign In. 

