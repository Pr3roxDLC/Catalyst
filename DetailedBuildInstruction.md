This is a more detailed guide on how to build the client

# Requirements

This requires the java jdk I recomend the adoptium jdk [(download)](https://adoptium.net)

You should also set up your **JAVA_HOME** since your builds will fail since java is *funky*
#### ^this realy only aplies with windows.

In linux you need to have **gradlew** installed since again java is *funky*
In linux you should install it with you package manager since it's mostlikely called gradlew or something similar.


# Building it

## Windows
On windows you need to run the following commands
```
gradlew setupdecompworkspace
gradlew clean
gradlew build
``` 

## Linux
On linux it's quite similar you just need to add ./ to gradlew e.g.
```
./gradlew setupdecompworkspace
./gradlew clean
./gradlew build
``` 
