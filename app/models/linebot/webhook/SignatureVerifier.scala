package models.linebot.webhook

object SignatureVerifier {
  def verify(secret: String, bodyString: String, signature: String): Boolean = {
    // TODO: impl. X-Line-Signature Verification
    true
  }
}
