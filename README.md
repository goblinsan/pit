# Pit Server

This is the server side implementation of the classic card game [Pit](https://en.wikipedia.org/wiki/Pit_(game))
 - backend hosted via Spring Boot with a React Dashboard frontend

## Getting Started

The backend is a standard maven spring boot app.
The front-end was started with [create-react-app](https://github.com/facebook/create-react-app)
The Maven build will try to execute a "yarn build" on the front-end code, but the system will need to first have NPM and Yarn installed.
Which brings us to...

### Prerequisites

There is a runnable jar provided in the "runnable" directory.  So you could just run that.
If you do want to build - Maven will take care of the java dependencies and yarn will handle the frontend build.
This means you'll need an IDE capable of pulling in maven dependencies for the java code, and an IDE that can handle npm and react.


### Running Dev Instances

To start the back-end in dev use your IDE to run the GameAppWeb (Spring Boot entry point)

To start the front-end in dev:
```
    cd to dashboard
    yarn start
``` 

### Installing

If you just want to run the jar you'd do something like this:

```
sudo "nohup java -jar pit-server-1.0-SNAPSHOT.jar pitserver.log 2>&1 &"
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Web Framework
* [React](https://reactjs.org/) - Dashboard Framework
* [Maven](https://maven.apache.org/) - Dependency Management
* [Yarn](https://yarnpkg.com) - Frontend Build


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

