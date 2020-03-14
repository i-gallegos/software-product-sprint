// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts =
      ['I have never broken a bone!', 'I used to play soccer!', 'My favorite animal is sheep!', 'I love pumpkin-flavored foods!'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Fetch content from '/data' url.
 */
function getComments() {
  
  fetch('/auth').then(response => response.text()).then((login) => {
      if (!login.includes("Logged in.")) { // not logged in, hide comment form and display
          document.getElementById("comments-form").style.display = "none";
          document.getElementById("comments-container").style.display = "none";

          // display log in link
          const authLink = document.getElementById('comments-link');
          authLink.innerHTML = '<h3 class="center">Log in <a href="auth">here</a> to add comments.</h3>'

      } else { // logged in, display comments
          document.getElementById("comments-form").style.display = "block";
          document.getElementById("comments-container").style.display = "block";

          // display comments from JSON object
          displayComments();

          // display log out link
          const logoutLink = document.getElementById('comments-link');
          logoutLink.innerHTML = '<h3 class="center">Click <a href="logout">here</a> to log out.</h3>';
      }
  }); 
}

function displayComments() {
    fetch('/data').then(response => response.json()).then((comments) => {
    const commentsListElement = document.getElementById('comments-container');
    commentsListElement.innerHTML = '';

    counter = 0;
    while (true) {
        const key = "comment" + counter.toString();
        if (!comments.hasOwnProperty(key)) {
            break;
        }
        commentsListElement.appendChild(createListElement(comments[key].name + ": " + comments[key].text));
        counter = counter + 1;
    }
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

