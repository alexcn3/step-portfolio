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

async function getRecs() {
  const response = await fetch('/suggestions');
  const recs = await response.json();
  showRecs(recs);
}

function showRecs(results) {
  const recContainer = document.getElementById('sugs');
  recContainer.innerHTML = '';
  results.forEach((rec) => {
    recContainer.appendChild(createRecElement(rec));
  });
  if (results.length == 0) {
    recContainer.innerText = "No user suggestions :(";
  }
}

function createRecElement(recObject) {
  const liElement = document.createElement('li');
  liElement.classList.add(getRecColor(recObject.category));
  text = recObject.suggest.replace(/</g, "&lt;").replace(/>/g, "&gt;");
  liElement.innerText = text;
  if (recObject.image) {
    liElement.appendChild(createImgElement(recObject.image));
  }
  return liElement;
}



function createImgElement(image) {
  const rowElement = document.createElement('div');
  rowElement.classList.add('row');
  const colElement = document.createElement('div');
  colElement.classList.add('col-sm-12');
  const imgElement = document.createElement('img');
  imgElement.classList.add('sug-resize');
  imgElement.src = image;
  colElement.appendChild(imgElement);
  rowElement.appendChild(colElement);
  return rowElement;
}

function getRecColor(category) {
  let color = 'white';
  if (category == 'music') {
    color = 'underline-blue';
  } else if (category == 'tv') {
    color = 'underline-yellow';
  } else if (category == 'book') {
    color = 'underline-pink';
  }
  return color;
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
  colElement.classList.add('col-sm-5', 'text-right');
  text = text.replace(/</g, "&lt;").replace(/>/g, "&gt;");
  colElement.innerHTML = text;
  return colElement;
}

function createCommentBody(text) {
  const colElement = document.createElement('div');
  colElement.classList.add('col-sm-5', 'comment-area', 'text-left');
  text = text.replace(/</g, "&lt;").replace(/>/g, "&gt;");
  colElement.innerHTML = text;
  return colElement;
}

async function deleteAllComments() {
  const params = new URLSearchParams();
  params.append('type', 'comment');
  const response = await fetch('/delete-data', {method: 'POST', body: params});
  document.getElementById('comments').classList.remove('in')
}

async function deleteAllRecs() {
  const params = new URLSearchParams();
  params.append('type', 'suggestion');
  const response = await fetch('/delete-data', {method: 'POST', body: params});
  document.getElementById('sugs').classList.remove('in')
}


async function getSpecificSug(category) {
  const params = new URLSearchParams();
  params.append('category', category);
  const response = await fetch('/suggestions', {method: 'POST', body: params});
  const recs = await response.json();
  showRecs(recs);
}

async function getSearchResults() {
  const params = new URLSearchParams();
  const searchForm = document.getElementById('search-form');
  const searchTerm = searchForm.elements[0].value;
  params.append('search', searchTerm);
  const response = await fetch('/suggestions', {method: 'POST', body: params});
  const results = await response.json();
  showRecs(results);
}

async function checkLogin() {
  const response = await fetch('/login');
  const result = await response.json();
  loginContainer = document.getElementById('login');
  loginContainer.href = result.logLink;
  commentContainer = document.getElementById('leave-comment');

  if (result.logCheck == 'true' || result.logCheck == 'admin'){ 
    loginContainer.innerText = 'Log Out';
    if (commentContainer) {
      commentContainer.disabled = false;
      commentContainer.classList.remove('disabled');
    }
  } else {
    loginContainer.innerText = 'Log In';
    if (commentContainer) {
      commentContainer.disabled = true;
      commentContainer.classList.add('disabled');
    }
  }

  if (result.logCheck == 'admin') {
    if (document.getElementById('comment-delete')){
      document.getElementById('comment-delete').disabled = false;
    } else if (document.getElementById('sug-delete')){
      document.getElementById('sug-delete').disabled = false;
    }
  }
}


function fetchBlobstoreUrl() {
  fetch('/blobstore-url')
    .then((response) => {
      return response.text();
    })
    .then((imageUploadUrl) => {
      const messageForm = document.getElementById('suggest-form');
      messageForm.action = imageUploadUrl;
    });
}
