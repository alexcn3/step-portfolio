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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.sps.data.Suggestion;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/suggestions")
public class SuggestServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    showResults(response, "", "");
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userCategory = getParameter(request, "category", "");
    String userSearch = getParameter(request, "search", "");
    if (userCategory.equals("")) {
      if (userSearch.equals("")){  
        String suggestion = getParameter(request, "suggestion-body", "");
        String category = getParameter(request, "category-select", "");
        long timestamp = System.currentTimeMillis();
        Entity taskEntity = new Entity("Suggestion");
        taskEntity.setProperty("suggest", suggestion);
        taskEntity.setProperty("category", category);
        taskEntity.setProperty("timestamp", timestamp);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(taskEntity);
        response.sendRedirect("/index.html");
      } else {
        showResults(response, "", userSearch);
      }
    } else {
       showResults(response, userCategory, "");
    }
  }

  public void showResults(HttpServletResponse response, String sugCategory, String sugSearch) throws IOException {
    Query query = new Query("Suggestion").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Suggestion> suggestions = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String suggestion = (String) entity.getProperty("suggest");
      String category = (String) entity.getProperty("category");
      long timestamp = (long) entity.getProperty("timestamp");
      Suggestion completeSuggest = new Suggestion(id, suggestion, category, timestamp);
      if (sugCategory.equals("")){
        if (sugSearch.equals("")){
          suggestions.add(completeSuggest);
        } else if (sugSearch.toLowerCase().equals(suggestion.toLowerCase())) {
          suggestions.add(completeSuggest);
        }
      } else if (sugCategory.equals(category)) {
        suggestions.add(completeSuggest);
      }
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(suggestions));
  }

  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}