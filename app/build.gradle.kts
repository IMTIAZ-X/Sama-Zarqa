plugins {
    alias(libs.plugins.android.application)
   // alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
  //  alias(libs.plugins.compose.compiler)
    
}

android {
    namespace = "com.imtbytes.samazarqa"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.imtbytes.samazarqa"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

     //   testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    
    signingConfigs {
        getByName("debug") {
            // Default debug keys
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
        create("release") {
        
            val storeFilePath = System.getenv("RELEASE_STORE_FILE") ?: "imtbytes.p12"
            val storePass = System.getenv("RELEASE_STORE_PASSWORD") ?: "imtbytes"
            val keyAliasName = System.getenv("RELEASE_KEY_ALIAS") ?: "imtbytes" 
            val keyPass = System.getenv("RELEASE_KEY_PASSWORD") ?: "imtbytes"
            val storeType = System.getenv("RELEASE_STORE_TYPE") ?: "pkcs12"
            
            storeFile = file(storeFilePath)
            storePassword = storePass
            keyAlias = keyAliasName
            keyPassword = keyPass
            
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true 
        }
    }
    
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            
            isMinifyEnabled = false
            isShrinkResources = false
            
            // debugging support
            matchingFallbacks += listOf("release")
        }
        
    getByName("release") {
    
            isMinifyEnabled = true 
            isShrinkResources = true
            
            // Profiles for 30% faster app startup
            @Suppress("UnstableApiUsage")
          //  baselineProfile.experimentalProperties["android.experimental.baselineprofiles.enable"] = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            buildConfigField("Boolean", "ENFORCE_SECURITY", "true")
            
            signingConfig = signingConfigs.getByName("release")
            
        }
    }

    compileOptions {
    
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_23)
            
            freeCompilerArgs.addAll(
                "-Xcontext-receivers",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xjvm-default=all",
                "-Xconsistent-data-class-copy",
                "-Xnon-local-break-continue",
                 "-opt-in=kotlin.RequiresOptIn",
                 "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                 "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true // Enabled for API Keys and professional environment management
    }
androidComponents {
    onVariants { variant ->
        if (variant.buildType == "release") {
            variant.packaging.resources.excludes.addAll(
                listOf(
                    "kotlin/**",
                    "kotlin-tooling-metadata.json",
                    "assets/dexopt/**",
                    "META-INF/LICENSE",
                    "META-INF/DEPENDENCIES",
                    "META-INF/*.kotlin_module",
                    "**'/DebugProbesKt.bin",
                    "okhttp3/internal/publicsuffix/NOTICE",
                    "okhttp3/**",
                    "/META-INF/{AL2.0,LGPL2.1}"
             	   )
        	    )
    	    }
  	    }
	}
}

dependencies {
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Navigation (Essential for Multi-screen apps)
    implementation(libs.androidx.navigation.compose)

    // UI Layer (Compose BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    
    // UI Tooling (Debug mode)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
