package com.example.gymnastlink.utils

object Constants {
    object Collections {
        const val POSTS = "posts"
        const val SECRETS = "secrets"
    }

    object Secrets {
        const val EXERCISE_DB = "EXERCISE_DB"
        const val API_KEY = "API_KEY"
        const val URL = "URL"
    }

    object URLS {
        const val GET_EXERCISE_BY_NAME_FORMAT = "https://%s/exercises/name/%s?offset=0&limit=12"
    }

    const val CACHE_DURATION = 5 * 60 * 1000 // 5 minutes in milliseconds
}