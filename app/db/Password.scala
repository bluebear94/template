package db

import java.security.SecureRandom
import org.apache.commons.codec.binary.Hex
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordFramework {
  lazy val rand = new SecureRandom()

  def randomSalt(numBytes: Int): Array[Byte] = {
    val result = new Array[Byte](numBytes)
    rand.nextBytes(result)
    result
  }
  
  def hash(raw: String): String = Pbkdf2WithHmacSha1.encode(raw)
  def checks(raw: String, encoded: String): Boolean = Pbkdf2WithHmacSha1.checks(raw, encoded)
}

/** A PasswordScheme provides a non-reversible way to turn a given String into
 *  another String (encoded), and a way to see if a raw and encoded String match
 *  one another.  
 */
trait PasswordHash {
  def encode(raw: String): String
  def checks(raw: String, encoded: String): Boolean
}

/** An object representing PBKDF2 password hashing.
 *  The algorithm relies on a random salt value and the encoding being repeated
 *  for a certain number of iterations. When a raw password is encoded, the
 *  result is a String of the form "pbkdf2\$iterations\$salt\$hash"
 */
object Pbkdf2WithHmacSha1 extends PasswordHash {
  private[this] val algorithm = "pbkdf2sha1"
  private[this] val defaultIterations = 10000
  private[this] val saltBytes = 8
  private[this] val (factoryName, resultSizeInBytes) = ("PBKDF2WithHmacSHA1", 160)
  
  def encode(raw: String): String = {
    encode(raw, PasswordFramework.randomSalt(saltBytes), defaultIterations)
  }
  
  def encode(raw: String, salt: Array[Byte], iterations: Int): String = {
    val factory = SecretKeyFactory.getInstance(factoryName)
    val spec = new PBEKeySpec(raw.toCharArray(), salt, iterations, resultSizeInBytes);
    val hash = new String(Hex.encodeHex(factory.generateSecret(spec).getEncoded()))
    s"${algorithm}$$${defaultIterations}$$${new String(Hex.encodeHex(salt))}$$${hash}"
  }
  
  def checks(raw: String, encoded: String): Boolean = {
    "^pbkdf2sha1\\$\\d+\\$([0-9a-fA-F][0-9a-fA-F])+\\$([0-9a-fA-F][0-9a-fA-F])+$".r.pattern.matcher(encoded).matches &&
    { encoded.split("\\$").toList match {
        case List(_, its, saltString, hashString) => {
          val iterations = its.toInt
          val salt = Hex.decodeHex(saltString.toCharArray())
          encode(raw, salt, iterations) == encoded
        }
      }
    }
  }
}
