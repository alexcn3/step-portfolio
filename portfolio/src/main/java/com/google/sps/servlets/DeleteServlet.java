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
import com.google.sps.data.Comment;
import com.google.sps.data.Suggestion;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;


@WebServlet("/delete-data")
public class DeleteServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (request.getParameter("type").equals("comment")) {
      Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
      deleteKeys(query);
    } else if (request.getParameter("type").equals("suggestion")) {
      Query query = new Query("Suggestion").addSort("timestamp", SortDirection.DESCENDING);
      deleteKeys(query);
    }
    
    response.setContentType("application/html;");
    response.getWriter().println();
  }

  private void deleteKeys(Query queryObject) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(queryObject);
    for (Entity entity : results.asIterable()) {
      Key entityKey = entity.getKey();
      datastore.delete(entityKey); 
    }
  }
}