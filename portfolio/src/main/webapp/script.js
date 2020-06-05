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
 * Adds a random entertainment suggestion to the page.
 */
function addRecommendation() {
  const recommendations =
      ['Rosie by Loveleo', 'A Good Song Never Dies by Saint Motel ', 'Treacherous Doctor by Wallows', 
      'Freaking Out by The Wrecks', 'Klink by Smino', 'Catch 22 by Joseph Heller', 'Wolf by Wold by Ryan Graudin',
      'Talking to Strangers by Malcolm Gladwell', 'Vicious by V.E. Schwab', 'Dangerous Girls by Abigail Haas', 
      'Nikita (TV)', 'iZombie (TV)', 'Happy Endings (TV)', 'Everything Sucks (TV)', 'American Vandal (TV)'];

  const recommendation = recommendations[Math.floor(Math.random() * recommendations.length)];

  // Add it to the page.
  const recommendationContainer = document.getElementById('rec-container');
  recommendationContainer.innerText = recommendation;
}

async function getComments() {
  const response = await fetch('/data');
  const comments = await response.json();
  const commentContainer = document.getElementById('comment-container');
  commentContainer.innerHTML = '';
  comments.forEach((comment) => {
    commentContainer.appendChild(createCommentElement(comment));
  });
  if (comments.length == 0) {
    document.getElementById('comments').innerHTML = "No comments at this time :(";
  }
}


function createCommentElement(commentObject) {
  const rowElement = document.createElement('div');
  rowElement.classList.add('row');
  rowElement.appendChild(createCommentName(commentObject.commenter));
  rowElement.appendChild(createCommentBody(commentObject.comment));
  return rowElement;
}

function createCommentName(text) {
  const colElement = document.createElement('div');
  colElement.classList.add('col-sm-4', 'text-right');
  colElement.innerHTML = text;
  return colElement;
}

function createCommentBody(text) {
  const colElement = document.createElement('div');
  colElement.classList.add('col-sm-5', 'comment-area', 'text-left');
  colElement.innerHTML = text;
  return colElement;
}

async function deleteAllComments(id_list) {
  const response = await fetch('/delete-data', {method: 'POST'});
  document.getElementById('comments').classList.remove('in')
}

