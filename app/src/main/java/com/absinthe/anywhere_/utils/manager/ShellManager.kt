package com.absinthe.anywhere_.utils.manager

import com.absinthe.anywhere_.BuildConfig
import com.topjohnwu.superuser.Shell

object ShellManager {

  init {
    Shell.enableVerboseLogging = BuildConfig.DEBUG
    Shell.setDefaultBuilder(
      Shell.Builder.create()
        .setFlags(Shell.FLAG_REDIRECT_STDERR)
        .setTimeout(10)
    )
  }

  fun exec(command: String): String {
    return Shell.cmd(command).exec().out.toString()
  }

  fun acquireRoot(): Boolean {
    return Shell.cmd("su").exec().isSuccess
  }
}
