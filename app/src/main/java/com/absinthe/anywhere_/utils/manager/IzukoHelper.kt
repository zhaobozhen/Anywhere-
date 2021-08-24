package com.absinthe.anywhere_.utils.manager

import androidx.annotation.Keep

@Keep
object IzukoHelper {

  init {
    System.loadLibrary("izuko")
  }

  val cipherKey: String
    external get

  external fun checkSignature()

}
