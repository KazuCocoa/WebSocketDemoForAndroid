# https://bazel.build/versions/master/docs/be/android.html#android_binary
android_library(
    name = "lib",
    srcs = ["Lib.java"],
)

android_binary(
    name = "websocketdemoforandroid",
    custom_package = "com.kazucocoa.websocketdemoforandroid",
    manifest = "src/main/AndroidManifest.xml",
    resource_files = glob(["src/main/res/**"]),
    visibility = ["//visibility:public"],
    deps=["@androidsdk//com.android.support:appcompat-v7-25.0.0"],
)
