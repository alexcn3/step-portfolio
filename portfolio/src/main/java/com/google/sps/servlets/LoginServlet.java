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

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    String json = null;

    if (userService.isUserLoggedIn()) {
      String logoutUrl = userService.createLogoutURL("/");
      String email = userService.getCurrentUser().getEmail();
      if (email.equals("acnwigwe@google.com")) {
        json = convertToJson(logoutUrl, "admin");
      } else {
        json = convertToJson(logoutUrl, "true"); 
      }
    } else {
      String loginUrl = userService.createLoginURL("/");
      json = convertToJson(loginUrl, "false");
    }

    response.setContentType("application/json;");
    response.getWriter().println(json);
    
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendRedirect("/projects.html");
  }

  private String convertToJson(String logLink, String logCheck) {
    String json = "{";
    json += "\"logLink\": ";
    json += "\"" + logLink + "\"";
    json += ", ";
    json += "\"logCheck\": ";
    json += "\"" + logCheck + "\"";
    json += "}";
    return json;
  }
}
