package app.heavenlypillar.midterm.data.api

import app.heavenlypillar.midterm.data.model.YnabResponse
import app.heavenlypillar.midterm.data.model.YnabSaveScheduledWrapper
import app.heavenlypillar.midterm.data.model.YnabSaveTransactionWrapper
import app.heavenlypillar.midterm.data.model.YnabScheduledResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PUT

interface YnabApiService {

    @GET("v1/budgets/{budgetId}/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String
    ): YnabResponse

    @GET("v1/budgets/{budgetId}/scheduled_transactions")
    suspend fun getScheduledTransactions(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String
    ): YnabScheduledResponse

    @POST("v1/budgets/{budgetId}/transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String,
        @Body payload: YnabSaveTransactionWrapper
    ): YnabResponse

    @PUT("v1/budgets/{budgetId}/transactions/{transactionId}")
    suspend fun updateTransaction(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String,
        @Path("transactionId") transactionId: String,
        @Body payload: YnabSaveTransactionWrapper
    ): YnabResponse

    @DELETE("v1/budgets/{budgetId}/transactions/{transactionId}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String,
        @Path("transactionId") transactionId: String
    ): retrofit2.Response<Unit>

    @POST("v1/budgets/{budgetId}/scheduled_transactions")
    suspend fun createScheduledTransaction(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String,
        @Body payload: YnabSaveScheduledWrapper
    ): retrofit2.Response<Any>

    @DELETE("v1/budgets/{budgetId}/scheduled_transactions/{transactionId}")
    suspend fun deleteScheduledTransaction(
        @Header("Authorization") token: String,
        @Path("budgetId") budgetId: String,
        @Path("transactionId") transactionId: String
    ): retrofit2.Response<Unit>

    companion object {
        private const val BASE_URL = "https://api.ynab.com/"

        val instance: YnabApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(YnabApiService::class.java)
        }
    }
}