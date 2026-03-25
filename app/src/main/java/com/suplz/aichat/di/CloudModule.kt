package com.suplz.aichat.di

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.suplz.aichat.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideAmazonS3Client(): AmazonS3Client {
        val credentials = BasicAWSCredentials(
            BuildConfig.S3_ACCESS_KEY,
            BuildConfig.S3_SECRET_KEY
        )
        return AmazonS3Client(credentials).apply {
            setEndpoint(BuildConfig.S3_ENDPOINT)
        }
    }
}