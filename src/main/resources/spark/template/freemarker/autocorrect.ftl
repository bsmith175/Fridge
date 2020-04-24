<#assign content>
<p>
<div class="container">
  <div class="row">
    <div class="two columns"> </div>
    <div class="eight columns">
      <div class="text-center">
        <h1>Autocorrect</h1>
      </div>
      <label for="autocorrect-input">Type here</label>
      <input id="autocorrect-input" name="text" class="u-full-width"
          type="text"
          placeholder="I finally get a bouquet and it's a goodbye present. That's depressing.">
      <ul id="suggestions" aria-live=polite>
      </ul>
      </input>
    </div>
  </div>
</div>
</p>
</p>
</#assign>

<#include "main.ftl">