package db

import java.io.File
import db._

object TestData {
  def load(debug: Boolean = true) {
    if (debug) println("Deleting old database")
    val dbFile = new File("./play-polls.h2.db")
    if (dbFile.exists()) dbFile.delete()
    
    if (debug) println("Starting sample application")
    db.startDevApp()
    
    if (debug) println("Creating table: users")
    Users.createTable()
    if (debug) println("Adding users...")
    if (debug) println("  lskywalk01, Luke, Skywalker")
    Users.addUser("lskywalk01", "Luke", "Skywalker")
    if (debug) println("  hsolo01, Han, Solo")
    Users.addUser("hsolo01", "Han", "Solo")
    if (debug) println("  lorgana01, Leia, Organa")
    Users.addUser("lorgana01", "Leia", "Organa")
    if (debug) println("  c3po, See, Threepio")
    Users.addUser("c3po", "See", "Threepio")
    Users.setPassword("lskywalk01", "theforce")
    Users.setPassword("hsolo01", "chewie")
    Users.setPassword("lorgana01", "alderaan")
    Users.setPassword("c3po", "r2d2")
  }
}