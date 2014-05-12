package db

import anorm._
import play.api.db.DB
import play.api.Play.current
import java.sql._

// use a case class to model the stuff you save in a table
case class User(id: Long, username: String, first: String, last: String, joinDate: Timestamp)

object Users {
  // creates the table in the database
  def createTable() {
    DB.withConnection { implicit c => 
      SQL(createStmt).execute()
    } 
  }
  
  def addUser(username: String, first: String, last: String) {
    DB.withConnection { implicit c =>
      SQL(insertStmt).on('username -> username, 'first -> first, 'last -> last).execute()  
    }
  }
  
  val idQuery = "SELECT id, username, first, last FROM users WHERE id={id}"
  def getById(id: Long): Option[User] = {
    val query = SQL(idQuery).on('id -> id)
    DB.withConnection { implicit c =>
      query().map {
        case Row(theId: Long, username: String, Some(first: String), Some(last: String)) =>
          User(theId, username, first, last, new Timestamp(new java.util.Date().getTime()))
      }.headOption
    }
  }
  
  val usernameQuery = "SELECT id, username, first, last FROM users WHERE username={username}"
  def getByUsername(username: String): Option[User] = {
    val query = SQL(usernameQuery).on('username -> username)
    DB.withConnection { implicit c => 
      query().map {
        case Row(theId: Long, username: String, Some(first: String), Some(last: String))
        => User(theId, username, first, last, new Timestamp(new java.util.Date().getTime()))
      }.headOption  
    }
  }
  
  val passwordUpdate = "UPDATE users SET pwHash={pwHash} WHERE id={id}"
  def setPassword(id: Long, newPassword: String) {
    DB.withConnection { implicit c =>
      SQL(passwordUpdate).on('pwHash -> PasswordFramework.hash(newPassword), 'id -> id).execute()  
    }
  }
  def setPassword(username: String, newPassword: String) {
    getByUsername(username).map((u: User) => setPassword(u, newPassword))
  }
  def setPassword(user: User, newPassword: String) {
    setPassword(user.id, newPassword)
  }
  
  val getPassword = "SELECT pwHash FROM users WHERE id={id}"
  def checkPassword(id: Long, password: String): Boolean = {
    val query = SQL(getPassword).on('id -> id)
    DB.withConnection { implicit c =>
      val pwHash: Option[String] = query().map {
        case Row(Some(pw: String)) => pw
      }.headOption
      pwHash.map(encoded => PasswordFramework.checks(password, encoded)).getOrElse(false)
    }
  }
  def checkPassword(username: String, password: String): Boolean = {
    getByUsername(username).map((u: User) => checkPassword(u, password)).getOrElse(false)
  }
  def checkPassword(u: User, password: String): Boolean = {
    checkPassword(u.id, password)
  }

  // SQL statements
  // Note that these use Scala's multi-line strings and the
  // stripMargin method, so the strings look pretty
  val createStmt = 
    """CREATE TABLE users (
      |  id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
      |  username VARCHAR(20) UNIQUE NOT NULL,
      |  first VARCHAR(30),
      |  last VARCHAR(30),
      |  pwHash VARCHAR(256)
    )""".stripMargin
  
  // In this INSERT statement, we use {varName} for data
  // that will be filled in when we execute
  val insertStmt = 
    "INSERT INTO users (username, first, last) VALUES ({username}, {first}, {last})"
  
    
}