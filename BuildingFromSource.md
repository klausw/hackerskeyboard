_Instructions originally contributed by Christian Holm Christensen, thank you!_

You can get the sources for the app from Github as usual.

## Developing using Android Studio ##

  * Download Android Studio from http://developer.android.com/sdk
  * Use the built-in tools to download the Android SDK
  * Import the source code

At this point the project is still using _ant_ for building, not _gradle_, but Android Studio will work with that also. Ignore the suggestion to migrate to _gradle_ if it prompts you to do that.

Using dictionaries requires using the Android NDK for native code compilation in addition to the SDK. The application will work without that, just without spelling dictionaries. This may be sufficient if you're testing new layout or other patch, but if you want a fully working version you'll need to install the NDK too:

  * Download the Android NDK from http://developer.android.com/sdk/ndk
  * TODO: not sure how this needs to be integrated in Android Studio.

## Developing using Eclipse ##

**Warning:** The Eclipse IDE is no longer officially supported by Google. If you're getting started, you'll probably want to use Android Studio instead (see above). Keeping the instructions around for reference.

First off, you need the Android SDK and NDK available at

> http://developer.android.com/sdk
> http://developer.android.com/sdk/ndk

I'll assume you've unpacked these to _~/android/sdk_ and _~/android/ndk_
respectively.  You don't need to do that, but adjust paths accordingly.

Then, you should get the Android Development Tool (ADT) plug-in at

> http://developer.android.com/sdk/eclipse-adt.html

To be effective, you should use the Git plug-in

Note, that this will check out a directory structure that doesn't really correspond to the directory structure expected by the ADT plug-in. You should therefore use the _File->New->Android Project_ wizard to set up another project.  Make sure you select _Create Project from existing sources_ and select the sub-directory _java_ of the sources you check out before.  The project settings should be filled out automatically - except the project name - it could be something like 'my-hackerskeyboard'

The app contains Native code (C++ code) which must be compiled into a shared library.  This library is then loaded at run-time by the application, and through the Java Native Interface (JNI) protocol (member) functions in that library is called.  However, the standard set-up of the ADT does not compile JNI code on its own.   You should therefore set up a custom builder.

Right click the project and select _Properties_, or select _Project->Properties_ from the menu.  Select _Builders_ and click _New_.   In the dialog, select _Program_.  In the next window, give it a name - say _Android NDK builder_, point the _Location_ to _~/android/ndk/ndk-build_ and set the working directory to the variable _${project\_loc}_.  In the _Refresh_ tab, select _Refresh project_. In the _Build Options_ tag, select _After a "Clean"_, _Manual builds_, _During auto builds_, and _During "Clean"_.

Now, clean the project (perhaps twice) and run the app.

## Other stuff ##

### Make Git ignore some (generated) files ###

Perhaps you want to add the file _.gitignore_ in your check-out directory with content like

```
        java/bin
        java/obj
        java/gen
        java/libs
        java/.classpath
        java/.project
        java/default.properties
        .project
```

so that you do not get 'funny' output from 'git status' or the like

Happy coding.
