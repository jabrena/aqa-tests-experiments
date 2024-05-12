# AQA-Tests // playlist-generators

```
          ____              _______        _       
    /\   / __ \     /\     |__   __|      | |      
   /  \ | |  | |   /  \ ______| | ___  ___| |_ ___ 
  / /\ \| |  | |  / /\ \______| |/ _ \/ __| __/ __|
 / ____ \ |__| | / ____ \     | |  __/\__ \ |_\__ \
/_/    \_\___\_\/_/    \_\    |_|\___||___/\__|___/
```

A small development to contribute to AQA-Tests.

```
git submodule update
sdk env

./mvnw clean verify
./mvnw clean verify -P aqa-tests

./mvnw -V clean
./mvnw prettier:write
./mvnw versions:display-dependency-updates
./mvnw versions:display-plugin-updates
./mvnw help:active-profiles
```

## References

- https://github.com/adoptium/aqa-tests
- https://github.com/openjdk/jcstress
- https://www.jbang.dev/
