package controllers

import play.api.mvc._
import db.Users
import org.dupontmanual.forms._
import org.dupontmanual.forms.fields._
import scala.util.Random
import org.dupontmanual.forms.widgets._

object Auth extends Controller {
  object LoginForm extends Form {
    val username = new TextField("Username")
    val password = new PasswordField("Password")

    val fields = List(username, password)
  }
  object SignUpForm extends Form {
    val username = new TextField("Username")
    val password = new PasswordField("Password")
    val passAgain = new PasswordField("Password, Again")
    val firstName = new TextField("First Name")
    val lastName = new TextField("Last Name")
    val r = new Random
    val a = Math.abs(r.nextInt(1000))
    val b = Math.abs(r.nextInt(1000))
    val s = a + b
    val addUs = new TextField(s"What is $a + $b?")
    val fields = List(username, password, passAgain, firstName, lastName, addUs)
  }

  def login = Action { implicit request =>
    if (request.session.get("user").isDefined)
      Redirect(routes.Application.index).flashing(
        "message" -> "You are already logged in.")
    else
      Ok(views.html.login(Binding(LoginForm)))
  }

  def loginP = Action { implicit request =>
    import LoginForm._
    Binding(LoginForm, request) match {
      case ib: InvalidBinding => Ok(views.html.login(ib))
      case vb: ValidBinding => {
        val newUser = vb.valueOf(username)
        val isValid = Users.checkPassword(newUser, vb.valueOf(password))
        if (isValid) {
          Redirect(routes.Application.index()).withSession(
            session + (("user", newUser))).flashing(
              "message" -> s"Welcome $newUser")
        } else {
          Redirect(routes.Application.index()).flashing(
            "message" -> "Incorrect username/password combination.")
        }
      }
    }
  }

  def logout = Action { implicit request =>
    request.session.get("user") match {
      case Some(_) =>
        Redirect(routes.Application.index()).withNewSession.flashing(
          "message" -> "Come back soon!")
      case None =>
        Redirect(routes.Application.index()).flashing(
          "message" -> "You must be logged in to logout.")
    }
  }
  def register = Action { implicit request =>
    if (request.session.get("user").isDefined)
      Redirect(routes.Application.index).flashing(
        "message" -> "You are already logged in.")
    else
      Ok(views.html.register(Binding(SignUpForm)))
  }
  def registerP = Action { implicit request =>
    import SignUpForm._
    Binding(SignUpForm, request) match {
      case ib: InvalidBinding => Ok(views.html.register(ib))
      case vb: ValidBinding => {
        val newUser = vb.valueOf(username)
        val nui = Users.getByUsername(newUser)
        val newPass = vb.valueOf(password)
        val newPass2 = vb.valueOf(passAgain)
        val first = vb.valueOf(firstName)
        val last = vb.valueOf(lastName)
        println(s"a = $a; b = $b")
        val t = vb.valueOf(addUs).toInt
        nui match {
          case Some(_) => Redirect(routes.Application.index).flashing(
            "message" -> s"User $newUser exists")
          case None => if (a + b != t)
            Redirect(routes.Application.index).flashing(
              "message" -> "You got the security question wrong.")
          else if (newPass != newPass2)
            Redirect(routes.Auth.register)
          else {
            Users.addUser(newUser, first, last)
            Users.setPassword(newUser, newPass)
            Redirect(routes.Application.index).flashing(
              "message" -> s"You have created a new profile: $newUser")
          }
        }
      }
    }
  }
}