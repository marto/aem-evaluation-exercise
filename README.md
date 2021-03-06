# AEM evaluation exercise

This excercise will have 3 tasks involved, which have to be completed in a day.

1. Create a scheduled task to run every 15 minutes to dowload weather forecast of Sydney region into a JCR node /content/weather.
⋅⋅*You can use Yahoo weather API or any other open source weather provider to fetch weather forecast.
2. Create a REST service to expose saved weather data with content type as JSON. The path of the API should be /bin/apis/weather
3. Create a weather component which will display weather forecast using the servlet created as a part of Step 2. The weather display should be responsive. You can choose your own layout. The component should display minimum and maximum temperature.

You are free to use your frameworks/libraries whichever you feel comfortable with. You can use existing templates or feel free to use your own templates.

Below are standard instructions for building an AEM project.

This is a project template for AEM-based applications. It is intended as a best-practice set of examples as well as a potential starting point to develop your own functionality.

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, templates, runmode specific configs as well as Hobbes-tests
* ui.content: contains sample content using the components from the ui.apps
* ui.tests: Java bundle containing JUnit tests that are executed server-side. This bundle is not to be deployed onto production.
* ui.launcher: contains glue code that deploys the ui.tests bundle (and dependent bundles) to the server and triggers the remote JUnit execution

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

If you have a running AEM instance you can build and package the whole project and deploy into AEM with  

    mvn clean install -PautoInstallPackage
    
Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallPackagePublish
    
Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

## IMPORTANT - IDE Setup 
This project uses [Lombok](projectlombok.org) - a java pre-processor that generates getters, setters, constructors, equals and hashCode methods from annotations. In order for your IDE to function and compile the source, you need to setup your IDE. Run `java -jar lombok.jar` and follow the instuctions. `lmobok.jar` can be downloaded from the [lombok site](projectlombok.org).

## Testing

There are three levels of testing contained in the project:

* unit test in core: this show-cases classic unit testing of the code contained in the bundle. To test, execute:

    mvn clean test

* server-side integration tests: this allows to run unit-like tests in the AEM-environment, ie on the AEM server. To test, execute:

    mvn clean integration-test -PintegrationTests

* client-side Hobbes.js tests: JavaScript-based browser-side tests that verify browser-side behavior. To test:

    in the browser, open the page in 'Developer mode', open the left panel and switch to the 'Tests' tab and find the generated 'MyName Tests' and run them.


## Maven settings

The project comes with the auto-public repository configured. To setup the repository in your Maven settings, refer to:

    http://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html
