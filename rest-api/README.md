# rest-api

## Instructions

Run API in localhost:7137 via terminal.

    sammontakoja@pop-os:~/defusing-bomb/rest-api$ dotnet run
    
Build docker image

    sammontakoja@pop-os:~/defusing-bomb/rest-api$ docker build -t defusing-bomb-rest-api .
    
Run container

    sammontakoja@pop-os:~/defusing-bomb/rest-api$ docker run --rm -it -p 5056:5056 -e ASPNETCORE_URLS=http://+:5056 defusing-bomb-rest-api
 


