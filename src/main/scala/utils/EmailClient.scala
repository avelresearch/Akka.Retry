package com.utils

trait EmailClient {
  def sendEmail( message: String) : Unit
}
