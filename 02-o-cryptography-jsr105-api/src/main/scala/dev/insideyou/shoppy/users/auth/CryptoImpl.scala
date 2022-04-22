package dev.insideyou
package shoppy
package users
package auth

import derevo.cats.show
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.spec.{ IvParameterSpec, PBEKeySpec, SecretKeySpec }
import javax.crypto.{ Cipher, SecretKeyFactory }

import cats.effect.Sync
import cats.syntax.all._
import eu.timepit.refined.auto._

object CryptoImpl {
  def make[F[_]: Sync](passwordSalt: PasswordSalt): F[Crypto] =
    Sync[F].delay(thingy(passwordSalt)).map {
      case (ec, dc) =>
        new Crypto {
          override def encrypt(password: Password): EncryptedPassword = {
            val base64 = Base64.getEncoder()
            val bytes  = password.value.getBytes("UTF-8")
            val result = new String(base64.encode(ec.value.doFinal(bytes)), "UTF-8")
            EncryptedPassword(result)
          }

          @scala.annotation.nowarn("cat=unused")
          private def decrypt(password: EncryptedPassword): Password = {
            val base64 = Base64.getDecoder()
            val bytes  = base64.decode(password.value.getBytes("UTF-8"))
            val result = new String(dc.value.doFinal(bytes), "UTF-8")
            Password(result)
          }
        }
    }

  private def thingy(passwordSalt: PasswordSalt): (EncryptCipher, DecryptCipher) = {
    val random  = new SecureRandom()
    val ivBytes = new Array[Byte](16)
    random.nextBytes(ivBytes)
    val iv       = new IvParameterSpec(ivBytes);
    val salt     = passwordSalt.secret.value.getBytes("UTF-8")
    val keySpec  = new PBEKeySpec("password".toCharArray(), salt, 65536, 256)
    val factory  = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val bytes    = factory.generateSecret(keySpec).getEncoded
    val sKeySpec = new SecretKeySpec(bytes, "AES")
    val eCipher  = EncryptCipher(Cipher.getInstance("AES/CBC/PKCS5Padding"))
    eCipher.value.init(Cipher.ENCRYPT_MODE, sKeySpec, iv)
    val dCipher = DecryptCipher(Cipher.getInstance("AES/CBC/PKCS5Padding"))
    dCipher.value.init(Cipher.DECRYPT_MODE, sKeySpec, iv)
    (eCipher, dCipher)
  }

  @derive(show)
  @newtype
  final case class PasswordSalt(secret: NonEmptyString)

  @newtype
  final case class EncryptCipher(value: Cipher)

  @newtype
  final case class DecryptCipher(value: Cipher)
}
