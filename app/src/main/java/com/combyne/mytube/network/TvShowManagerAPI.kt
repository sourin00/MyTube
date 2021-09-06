package com.combyne.mytube.network

import android.os.Looper
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.interceptor.ApolloInterceptor
import com.apollographql.apollo.interceptor.ApolloInterceptorChain
import okhttp3.OkHttpClient
import java.util.concurrent.Executor

class TvShowManagerAPI {

    // Prepare Apollo Client over here to use for making GraphQL calls
    fun getApolloClient(): ApolloClient {
        check(Looper.myLooper() == Looper.getMainLooper()) {}
        val okHttpClient = OkHttpClient.Builder().build()
        return ApolloClient.builder()
            .serverUrl("https://tv-show-manager.combyne.com/graphql")
            .okHttpClient(okHttpClient)
            .addApplicationInterceptor(object : ApolloInterceptor {
                override fun interceptAsync(
                    request: ApolloInterceptor.InterceptorRequest,
                    chain: ApolloInterceptorChain,
                    dispatcher: Executor,
                    callBack: ApolloInterceptor.CallBack
                ) {
                    val map = HashMap<String,String>(2)
                    map["X-Parse-Client-Key"] = "yiCk1DW6WHWG58wjj3C4pB/WyhpokCeDeSQEXA5HaicgGh4pTUd+3/rMOR5xu1Yi"
                    map["X-Parse-Application-Id"] = "AaQjHwTIQtkCOhtjJaN/nDtMdiftbzMWW5N8uRZ+DNX9LI8AOziS10eHuryBEcCI"
                    val reqHeaders = request.requestHeaders.toBuilder().addHeaders(map).build()
                    return chain.proceedAsync(request.toBuilder().requestHeaders(reqHeaders).build(), dispatcher, callBack)
                }

                override fun dispose() {
                    // no resources need to be disposed off as of now.
                }
            })
            .build()
    }

}