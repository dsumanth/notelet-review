# Play review-ktx references a compile-time-only nullness annotation from
# play-services-basement that is missing at runtime. Without this, apps that
# minify with R8 fail with "Missing class ...NoNullnessRewrite".
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
