<#assign content>
  <link rel="stylesheet" href="/css/stars.css">

<h1> Stars ⭐️⭐️⭐️ </h1>
  <h3> Search for neighbors by star name️ </h3>
  <form method="POST" action="/neighborsname">
    <ul>
      <li>
        <label for="num">Enter number of neighbors to find: </label>
        <input type="text" id="neighbors_by_name" name="num">
      </li>
      <li>
        <label for="starname">Enter star name: </label>
        <input type="text" id="neighbors_by_name" name="starname">
      </li>
      <li class="button">
        <button type="submit">Connect</button>
      </li>
    </ul>`
  </form>

  <h3> Search for neighbors by star coordinates️ </h3>
  <form method="POST" action="/neighborscoords">
    <ul>
      <li>
        <label for="num">Enter number of neighbors to find: </label>
        <input type="text" id="neighbors_by_pos" name="num">
      </li>
      <li>
        <label for="x">Enter star's x coordinate: </label>
        <input type="text" id="neighbors_by_pos" name="x">
      </li>
      <li>
        <label for="y">Enter star's y coordinate: </label>
        <input type="text" id="neighbors_by_pos" name="y">
      </li>
      <li>
        <label for="z">Enter star's z coordinate: </label>
        <input type="text" id="neighbors_by_pos" name="z">
      </li>
      <li class="button">
        <button type="submit">Connect</button>
      </li>
    </ul>
  </form>

  <h3> Radius search by star name️ </h3>
  <form method="POST" action="/radiusname">
    <ul>
      <li>
        <label for="num">Enter search radius: </label>
        <input type="text" id="neighbors_by_name" name="num">
      </li>
      <li>
        <label for="starname">Enter star name: </label>
        <input type="text" id="neighbors_by_name" name="starname">
      </li>
      <li class="button">
        <button type="submit">Connect</button>
      </li>
    </ul>`
  </form>

  <h3> Search for neighbors by star coordinates️ </h3>
  <form method="POST" action="/radiuscoords">
    <ul>
      <li>
        <label for="num">Enter search radius: </label>
        <input type="text" id="neighbors_by_pos" name="num">
      </li>
      <li>
        <label for="x">Enter star's x coordinate: </label>
        <input type="text" id="neighbors_by_pos" name="x">
      </li>
      <li>
        <label for="y">Enter star's y coordinate: </label>
        <input type="text" id="neighbors_by_pos" name="y">
      </li>
      <li>
        <label for="z">Enter star's z coordinate: </label>
        <input type="text" id="neighbors_by_pos" name="z">
      </li>
      <li class="button">
        <button type="submit">Connect</button>
      </li>
    </ul>
  </form>

    <h1> Found the following stars (sorted closest to furthest): </h1>
    ${results}

</#assign>
<#include "main.ftl">