<#assign content>


<!-- CODE FOR TEXT BOX GOES HERE -->

  <h1>Autocorrect</h1>
  <p class="class"> ${message} </p>


  <form method="POST" action="/results">
    <label for="text">Enter words here: </label><br>
    <textarea name="text" id="text"></textarea><br>

    <input type="submit">
  </form>
  <p id="ID"> ${suggestions} </p>








</#assign>
<#include "main.ftl">
