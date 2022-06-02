# sample-for-android-compose-navigation-issue

## issue: https://issuetracker.google.com/issues/234410822

Jetpack Compose version: `1.1.1`

Jetpack Compose component used: `androidx.navigation:navigation-compose:2.4.2` (fixed in `2.5.0-rc01`)

Android Studio Build: `Android Studio Electric Eel | 2022.1.1 Canary 2 - Build #AI-213.7172.25.2211.8571212, built on May 11, 2022 - Runtime version: 11.0.13+0-b1751.21-8125866 aarch64`

Kotlin version: `1.6.10`

### Description
Suspected race condition. Compose's `NavHost` sometimes removes the current route, and trigger `onForgotten()`, causing all sorts of problems.

*UPDATE*: fixed in `androidx.navigation:navigation-compose:2.5.0-rc01`

### Steps to Reproduce or Code Sample to Reproduce:
1. launch the app
2. move the app to the background
3. move the app to the foreground
4. sometimes the screen status is not as expected - for example, a blank screen if you use an `AndroidViewBinding()` that contains `FragmentContainerView` and in general all things that should be remembered are also forgotten.

When the application is moved to the background, sometimes, if a recomposition starts after `onStop()`, it causes the current route to be removed (for a recomposition only) and causes unexpected behavior.

The problem occurs only inside the `NavHost`.
The composable function is removed due to `rememberVisibleList()` in `NavHost.kt`.

### Sample app #1
https://github.com/omarmiatello/sample-for-android-compose-navigation-issue/blob/master/app/src/main/java/com/github/omarmiatello/composetest/MainActivity.kt

https://user-images.githubusercontent.com/4026448/171221467-1e0f3d6a-5530-4ecd-896d-142e01f1f769.mp4

```kotlin
setContent {
    var bool by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) { // "animation" loop
            delay(1)
            bool = !bool
        }
    }
    remember { println("I will be always remembered!") }
    Text(text = "Current value: $bool")
    NavHost(
        navController = rememberNavController(),
        startDestination = "test1",
    ) {
        composable(route = "test1") {
            remember { println("I will be remembered, but sometimes forgotten (race condition)") }
            DisposableEffect(Unit) {
                onDispose { println("disposed (current lifecycle: ${it.lifecycle.currentState}) - triggered by onForgotten()") }
            }
        }
    }
}
```

### Logs
```
---------------------------- PROCESS STARTED (8743) for package com.github.omarmiatello.composetest ----------------------------
        // open app
11:35:12.023 I will be always remembered!
11:35:12.113 I will be remembered, but sometimes forgotten (race condition)
11:35:15.307 onStop()
        // in background, route NOT disposed
        // in foreground
11:35:17.452 onStop()
11:35:17.472 disposed (current lifecycle: CREATED) - triggered by onForgotten()
        // in background, route disposed
        // in foreground
11:35:18.629 I will be remembered, but sometimes forgotten (race condition)
11:35:19.756 onStop()
11:35:19.768 disposed (current lifecycle: CREATED) - triggered by onForgotten()
        // in background, route disposed
        // in foreground
11:35:21.224 I will be remembered, but sometimes forgotten (race condition)
11:35:22.558 onStop()
        // in background, route NOT disposed
        // in foreground
11:35:24.727 onStop()
11:35:24.737 disposed (current lifecycle: CREATED) - triggered by onForgotten()
        // in background, route disposed
        // in foreground
11:35:26.452 I will be remembered, but sometimes forgotten (race condition)
```

### Sample app #2 (Compose + Navigation + ViewBinding + Fragment)
https://github.com/omarmiatello/sample-for-android-compose-navigation-issue/blob/fragment/app/src/main/java/com/github/omarmiatello/composetest/MainActivity.kt

```kotlin
setContent {
    var bool by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) { // "animation" loop
            delay(1)
            bool = !bool
        }
    }
    NavHost(
        navController = rememberNavController(),
        startDestination = "test1",
    ) {
        composable(route = "test1") {
            AndroidViewBinding({ inflater, parent, attachToParent ->
                FragmentExampleBinding.inflate(inflater, parent, attachToParent)
            })
        }
    }
    Text(text = "Current value: $bool")
}
```

https://user-images.githubusercontent.com/4026448/171221858-a02fe62c-bcb5-49a1-83b6-6228b6a1ca03.mp4
