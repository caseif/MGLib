# MGLib #

### What is MGLib? ###

MGLib is a library designed with ease-of-use in mind, and made to assist plugin developers in creating minigame plugins.
Generally speaking, almost all minigame plugins have the same basic functionality built into them. However, this basic
functionality (e.g. round management) takes a lot of time to create. If all the core code is effectively the same, why
not just use the same exact code instead of rewriting it every time? This is where MGLib comes into play. MGLib provides
an API for minigame plugins to use, so that they don't need to write it themselves. This can drastically reduce the size
and time investment of individual plugins. MGLib boasts an easy-to-use and understand API, paired with a large
featureset, with more functionality being planned and implemented every day.

### How to use MGLib ###

I've written up [a tutorial on BukkitDev](http://dev.bukkit.org/bukkit-plugins/mglib/pages/tutorial/) detailing exactly
how to build your own plugin using the library. I don't want to convert it to Markdown or maintain two copies, so for
now I must ask you to use that tutorial for reference.

Additionally, the library has [official documentation](http://jd.caseif.net/mglib) compiled
automatically by [my build server](http://ci.caseif.net/job/MGLib) for each commit. Feel free to reference that or
the documentation for any other build while using MGLib.

### Building ###

MGLib uses Gradle as its build system. To clone and build the plugin from scratch:

- `git clone https://github.com/caseif/mglib`
- `cd mglib`
- `cp scripts/* .git/hooks`
- `./gradlew` (`gradlew` or `sh ./gradlew` on Windows)

### Contribution guidelines ###

Have an idea for MGLib? Know how to Java? Awesome! First, be sure the idea follows MGLib's goals as a library. While it
is indeed very high-level, certain functionalities should still be left to the authors of the minigame plugins
themselves (e.g. something such as a gun system). Once you're certain your idea adheres to this guideline, fork the repo
and go to town implementing it, then submit a pull request when it's finished. If I like what you've done, I'll merge it
with the master branch, and it'll show up in the next version of the library!

Don't know how to Java? You can still contribute by
[creating an issue on Github](https://github.com/caseif/MGLib/issues). Just describe what you want added in detail,
and I'll review it and possibly add it to the library. As is the case with pull requests, you should verify that the
suggestion meets MGLib's goals.

## Localization ##

I want to make MGLib available in as many languages as possible. If you know a language other than English, you can help
translate the plugin on the [Crowdin page](https://crowdin.com/project/mglib).

### Who do I talk to? ###

I can be contacted [via email at mproncace@gmail.com](mailto:mproncace@gmail.com) or
[via PM on BukkitDev](http://dev.bukkit.org/home/send-private-message/?to=MaximRoncace).
