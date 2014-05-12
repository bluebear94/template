package controllers

import play.api.mvc._
import db.Users

object Profile extends Controller {
  def viewProfile(uid: Long) = Action { implicit request =>
    Ok(views.html.profile(uid))
  }
}