package com.absinthe.anywhere_.model

object CommandResult {
    const val RESULT_ERROR = "-1"
    const val RESULT_NULL = "1000"
    const val RESULT_SUCCESS = "1001"
    const val RESULT_NO_REACT_URL = "1002"
    const val RESULT_SHIZUKU_PERM_ERROR = "1003"
    const val RESULT_ROOT_PERM_ERROR = "1004"
    const val RESULT_FILE_URI_EXPOSED = "1005"
    const val RESULT_SECURITY_EXCEPTION = "1006"
    const val RESULT_URL_SCHEME = "1007"
    const val RESULT_EMPTY = "1008"

    val MAP: HashMap<String, String> = hashMapOf(
            Pair(RESULT_ERROR, "RESULT_ERROR"),
            Pair(RESULT_NULL, "RESULT_NULL"),
            Pair(RESULT_SUCCESS, "RESULT_SUCCESS"),
            Pair(RESULT_NO_REACT_URL, "RESULT_NO_REACT_URL"),
            Pair(RESULT_SHIZUKU_PERM_ERROR, "RESULT_SHIZUKU_PERM_ERROR"),
            Pair(RESULT_ROOT_PERM_ERROR, "RESULT_ROOT_PERM_ERROR"),
            Pair(RESULT_FILE_URI_EXPOSED, "RESULT_FILE_URI_EXPOSED"),
            Pair(RESULT_SECURITY_EXCEPTION, "RESULT_SECURITY_EXCEPTION"),
            Pair(RESULT_URL_SCHEME, "RESULT_URL_SCHEME"),
            Pair(RESULT_EMPTY, "RESULT_EMPTY")
    )
}