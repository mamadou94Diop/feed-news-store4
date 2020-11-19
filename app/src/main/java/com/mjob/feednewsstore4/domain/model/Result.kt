/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mjob.feednewsstore4.domain.model

import androidx.annotation.NonNull

data class Result<T>(val status: Int, val data: T?) {
    fun isError() = status == STATUS_ERROR
    fun isLoading() = status == STATUS_LOADING
    fun isSuccess() = status == STATUS_SUCCESS

    companion object {
        private const val STATUS_LOADING = 0
        private const val STATUS_SUCCESS = 1
        private const val STATUS_ERROR = -1

        /**
         * Helper method to create fresh state result
         */
        fun <T> success(@NonNull data: T): Result<T> {
            return Result(STATUS_SUCCESS, data)
        }

        /**
         * Helper method to create error state Result. Error state might also have the current data, if any
         */
        fun <T> error(item: T? = null): Result<T> {
            return Result(STATUS_ERROR, item)
        }

        /**
         * Helper method to create loading state Result.
         */
        fun <T> loading(data: T? = null): Result<T> {
            return Result(STATUS_LOADING, data)
        }
    }
}
