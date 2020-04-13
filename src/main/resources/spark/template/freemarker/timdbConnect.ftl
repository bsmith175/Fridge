<#assign content>
<form action="/results" method="post">
  <ul>
      <li>
          <label for="start actor">Start actor:</label>
          <input type="text" id="start actor" name="start_actor_name"
      </li>
      <li>
          <label for="end actor">End actor:</label>
          <input type="text" id="end actor" name="end_actor_name"
      </li>
      <li class="button">
          <button type="submit">Connect</button>
      </li>
  </ul>
</form>

</#assign>
<#include "main.ftl">